package com.video.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DisplayResource : Serializable {
    @SerializedName("config_height")
    public val config_height = 0

    @SerializedName("config_width")
    public val config_width = 0

    @SerializedName("src")
    public val src: String? = null
}