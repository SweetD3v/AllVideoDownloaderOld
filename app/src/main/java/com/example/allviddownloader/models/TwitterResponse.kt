package com.example.allviddownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class TwitterResponse : Serializable {
    @SerializedName("videos")
    public val videos: ArrayList<TwitterResponseModel>? = null
}