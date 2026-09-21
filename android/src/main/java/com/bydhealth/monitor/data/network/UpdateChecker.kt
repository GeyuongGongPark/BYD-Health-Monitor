package com.bydhealth.monitor.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "UpdateChecker"
private const val RELEASES_URL =
    "https://api.github.com/repos/GeyuongGongPark/BYD-Health-Monitor/releases/latest"
private const val VEHICLE_APK_PREFIX = "byd-vehicle-"
private const val TIMEOUT_MS = 10_000

data class UpdateInfo(
    val latestVersion: String,   // "0.1.0"
    val downloadUrl: String,
    val releaseNotes: String,
)

@Singleton
class UpdateChecker @Inject constructor() {

    /**
     * GitHub Releases API에서 최신 버전을 조회.
     * 현재 버전보다 높으면 UpdateInfo 반환, 최신이면 null.
     */
    suspend fun checkForUpdate(currentVersion: String): Result<UpdateInfo?> =
        withContext(Dispatchers.IO) {
            try {
                val conn = (URL(RELEASES_URL).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                    setRequestProperty("Accept", "application/vnd.github+json")
                }

                val code = conn.responseCode
                if (code != 200) {
                    conn.disconnect()
                    return@withContext Result.failure(Exception("HTTP $code"))
                }

                val body = conn.inputStream.bufferedReader().readText()
                conn.disconnect()

                val json = JSONObject(body)
                val tagName = json.getString("tag_name")         // "v0.1.0"
                val latestVersion = tagName.removePrefix("v")    // "0.1.0"
                val releaseNotes = json.optString("body", "")

                if (!isNewer(latestVersion, currentVersion)) {
                    Log.d(TAG, "Already up to date: $currentVersion")
                    return@withContext Result.success(null)
                }

                // assets에서 차량용 APK URL 추출
                val assets: JSONArray = json.getJSONArray("assets")
                var downloadUrl = ""
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.getString("name")
                    if (name.startsWith(VEHICLE_APK_PREFIX)) {
                        downloadUrl = asset.getString("browser_download_url")
                        break
                    }
                }

                if (downloadUrl.isEmpty()) {
                    return@withContext Result.failure(Exception("APK asset not found in release"))
                }

                Log.d(TAG, "Update available: $currentVersion → $latestVersion")
                Result.success(UpdateInfo(latestVersion, downloadUrl, releaseNotes))
            } catch (e: Exception) {
                Log.w(TAG, "checkForUpdate failed: ${e.message}")
                Result.failure(e)
            }
        }

    /** semantic version 비교 — latest > current 이면 true */
    private fun isNewer(latest: String, current: String): Boolean {
        val l = latest.split(".").map { it.toIntOrNull() ?: 0 }
        val c = current.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLen = maxOf(l.size, c.size)
        for (i in 0 until maxLen) {
            val lv = l.getOrElse(i) { 0 }
            val cv = c.getOrElse(i) { 0 }
            if (lv > cv) return true
            if (lv < cv) return false
        }
        return false
    }
}
