package com.demo.bitmapops

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import java.nio.ByteBuffer

open class JniBitmapHolder private constructor() {
//    companion object : SingletonHolder<JniBitmapHolder, Context>(::JniBitmapHolder)

    companion object {
        private var context: JniBitmapHolder? = null
        private var bitmap: Bitmap? = null

        fun getInstance(bitmapOrg: Bitmap?): JniBitmapHolder? {
            if (context == null) context = JniBitmapHolder()
            bitmap = bitmapOrg
            return context
        }
    }

    var _handler: ByteBuffer? = null

    init {
        System.loadLibrary("JniBitmapOperationsLibrary")
    }

    enum class ScaleMethod {
        NearestNeighbour,
        BilinearInterpolation
    }

    external fun jniStoreBitmapData(bitmap: Bitmap): ByteBuffer?

    external fun jniGetBitmapFromStoredBitmapData(
        handler: ByteBuffer
    ): Bitmap?

    external fun jniDrawBitmapOnCanvas(handler: ByteBuffer, canvas: Canvas, paint: Paint)

    external fun jniFreeBitmapData(handler: ByteBuffer)

    external fun jniRotateBitmapCcw90(handler: ByteBuffer)

    external fun jniRotateBitmapCw90(handler: ByteBuffer)

    external fun jniRotateBitmap180(handler: ByteBuffer)

    external fun jniCropBitmap(
        handler: ByteBuffer,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    )

    external fun jniScaleNNBitmap(handler: ByteBuffer, newWidth: Int, newHeight: Int)

    external fun jniScaleBIBitmap(handler: ByteBuffer, newWidth: Int, newHeight: Int)

    external fun jniFlipBitmapHorizontal(handler: ByteBuffer)

    external fun jniFlipBitmapVertical(handler: ByteBuffer)

    fun storeBitmap() {
        if (_handler != null) freeBitmap()
        _handler = jniStoreBitmapData(bitmap!!)
    }

    fun rotateBitmapCcw90() {
        if (_handler == null) return
        jniRotateBitmapCcw90(_handler!!)
    }

    fun rotateBitmapCw90() {
        if (_handler == null) return
        jniRotateBitmapCw90(_handler!!)
    }

    fun rotateBitmap180() {
        if (_handler == null) return
        jniRotateBitmap180(_handler!!)
    }

    fun cropBitmap(left: Int, top: Int, right: Int, bottom: Int) {
        if (_handler == null) return
        jniCropBitmap(_handler!!, left, top, right, bottom)
    }

    fun getBitmap(): Bitmap? {
        return if (_handler == null) null else jniGetBitmapFromStoredBitmapData(
            _handler!!
        )
    }

    fun drawBitmapOnCanvas(canvas: Canvas) {
        val paint = Paint()
        paint.apply {
            isAntiAlias = true
        }
        jniDrawBitmapOnCanvas(_handler!!, canvas, paint)
    }

    fun getBitmapAndFree(): Bitmap? {
        val bitmap = getBitmap()
        freeBitmap()
        return bitmap
    }

    fun scaleBitmap(
        newWidth: Int,
        newHeight: Int,
        scaleMethod: ScaleMethod?
    ) {
        if (_handler == null) return
        when (scaleMethod) {
            ScaleMethod.BilinearInterpolation -> jniScaleBIBitmap(
                _handler!!,
                newWidth,
                newHeight
            )
            ScaleMethod.NearestNeighbour -> jniScaleNNBitmap(
                _handler!!,
                newWidth,
                newHeight
            )
            else -> {}
        }
    }

    /**
     * flips a bitmap horizontally, as such: <br></br>
     *
     * <pre>
     * 123    321
     * 456 => 654
     * 789    987
    </pre> *
     */
    //
    fun flipBitmapHorizontal() {
        if (_handler == null) return
        jniFlipBitmapHorizontal(_handler!!)
    }

    /**
     * Flips the bitmap on the vertically, as such:<br></br>
     *
     * <pre>
     * 123    789
     * 456 => 456
     * 789    123
    </pre> *
     */
    fun flipBitmapVertical() {
        if (_handler == null) return
        jniFlipBitmapVertical(_handler!!)
    }

    fun freeBitmap() {
        if (_handler == null) return
        jniFreeBitmapData(_handler!!)
        _handler = null
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        if (_handler == null) return
        Log.w(
            "DEBUG",
            "JNI bitmap wasn't freed nicely.please remember to free the bitmap as soon as you can"
        )
        freeBitmap()
    }
}