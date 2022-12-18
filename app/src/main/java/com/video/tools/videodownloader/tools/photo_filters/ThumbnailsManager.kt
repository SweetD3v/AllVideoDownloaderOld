package com.video.tools.videodownloader.tools.photo_filters

import android.content.Context
import android.graphics.Bitmap
import com.video.tools.videodownloader.R

class ThumbnailsManager {
    companion object {
        private var filterThumbs: MutableList<ThumbnailItem> = ArrayList(10)
        private var processedThumbs: MutableList<ThumbnailItem> = ArrayList(10)
        fun addThumb(thumbnailItem: ThumbnailItem) {
            filterThumbs.add(thumbnailItem)
        }

        fun processThumbs(context: Context): MutableList<ThumbnailItem> {
            for (thumb in filterThumbs) {
                // scaling down the image
                val size: Float = context.resources.getDimension(R.dimen.thumbnail_size)
                thumb.image = thumb.image?.let {
                    Bitmap.createScaledBitmap(it, size.toInt(), size.toInt(), false)
                }
                thumb.image = thumb.filter.processFilter(thumb.image)
                //cropping circle
//                thumb.image = GeneralUtils.generateCircularBitmap(thumb.image)
                processedThumbs.add(thumb)
            }
            return processedThumbs
        }

        fun clearThumbs() {
            filterThumbs = ArrayList()
            processedThumbs = ArrayList()
        }
    }
}