package com.video.tools.videodownloader.tools.compress

import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityCompressSaveShareBinding
import com.video.tools.videodownloader.ui.activities.FullScreenActivity
import com.video.tools.videodownloader.utils.AdsUtils
import com.video.tools.videodownloader.utils.FileUtilsss
import com.video.tools.videodownloader.utils.NetworkState
import com.video.tools.videodownloader.utils.adjustInsets
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import java.io.File

class CompressSavePhotoActivity : FullScreenActivity() {

    companion object {
        const val IMAGE_URI_ORG = "original_uri"
        const val IMAGE_URI_CMP = "compressed_uri"
        const val IMAGE_PATH_CMP = "compressed_path"
        const val IMAGE_SIZE_ORG = "size_org"
        const val IMAGE_SIZE_CMP = "size_cmp"
    }

    var originalImgUri: Uri? = null
    var cmp_uri: Uri? = null
    var cmpFilePath: String = ""
    var compressedImage: File? = null
    var sizeOrg: String = ""
    var sizeCmp: String = ""

    val binding by lazy { ActivityCompressSaveShareBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (NetworkState.isOnline()) {
//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )

            AdsUtils.loadNative(
                this@CompressSavePhotoActivity,
                RemoteConfigUtils.adIdNative(),
                binding.adFrame
            )
        }

        originalImgUri = intent.getStringExtra(IMAGE_URI_ORG).toString().toUri()
        cmp_uri = intent.getStringExtra(IMAGE_URI_CMP).toString().toUri()
        cmpFilePath = intent.getStringExtra(IMAGE_PATH_CMP).toString()
        sizeOrg = intent.getStringExtra(IMAGE_SIZE_ORG).toString()
        sizeCmp = intent.getStringExtra(IMAGE_SIZE_CMP).toString()

        compressedImage = File(cmpFilePath)

        binding.run {

            binding.toolbar.txtTitle.text = getString(R.string.photo_compress)
            binding.toolbar.rlMain.adjustInsets(this@CompressSavePhotoActivity)
            binding.toolbar.root.background = ContextCompat.getDrawable(
                this@CompressSavePhotoActivity,
                R.drawable.top_bar_gradient_purple
            )

            toolbar.imgBack.setOnClickListener { onBackPressed() }

            originalImgUri?.let { uri ->
//            Glide.with(this)
//                .load(uri)
//                .into(binding.imgCompressLeft)
                imgCompressLeft.setImageURI(uri)
            }

            Log.e("TAG", "onCreate: $originalImgUri")

//        Glide.with(this)
//            .load(cmp_uri)
//            .into(binding.imgCompressRight)
            imgCompressRight.setImageURI(cmp_uri)
            Log.e("TAG", "onCreateCmp: $cmp_uri")

            textSizeOrg.text = "Before: ${sizeOrg}"
            textSizeCmp.text = "After: ${sizeCmp}"

            btnSaveImage.setOnClickListener {
                val destFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
                            + File.separator + getString(R.string.app_name),
                    "Compressed Photo"
                )
                if (!destFile.exists())
                    destFile.mkdirs()

                FileUtilsss.copyFile(
                    this@CompressSavePhotoActivity, cmp_uri,
                    File(
                        destFile,
                        compressedImage?.name.toString()
                    )
                ) { pathExported: String? ->
                    Toast.makeText(
                        this@CompressSavePhotoActivity,
                        "Saved!",
                        Toast.LENGTH_SHORT
                    ).show()
                    MediaScannerConnection.scanFile(
                        this@CompressSavePhotoActivity, arrayOf(
                            pathExported
                        ), null
                    ) { _: String?, uri: Uri? ->
                    }

                    onBackPressed()
                }
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, PhotoCmpHomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}