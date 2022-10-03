package com.example.allviddownloader.utils.apis

import com.google.gson.annotations.SerializedName

class FBModel {
    @SerializedName("hasError")
    var hasError: Boolean = false

    @SerializedName("errorMessage")
    var errorMessage: String? = ""

    @SerializedName("body")
    var fbModelDetails: FBModelDetails? = FBModelDetails()

    inner class FBModelDetails {
        @SerializedName("video")
        var video: String? = ""

        @SerializedName("videoHD")
        var videoHD: String? = ""
    }
}