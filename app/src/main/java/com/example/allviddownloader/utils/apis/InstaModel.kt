package com.example.allviddownloader.utils.apis

import com.google.gson.annotations.SerializedName

class InstaModel {

    @SerializedName("media")
    var media: String = ""

    @SerializedName("Thumbnail")
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