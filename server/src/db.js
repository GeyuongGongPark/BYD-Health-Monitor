const { Pool } = require('pg');
const crypto = require('crypto');

const pool = new Pool({ connectionString: process.env.DATABASE_URL });

function hashAccessToken(accessToken) {
  return crypto.createHash('sha256').update(accessToken).digest('hex');
}

function registerTokenQuery(db, token, platform, sandbox = false, vehicleId = null, accessTokenHash = null) {
  return db.query(`
    INSERT INTO device_tokens (token, platform, sandbox, vehicle_id, access_token_hash, last_seen)
    VALUES ($1, $2, $3, $4, $5, NOW())
    ON CONFLICT (token) DO UPDATE
      SET last_seen  = NOW(),
          platform   = $2,
          sandbox    = $3,
          vehicle_id = COALESCE($4, device_tokens.vehicle_id),
          access_token_hash = COALESCE($5, device_tokens.access_token_hash)
  `, [token, platform, sandbox, vehicleId, accessTokenHash]);
}

async function initDB() {
  await pool.query('CREATE EXTENSION IF NOT EXISTS pgcrypto');

  // 기존 테이블
  await pool.query(`
    CREATE TABLE IF NOT EXISTS device_tokens (
      id            SERIAL PRIMARY KEY,
      token         TEXT        NOT NULL UNIQUE,
      platform      VARCHAR(10) NOT NULL,
      sandbox       BOOLEAN     NOT NULL DEFAULT false,
      vehicle_id    UUID,
      access_token_hash TEXT,
      registered_at TIMESTAMPTZ DEFAULT NOW(),
      last_seen     TIMESTAMPTZ DEFAULT NOW()
    )
  `);
  await pool.query(`
    ALTER TABLE device_tokens ADD COLUMN IF NOT EXISTS sandbox    BOOLEAN NOT NULL DEFAULT false
  `);
  await pool.query(`
    ALTER TABLE device_tokens ADD COLUMN IF NOT EXISTS vehicle_id UUID
  `);
  await pool.query(`
    ALTER TABLE device_tokens ADD COLUMN IF NOT EXISTS access_token_hash TEXT
  `);

  await pool.query(`
    CREATE TABLE IF NOT EXISTS alert_history (
      id          SERIAL PRIMARY KEY,
      code        INTEGER      NOT NULL,
      severity    VARCHAR(10)  NOT NULL,
      name        TEXT         NOT NULL,
      description TEXT         NOT NULL,
      vin         TEXT,
      alerted_at  TIMESTAMPTZ  DEFAULT NOW()
    )
  `);

  // 페어링 관련 테이블
  await pool.query(`
    CREATE TABLE IF NOT EXISTS vehicles (
      id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
      vin        TEXT        UNIQUE NOT NULL,
      created_at TIMESTAMPTZ DEFAULT NOW()
    )
  `);

  await pool.query(`
    CREATE TABLE IF NOT EXISTS pairing_codes (
      id          SERIAL PRIMARY KEY,
      vehicle_id  UUID        NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
      code        CHAR(6)     NOT NULL,
      expires_at  TIMESTAMPTZ NOT NULL,
      used        BOOLEAN     NOT NULL DEFAULT false
    )
  `);

  // 인덱스 — companionAuth가 매 요청마다 access_token_hash로 조회하므로 필수
  await pool.query(`
    CREATE INDEX IF NOT EXISTS idx_device_tokens_access_token_hash
    ON device_tokens(access_token_hash)
    WHERE access_token_hash IS NOT NULL
  `);

  console.log('[db] tables ready');
}

// ─── device_tokens ─────────────────────────────────────────────────────────

async function registerToken(token, platform, sandbox = false, vehicleId = null, accessTokenHash = null) {
  await registerTokenQuery(pool, token, platform, sandbox, vehicleId, accessTokenHash);
}

async function unregisterToken(token, vehicleId = null) {
  if (vehicleId) {
    await pool.query('DELETE FROM device_tokens WHERE token = $1 AND vehicle_id = $2', [token, vehicleId]);
    return;
  }
  await pool.query('DELETE FROM device_tokens WHERE token = $1', [token]);
}

async function getIosTokens(sandbox = false) {
  const res = await pool.query(
    'SELECT token FROM device_tokens WHERE platform = $1 AND sandbox = $2',
    ['ios', sandbox],
  );
  return res.rows.map(r => r.token);
}

async function getAndroidTokens() {
  const res = await pool.query(
    'SELECT token FROM device_tokens WHERE platform = $1',
    ['android'],
  );
  return res.rows.map(r => r.token);
}

async function getIosTokensByVehicle(vehicleId, sandbox = false) {
  const res = await pool.query(
    'SELECT token FROM device_tokens WHERE platform = $1 AND sandbox = $2 AND vehicle_id = $3',
    ['ios', sandbox, vehicleId],
  );
  return res.rows.map(r => r.token);
}

async function getAndroidTokensByVehicle(vehicleId) {
  const res = await pool.query(
    'SELECT token FROM device_tokens WHERE platform = $1 AND vehicle_id = $2',
    ['android', vehicleId],
  );
  return res.rows.map(r => r.token);
}

