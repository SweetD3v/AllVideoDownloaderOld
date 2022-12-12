package com.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class WallpaperModel : Serializable {

    @SerializedName("page")
    public var page: Int = 0

    @SerializedName("per_page")
    public var per_page: Int = 0

    @SerializedName("photos")
    public var photosDetails: MutableList<PhotosDetails>? = mutableListOf()

    class PhotosDetails {
        @SerializedName("id")
        public var id: Int = 0

        @SerializedName("width")
        public var width: Int = 0

        @SerializedName("height")
        public var height: Int = 0

        @SerializedName("url")
        public var url: String = ""

        @SerializedName("avg_color")
        public var avg_color: String = ""

        @SerializedName("src")
        public var src: ImageDetails? = ImageDetails()

        class ImageDetails {
            @SerializedName("original")
            public var original: String = ""

            @SerializedName("large2x")
            public var large2x: String = ""

            @SerializedName("large")
            public var large: String = ""

            @SerializedName("medium")
            public var medium: String = ""

            @SerializedName("small")
            public var small: String = ""

            @SerializedName("portrait")
            public var portrait: String = ""

            @SerializedName("landscape")
            public var landscape: String = ""

            @SerializedName("tiny")
            public var tiny: String = ""
        }
    }
}