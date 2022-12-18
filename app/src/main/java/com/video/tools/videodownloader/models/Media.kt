package com.video.tools.videodownloader.models

import android.content.Context
import android.net.Uri

data class Media(
    val uri: Uri,
    val path: String,
    var isVideo: Boolean,
    val date: Long,
) {
    var selected: Boolean = false

    constructor(
        uri: Uri,
        path: String,
        isVideo: Boolean,
        date: Long,
        selected: Boolean
    ) : this(uri, path, isVideo, date) {
        this.selected = selected
    }

    override fun toString(): String {
        return path
    }

    fun isVideoFile(ctx: Context): Boolean {
        isVideo = ctx.contentResolver.getType(uri).toString().contains("video")
                || path.endsWith(".mp4")
        return isVideo
    }
}
