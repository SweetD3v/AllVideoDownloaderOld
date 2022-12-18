package com.video.tools.videodownloader.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import java.io.File

class SingleMediaScanner(
    var context: Context?, var mFile: File
) : MediaScannerConnection.MediaScannerConnectionClient {
    var mMs = MediaScannerConnection(context, this)

    init {
        mMs.connect()
    }

    override fun onMediaScannerConnected() {
        mMs.scanFile(mFile.absolutePath, null)
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        mMs.disconnect();
    }
}