package com.example.allviddownloader.tools.photo_filters

import android.graphics.Bitmap
import com.zomato.photofilters.imageprocessors.Filter

class ThumbnailItem(
    var image: Bitmap?,
    var filter: Filter
) {
    constructor() : this(null, Filter())
}