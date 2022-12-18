package com.video.tools.videodownloader.tools.photo_filters

import android.graphics.Bitmap
import com.zomato.photofilters.imageprocessors.Filter

class ThumbnailItem(
    var image: Bitmap?,
    var filter: Filter
) {
    constructor() : this(null, Filter())
}