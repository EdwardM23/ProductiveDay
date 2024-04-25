package com.edward.productiveday

import android.content.Context
import android.content.SharedPreferences
import com.edward.productiveday.utils.Constants
import com.google.gson.Gson


class AppLockPreference {
    private val PREF_LOCKED_APP_LIST = "locked_app_list"
    private val PREF_SERVICE_ENABLED = "service_enabled"

    fun isServiceEnabled(context: Context): Boolean {
        val settings = context.getSharedPreferences(
            Constants.MY_PREFERENCES,
            Context.MODE_PRIVATE
        )

        if (settings.contains(PREF_SERVICE_ENABLED)) {
            val jsonLocked = settings.getString(PREF_SERVICE_ENABLED, null)
            val gson = Gson()
            return gson.fromJson(jsonLocked, Boolean::class.java)
        } else{
            return false;
        }
    }

    fun saveServiceEnabled(context: Context, serviceEnabled: Boolean) {
        val settings: SharedPreferences =
            context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE)
        val editor = settings.edit()

        val gson = Gson()
        val jsonLockedApp = gson.toJson(serviceEnabled)
        editor.putString(PREF_SERVICE_ENABLED, jsonLockedApp)
        editor.apply()
    }

    fun saveLockedAppList(context: Context, lockedApp: List<String>) {
        val settings: SharedPreferences =
            context.getSharedPreferences(Constants.MY_PREFERENCES, Context.MODE_PRIVATE)
        val editor = settings.edit()

        val gson = Gson()
        val jsonLockedApp = gson.toJson(lockedApp)
        editor.putString(PREF_LOCKED_APP_LIST, jsonLockedApp)
        editor.apply()
    }

    fun addLockedApp(context: Context, app: String) {
        var lockedApp: ArrayList<String>? = getLockedAppList(context)
        if (lockedApp == null) lockedApp = ArrayList()

        lockedApp.add(app)
        saveLockedAppList(context, lockedApp)
    }

    fun removeLocked(context: Context, app: String) {
        val locked: ArrayList<String>? = getLockedAppList(context)
        if (locked != null) {
            locked.remove(app)
            saveLockedAppList(context, locked)
        }
    }

    fun getLockedAppList(context: Context): ArrayList<String>? {
        var locked: List<String>?
        val settings = context.getSharedPreferences(
            Constants.MY_PREFERENCES,
            Context.MODE_PRIVATE
        )

        if (settings.contains(PREF_LOCKED_APP_LIST)) {
            val jsonLocked = settings.getString(PREF_LOCKED_APP_LIST, null)
            val gson = Gson()
            val lockedItems = gson.fromJson(
                jsonLocked,
                Array<String>::class.java
            )
            locked = mutableListOf(*lockedItems)
            locked = ArrayList(locked)
        } else
            return null
        return locked
    }
}