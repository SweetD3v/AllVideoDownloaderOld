package com.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VideoVersionModel : Serializable {
    @SerializedName("height")
    public var height = 0

    @SerializedName("id")
    public var id: String? = null

    @SerializedName("type")
    public var type = 0

    @SerializedName("url")
    public var url: String? = null

    @SerializedName("width")
    public var width = 0
}