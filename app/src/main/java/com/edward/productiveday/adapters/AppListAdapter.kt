package com.edward.productiveday.adapters

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.edward.productiveday.AppLockPreference
import com.edward.productiveday.R
import com.edward.productiveday.databinding.AdapterAppListBinding
import com.edward.productiveday.models.AppModel
import com.edward.productiveday.utils.DisplayToast

class AppListAdapter(private var appList: ArrayList<AppModel>, context: Context):
    RecyclerView.Adapter<AppListAdapter.ViewHolder>() {
        private lateinit var binding: AdapterAppListBinding
        private val context = context
    private val appLockPreference: AppLockPreference

    init {
        appLockPreference = AppLockPreference()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_app_list, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appModel = appList[position]

        holder.logo.setImageDrawable(appModel.logo)
        holder.name.text = appModel.name
        holder.switch.setOnCheckedChangeListener(null)
        holder.switch.isChecked = checkAppLocked(appModel.packageName)

        holder.switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                appLockPreference.addLockedApp(context, appModel.packageName)
            } else{
                appLockPreference.removeLocked(context, appModel.packageName)
            }
        }

    }

    private fun checkAppLocked(packageName: String): Boolean {
        val lockedAppList = appLockPreference.getLockedAppList(context)
        if (lockedAppList != null) {
            for(app in lockedAppList){
                if(app == packageName) return true;
            }
        }
        return false
    }

    public fun filterList(filteredList: ArrayList<AppModel>){
        appList = filteredList
        notifyDataSetChanged()
    }

    class ViewHolder(ItemView: View): RecyclerView.ViewHolder(ItemView) {
        val logo: ImageView = itemView.findViewById(R.id.ivLogo)
        val name: TextView = itemView.findViewById(R.id.tvName)
        val switch: Switch = itemView.findViewById(R.id.switchLock)
    }
}