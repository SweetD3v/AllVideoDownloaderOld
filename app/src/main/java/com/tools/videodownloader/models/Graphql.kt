package com.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Graphql : Serializable {
    @SerializedName("shortcode_media")
    public val shortcode_media: ShortcodeMedia? = null
}