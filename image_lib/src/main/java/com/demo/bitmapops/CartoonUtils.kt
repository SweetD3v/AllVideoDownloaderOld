package com.demo.bitmapops

import android.content.Context
import android.graphics.Bitmap
import java.nio.ByteBuffer

class CartoonUtils {
    companion object {
        init {
            System.loadLibrary("Native")
//            System.loadLibrary("native-lib-cartoon")
        }

        fun getInstance(): CartoonUtils {
            return CartoonUtils()
        }
    }

    external fun getSketchBitmap(width: Int, height: Int, orgBitmap: Bitmap): Bitmap
    external fun stringFromJNI(): String

    external fun getBitmap(
        context: Context,
        orgBitmap: Bitmap,
        byteBuffer: ByteBuffer,
        width: Int,
        height: Int
    ): Bitmap
}