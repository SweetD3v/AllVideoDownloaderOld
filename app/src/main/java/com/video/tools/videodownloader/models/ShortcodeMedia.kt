package com.video.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ShortcodeMedia : Serializable {
    @SerializedName("accessibility_caption")
    public val accessibility_caption: String? = null

    @SerializedName("display_resources")
    public val display_resources: List<DisplayResource>? = null

    @SerializedName("display_url")
    public val display_url: String? = null

    @SerializedName("edge_sidecar_to_children")
    public val edge_sidecar_to_children: EdgeSidecarToChildren? = null

    @SerializedName("is_video")
    public val is_video = false

    @SerializedName("video_url")
    public val video_url: String? = null
}