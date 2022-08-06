package com.example.allviddownloader.models.ringtone

import com.google.gson.annotations.SerializedName

class RingPreviewModel {
    @SerializedName("id")
    var id: Long? = 0

    @SerializedName("name")
    var name: String? = ""

    @SerializedName("geotag")
    var geotag: String? = ""

    @SerializedName("type")
    var type: String? = ""

    @SerializedName("filesize")
    var filesize: Long? = 0

    @SerializedName("duration")
    var duration: Float? = 0f

    @SerializedName("samplerate")
    var samplerate: Long? = 0

    @SerializedName("download")
    var download: String? = ""
}