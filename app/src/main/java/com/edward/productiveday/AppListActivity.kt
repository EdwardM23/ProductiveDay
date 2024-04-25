package com.edward.productiveday

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.edward.productiveday.adapters.AppListAdapter
import com.edward.productiveday.database.DatabaseHelper
import com.edward.productiveday.databinding.ActivityAppListBinding
import com.edward.productiveday.models.AppModel

class AppListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppListBinding
    private lateinit var dbHelper: DatabaseHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAppListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = binding.rvAppList
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = AppListAdapter(getInstalledApps(), this)
        recyclerView.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getInstalledApps(): List<AppModel>{
        dbHelper = DatabaseHelper(this)
//        val packageManager: PackageManager = this.packageManager
//        val appList: List<ApplicationInfo> = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//        for(app in appList){
//            if(packageManager.getLaunchIntentForPackage(app.packageName) != null){
//                val appName = app.loadLabel(packageManager).toString()
//                val appIcon = app.loadIcon(packageManager)
//                val appPackageName = app.packageName
//                Log.d("Test", appPackageName)
//                val appModel = AppModel(appIcon, appName, appPackageName);
//                filteredAppList.add(appModel)
//            }
//        }

        return dbHelper.getAll()
    }
}