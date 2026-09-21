const express = require('express');
const {
  initDB,
  registerToken, unregisterToken,
  getIosTokensByVehicle, getAndroidTokensByVehicle,
  getSessionByAccessToken,
  saveAlert, getAlerts,
  cleanOldTokens,
  generatePairingCode, confirmPairing,
  getVehicleIdByVin,
} = require('./db');
const { sendApns } = require('./apns');
const { sendFcm  } = require('./fcm');

// ─── 환경변수 검증 ─────────────────────────────────────────────────────────────

const REQUIRED = ['DATABASE_URL', 'API_KEY'];
for (const key of REQUIRED) {
  if (!process.env[key]) {
    console.error(`[startup] Missing required env var: ${key}`);
    process.exit(1);
  }
}

// ─── Express ──────────────────────────────────────────────────────────────────

const app = express();
app.use(express.json());

const auth = (req, res, next) => {
  const key = req.headers['authorization']?.replace('Bearer ', '');
  if (key !== process.env.API_KEY) return res.status(401).json({ error: 'Unauthorized' });
  next();
};

const companionAuth = async (req, res, next) => {
  try {
    const token = req.headers['authorization']?.replace('Bearer ', '');
    const session = await getSessionByAccessToken(token);
    if (!session) return res.status(401).json({ error: 'Unauthorized' });
    req.vehicleId = session.vehicle_id;
    req.accessTokenHash = session.access_token_hash;
    next();
  } catch (err) {
    console.error(`[auth] companion auth failed: ${err.message}`);
    res.status(500).json({ error: 'Auth failed' });
  }
};

// ─── Health ───────────────────────────────────────────────────────────────────

app.get('/health', (_, res) => res.json({ ok: true, ts: new Date().toISOString() }));

// ─── 페어링: 차량 앱 → 코드 발급 ──────────────────────────────────────────────

/**
 * POST /pair/generate
 * 차량 앱(DiLink)이 API Key 인증 후 VIN을 보내면 6자리 코드 반환.
 * Body: { vin }
 * Response: { code, vehicleId, expiresAt }
 */
app.post('/pair/generate', auth, async (req, res) => {
  const { vin } = req.body;
  if (!vin) return res.status(400).json({ error: 'vin required' });

  const result = await generatePairingCode(vin);
  console.log(`[pair] generated code=${result.code} vin=${vin}`);
  res.json({ code: result.code, vehicleId: result.vehicleId, expiresAt: result.expiresAt });
});

// ─── 페어링: 컴패니언 앱 → 코드 확인 + 토큰 등록 ─────────────────────────────

/**
 * POST /pair/confirm
 * 컴패니언 앱이 사용자 입력 코드 + 푸시 토큰을 보내면 vehicle_id 반환.
 * 별도 API Key 불필요 — 코드 자체가 인증 수단.
 * Body: { code, token, platform, sandbox? }
 * Response: { vehicleId, vin, accessToken }
 */
app.post('/pair/confirm', async (req, res) => {
  const { code, token, platform, sandbox } = req.body;
  if (!code || !token || !['ios', 'android'].includes(platform)) {
    return res.status(400).json({ error: 'code, token, platform(ios|android) required' });
  }

  const isSandbox = sandbox === '1' || sandbox === true;
  const result = await confirmPairing({ code, token, platform, sandbox: isSandbox });

  if (!result) {
    return res.status(400).json({ error: 'Invalid or expired pairing code' });
  }

  console.log(`[pair] confirmed code=${code} vehicleId=${result.vehicleId} platform=${platform}`);
  res.json({ vehicleId: result.vehicleId, vin: result.vin, accessToken: result.accessToken });
});

// ─── 컴패니언 앱: token 등록/해제 ─────────────────────────────────────────────

