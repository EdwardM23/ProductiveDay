package com.edward.productiveday.views

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.view.updateLayoutParams
import com.edward.productiveday.R
import com.edward.productiveday.RotatePhoneActivity
import com.edward.productiveday.databinding.ActivityRotatePhoneBinding
import com.edward.productiveday.services.AppCheckService
import com.edward.productiveday.utils.DisplayToast

class RotatePhoneView(context: Context): View(context), SensorEventListener{
    private val TAG = "RotatePhoneView"
    private lateinit var unlockScreenView: View
    private lateinit var wm: WindowManager
    private lateinit var btnClose: Button
    private lateinit var tvCounter: TextView
    private var isViewShown = false

    private var sensorManager: SensorManager
    private var gyroscope: Sensor? = null
    private var totalRotation: Float = 0.0f
    private var mediaPlayer: MediaPlayer

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mediaPlayer = MediaPlayer.create(context, R.raw.success)

        this.isFocusable = true
        this.isFocusableInTouchMode = true
    }

    fun displayOverlay(){
        val overlayIntent = Intent(context, RotatePhoneActivity::class.java)
        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val inflater = LayoutInflater.from(context)
        unlockScreenView = inflater.inflate(R.layout.activity_rotate_phone, null)

        wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            0,
            PixelFormat.TRANSPARENT
        )
        unlockScreenView.isFocusable = true
        unlockScreenView.isFocusableInTouchMode = true

        wm.addView(unlockScreenView, params)
        unlockScreenView.requestFocus()
        Log.d(TAG, "View focused: "+ unlockScreenView.hasFocus().toString())

        btnClose = unlockScreenView.findViewById(R.id.btnClose)
        btnClose.setOnClickListener {
            incrementCounter()
        }

        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun closeOverlay(){
        if(unlockScreenView.windowToken != null){
            wm.removeView(unlockScreenView)
        }
        sensorManager.unregisterListener(this)
    }

    private fun incrementCounter(){
        mediaPlayer.start()
        tvCounter = unlockScreenView.findViewById(R.id.tvCounter)
        val counter: Int = tvCounter.text.toString().toInt()

        if(counter == 2){
            closeOverlay()
        } else{
            tvCounter.text = (counter + 1).toString()
        }
    }

    private fun setParams(params: WindowManager.LayoutParams, focusable: Boolean): WindowManager.LayoutParams{
        if(focusable){
            params.flags -= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        } else{
            params.flags += WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }

        return params
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        event?.let { keyEvent ->
            Log.d("CustomView", "Key Event: Action=${keyEvent.action}, KeyCode=${keyEvent.keyCode}")
        }
        return true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isViewShown = true
    }

    override fun isFocused(): Boolean {
        return true
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isViewShown = false
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_GYROSCOPE){
            val rotation = event.values[2]
            totalRotation += rotation

            if(totalRotation >= 25.0f){
                totalRotation = 0.0f
                incrementCounter()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}