const { unregisterToken } = require('./db');

let messaging = null;

function getMessaging() {
  if (!messaging) {
    const admin = require('firebase-admin');
    if (!admin.apps.length) {
      const serviceAccount = JSON.parse(process.env.FCM_SERVICE_ACCOUNT);
      admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });
    }
    messaging = admin.messaging();
  }
  return messaging;
}

/**
 * Android FCM push (high-priority visible notification)
 */
async function sendFcm(tokens, { title, body, data = {} }) {
  if (!tokens.length) return;
  if (!process.env.FCM_SERVICE_ACCOUNT) {
    console.log('[fcm] FCM_SERVICE_ACCOUNT not set, skipping');
    return;
  }

  const msg = getMessaging();
  let ok = 0, fail = 0;

  for (let i = 0; i < tokens.length; i += 500) {
    const batch = tokens.slice(i, i + 500);
    try {
      const res = await msg.sendEachForMulticast({
        tokens: batch,
        notification: { title, body },
        data: Object.fromEntries(Object.entries(data).map(([k, v]) => [k, String(v)])),
        android: { priority: 'high' },
      });
      ok   += res.successCount;
      fail += res.failureCount;

      for (let j = 0; j < res.responses.length; j++) {
        const r = res.responses[j];
        if (!r.success) {
          const code = r.error?.code;
          if (
            code === 'messaging/registration-token-not-registered' ||
            code === 'messaging/invalid-registration-token'
          ) {
            await unregisterToken(batch[j]);
            console.log(`[fcm] removed stale token ${batch[j].slice(-8)}`);
          } else {
            console.error(`[fcm] error token=${batch[j].slice(-8)} code=${code}`);
          }
        }
      }
    } catch (err) {
      console.error('[fcm] batch error:', err.message);
    }
  }
  console.log(`[fcm] sent total=${tokens.length} ok=${ok} fail=${fail}`);
}

module.exports = { sendFcm };
