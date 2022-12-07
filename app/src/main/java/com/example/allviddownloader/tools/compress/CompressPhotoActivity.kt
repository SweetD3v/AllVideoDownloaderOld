package com.example.allviddownloader.tools.compress

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.allviddownloader.R
import com.example.allviddownloader.collage_maker.utils.SystemUtils
import com.example.allviddownloader.databinding.ActivityCompressPhotoBinding
import com.example.allviddownloader.ui.activities.FullScreenActivity
import com.example.allviddownloader.utils.*
import id.zelory.compressor.Compressor
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.IOException

class CompressPhotoActivity : FullScreenActivity() {
    val binding by lazy { ActivityCompressPhotoBinding.inflate(layoutInflater) }

    var originalImgUri: Uri? = null
    var tempCopyFile: File? = null
    var srcFile: File? = null
    var tempDestFile: File? = null
    var destFile: File? = null

    var preset = 100
    var orgFilePath = ""
    var finalSize = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (NetworkState.isOnline()) {
//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )
            AdsUtils.loadNativeSmall(
                this@CompressPhotoActivity,
                getString(R.string.admob_native_id),
                binding.adFrame
            )
        }

        binding.run {
            toolbar.txtTitle.text = getString(R.string.photo_compress)
            toolbar.rlMain.adjustInsets(this@CompressPhotoActivity)
            toolbar.root.background = ContextCompat.getDrawable(
                this@CompressPhotoActivity,
                R.drawable.top_bar_gradient_light_blue
            )
            toolbar.imgBack.setOnClickListener { onBackPressed() }
        }

        if (intent.hasExtra(PhotoCmpHomeActivity.SELECTED_PHOTO)) {
            originalImgUri = Uri.parse(intent.getStringExtra(PhotoCmpHomeActivity.SELECTED_PHOTO))
            binding.imgCompress.setImageURI(originalImgUri)
        }
        binding.sliderCompress.value = 100f
        preset = 100
        binding.txtCompressPercent.text = "100 %"


        originalImgUri?.let { uri ->
            orgFilePath = SystemUtils.getRealPathFromUri(this, uri).toString()
            binding.txtPhotoSize.text =
                "Photo size: ${FileUtilsss.getFileLength(File(orgFilePath).length())}"
            finalSize = File(orgFilePath).length()
            binding.txtCompressPercent.text = "100 %"
        }

        binding.sliderCompress.addOnChangeListener { slider, value, fromUser ->
            val progress = value.toInt()
            preset = progress

            finalSize = ((File(orgFilePath).length() * progress) / 100.0).toLong()
            binding.txtPhotoSize.text = "Photo size: ${FileUtilsss.getFileLength(finalSize)}"
            binding.txtCompressPercent.text = "100 %"
        }

