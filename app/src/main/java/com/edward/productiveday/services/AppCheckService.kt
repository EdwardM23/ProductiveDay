package com.edward.productiveday.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.edward.productiveday.AppLockPreference
import com.edward.productiveday.ServiceRestart
import com.edward.productiveday.views.RotatePhoneView
import java.util.Date
import java.util.SortedMap
import java.util.Timer
import java.util.TimerTask
import java.util.TreeMap

class AppCheckService: Service() {

    private lateinit var timer: Timer
    private val handler = Handler()
    private val TAG: String = "AppCheckService"
    private lateinit var rotatePhoneView: RotatePhoneView
    private lateinit var appLockPreference: AppLockPreference
    private lateinit var lockedAppList: List<String>
    private lateinit var context: Context
    private var counter = 0
    private var currentApp = ""
    private var previousApp = ""
    private lateinit var createdTime: Date
    private lateinit var notification: Notification

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "OnStartCommand Called")

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification() {
        var CHANNEL_ID = "MyServiceChannel"
        val notificationChannel = NotificationChannel(CHANNEL_ID, "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.description = "Producktive app, foreground service"
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

        notification = NotificationCompat.Builder(context, CHANNEL_ID).setContentTitle("Producktive").setContentText("Text...").build()
        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        appLockPreference = AppLockPreference()
        context = applicationContext
        rotatePhoneView = RotatePhoneView(context)
        lockedAppList = appLockPreference.getLockedAppList(context)!!
        createdTime = Date()

        timer = Timer("AppCheckService")
        createNotification()
        try {
            timer.schedule(updateTask, 0, 1000L)
            appLockPreference.saveServiceEnabled(context, true)
        } catch (e: Exception){
            Log.d(TAG, "catch timer.schedule()")
        }
    }

    private val updateTask: TimerTask = object : TimerTask() {
        override fun run() {
            Log.d(TAG, "Timer $createdTime is running " + counter++)
            lockedAppList = appLockPreference.getLockedAppList(context)!!

            if(checkOpenedApp() && rotatePhoneView.windowToken == null && currentApp != ""){
                if (currentApp != previousApp){
                    showUnlockScreenOverlay()
                    previousApp = currentApp
                }
            }
        }
    }

    private fun showUnlockScreenOverlay(){
        handler.postDelayed({
            rotatePhoneView.isFocusable = true
            rotatePhoneView.displayOverlay()
        }, 0)
    }

    private fun hideUnlockScreenOverlay(){
        rotatePhoneView.closeOverlay()
    }

    private fun checkOpenedApp(): Boolean {
        var mpackageName = ""

        val usageStatsManager = this.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000;

        val stats: List<UsageStats> = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        if(stats.isNotEmpty()){
            val runningTask: SortedMap<Long, UsageStats> = TreeMap()
            for(usageStats in stats){
                runningTask[usageStats.lastTimeUsed] = usageStats;
            }
            if (runningTask.isEmpty()) {
                mpackageName = "";
            }else {
                mpackageName = runningTask[runningTask.lastKey()]!!.packageName;
            }
        }

        for(app in lockedAppList){
            if(app == mpackageName){
                currentApp = app
                return true
            }
        }
        return false;
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "Service OnTaskRemoved")
        val restartServiceIntent = Intent(context, ServiceRestart::class.java)
        sendBroadcast(restartServiceIntent)

//        super.onTaskRemoved(rootIntent)
    }

    private fun stopTimer(){
        appLockPreference.saveServiceEnabled(context, false)
        timer.cancel()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
        Log.d(TAG, "Stopping Service")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service OnDestroy")
        stopTimer()
    }
}