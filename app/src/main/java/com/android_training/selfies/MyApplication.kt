package com.android_training.selfies

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MyApplication.appContext = applicationContext

    }
    companion object{
        lateinit var appContext: Context

        fun toast(message: String){
            Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
        }

        fun log(message: String){
            Log.d("myApp", message)
        }

    }
}