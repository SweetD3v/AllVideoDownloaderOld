package com.example.allviddownloader.tools.cartoonify

//import com.demo.bitmapops.JniBitmapHolder
//import org.opencv.android.OpenCVLoader
//import org.opencv.android.Utils
//import org.opencv.core.Core
//import org.opencv.core.CvType
//import org.opencv.core.Mat
//import org.opencv.imgproc.Imgproc
import android.graphics.*
import android.os.Bundle
import android.util.Base64
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityCartoonBinding
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import java.io.ByteArrayOutputStream
import java.nio.IntBuffer

class CartoonActivity : BaseActivity() {

    companion object {
        const val SELECTED_PHOTO = "SELECTED_PHOTO"

//        init {
//            if (OpenCVLoader.initDebug())
//                Log.e("TAG", ": OpenCV460 Loaded")
//            else Log.e("TAG", ": OpenCV460 Loading Error")
//        }
    }

//    var imgMat: Mat? = null
//    var tempMat1: Mat? = null
//    var tempMat2: Mat? = null
//    var finalBitmapImage: Bitmap? = null
//    var orgBitmap: Bitmap? = null

    val binding by lazy { ActivityCartoonBinding.inflate(layoutInflater) }
//    var instance: JniBitmapHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@CartoonActivity, getString(R.string.banner_id_details),
                    bannerContainer
                )

            imgBack.setOnClickListener {
                onBackPressed()
            }
        }

//        orgBitmap = getBitmapFromUri(
//            this@CartoonActivity,
//            intent.getStringExtra(SELECTED_PHOTO)?.toUri()
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

        binding.btnCartoonifyImage.setOnClickListener {
//            if (orgBitmap != null) {
//                if (!NetworkState.isOnline()) {
//                    toastShort(
//                        this@CartoonActivity,
//                        "Please connect to the internet to cartoonify an image."
//                    )
//                } else {
//                    orgBitmap?.let { bmp ->
//                        object : AsyncTaskRunner<Bitmap, String?>(this@CartoonActivity) {
//                            override fun doInBackground(params: Bitmap?): String? {
//                                return getBase64(bmp)
//                            }
//
//                            override fun onPostExecute(str: String?) {
//                                super.onPostExecute(str)
//                                str?.let {
//                                    Log.e("TAG", "base64Str: $str")
//                                    if (NetworkState.isOnline()) {
//                                        toonifyImage(str)
//                                    } else {
//                                        Toast.makeText(
//                                            this@CartoonActivity,
//                                            "Connect to the internet",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//                            }
//                        }.execute(bmp, true)
//                    }
//                }
//            } else {
//                toastShort(
//                    this@CartoonActivity,
//                    "Select an image to cartoonify"
//                )
//            }
//            cartoonifyImg()
        }
    }

//    private fun setImage(bmp: Bitmap) {
//        orgBitmap = bmp
//        binding.imgDisplay.setImageBitmap(bmp)
//    }

//    private fun cartoonifyImg() {
//        try {
//            orgBitmap?.let { bmp ->
//                object : AsyncTaskRunner<Bitmap, Bitmap>(this) {
//                    override fun doInBackground(bitmap: Bitmap?): Bitmap {
//                        val bitmapFinal = cartoonifyImageNew(
//                            bitmap,
//                            12,
//                            12,
//                            10.0,
//                            10.0,
//                            7,
//                            3
//                        )
//                        if (bitmapFinal != null)
//                            return bitmapFinal
//                        else return bmp
//                    }
//
//                    override fun onPostExecute(resultBmp: Bitmap?) {
//                        super.onPostExecute(resultBmp)
//
//                        finalBitmapImage = resultBmp
//                        AdsUtils.loadInterstitialAd(this@CartoonActivity,
//                            getString(R.string.interstitial_id),
//                            object : AdsUtils.Companion.FullScreenCallback() {
//                                override fun continueExecution() {
//                                    SaveShareCartoonActivity.finalBitmapImage = finalBitmapImage
//                                    startActivity(
//                                        Intent(
//                                            this@CartoonActivity,
//                                            SaveShareCartoonActivity::class.java
//                                        ).apply {
//                                            putExtra("type", 0)
//                                        })
//                                }
//                            })
//                    }
//                }.execute(bmp, true)
//            }
//        } catch (e: Exception) {
//            Log.e("TAG", "cartoonifyExc: ${e.localizedMessage}")
//        }
//    }

//    private fun cartoonifyImageNew(
//        bitmap: Bitmap?,
//        numBilateral: Int,
//        bDiameter: Int,
//        sigmaColor: Double,
//        sigmaSpace: Double,
//        mDiameter: Int,
//        eDiameter: Int
//    ): Bitmap? {
//        if (bitmap != null) {
//            imgMat = Mat(bitmap.height, bitmap.width, CvType.CV_8UC3)
//            tempMat1 = Mat(bitmap.height, bitmap.width, CvType.CV_8UC3)
//            tempMat2 = Mat(bitmap.height, bitmap.width, CvType.CV_8UC3)
//        }
//
//        Utils.bitmapToMat(bitmap, imgMat)
//
//        imgMat!!.copyTo(tempMat1)
//        imgMat!!.copyTo(tempMat2)
//
//        Imgproc.cvtColor(tempMat1, tempMat1, Imgproc.COLOR_BGRA2RGB)
//        Imgproc.cvtColor(tempMat2, tempMat2, Imgproc.COLOR_BGRA2RGB)
//
//        for (i in 0..1) {
//            Imgproc.pyrDown(tempMat1, tempMat1)
//        }
//        for (i in 0 until numBilateral) {
//            Imgproc.bilateralFilter(tempMat1, tempMat2, bDiameter, sigmaColor, sigmaSpace)
//            System.gc()
//            tempMat2!!.copyTo(tempMat1)
//        }
//        for (i in 0..1) {
//            Imgproc.pyrUp(tempMat1, tempMat1)
//        }
//        Imgproc.resize(tempMat1, tempMat1, imgMat!!.size())
//        Imgproc.cvtColor(tempMat2, tempMat2, Imgproc.COLOR_RGB2GRAY)
//        Imgproc.cvtColor(imgMat, tempMat2, Imgproc.COLOR_RGB2GRAY)
//
//        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGB2GRAY)
//        Imgproc.medianBlur(tempMat2, imgMat, mDiameter)
//
//        Imgproc.adaptiveThreshold(
//            imgMat,
//            tempMat2,
//            255.0,
//            Imgproc.ADAPTIVE_THRESH_MEAN_C,
//            Imgproc.THRESH_BINARY,
//            eDiameter,
//            2.0
//        )
//        Imgproc.cvtColor(tempMat2, tempMat2, Imgproc.COLOR_GRAY2RGB)
//        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_GRAY2RGB)
//        Core.bitwise_and(tempMat1, tempMat2, imgMat)
//        Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_RGB2BGRA)
//        Utils.matToBitmap(imgMat, bitmap)
//        if (bitmap != null) {
//            finalBitmapImage = bitmap.copy(bitmap.config, true)
//        }
//        imgMat!!.release()
//        tempMat1!!.release()
//        tempMat2!!.release()
//
//        return finalBitmapImage
//    }

//    private fun sketchifyImage() {
//        finalBitmapImage = Changetosketch(orgBitmap)
//        binding.imgDisplay.setImageBitmap(finalBitmapImage)
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