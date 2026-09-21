package com.bydhealth.monitor.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.bydhealth.monitor.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG        = "PairingRepository"
private const val PREFS_NAME = "pairing"
private const val KEY_CODE   = "pairing_code"
private const val KEY_VID    = "vehicle_id"
private const val KEY_EXP    = "expires_at"  // epoch ms

private const val TIMEOUT_MS  = 10_000

data class PairingCode(
    val code: String,
    val vehicleId: String,
    val expiresAt: Long,  // epoch ms
)

@Singleton
class PairingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** 현재 유효한 캐시된 코드 반환 (5분 유효, 만료 1분 전 재발급) */
    fun getCachedCode(): PairingCode? {
        val code = prefs.getString(KEY_CODE, null) ?: return null
        val vid  = prefs.getString(KEY_VID,  null) ?: return null
        val exp  = prefs.getLong(KEY_EXP, 0L)
        if (System.currentTimeMillis() > exp - 60_000L) return null  // 만료 1분 전이면 무효
        return PairingCode(code, vid, exp)
    }

    /** 서버에서 새 페어링 코드 발급 */
    suspend fun generateCode(vin: String): Result<PairingCode> = withContext(Dispatchers.IO) {
        try {
            if (BuildConfig.SERVER_API_KEY.isBlank()) {
                return@withContext Result.failure(Exception("SERVER_API_KEY missing"))
            }
            val payload = JSONObject().put("vin", vin).toString()
            val url  = URL("${BuildConfig.SERVER_BASE_URL}/pair/generate")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = TIMEOUT_MS
                readTimeout    = TIMEOUT_MS
                doOutput = true
                setRequestProperty("Content-Type",  "application/json")
                setRequestProperty("Authorization", "Bearer ${BuildConfig.SERVER_API_KEY}")
            }

            OutputStreamWriter(conn.outputStream).use { it.write(payload) }

            val httpCode = conn.responseCode
            if (httpCode !in 200..299) {
                conn.disconnect()
                return@withContext Result.failure(Exception("HTTP $httpCode"))
            }

            val body   = conn.inputStream.bufferedReader().readText()
            conn.disconnect()

            val json   = JSONObject(body)
            val code   = json.getString("code")
            val vid    = json.getString("vehicleId")
            val expStr = json.getString("expiresAt")  // ISO8601

            // ISO8601 → epoch ms
            val exp = java.time.Instant.parse(expStr).toEpochMilli()

            prefs.edit()
                .putString(KEY_CODE, code)
                .putString(KEY_VID,  vid)
                .putLong(KEY_EXP,    exp)
                .apply()

            Log.d(TAG, "Generated pairing code=$code vehicleId=$vid")
            Result.success(PairingCode(code, vid, exp))
        } catch (e: Exception) {
            Log.e(TAG, "generateCode failed: ${e.message}")
            Result.failure(e)
        }
    }
}
