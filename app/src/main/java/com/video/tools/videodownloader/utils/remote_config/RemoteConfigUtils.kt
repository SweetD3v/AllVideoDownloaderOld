package com.video.tools.videodownloader.utils.remote_config

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.models.FireAdModel

class RemoteConfigUtils {
    companion object {
        private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
        private val ADMOB_DATA = "ad_manage"
        private var admobData: FireAdModel? = null

        public fun fetchDataFromRemoteConfig(context: Context) {
            FirebaseApp.initializeApp(context)
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()

            mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
            mFirebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
            mFirebaseRemoteConfig!!.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var admob_data: String? = null


                        if (mFirebaseRemoteConfig!!.getString(ADMOB_DATA) != "-1") {
                            admob_data = mFirebaseRemoteConfig!!.getString(ADMOB_DATA)
                            Log.e("FRC", "onFirebaseComplete: ${admob_data}")
                        }

                        val gsonMoreApps = Gson()
                        val typeMoreApps = object : TypeToken<FireAdModel?>() {}.type
                        admobData = gsonMoreApps.fromJson(admob_data, typeMoreApps)
                    }
                }
        }

        fun isAdmobEnabled(): Boolean {
            return admobData?.ad_type?.admob?.enabled == true
        }

        fun adIdInterstital(): String {
            return if (isAdmobEnabled())
                admobData?.ad_type?.admob?.interstitial.toString()
            else "-"
        }

        fun adIdBanner(): String {
            return if (isAdmobEnabled())
                admobData?.ad_type?.admob?.banner.toString()
            else "-"
        }

        fun adIdNative(): String {
            return if (isAdmobEnabled())
                admobData?.ad_type?.admob?.native.toString()
            else "-"
        }

        fun adIdAppOpen(): String {
            return if (isAdmobEnabled())
                admobData?.ad_type?.admob?.app_open.toString()
            else "-"
        }
    }
}