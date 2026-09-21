package com.bydhealth.monitor.data.vehicle

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "VehicleIdentityRepo"
private const val PREFS_NAME = "vehicle_identity"
private const val KEY_FALLBACK_VIN = "fallback_vin"

@Singleton
class VehicleIdentityRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiLoader: BydApiLoader,
) {
    fun getVin(): String {
        val realVin = try {
            apiLoader.bodyworkDevice?.getAutoVIN()?.trim()
        } catch (e: Throwable) {
            Log.w(TAG, "VIN load failed: ${e.message}")
            null
        }

        if (!realVin.isNullOrBlank()) return realVin

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val cached = prefs.getString(KEY_FALLBACK_VIN, null)
        if (!cached.isNullOrBlank()) return cached

        val generated = "MOCK-${UUID.randomUUID().toString().take(12).uppercase()}"
        prefs.edit().putString(KEY_FALLBACK_VIN, generated).apply()
        return generated
    }
}
