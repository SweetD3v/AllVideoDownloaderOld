package com.video.tools.videodownloader.models.ringtone

import com.google.gson.annotations.SerializedName

class RingtoneModel {

    @SerializedName("next")
    public var nextPageUrl: String? = ""

    @SerializedName("results")
    public var ringtonesListList: MutableList<RingToneDetails>? = mutableListOf()

    class RingToneDetails {
        @SerializedName("id")
        var id: Long? = 0

        @SerializedName("name")
        var name: String? = ""

        @SerializedName("isPlaying")
        var isPlaying: Boolean? = false

        @SerializedName("tags")
        var tags: MutableList<String>? = mutableListOf()

        constructor(id: Long?, name: String?, tags: MutableList<String>?) {
            this.id = id
            this.name = name
            this.tags = tags
        }

        constructor()
    }
}