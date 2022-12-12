package com.tools.videodownloader.models

import com.google.gson.annotations.SerializedName

class WallModelPixabay {
    @SerializedName("hits")
    public var hits: MutableList<PhotoDetails>? = null

    class PhotoDetails {
        @SerializedName("id")
        public var id: Int = 0

        @SerializedName("pageURL")
        public var pageURL: String = ""

        @SerializedName("tags")
        public var tags: String = ""

        @SerializedName("previewURL")
        public var previewURL: String = ""

        @SerializedName("previewWidth")
        public var previewWidth: Int = 0

        @SerializedName("previewHeight")
        public var previewHeight: Int = 0

        @SerializedName("largeImageURL")
        public var largeImageURL: String = ""

        @SerializedName("imageWidth")
        public var imageWidth: Int = 0

        @SerializedName("imageHeight")
        public var imageHeight: Int = 0

        @SerializedName("imageSize")
        public var imageSize: Long = 0

        @SerializedName("downloads")
        public var downloads: Long = 0

        @SerializedName("user_id")
        public var user_id: Long = 0

        @SerializedName("user")
        public var user: String = ""

        @SerializedName("userImageURL")
        public var userImageURL: String = ""
    }
}