package com.bydhealth.monitor

import android.app.Application
import com.bydhealth.monitor.ui.common.MalfunctionNotification
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HealthMonitorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MalfunctionNotification.createChannel(this)
    }
}
