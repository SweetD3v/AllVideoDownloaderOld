package com.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FullDetailModel : Serializable {
    @SerializedName("reel_feed")
    public val reel_feed: ReelFeedModel? = null

    @SerializedName("user_detail")
    public val user_detail: UserDetailModel? = null
}