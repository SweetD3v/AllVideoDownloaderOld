package com.video.tools.videodownloader.models

import com.google.gson.annotations.SerializedName

class FireAdModel {
    @SerializedName("ad_type")
    var ad_type: AdmobClass = AdmobClass()

    inner class AdmobClass {
        @SerializedName("admob")
        var admob: AdmobClassDetails = AdmobClassDetails()

        inner class AdmobClassDetails {
            @SerializedName("enabled")
            var enabled = true

            @SerializedName("interstitial")
            var interstitial = ""

            @SerializedName("native")
            var native = ""

            @SerializedName("banner")
            var banner = ""

            @SerializedName("app_open")
            var app_open = ""
        }
    }
}