async function getSessionByAccessToken(accessToken) {
  if (!accessToken) return null;
  const accessTokenHash = hashAccessToken(accessToken);
  const res = await pool.query(`
    SELECT vehicle_id, access_token_hash
    FROM device_tokens
    WHERE access_token_hash = $1
      AND vehicle_id IS NOT NULL
    LIMIT 1
  `, [accessTokenHash]);
  return res.rows[0] || null;
}

// ─── alert_history ─────────────────────────────────────────────────────────

async function saveAlert({ code, severity, name, description, vin }) {
  const res = await pool.query(`
    INSERT INTO alert_history (code, severity, name, description, vin)
    VALUES ($1, $2, $3, $4, $5)
    RETURNING *
  `, [code, severity, name, description, vin || null]);
  return res.rows[0];
}

async function getAlerts({ limit = 50, offset = 0, vehicleId = null } = {}) {
  let res;
  if (vehicleId) {
    res = await pool.query(`
      SELECT ah.*
      FROM alert_history ah
      JOIN vehicles v ON v.vin = ah.vin
      WHERE v.id = $1
      ORDER BY ah.alerted_at DESC
      LIMIT $2 OFFSET $3
    `, [vehicleId, limit, offset]);
  } else {
    res = await pool.query(
      'SELECT * FROM alert_history ORDER BY alerted_at DESC LIMIT $1 OFFSET $2',
      [limit, offset],
    );
  }
  return res.rows;
}

async function cleanOldTokens(days = 90) {
  const res = await pool.query(
    `DELETE FROM device_tokens WHERE last_seen < NOW() - INTERVAL '${parseInt(days)} days'`,
  );
  console.log(`[db] cleaned ${res.rowCount} stale tokens (>${days}d)`);
}

// ─── pairing ────────────────────────────────────────────────────────────────

/** VIN으로 차량 레코드를 조회하거나 새로 생성 */
async function getOrCreateVehicle(vin) {
  await pool.query(`
    INSERT INTO vehicles (vin) VALUES ($1)
    ON CONFLICT (vin) DO NOTHING
  `, [vin]);
  const res = await pool.query('SELECT id FROM vehicles WHERE vin = $1', [vin]);
  return res.rows[0].id;  // UUID
}

/** 6자리 숫자 페어링 코드 생성 (5분 유효) */
async function generatePairingCode(vin) {
  const vehicleId = await getOrCreateVehicle(vin);

  // 기존 미사용 코드 무효화
  await pool.query(
    'UPDATE pairing_codes SET used = true WHERE vehicle_id = $1 AND used = false',
    [vehicleId],
  );

  const code = String(crypto.randomInt(100000, 999999));
  const expiresAt = new Date(Date.now() + 5 * 60 * 1000); // 5분

  await pool.query(`
    INSERT INTO pairing_codes (vehicle_id, code, expires_at)
    VALUES ($1, $2, $3)
  `, [vehicleId, code, expiresAt]);

  return { vehicleId, code, expiresAt };
}

/** 페어링 코드 검증 후 토큰 등록 */
async function confirmPairing({ code, token, platform, sandbox }) {
  const client = await pool.connect();
  try {
    await client.query('BEGIN');
    const res = await client.query(`
      WITH target AS (
        SELECT id
        FROM pairing_codes
        WHERE code = $1
          AND used = false
          AND expires_at > NOW()
        ORDER BY expires_at DESC
        LIMIT 1
        FOR UPDATE SKIP LOCKED
      ),
      claimed AS (
        UPDATE pairing_codes pc
        SET used = true
        FROM target, vehicles v
        WHERE pc.id = target.id
          AND v.id = pc.vehicle_id
        RETURNING pc.vehicle_id, v.vin
      )
      SELECT vehicle_id, vin FROM claimed
      LIMIT 1
    `, [code]);

    if (res.rows.length === 0) {
      await client.query('ROLLBACK');
      return null;
    }

    const { vehicle_id: vehicleId, vin } = res.rows[0];
    const accessToken = crypto.randomBytes(32).toString('hex');
    const accessTokenHash = hashAccessToken(accessToken);
    await registerTokenQuery(client, token, platform, sandbox, vehicleId, accessTokenHash);
    await client.query('COMMIT');
    return { vehicleId, vin, accessToken };
  } catch (err) {
    await client.query('ROLLBACK');
    throw err;
  } finally {
    client.release();
  }
}

/** VIN으로 vehicle_id 조회 */
async function getVehicleIdByVin(vin) {
  const res = await pool.query('SELECT id FROM vehicles WHERE vin = $1', [vin]);
  return res.rows[0]?.id || null;
}

module.exports = {
  initDB,
  registerToken, unregisterToken,
  getIosTokens, getAndroidTokens,
  getIosTokensByVehicle, getAndroidTokensByVehicle,
  getSessionByAccessToken,
  saveAlert, getAlerts,
  cleanOldTokens,
  generatePairingCode, confirmPairing,
  getVehicleIdByVin,
};
