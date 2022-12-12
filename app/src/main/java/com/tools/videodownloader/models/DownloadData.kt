package com.tools.videodownloader.models

class DownloadData(
    var url: String,
    var name: String,
    var path: String
) {
    constructor() : this("", "", "")
}