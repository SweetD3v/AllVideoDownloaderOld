package com.tools.videodownloader.tools.cartoonify

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivitySaveShareCartoonBinding
import com.tools.videodownloader.ui.activities.BaseActivity
import com.tools.videodownloader.utils.*
import java.text.SimpleDateFormat
import java.util.*

class SaveShareCartoonActivity : BaseActivity() {
    companion object {
        var finalBitmapImage: Bitmap? = null
    }

    val binding by lazy { ActivitySaveShareCartoonBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@SaveShareCartoonActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNativeSmall(
                    this@SaveShareCartoonActivity,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            imgBack.setOnClickListener {
                onBackPressed()
            }

            imgCartooned.setImageBitmap(finalBitmapImage)

            btnSaveImage.setOnClickListener {
                val sdf = SimpleDateFormat("dd_MM_ss_yyyy", Locale.getDefault())
                val fileName = sdf.format(Date(System.currentTimeMillis()))
                Log.e("TAG", "onCreate: ${RootDirectoryCartoonified.name}")
                if (intent.getIntExtra("type", 0) == 0)
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        saveBitmap(
//                            this@SaveShareCartoonActivity,
//                            finalBitmapImage,
//                            "IMG_Cartoon_$fileName",
//                            RootDirectoryCartoonified.absolutePath
//                        )
//                    } else {
                    FileUtilsss.saveBitmapAsFileTools(
                        this@SaveShareCartoonActivity,
                        finalBitmapImage,
                        "IMG_Cartoon_$fileName",
                        RootDirectoryCartoonified.name
                    ) { pathExported ->
                        MediaScannerConnection.scanFile(
                            this@SaveShareCartoonActivity, arrayOf(
                                pathExported
                            ), null
                        ) { _: String?, uri: Uri? ->
                        }
                    }
//                    }
                else {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        saveBitmap(
//                            this@SaveShareCartoonActivity,
//                            finalBitmapImage,
//                            "IMG_Sketchify_$fileName",
//                            RootDirectorySketchified.absolutePath
//                        )
//                    } else {
                    FileUtilsss.saveBitmapAsFileTools(
                        this@SaveShareCartoonActivity,
                        finalBitmapImage,
                        "IMG_Sketchify_$fileName",
                        RootDirectorySketchified.name
                    ) { pathExported ->
                        MediaScannerConnection.scanFile(
                            this@SaveShareCartoonActivity, arrayOf(
                                pathExported
                            ), null
                        ) { _: String?, uri: Uri? ->
                        }
                    }
//                    }
                }
                Toast.makeText(this@SaveShareCartoonActivity, "Image saved", Toast.LENGTH_SHORT)
                    .show()
                finalBitmapImage = null
                onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        if (intent.getIntExtra("type", 0) == 0) {
            startActivity(Intent(this, CartoonifyHomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        } else {
            startActivity(Intent(this, SketchifyHomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            })
        }
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}