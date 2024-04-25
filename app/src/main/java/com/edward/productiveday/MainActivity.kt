package com.edward.productiveday

import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edward.productiveday.database.DatabaseHelper
import com.edward.productiveday.databinding.ActivityMainBinding
import com.edward.productiveday.services.AppCheckService


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var serviceIntent: Intent

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        checkUsageStatsPermission(this)
//        checkOverlayPermission(this)
//        startService()
//        refreshAppDatabase();

        binding.btnAppList.setOnClickListener {
            val appListIntent = Intent(this, AppListActivity::class.java)
            startActivity(appListIntent)
        }

        binding.btnStart.setOnClickListener {
            startService()
        }

        binding.btnStop.setOnClickListener {
            stopService()
        }
    }

    private fun refreshAppDatabase() {
        dbHelper = DatabaseHelper(this)
        dbHelper.deleteAll()

        val packageManager: PackageManager = this.packageManager
        val appList: List<ApplicationInfo> = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for(app in appList){
            if(packageManager.getLaunchIntentForPackage(app.packageName) != null){
                val appName = app.loadLabel(packageManager).toString()
                val appIcon = app.loadIcon(packageManager)
                val appPackageName = app.packageName

                dbHelper.addData(appName, appPackageName, appIcon)
            }
        }
    }

    private fun startService(){
        serviceIntent = Intent(this, AppCheckService::class.java)
        startService(serviceIntent)
        binding.textView.text = isServiceRunning(AppCheckService::class.java).toString()
    }

    private fun stopService(){
        if (isServiceRunning(AppCheckService::class.java)){
            stopService(serviceIntent)
        }
        binding.textView.text = isServiceRunning(AppCheckService::class.java).toString()
    }

    fun checkUsageStatsPermission(context: Context) {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else{
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }

        if(mode == AppOpsManager.MODE_ALLOWED) return

        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        context.startActivity(intent)
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.getName() == service.service.className) {
                Log.d("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.d("isMyServiceRunning?", false.toString() + "")
        return false
    }

    fun checkOverlayPermission(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    override fun onDestroy() {
//        val broadcastIntent = Intent(this, ServiceRestart::class.java)
//        sendBroadcast(broadcastIntent)
        stopService(serviceIntent)
        Log.d("MainActivity", "Activity OnDestroy")
        super.onDestroy()
    }
}
