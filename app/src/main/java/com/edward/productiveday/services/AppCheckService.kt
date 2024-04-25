package com.edward.productiveday.services

import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.edward.productiveday.AppLockPreference
import com.edward.productiveday.ServiceRestart
import com.edward.productiveday.views.RotatePhoneView
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer = Timer("AppCheckService")
        try {
            timer.schedule(updateTask, 0, 1000L)
        } catch (e: Exception){
            Log.d(TAG, "catch timer.schedule()")
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        rotatePhoneView = RotatePhoneView(context)
        appLockPreference = AppLockPreference()
        lockedAppList = appLockPreference.getLockedAppList(context)!!
    }

    private val updateTask: TimerTask = object : TimerTask() {
        override fun run() {
            Log.d(TAG, "Timer is running " + counter++)
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

    //    private fun startTimer(){
//        timer = Timer("AppCheckService")
//        timer.schedule(updateTask, 0, 1000L)
//    }
//    override fun onTaskRemoved(rootIntent: Intent?) {
//        super.onTaskRemoved(rootIntent)
//        Log.d(TAG, "OnTaskRemoved")
//    }

    private fun stopTimer(){
        timer.cancel()
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
        val broadcastIntent = Intent(this, ServiceRestart::class.java)
        sendBroadcast(broadcastIntent)
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service OnDestroy")
        stopTimer()
    }
}