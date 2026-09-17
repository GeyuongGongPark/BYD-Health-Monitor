const express = require('express');
const {
  initDB,
  registerToken, unregisterToken,
  getIosTokens, getAndroidTokens,
  saveAlert, getAlerts,
  cleanOldTokens,
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

// ─── Health ───────────────────────────────────────────────────────────────────

app.get('/health', (_, res) => res.json({ ok: true, ts: new Date().toISOString() }));

// ─── 컴패니언 앱: token 등록/해제 ─────────────────────────────────────────────

app.post('/api/register', auth, async (req, res) => {
  const { token, platform, sandbox } = req.body;
  if (!token || !['ios', 'android'].includes(platform)) {
    return res.status(400).json({ error: 'token and platform(ios|android) required' });
  }
  const isSandbox = sandbox === '1' || sandbox === true;
  await registerToken(token, platform, isSandbox);
  console.log(`[api] registered token=${token.slice(-8)} platform=${platform} sandbox=${isSandbox}`);
  res.json({ ok: true });
});

app.delete('/api/unregister', auth, async (req, res) => {
  const { token } = req.body;
  if (!token) return res.status(400).json({ error: 'token required' });
  await unregisterToken(token);
  console.log(`[api] unregistered token=${token.slice(-8)}`);
  res.json({ ok: true });
});

// ─── DiLink 앱: 고장 알림 수신 → push 전송 ────────────────────────────────────

/**
 * POST /alert
 * Body: { code, severity, name, description, vin? }
 * - alert_history에 저장
 * - 등록된 모든 기기(iOS + Android)에 push 전송
 */
app.post('/alert', auth, async (req, res) => {
  const { code, severity, name, description, vin } = req.body;
  if (!code || !severity || !name || !description) {
    return res.status(400).json({ error: 'code, severity, name, description required' });
  }

  console.log(`[alert] received code=${code} severity=${severity} name=${name}`);

  // 이력 저장
  const record = await saveAlert({ code, severity, name, description, vin });

  // push 메시지 구성
  const severityLabel = severity === 'CRITICAL' ? '긴급' : severity === 'WARNING' ? '주의' : '정보';
  const title = `[${severityLabel}] ${name}`;
  const body  = description;
  const data  = { code: String(code), severity, name, alertId: String(record.id) };

  // 등록된 토큰 조회 후 병렬 push 전송
  const [iosProd, iosSandbox, androidTokens] = await Promise.all([
    getIosTokens(false),
    getIosTokens(true),
    getAndroidTokens(),
  ]);

  await Promise.all([
    sendApns(iosProd,    false, { title, body, data }),
    sendApns(iosSandbox, true,  { title, body, data }),
    sendFcm(androidTokens,     { title, body, data }),
  ]);

  res.json({ ok: true, alertId: record.id });
});

// ─── 컴패니언 앱: 알림 이력 조회 ──────────────────────────────────────────────

app.get('/alerts', auth, async (req, res) => {
  const limit  = Math.min(parseInt(req.query.limit  || '50'), 200);
  const offset = parseInt(req.query.offset || '0');
  const alerts = await getAlerts({ limit, offset });
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
