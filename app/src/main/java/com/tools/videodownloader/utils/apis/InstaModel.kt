package com.tools.videodownloader.utils.apis

import com.google.gson.annotations.SerializedName

class InstaModel {

    @SerializedName("media")
    var media: String = ""

    @SerializedName("thumbnail")
    var thumbnail: String = ""

    @SerializedName("title")
    var title: String = ""

    @SerializedName("Type")
    var type: String = ""

    fun getPostType(): POST_TYPE {
        if (type.contains("video", true))
            return POST_TYPE.VIDEO
        else return POST_TYPE.PHOTO
    }
}