package com.video.tools.videodownloader.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ItemModel : Serializable {
    @SerializedName("can_reply")
    public var can_reply = false

    @SerializedName("can_reshare")
    public var can_reshare = false

    @SerializedName("caption_is_edited")
    public var caption_is_edited = false

    @SerializedName("caption_position")
    public var caption_position = 0

    @SerializedName("client_cache_key")
    public var client_cache_key: String? = null

    @SerializedName("code")
    public var code: String? = null

    @SerializedName("device_timestamp")
    public var device_timestamp: Long = 0

    @SerializedName("expiring_at")
    public var expiring_at: Long = 0

    @SerializedName("filter_type")
    public var filter_type = 0

    @SerializedName("has_audio")
    public var has_audio = false

    @SerializedName("id")
    public var id: String? = null

    @SerializedName("image_versions2")
    public var image_versions2: ImageVersionModel? = null

    @SerializedName("is_reel_media")
    public var is_reel_media = false

    @SerializedName("media_type")
    public var media_type = 0

    @SerializedName("organic_tracking_token")
    public var organic_tracking_token: String? = null

    @SerializedName("original_height")
    public var original_height = 0

    @SerializedName("original_width")
    public var original_width = 0

    @SerializedName("photo_of_you")
    public var photo_of_you = false

    @SerializedName("pk")
    public var pk: Long = 0

    @SerializedName("taken_at")
    public var taken_at: Long = 0

    @SerializedName("video_duration")
    public var video_duration = 0.0

    @SerializedName("video_versions")
    public var video_versions: ArrayList<VideoVersionModel?>? = null
}