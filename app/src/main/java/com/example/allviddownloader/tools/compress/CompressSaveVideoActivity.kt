package com.example.allviddownloader.tools.compress

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityCompressSaveVideoBinding
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.FileUtilsss
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.getVideoThumbnail
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class CompressSaveVideoActivity : BaseActivity() {
    val binding by lazy { ActivityCompressSaveVideoBinding.inflate(layoutInflater) }

    var originalImgUri: Uri? = null
    var cmp_uri: Uri? = null
    var cmpFilePath: String = ""
    var compressedImage: File? = null
    var sizeOrg: String = ""
    var sizeCmp: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (NetworkState.isOnline()) {
//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )

            AdsUtils.loadNative(
                this@CompressSaveVideoActivity,
                getString(R.string.admob_native_id),
                binding.adFrame
            )
        }

        originalImgUri =
            intent.getStringExtra(CompressVideoActivity.IMAGE_URI_ORG).toString().toUri()
        cmp_uri = intent.getStringExtra(CompressVideoActivity.IMAGE_URI_CMP).toString().toUri()
        cmpFilePath = intent.getStringExtra(CompressVideoActivity.IMAGE_PATH_CMP).toString()
        sizeOrg = intent.getStringExtra(CompressVideoActivity.IMAGE_SIZE_ORG).toString()
        sizeCmp = intent.getStringExtra(CompressVideoActivity.IMAGE_SIZE_CMP).toString()

        compressedImage = File(cmpFilePath)

        binding.run {
            originalImgUri?.let { uri ->
                val bmp = getVideoThumbnail(this@CompressSaveVideoActivity, uri)
                imgCompressLeft.setImageBitmap(bmp)
            }

            val bmp = getVideoThumbnail(this@CompressSaveVideoActivity, cmp_uri!!)
            imgCompressRight.setImageBitmap(bmp)

            textSizeOrg.text = "Before: $sizeOrg"
            textSizeCmp.text = "After: $sizeCmp"

            btnSaveImage.setOnClickListener {
                val destFile = File(cmpFilePath)
                val saveFile = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
                            + File.separator + getString(R.string.app_name),
                    "Compressed Video"
                )
                if (!saveFile.exists())
                    saveFile.mkdirs()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        FileUtilsss.saveVideoAPI30(
                            this@CompressSaveVideoActivity, destFile,
                            "Compressed_VID_" + System.currentTimeMillis(),
                            saveFile
                        ) {
                            Toast.makeText(
                                this@CompressSaveVideoActivity,
                                "Saved!",
                                Toast.LENGTH_SHORT
                            ).show()

                            AdsUtils.loadInterstitialAd(this@CompressSaveVideoActivity,
                                getString(R.string.interstitial_id),
                                object : AdsUtils.Companion.FullScreenCallback() {
                                    override fun continueExecution() {
                                        onBackPressed()
                                    }
                                })
                        }
                    } catch (e: FileNotFoundException) {
                        Log.e("TAG", "FileNotFoundException: " + e.localizedMessage)
                    }
                } else {
                    try {
                        val uri = FileProvider.getUriForFile(
                            this@CompressSaveVideoActivity,
                            "$packageName.provider", destFile
                        )
                        FileUtilsss.copyFile(
                            this@CompressSaveVideoActivity, uri,
                            saveFile
                        ) { path: String? ->
                            Toast.makeText(
                                this@CompressSaveVideoActivity,
                                "Saved!",
                                Toast.LENGTH_SHORT
                            ).show()
                            onBackPressed()
                        }
                    } catch (e: IOException) {
                        Log.e("TAG", "IOException: " + e.localizedMessage)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, CompressVideoHomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}