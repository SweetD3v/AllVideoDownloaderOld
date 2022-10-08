package com.example.allviddownloader

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.Util

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

    fun buildHttpDataSourceFactory(bandwidthMeter: DefaultBandwidthMeter?): HttpDataSource.Factory {
        return DefaultHttpDataSourceFactory(Util.getUserAgent(this, "AllVidApp"), bandwidthMeter)
    }
}