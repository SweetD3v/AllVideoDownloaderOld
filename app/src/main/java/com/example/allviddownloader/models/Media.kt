package com.example.allviddownloader.models

import android.net.Uri

data class Media(
    val uri: Uri,
    val path: String,
    val isVideo: Boolean,
    val date: Long,
) {
    override fun toString(): String {
        return uri.toString()
    }
}
