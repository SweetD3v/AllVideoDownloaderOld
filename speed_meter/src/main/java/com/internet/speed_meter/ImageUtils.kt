package com.internet.speed_meter

import android.content.Context
import android.graphics.*

class ImageUtils {
    companion object {

        fun createBitmapFromString(ctx: Context, speed: String, units: String): Bitmap {
            var unit = "$units/s"

            var paint = Paint()
            paint.isAntiAlias = true
            paint.textSize = 96f
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.createFromAsset(ctx.assets, "fonts/roboto_bold.ttf")

            var unitsPaint = Paint()
            unitsPaint.isAntiAlias = true
            unitsPaint.textSize = 60f
            unitsPaint.textAlign = Paint.Align.CENTER
            unitsPaint.typeface = Typeface.createFromAsset(ctx.assets, "fonts/roboto_bold.ttf")

            var speedBounds = Rect()
            paint.getTextBounds(speed, 0, speed.length, speedBounds)

            var unitsBounds = Rect()
            unitsPaint.getTextBounds(unit, 0, unit.length, unitsBounds)

            var width = if (speedBounds.width() > unitsBounds.width()) {
                speedBounds.width()
            } else {
                unitsBounds.width()
            }

            var bitmap = Bitmap.createBitmap(
                width + 10, 150,
                Bitmap.Config.ARGB_8888
            )

            var canvas = Canvas(bitmap)
            canvas.drawText(speed, (width / 2F + 5), 72f, paint)
            canvas.drawText(unit, width / 2F, 136f, unitsPaint)

            return bitmap
        }
    }
}