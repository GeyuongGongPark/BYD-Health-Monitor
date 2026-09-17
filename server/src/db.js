const { Pool } = require('pg');

const pool = new Pool({ connectionString: process.env.DATABASE_URL });

async function initDB() {
  await pool.query(`
    CREATE TABLE IF NOT EXISTS device_tokens (
      id            SERIAL PRIMARY KEY,
      token         TEXT        NOT NULL UNIQUE,
      platform      VARCHAR(10) NOT NULL,
      sandbox       BOOLEAN     NOT NULL DEFAULT false,
      registered_at TIMESTAMPTZ DEFAULT NOW(),
      last_seen     TIMESTAMPTZ DEFAULT NOW()
    )
  `);
  await pool.query(`
    ALTER TABLE device_tokens ADD COLUMN IF NOT EXISTS sandbox BOOLEAN NOT NULL DEFAULT false
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
  console.log('[db] tables ready');
}

async function registerToken(token, platform, sandbox = false) {
  await pool.query(`
    INSERT INTO device_tokens (token, platform, sandbox, last_seen)
    VALUES ($1, $2, $3, NOW())
    ON CONFLICT (token) DO UPDATE SET last_seen = NOW(), platform = $2, sandbox = $3
  `, [token, platform, sandbox]);
}

async function unregisterToken(token) {
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

async function saveAlert({ code, severity, name, description, vin }) {
  const res = await pool.query(`
    INSERT INTO alert_history (code, severity, name, description, vin)
    VALUES ($1, $2, $3, $4, $5)
    RETURNING *
  `, [code, severity, name, description, vin || null]);
  return res.rows[0];
}

async function getAlerts({ limit = 50, offset = 0 } = {}) {
  const res = await pool.query(
    'SELECT * FROM alert_history ORDER BY alerted_at DESC LIMIT $1 OFFSET $2',
    [limit, offset],
  );
  return res.rows;
}

async function cleanOldTokens(days = 90) {
  const res = await pool.query(
    `DELETE FROM device_tokens WHERE last_seen < NOW() - INTERVAL '${parseInt(days)} days'`,
  );
  console.log(`[db] cleaned ${res.rowCount} stale tokens (>${days}d)`);
}

module.exports = {
  initDB,
  registerToken, unregisterToken,
  getIosTokens, getAndroidTokens,
  saveAlert, getAlerts,
  cleanOldTokens,
};
