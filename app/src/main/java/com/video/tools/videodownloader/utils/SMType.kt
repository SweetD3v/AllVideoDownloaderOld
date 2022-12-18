package com.video.tools.videodownloader.utils

enum class SMType {


    INSTA(0),
    FACEBOOK(1),
    TWITTER(2),
    VIMEO(3),
    WHATSAPP(4);

    val type: Int

    constructor(ordinal: Int) {
        type = ordinal
    }
}