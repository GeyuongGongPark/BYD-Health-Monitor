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
import java.security.MessageDigest
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

    /** 서버에서 새 페어링 코드 발급. 서버/API 키 없으면 로컬 생성으로 폴백. */
    suspend fun generateCode(vin: String): Result<PairingCode> = withContext(Dispatchers.IO) {
        // 서버 연동 가능할 때만 시도
        if (BuildConfig.SERVER_API_KEY.isNotBlank()) {
            try {
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
                if (httpCode in 200..299) {
                    val body   = conn.inputStream.bufferedReader().readText()
                    conn.disconnect()
                    val json   = JSONObject(body)
                    val code   = json.getString("code")
                    val vid    = json.getString("vehicleId")
                    val exp    = java.time.Instant.parse(json.getString("expiresAt")).toEpochMilli()
                    prefs.edit().putString(KEY_CODE, code).putString(KEY_VID, vid).putLong(KEY_EXP, exp).apply()
                    Log.d(TAG, "Server pairing code=$code")
                    return@withContext Result.success(PairingCode(code, vid, exp))
                }
                conn.disconnect()
            } catch (e: Exception) {
                Log.w(TAG, "Server unavailable, falling back to local: ${e.message}")
            }
        }

        // 로컬 코드 생성: VIN + 5분 타임슬롯 → SHA-256 → 6자리
        val pairing = generateLocalCode(vin)
        prefs.edit()
            .putString(KEY_CODE, pairing.code)
            .putString(KEY_VID,  pairing.vehicleId)
            .putLong(KEY_EXP,    pairing.expiresAt)
            .apply()
        Log.d(TAG, "Local pairing code=${pairing.code}")
        Result.success(pairing)
    }

    /**
     * 서버 없이 VIN + 5분 타임슬롯 기반으로 6자리 코드를 결정론적으로 생성.
     * 컴패니언 앱이 같은 알고리즘으로 검증 가능.
     */
    private fun generateLocalCode(vin: String): PairingCode {
        val slotMs   = 5 * 60 * 1000L
        val timeSlot = System.currentTimeMillis() / slotMs
        val input    = "${vin}_${timeSlot}".toByteArray()
        val hash     = MessageDigest.getInstance("SHA-256").digest(input)
        val num      = ((hash[0].toInt() and 0xFF) shl 16 or
                        ((hash[1].toInt() and 0xFF) shl 8) or
                        (hash[2].toInt() and 0xFF)) % 1_000_000
        val code     = num.toString().padStart(6, '0')
        val expiresAt = (timeSlot + 1) * slotMs
        return PairingCode(code = code, vehicleId = vin, expiresAt = expiresAt)
    }
}
