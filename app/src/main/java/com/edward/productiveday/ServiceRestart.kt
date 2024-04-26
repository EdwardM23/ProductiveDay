package com.edward.productiveday

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.edward.productiveday.services.AppCheckService


class ServiceRestart: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Broadcast Restart", "Service tried to stop")

        val appLockPreference = AppLockPreference()
        if (appLockPreference.isServiceEnabled(context!!)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, AppCheckService::class.java))
            } else {
                context.startService(Intent(context, AppCheckService::class.java))
            }
        }
    }
}