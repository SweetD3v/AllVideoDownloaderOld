package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class User : Serializable {
    @SerializedName("biography")
    public val biography: String? = null

    @SerializedName("follower_count")
    public val follower_count = 0

    @SerializedName("following_count")
    public val following_count = 0

    @SerializedName("full_name")
    public val full_name: String? = null

    @SerializedName("hd_profile_pic_url_info")
    public val hdProfileModel: HDProfileModel? = null

    @SerializedName("is_public")
    public val is_public = false

    @SerializedName("is_verified")
    public val is_verified = false

    @SerializedName("media_count")
    public val media_count = 0

    @SerializedName("mutual_followers_count")
    public val mutual_followers_count = 0

    @SerializedName("pk")
    public val pk: Long = 0

    @SerializedName("profile_context")
    public val profile_context: String? = null

    @SerializedName("profile_pic_id")
    public val profile_pic_id: String? = null

    @SerializedName("profile_pic_url")
    public val profile_pic_url: String? = null

    @SerializedName("total_igtv_videos")
    public val total_igtv_videos: String? = null

    @SerializedName("username")
    public val username: String? = null
}
