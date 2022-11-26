package com.example.allviddownloader.phone_booster.models

import android.graphics.drawable.Drawable

class AppModel {
    var appName = ""
    var packageName = ""
    var icon: Drawable? = null
    var versionName = ""
    var versionCode = 1
    var permissions: Array<String> = arrayOf()
    var isSensitive = false
}