package com.example.allviddownloader

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication

class AllVidApp : MultiDexApplication() {
    companion object {
        lateinit var mInstance: AllVidApp

        @Synchronized
        fun getInstance(): AllVidApp {
            return mInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}