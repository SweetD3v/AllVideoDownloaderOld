package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CandidatesModel : Serializable {
    @SerializedName("height")
    open val height = 0

    @SerializedName("scans_profile")
    open val scans_profile: String? = null

    @SerializedName("url")
    open val url: String? = null

    @SerializedName("width")
    open val width = 0
}