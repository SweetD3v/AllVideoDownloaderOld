package com.tools.videodownloader.models

import androidx.documentfile.provider.DocumentFile
import java.io.File


class Status(public val documentFile: DocumentFile) {
    constructor() : this(DocumentFile.fromFile(File("")))

    public var file: File? = File("")
    public var str: String = ""
    public var str2: String = ""
    public val isApi30 = true
    public var isVideo = false
    public var path: String? = str2
    public var title: String? = str

    init {
        isVideo = file?.name?.endsWith(".mp4") == true
    }

    override fun toString(): String {
        return path.toString()
    }
}