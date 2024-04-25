package com.edward.productiveday.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.edward.productiveday.models.AppModel
import com.edward.productiveday.utils.BitmapBase64
import kotlin.io.encoding.Base64

class DatabaseHelper(context: Context): SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object{
        private val DATABASE_NAME = "Producktive"
        private val DATABASE_VERSION = 1

        val TABLE_NAME = "InstalledApps"
        val KEY_ID = "id"
        val KEY_APP_NAME = "appName"
        val KEY_PACKAGE_NAME = "packageName"
        val KEY_ICON = "icon"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_ICON + " TEXT,"
                + KEY_APP_NAME + " TEXT,"
                + KEY_PACKAGE_NAME + " TEXT"
                + ")")

        db?.execSQL(query)
    }

    fun getAll(): List<AppModel>{
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $KEY_APP_NAME"
        val appList: ArrayList<AppModel> = ArrayList()
        var cursor = db.rawQuery(query, null)

        var appName: String
        var packageName: String
        var iconBase64: String
        var icon: Drawable
        if(cursor.moveToFirst()){
            do{
                appName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_APP_NAME))
                packageName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PACKAGE_NAME))
                iconBase64 = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ICON))
                icon = BitmapDrawable(BitmapBase64.convert(iconBase64))
                val appModel = AppModel(icon, appName, packageName)
                appList.add(appModel)
            } while(cursor.moveToNext())
        }
        db.close()
        cursor.close()

        return appList
    }

    fun addData(appName: String, packageName: String, icon: Drawable){
        val db = this.writableDatabase
        val values = ContentValues()

        val bitmapIcon: Bitmap = icon.toBitmap()
        values.put(KEY_ICON, BitmapBase64.convert(bitmapIcon))
        values.put(KEY_APP_NAME, appName)
        values.put(KEY_PACKAGE_NAME, packageName)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun deleteAll(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

}