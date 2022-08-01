package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserModel : Serializable {
    @SerializedName("full_name")
    public var full_name: String? = null
    var isFav = 0

    @SerializedName("is_public")
    public var is_public = false

    @SerializedName("is_verified")
    public var is_verified = false

    @SerializedName("pk")
    public var pk: Long = 0

    @SerializedName("profile_pic_id")
    public var profile_pic_id: String? = null

    @SerializedName("profile_pic_url")
    public var profile_pic_url: String? = null

    @SerializedName("username")
    public var username: String? = null
}