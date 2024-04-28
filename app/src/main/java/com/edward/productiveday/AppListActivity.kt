package com.edward.productiveday

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.edward.productiveday.adapters.AppListAdapter
import com.edward.productiveday.database.DatabaseHelper
import com.edward.productiveday.databinding.ActivityAppListBinding
import com.edward.productiveday.models.AppModel

class AppListActivity : AppCompatActivity() {

    private var applicationList = ArrayList<AppModel>()
    private lateinit var binding: ActivityAppListBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: AppListAdapter

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

        getInstalledApps()

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText.toString())
                return false
            }
        })
    }

    private fun filter(query: String){
        var filteredList = ArrayList<AppModel>()
        for(app in applicationList){
            if(app.name.contains(query, true)){
                filteredList.add(app)
            }
        }
        adapter.filterList(filteredList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getInstalledApps() {
        dbHelper = DatabaseHelper(this)
        for(app in dbHelper.getAll()){
            applicationList.add(app)
        }

        adapter = AppListAdapter(applicationList, this)
        binding.rvAppList.layoutManager = LinearLayoutManager(this)
        binding.rvAppList.adapter = adapter
    }
}