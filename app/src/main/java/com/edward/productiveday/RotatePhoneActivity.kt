package com.edward.productiveday

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edward.productiveday.databinding.ActivityRotatePhoneBinding
import com.edward.productiveday.services.AppCheckService

class RotatePhoneActivity : AppCompatActivity() {

    private val TAG = "RotatePhoneActivity"
    private lateinit var binding: ActivityRotatePhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityRotatePhoneBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        binding.btnClose.setOnClickListener {
//            buttonClosePressed()
//        }
    }

    private fun buttonClosePressed(){
//        Log.d(TAG, "close pressed")
//        Toast.makeText(this, "close pressed", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        removeOverlay();
    }

    private fun removeOverlay() {
//        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val overlay = AppCheckService.getOverlay()
//        if(overlay.isActivated){
//            wm.removeView(overlay)
//        }
    }


}