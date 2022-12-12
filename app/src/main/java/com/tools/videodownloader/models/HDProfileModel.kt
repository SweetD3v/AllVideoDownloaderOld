package com.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class HDProfileModel : Serializable {
    @SerializedName("height")
    public val height = 0

    @SerializedName("url")
    public val url: String? = null

    @SerializedName("width")
    public val width = 0
}
