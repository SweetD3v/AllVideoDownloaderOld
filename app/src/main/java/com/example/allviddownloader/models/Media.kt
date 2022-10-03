package com.example.allviddownloader.models

import android.net.Uri

data class Media(
    val uri: Uri,
    val path: String,
    val isVideo: Boolean,
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

    fun isVideoFile(): Boolean {
        return path.endsWith(".mp4")
    }
}
