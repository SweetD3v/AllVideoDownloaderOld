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

    @SerializedName("previews")
    var previews: Previews? = Previews()

    class Previews {
        @SerializedName("preview-lq-ogg")
        var previewLQOgg: String? = ""

        @SerializedName("preview-lq-mp3")
        var previewLQMp3: String? = ""

        @SerializedName("preview-hq-ogg")
        var previewHQOgg: String? = ""

        @SerializedName("preview-hq-mp3")
        var previewHQMp3: String? = ""
    }
}