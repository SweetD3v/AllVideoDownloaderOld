package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Node : Serializable {
    @SerializedName("display_resources")
    public val display_resources: List<DisplayResource>? = null

    @SerializedName("display_url")
    public val display_url: String? = null

    @SerializedName("is_video")
    public val is_video = false

    @SerializedName("video_url")
    public val video_url: String? = null
}