app.post('/api/register', companionAuth, async (req, res) => {
  const { token, platform, sandbox } = req.body;
  if (!token || !['ios', 'android'].includes(platform)) {
    return res.status(400).json({ error: 'token and platform(ios|android) required' });
  }
  const isSandbox = sandbox === '1' || sandbox === true;
  await registerToken(token, platform, isSandbox, req.vehicleId, req.accessTokenHash);
  console.log(`[api] registered token=${token.slice(-8)} platform=${platform} sandbox=${isSandbox}`);
  res.json({ ok: true });
});

app.delete('/api/unregister', companionAuth, async (req, res) => {
  const { token } = req.body;
  if (!token) return res.status(400).json({ error: 'token required' });
  await unregisterToken(token, req.vehicleId);
  console.log(`[api] unregistered token=${token.slice(-8)}`);
  res.json({ ok: true });
});

// ─── DiLink 앱: 고장 알림 수신 → push 전송 ────────────────────────────────────

/**
 * POST /alert
 * Body: { code, severity, name, description, vin? }
 * - VIN이 있으면 해당 차량에 페어링된 기기에만 전송
 * - VIN이 없거나 페어링 기기가 없으면 push 없이 이력만 저장
 */
app.post('/alert', auth, async (req, res) => {
  const { code, severity, name, description, vin } = req.body;
  if (!code || !severity || !name || !description) {
    return res.status(400).json({ error: 'code, severity, name, description required' });
  }

  console.log(`[alert] received code=${code} severity=${severity} name=${name} vin=${vin}`);

  const record = await saveAlert({ code, severity, name, description, vin });

  const severityLabel = severity === 'CRITICAL' ? '긴급' : severity === 'WARNING' ? '주의' : '정보';
  const title = `[${severityLabel}] ${name}`;
  const body  = description;
  const data  = { code: String(code), severity, name, alertId: String(record.id) };

  let iosProd = [], iosSandbox = [], androidTokens = [];

  const vehicleId = vin ? await getVehicleIdByVin(vin) : null;

  if (vehicleId) {
    [iosProd, iosSandbox, androidTokens] = await Promise.all([
      getIosTokensByVehicle(vehicleId, false),
      getIosTokensByVehicle(vehicleId, true),
      getAndroidTokensByVehicle(vehicleId),
    ]);
    console.log(`[alert] targeting vehicleId=${vehicleId}: ios=${iosProd.length + iosSandbox.length} android=${androidTokens.length}`);
  } else if (vin) {
    console.log(`[alert] no paired vehicle found for vin=${vin}`);
  } else {
    console.log('[alert] vin missing; saved history without push broadcast');
  }

  await Promise.all([
    sendApns(iosProd,    false, { title, body, data }),
    sendApns(iosSandbox, true,  { title, body, data }),
    sendFcm(androidTokens,     { title, body, data }),
  ]);

  res.json({ ok: true, alertId: record.id });
});

// ─── 컴패니언 앱: 알림 이력 조회 ──────────────────────────────────────────────

app.get('/alerts', companionAuth, async (req, res) => {
  const limit  = Math.min(parseInt(req.query.limit  || '50'), 200);
  const offset = parseInt(req.query.offset || '0');
  const alerts = await getAlerts({ limit, offset, vehicleId: req.vehicleId });
  res.json({ alerts });
});

// ─── 주기적 토큰 정리 (매주 일요일 자정) ──────────────────────────────────────

function scheduleWeeklyCleanup() {
  const MS_WEEK = 7 * 24 * 60 * 60 * 1000;
  const now  = new Date();
  const next = new Date(now);
  next.setDate(now.getDate() + (7 - now.getDay()) % 7 || 7);
  next.setHours(0, 0, 0, 0);
  const delay = next - now;
  setTimeout(() => {
    cleanOldTokens(90);
    setInterval(() => cleanOldTokens(90), MS_WEEK);
  }, delay);
}

// ─── 서버 시작 ────────────────────────────────────────────────────────────────

const PORT = process.env.PORT || 3000;

initDB().then(() => {
  app.listen(PORT, () => {
    console.log(`[server] listening on port ${PORT}`);
    scheduleWeeklyCleanup();
  });
}).catch(err => {
  console.error('[startup] DB init failed:', err.message);
  process.exit(1);
});
