package com.tools.videodownloader.utils.apis

import com.google.gson.annotations.SerializedName

class FBModel {
    @SerializedName("error")
    var error: Boolean = false

    @SerializedName("data")
    var data: FBData? = FBData()

    class FBData {
        @SerializedName("data")
        var data: FBVideo? = FBVideo()

        class FBVideo {
            @SerializedName("video")
            var video: FBVideoDetails = FBVideoDetails()

            class FBVideoDetails {
                @SerializedName("playable_url")
                var playable_url: String? = ""
            }
        }
    }
}