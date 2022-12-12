package com.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TwitterResponseModel : Serializable {
    @SerializedName("bitrate")
    public val bitrate = 0

    @SerializedName("duration")
    public val duration = 0

    @SerializedName("size")
    public val size = 0

    @SerializedName("source")
    public val source: String? = null

    @SerializedName("text")
    public val text: String? = null

    @SerializedName("thumb")
    public val thumb: String? = null

    @SerializedName("type")
    public val type: String? = null

    @SerializedName("url")
    public val url: String? = null
}
