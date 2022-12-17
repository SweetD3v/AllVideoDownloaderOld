package com.tools.videodownloader.tools.cartoonify

//import android.content.Intent
//import android.util.Log
//import androidx.core.net.toUri
//import com.demo.bitmapops.JniBitmapHolder
//import org.opencv.android.OpenCVLoader
//import org.opencv.core.Mat
import android.graphics.*
import android.os.Bundle
import android.util.Base64
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivitySketchifyBinding
import com.tools.videodownloader.ui.activities.BaseActivity
import com.tools.videodownloader.utils.AdsUtils
import com.tools.videodownloader.utils.NetworkState
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import java.io.ByteArrayOutputStream
import java.nio.IntBuffer

class SketchifyActivity : BaseActivity() {
    companion object {
//        init {
//            if (OpenCVLoader.initDebug())
//                Log.e("TAG", ": OpenCV460 Loaded")
//            else Log.e("TAG", ": OpenCV460 Loading Error")
//        }
    }

    val binding by lazy { ActivitySketchifyBinding.inflate(layoutInflater) }

    //    var imgMat: Mat? = null
//    var tempMat1: Mat? = null
//    var tempMat2: Mat? = null
    var finalBitmapImage: Bitmap? = null
//    var orgBitmap: Bitmap? = null

//    var instance: JniBitmapHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@SketchifyActivity, RemoteConfigUtils.adIdBanner(),
                    bannerContainer
                )

            imgBack.setOnClickListener {
                onBackPressed()
            }
        }

//        orgBitmap = getBitmapFromUri(
//            this@SketchifyActivity,
//            intent.getStringExtra(CartoonActivity.SELECTED_PHOTO)?.toUri()
//        )
//        orgBitmap?.let {
//            instance = JniBitmapHolder.getInstance(it)
//            object : AsyncTaskRunner<Bitmap, Bitmap?>(this) {
//                override fun doInBackground(params: Bitmap?): Bitmap? {
//                    instance?.storeBitmap()
////                                instance?.rotateBitmapCw90()
//                    instance?.getBitmap()?.let { bmp ->
//                        return bmp
//                    }
//                    return null
//                }
//
//                override fun onPostExecute(result: Bitmap?) {
//                    result?.let { it1 ->
//                        val canvas = Canvas(result)
//                        instance?.drawBitmapOnCanvas(canvas)
//                        setImage(it1)
//                    }
//                    super.onPostExecute(result)
//                }
//            }.execute(it, true)
//        }
//
//        binding.btnSketchifyImage.setOnClickListener {
//            if (orgBitmap != null) {
//                if (!NetworkState.isOnline()) {
//                    toastShort(
//                        this@SketchifyActivity,
//                        "Please connect to the internet to sketchify an image."
//                    )
//                } else {
//                    orgBitmap?.let { bmp ->
//                        object : AsyncTaskRunner<Bitmap?, Void?>(this@SketchifyActivity) {
//                            override fun doInBackground(params: Bitmap?): Void? {
//                                sketchifyImage()
//                                return null
//                            }
//
//                            override fun onPostExecute(result: Void?) {
//                                super.onPostExecute(result)
//                                AdsUtils.loadInterstitialAd(this@SketchifyActivity,
//                                    getString(R.string.interstitial_id),
//                                    object : AdsUtils.Companion.FullScreenCallback() {
//                                        override fun continueExecution() {
//                                            SaveShareCartoonActivity.finalBitmapImage =
//                                                finalBitmapImage
//                                            startActivity(
//                                                Intent(
//                                                    this@SketchifyActivity,
//                                                    SaveShareCartoonActivity::class.java
//                                                ).apply {
//                                                    putExtra("type", 1)
//                                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                                })
//                                        }
//                                    })
//                            }
//                        }.execute(bmp, true)
//                    }
//                }
//            } else {
//                toastShort(
//                    this@SketchifyActivity,
//                    "Select an image to sketchify"
//                )
//            }
//        }
    }

