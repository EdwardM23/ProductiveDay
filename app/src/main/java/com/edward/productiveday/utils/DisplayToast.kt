package com.edward.productiveday.utils

import android.content.Context
import android.widget.Toast

class DisplayToast {
    companion object{
        fun displayToast(context: Context, text: String){
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}