        binding.btnCompress.setOnClickListener { v ->
            AdsUtils.loadInterstitialAd(
                this,
                getString(R.string.interstitial_id),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        startCompressingPhoto()
                    }
                })
        }
    }

    private fun startCompressingPhoto() {
        object : AsyncTaskRunner<Uri?, Bitmap?>(this@CompressPhotoActivity) {
            override fun doInBackground(params: Uri?): Bitmap? {
                return compressImageNew()
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                Log.e(
                    "TAG",
                    "compressImage: " + compressedImage?.absolutePath
                )

                val uri =
                    FileProvider.getUriForFile(
                        this@CompressPhotoActivity, "$packageName.provider",
                        compressedImage!!
                    )

                startActivity(
                    Intent(
                        this@CompressPhotoActivity,
                        CompressSavePhotoActivity::class.java
                    ).putExtra(
                        CompressSavePhotoActivity.IMAGE_URI_ORG,
                        originalImgUri.toString()
                    ).putExtra(
                        CompressSavePhotoActivity.IMAGE_URI_CMP,
                        uri.toString()
                    ).putExtra(
                        CompressSavePhotoActivity.IMAGE_PATH_CMP,
                        compressedImage?.absolutePath.toString()
                    ).putExtra(
                        CompressSavePhotoActivity.IMAGE_SIZE_ORG,
                        FileUtilsss.getFileLength(File(orgFilePath).length())
                    ).putExtra(
                        CompressSavePhotoActivity.IMAGE_SIZE_CMP,
                        FileUtilsss.getFileLength(finalSize)
                    )
                )
            }
        }.execute(originalImgUri, true)
    }

    var compressedImage: File? = null
    var disposable: Disposable? = null

    private fun compressImageNew(): Bitmap? {
        tempCopyFile = File("img_temp", "TEMP_IMG_" + System.currentTimeMillis() + ".jpg")
        srcFile = File(externalCacheDir, tempCopyFile!!.absolutePath)
        if (srcFile!!.exists()) srcFile!!.delete()
        tempDestFile =
            File("img_compressed", "Compressed_photo_" + System.currentTimeMillis() + ".jpg")
        destFile = File(externalCacheDir, tempDestFile!!.absolutePath)
        if (destFile!!.exists()) destFile!!.delete()
        if (!srcFile!!.parentFile.exists()) srcFile!!.parentFile.mkdirs()
        if (!destFile!!.parentFile.exists()) destFile!!.parentFile.mkdirs()

        try {
            FileUtilsss.copyFile(this, originalImgUri, srcFile!!) { path: String? ->
                try {
                    compressedImage = Compressor(this)
                        .setQuality(preset)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(destFile!!.parentFile!!.path)
                        .compressToFile(File(path.toString()))
                    Log.e(
                        "TAG",
                        "compressImageNew: ${FileUtilsss.getFileLength(compressedImage?.length())}"
                    )
                    Log.e("TAG", "compressImageNewLength: ${compressedImage?.length()}")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("TAG", "compressImageNew: " + e.message)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val options1 = BitmapFactory.Options()
        options1.inJustDecodeBounds = true
        return BitmapFactory.decodeFile(compressedImage!!.absolutePath, options1)
    }

//    @Throws(IOException::class)
//    fun compressImage(): Bitmap? {
//        Log.e("TAG", "compressImage: $preset")
//        tempCopyFile = File("img_temp", "TEMP_IMG_" + System.currentTimeMillis() + ".jpg")
//        srcFile = File(externalCacheDir, tempCopyFile!!.absolutePath)
//        if (srcFile!!.exists()) srcFile!!.delete()
//        tempDestFile =
//            File("img_compressed", "Compressed_photo_" + System.currentTimeMillis() + ".jpg")
//        destFile = File(externalCacheDir, tempDestFile!!.absolutePath)
//        if (destFile!!.exists()) destFile!!.delete()
//        if (!srcFile!!.parentFile.exists()) srcFile!!.parentFile.mkdirs()
//        if (!destFile!!.parentFile.exists()) destFile!!.parentFile.mkdirs()
//        copyFile(this, originalImgUri, srcFile!!) { path: String? ->
//            var scaledBitmap: Bitmap? = null
//            val options =
//                BitmapFactory.Options()
//            options.inJustDecodeBounds = true
//            var bmp = BitmapFactory.decodeFile(path, options)
//            var actualHeight = options.outHeight
//            var actualWidth = options.outWidth
//            val maxHeight = 816.0f
//            val maxWidth = 612.0f
//            var imgRatio = (actualWidth / actualHeight).toFloat()
//            val maxRatio = maxWidth / maxHeight
//            if (actualHeight > maxHeight || actualWidth > maxWidth) {
//                if (imgRatio < maxRatio) {
//                    imgRatio = maxHeight / actualHeight
//                    actualWidth = (imgRatio * actualWidth).toInt()
//                    actualHeight = maxHeight.toInt()
//                } else if (imgRatio > maxRatio) {
//                    imgRatio = maxWidth / actualWidth
//                    actualHeight = (imgRatio * actualHeight).toInt()
//                    actualWidth = maxWidth.toInt()
//                } else {
//                    actualHeight = maxHeight.toInt()
//                    actualWidth = maxWidth.toInt()
//                }
//            }
//            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
//            options.inJustDecodeBounds = false
//            options.inPurgeable = true
//            options.inInputShareable = true
//            options.inTempStorage = ByteArray(16 * 1024)
//            try {
//                bmp = BitmapFactory.decodeFile(path, options)
//            } catch (exception: OutOfMemoryError) {
//                exception.printStackTrace()
//            }
//            try {
//                scaledBitmap = Bitmap.createBitmap(
//                    actualWidth,
//                    actualHeight,
//                    Bitmap.Config.ARGB_8888
//                )
//            } catch (exception: OutOfMemoryError) {
//                exception.printStackTrace()
//            }
//            val ratioX = actualWidth / options.outWidth.toFloat()
//            val ratioY = actualHeight / options.outHeight.toFloat()
//            val middleX = actualWidth / 2.0f
//            val middleY = actualHeight / 2.0f
//            val scaleMatrix = Matrix()
//            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
//            val canvas = Canvas(scaledBitmap!!)
//            canvas.setMatrix(scaleMatrix)
//            canvas.drawBitmap(
//                bmp,
//                middleX - bmp.width / 2,
//                middleY - bmp.height / 2,
//                Paint(Paint.FILTER_BITMAP_FLAG)
//            )
//            val exif: ExifInterface
//            try {
//                exif = ExifInterface(path!!)
//                val orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION, 0
//                )
//                Log.d("EXIF", "Exif: $orientation")
//                val matrix = Matrix()
//                if (orientation == 6) {
//                    matrix.postRotate(90f)
//                    Log.d("EXIF", "Exif: $orientation")
//                } else if (orientation == 3) {
//                    matrix.postRotate(180f)
//                    Log.d("EXIF", "Exif: $orientation")
//                } else if (orientation == 8) {
//                    matrix.postRotate(270f)
//                    Log.d("EXIF", "Exif: $orientation")
//                }
//                scaledBitmap = Bitmap.createBitmap(
//                    scaledBitmap, 0, 0,
//                    scaledBitmap.width, scaledBitmap.height, matrix,
//                    true
//                )
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            var out: FileOutputStream? = null
//            try {
//                out = FileOutputStream(destFile)
//                scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, preset, out)
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            } finally {
//                if (out != null) {
//                    try {
//                        out.close()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//            null
//        }
//        val bmpFinal = FileProvider.getUriForFile(
//            this@CompressPhotoActivity,
//            "$packageName.provider",
//            destFile!!
//        )
//        return getBitmap(this, bmpFinal)
//    }

    fun getFilename(): String? {
        val file = File(
            Environment.getExternalStorageDirectory().path,
            getString(R.string.app_name) + File.separator + "Compressed Image"
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    override fun onDestroy() {
        if (disposable != null) disposable!!.dispose()
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}