//    private fun setImage(bmp: Bitmap) {
//        orgBitmap = bmp
//        binding.imgDisplay.setImageBitmap(bmp)
//    }
//
//    private fun sketchifyImage() {
//        finalBitmapImage = Changetosketch(orgBitmap)
//    }

    fun Changetosketch(bmp: Bitmap?): Bitmap? {
        var Copy: Bitmap?
        var Invert: Bitmap?
        val Result: Bitmap?
        Copy = bmp
        Copy = toGrayscale(Copy!!)
        Invert = createInvertedBitmap(Copy!!)
        Invert = Blur.blur(this, Invert)
        Result = ColorDodgeBlend(Invert, Copy)
        return Result
    }

    private fun colordodge(in1: Int, in2: Int): Int {
        val image = in2.toFloat()
        val mask = in1.toFloat()
        return (if (image == 255f) image else Math.min(
            255f,
            (mask.toLong() shl 8) / (255 - image)
        )).toInt()
    }

    fun toGrayscale(bmpOriginal: Bitmap): Bitmap? {
        val width: Int
        val height: Int
        height = bmpOriginal.height
        width = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return bmpGrayscale
    }

    fun createInvertedBitmap(src: Bitmap): Bitmap? {
        val colorMatrix_Inverted = ColorMatrix(
            floatArrayOf(
                -1f,
                0f,
                0f,
                0f,
                255f,
                0f,
                -1f,
                0f,
                0f,
                255f,
                0f,
                0f,
                -1f,
                0f,
                255f,
                0f,
                0f,
                0f,
                1f,
                0f
            )
        )
        val ColorFilter_Sepia: ColorFilter = ColorMatrixColorFilter(
            colorMatrix_Inverted
        )
        val bitmap = Bitmap.createBitmap(
            src.width, src.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.colorFilter = ColorFilter_Sepia
        canvas.drawBitmap(src, 0f, 0f, paint)
        return bitmap
    }

    /**
     * Blends 2 bitmaps to one and adds the color dodge blend mode to it.
     */
    fun ColorDodgeBlend(source: Bitmap, layer: Bitmap): Bitmap? {
        val base = source.copy(Bitmap.Config.ARGB_8888, true)
        val blend = layer.copy(Bitmap.Config.ARGB_8888, false)
        val buffBase: IntBuffer = IntBuffer.allocate(base.width * base.height)
        base.copyPixelsToBuffer(buffBase)
        buffBase.rewind()
        val buffBlend: IntBuffer = IntBuffer.allocate(blend.width * blend.height)
        blend.copyPixelsToBuffer(buffBlend)
        buffBlend.rewind()
        val buffOut: IntBuffer = IntBuffer.allocate(base.width * base.height)
        buffOut.rewind()
        while (buffOut.position() < buffOut.limit()) {
            val filterInt: Int = buffBlend.get()
            val srcInt: Int = buffBase.get()
            val redValueFilter: Int = Color.red(filterInt)
            val greenValueFilter: Int = Color.green(filterInt)
            val blueValueFilter: Int = Color.blue(filterInt)
            val redValueSrc: Int = Color.red(srcInt)
            val greenValueSrc: Int = Color.green(srcInt)
            val blueValueSrc: Int = Color.blue(srcInt)
            val redValueFinal = colordodge(redValueFilter, redValueSrc)
            val greenValueFinal = colordodge(greenValueFilter, greenValueSrc)
            val blueValueFinal = colordodge(blueValueFilter, blueValueSrc)
            val pixel: Int = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal)
            buffOut.put(pixel)
        }
        buffOut.rewind()
        base.copyPixelsFromBuffer(buffOut)
        blend.recycle()
        return base
    }

    private fun getBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getImageJson(str: String): String {
        return "{\"data\": [data:image/jpeg;base64,$str]}"
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}