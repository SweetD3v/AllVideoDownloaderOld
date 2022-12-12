package com.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ReelFeedModel : Serializable {
    @SerializedName("expiring_atexpiring_at")
    public val expiring_at: Long = 0

    @SerializedName("id")
    public val id: Long = 0

    @SerializedName("items")
    public val items: ArrayList<ItemModel>? = null

    @SerializedName("latest_reel_media")
    public val latest_reel_media: Long = 0

    @SerializedName("media_count")
    public val media_count = 0

    @SerializedName("reel_type")
    public val reel_type: String? = null

    @SerializedName("seen")
    public val seen: Long = 0
}
