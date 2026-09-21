package com.bydhealth.monitor.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.bydhealth.monitor.BuildConfig
import com.bydhealth.monitor.domain.model.MalfunctionInfo
import com.bydhealth.monitor.domain.model.Severity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG           = "AlertDispatcher"
private const val PREFS_NAME    = "alert_dispatcher"
private const val COOLDOWN_MS   = 60 * 60 * 1000L  // 1시간 중복 억제
private const val MAX_RETRY     = 3
private const val TIMEOUT_MS    = 10_000

/**
 * CRITICAL 고장 이벤트를 Railway 서버로 HTTP POST 전송.
 *
 * - 동일 코드 1시간 내 재발송 억제 (SharedPreferences 기반)
 * - 네트워크 실패 시 최대 3회 Exponential Backoff 재시도
 * - DiLink에 GMS 없으므로 FCM 직접 발송 불가 → 서버 경유
 */
@Singleton
class AlertDispatcher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkStateMonitor: NetworkStateMonitor,
) {
    private val serverUrl = "${BuildConfig.SERVER_BASE_URL}/alert"
    private val apiKey    = BuildConfig.SERVER_API_KEY

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * CRITICAL 고장 목록에서 새로 발생한 코드만 서버로 전송.
     * @param malfunctions 현재 활성 고장 목록
     * @param vin VIN 번호 (옵션)
     */
    suspend fun dispatch(malfunctions: List<MalfunctionInfo>, vin: String? = null) {
        if (!networkStateMonitor.hasActiveNetwork()) {
            Log.w(TAG, "No network, skipping dispatch")
            return
        }
        if (apiKey.isBlank()) {
            Log.w(TAG, "SERVER_API_KEY is missing, skipping dispatch")
            return
        }

        val criticals = malfunctions.filter { it.severity == Severity.CRITICAL }
        for (malfunction in criticals) {
            if (isCoolingDown(malfunction.code)) continue
            val success = sendWithRetry(malfunction, vin)
            if (success) recordSent(malfunction.code)
        }
    }

    private fun isCoolingDown(code: Int): Boolean {
        val lastSent = prefs.getLong("last_sent_$code", 0L)
        return System.currentTimeMillis() - lastSent < COOLDOWN_MS
    }

    private fun recordSent(code: Int) {
        prefs.edit().putLong("last_sent_$code", System.currentTimeMillis()).apply()
    }

    private suspend fun sendWithRetry(malfunction: MalfunctionInfo, vin: String?): Boolean =
        withContext(Dispatchers.IO) {
            var delayMs = 2_000L
            repeat(MAX_RETRY) { attempt ->
                try {
                    val ok = post(malfunction, vin)
                    if (ok) return@withContext true
                } catch (e: Exception) {
                    Log.w(TAG, "Attempt ${attempt + 1} failed: ${e.message}")
                }
                if (attempt < MAX_RETRY - 1) {
                    Thread.sleep(delayMs)
                    delayMs *= 2
                }
            }
            Log.e(TAG, "All retries failed for code=${malfunction.code}")
            false
        }

    private fun post(malfunction: MalfunctionInfo, vin: String?): Boolean {
        val payload = JSONObject().apply {
            put("code",        malfunction.code)
            put("severity",    malfunction.severity.name)
            put("name",        malfunction.name)
            put("description", malfunction.description)
            if (vin != null) put("vin", vin)
        }.toString()

        val url  = URL(serverUrl)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod    = "POST"
            connectTimeout   = TIMEOUT_MS
            readTimeout      = TIMEOUT_MS
            doOutput         = true
            setRequestProperty("Content-Type",  "application/json")
            setRequestProperty("Authorization", "Bearer $apiKey")
        }

        return try {
            OutputStreamWriter(conn.outputStream).use { it.write(payload) }
            val code = conn.responseCode
            Log.d(TAG, "POST /alert → HTTP $code (malfunction=${malfunction.code})")
            code in 200..299
        } finally {
            conn.disconnect()
        }
    }
}
