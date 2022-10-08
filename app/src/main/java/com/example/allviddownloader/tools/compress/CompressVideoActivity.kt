package com.example.allviddownloader.tools.compress

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.allviddownloader.R
import com.example.allviddownloader.collage_maker.utils.SystemUtils
import com.example.allviddownloader.databinding.ActivityCompressVideoBinding
import com.example.allviddownloader.databinding.LayoutCompressPdBinding
import com.example.allviddownloader.tools.cartoonify.CartoonActivity
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.FileUtilsss
import com.example.allviddownloader.utils.NetworkState
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class CompressVideoActivity : BaseActivity() {

    companion object {
        const val IMAGE_URI_ORG = "original_uri"
        const val IMAGE_URI_CMP = "compressed_uri"
        const val IMAGE_PATH_CMP = "compressed_path"
        const val IMAGE_SIZE_ORG = "size_org"
        const val IMAGE_SIZE_CMP = "size_cmp"
    }

    val binding by lazy { ActivityCompressVideoBinding.inflate(layoutInflater) }
    lateinit var pdBinding: LayoutCompressPdBinding
    lateinit var myRxFFmpegSubscriber: MyRxFFmpegSubscriber

    var originalVidUri: Uri? = null

    var tempCopyFile: File? = null
    var srcFile: File? = null
    var tempDestFile: File? = null
    var destFile: File? = null
    var preset = "30"
    var orgSize = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (NetworkState.isOnline()) {
            AdsUtils.loadBanner(
                this@CompressVideoActivity, binding.bannerContainer,
                getString(R.string.banner_id_details)
            )
        }

        binding.run {
            imgBack.setOnClickListener { onBackPressed() }
            if (intent.hasExtra(CartoonActivity.SELECTED_PHOTO)) {
                originalVidUri = Uri.parse(intent.getStringExtra(CartoonActivity.SELECTED_PHOTO))
                orgSize = File(
                    SystemUtils.getRealPathFromUri(
                        this@CompressVideoActivity,
                        originalVidUri
                    )
                ).length()
                val cmpSize = (orgSize * 75 / 100.0).toLong()
                txtVideoSize.setText(
                    "Video size: " + FileUtilsss.getFileLength(
                        cmpSize
                    )
                )
                videoView.setVideoURI(originalVidUri)
                val mediaController = MediaController(this@CompressVideoActivity)
                mediaController.setAnchorView(videoView)
                mediaController.setMediaPlayer(videoView)
                videoView.setMediaController(mediaController)
                videoView.start()
            }
            sliderCompress.addOnChangeListener { slider, value, fromUser ->
                val progress = value.toInt()
                val cmpSize: Long = (orgSize * value / 100.0).toLong()
                when (progress) {
                    0 -> {
                        preset = "0"
                        txtCompressPercent.setText("0%")
                        txtVideoSize.setText("")
                    }
                    25 -> {
                        preset = "30"
                        txtCompressPercent.setText(R.string._25_low)
                        txtVideoSize.setText(
                            "Video size: " + FileUtilsss.Companion.getFileLength(
                                cmpSize
                            )
                        )
                    }
                    50 -> {
                        preset = "40"
                        txtCompressPercent.setText(R.string._50_medium)
                        txtVideoSize.setText(
                            "Video size: " + FileUtilsss.Companion.getFileLength(
                                cmpSize
                            )
                        )
                    }
                    75 -> {
                        preset = "50"
                        txtCompressPercent.setText(R.string._75_high)
                        txtVideoSize.setText(
                            "Video size: " + FileUtilsss.Companion.getFileLength(
                                cmpSize
                            )
                        )
                    }
                    100 -> {
                        preset = "100"
                        txtCompressPercent.setText("100% (None)")
                        txtVideoSize.setText(
                            "Video size: " + FileUtilsss.Companion.getFileLength(
                                cmpSize
                            )
                        )
                    }
                }
            }
            btnCompress.setOnClickListener { v ->
                AdsUtils.loadInterstitialAd(
                    this@CompressVideoActivity,
                    getString(R.string.interstitial_id),
                    object : AdsUtils.Companion.FullScreenCallback() {
                        override fun continueExecution() {
                            startCompressingVideo()
                        }
                    })
            }
        }
    }

    private fun startCompressingVideo() {
        if (preset == "0") {
            Toast.makeText(
                this@CompressVideoActivity,
                "Video compression to 0% is not possible",
                Toast.LENGTH_SHORT
            )
                .show()
        } else if (preset == "50") {
            binding.videoView.pause()
            compressVideo()
        } else if (preset == "40") {
            binding.videoView.pause()
            compressVideo()
        } else if (preset == "30") {
            binding.videoView.pause()
            compressVideo()
        } else {
            tempCopyFile = File("vid_temp", "TEMP_VID_" + System.currentTimeMillis() + ".mp4")
            srcFile = File(cacheDir, tempCopyFile!!.absolutePath)
            if (srcFile!!.exists()) srcFile!!.delete()
            tempDestFile =
                File("vid_compressed", "Compressed_video_" + System.currentTimeMillis() + ".mp4")
            destFile = File(cacheDir, tempDestFile!!.absolutePath)
            if (destFile!!.exists()) destFile!!.delete()
            if (!srcFile!!.parentFile.exists()) srcFile!!.parentFile.mkdirs()
            if (!destFile!!.parentFile.exists()) destFile!!.parentFile.mkdirs()
            try {
                FileUtilsss.copyFile(
                    this@CompressVideoActivity,
                    originalVidUri,
                    srcFile!!
                ) { path ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        try {
                            FileUtilsss.Companion.saveVideoAPI30(
                                this@CompressVideoActivity, File(path),
                                "Compressed_VID_" + System.currentTimeMillis(),
                                File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                                    getString(R.string.app_name) + File.separator + "Compressed Video"
                                )
                            ) {
                                Toast.makeText(
                                    this@CompressVideoActivity,
                                    "Compressed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                                null
                            }
                        } catch (e: FileNotFoundException) {
                            Log.e(
                                "TAG",
                                "FileNotFoundException: " + e.localizedMessage
                            )
                        }
                    } else {
                        try {
                            val uri = FileProvider.getUriForFile(
                                this@CompressVideoActivity,
                                "$packageName.provider",
                                File(path)
                            )
                            FileUtilsss.Companion.copyFile(
                                this@CompressVideoActivity, uri,
                                File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                                    getString(R.string.app_name) + File.separator + "Compressed Video"
                                )
                            ) { path1 ->
                                Toast.makeText(
                                    this@CompressVideoActivity,
                                    "Compressed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                                null
                            }
                        } catch (e: IOException) {
                            Log.e("TAG", "IOException: " + e.localizedMessage)
                        }
                    }
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("TAG", "onCreate: " + e.message)
            }
        }
    }

    private fun compressVideo() {
        tempCopyFile = File("vid_temp", "TEMP_VID_" + System.currentTimeMillis() + ".mp4")
        srcFile = File(cacheDir, tempCopyFile!!.absolutePath)
        if (srcFile!!.exists()) srcFile!!.delete()
        tempDestFile =
            File("vid_compressed", "Compressed_video_" + System.currentTimeMillis() + ".mp4")
        destFile = File(cacheDir, tempDestFile!!.absolutePath)
        if (destFile!!.exists()) destFile!!.delete()
        if (!srcFile!!.parentFile.exists()) srcFile!!.parentFile.mkdirs()
        if (!destFile!!.parentFile.exists()) destFile!!.parentFile.mkdirs()
        pdBinding = LayoutCompressPdBinding.inflate(layoutInflater)
        try {
            FileUtilsss.copyFile(this@CompressVideoActivity, originalVidUri, srcFile!!) { path ->
                val srcPath: String = path
                Log.e("TAG", "srcPath: $srcPath")
                //                String cmdBlur = "ffmpeg -y -i " + srcPath + " -vf boxblur=25:5 -preset superfast " + destFile;
                val cmdCompress =
                    "ffmpeg -y -i $srcPath -vcodec libx264 -crf $preset -preset faster $destFile"
                val commands =
                    cmdCompress.split(" ".toRegex()).toTypedArray()
                myRxFFmpegSubscriber = MyRxFFmpegSubscriber()
                myRxFFmpegSubscriber!!.showPD()
                RxFFmpegInvoke.getInstance()
                    .runCommandRxJava(commands)
                    .subscribe(myRxFFmpegSubscriber)
                null
            }
        } catch (e: Exception) {
            Log.e("TAG", "compressVideo: " + e.localizedMessage)
        }
    }

    inner class MyRxFFmpegSubscriber : RxFFmpegSubscriber() {
        var alertDialog: AlertDialog? = null
        fun showPD() {
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(this@CompressVideoActivity, R.style.MyProgressDialog)
                    .setView(pdBinding.getRoot())
                    .setCancelable(false)
            pdBinding.btnCancel.setOnClickListener { v ->
                alertDialog!!.dismiss()
                myRxFFmpegSubscriber.cancel()
            }
            if (alertDialog == null) {
                alertDialog = builder.create()
            }
            alertDialog!!.show()
        }

        fun hidePD() {
            runOnUiThread(Runnable {
                if (alertDialog != null && alertDialog!!.isShowing) {
                    pdBinding.progressCompress.setProgress(0)
                    pdBinding.txtProgressValue.setText("0%")
                    alertDialog!!.dismiss()
                }
            })
        }

        override fun onFinish() {
            hidePD()
            Log.e("TAG", "onFinish: " + destFile!!.getAbsolutePath())
            AdsUtils.Companion.loadInterstitialAd(this@CompressVideoActivity,
                getString(R.string.interstitial_id),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        val saveIntent: Intent = Intent(
                            this@CompressVideoActivity,
                            CompressSaveVideoActivity::class.java
                        )
                        saveIntent.putExtra(
                            IMAGE_URI_ORG,
                            originalVidUri.toString()
                        )
                        val cmpUri = FileProvider.getUriForFile(
                            this@CompressVideoActivity,
                            getPackageName() + ".provider",
                            destFile!!
                        )
                        saveIntent.putExtra(
                            IMAGE_URI_CMP,
                            cmpUri.toString()
                        )
                        saveIntent.putExtra(
                            IMAGE_PATH_CMP,
                            destFile!!.getAbsolutePath()
                        )
                        val orgFilePath: String =
                            SystemUtils.getRealPathFromUri(
                                this@CompressVideoActivity,
                                originalVidUri
                            )
                        saveIntent.putExtra(
                            IMAGE_SIZE_ORG,
                            FileUtilsss.getFileLength(File(orgFilePath).length())
                        )
                        saveIntent.putExtra(
                            IMAGE_SIZE_CMP,
                            FileUtilsss.getFileLength(destFile!!.length())
                        )
                        startActivity(saveIntent)
                    }
                })
        }

        override fun onProgress(progress: Int, progressTime: Long) {
            runOnUiThread(Runnable {
                pdBinding.txtProgressValue.setText("$progress%")
                pdBinding.progressCompress.setProgress(progress)
            })
            Log.e("TAG", "onProgress: $progress")
            Log.e("TAG", "progressTime: $progressTime")
        }

        override fun onCancel() {
            hidePD()
            Log.e("TAG", "onCancel: ")
        }

        override fun onError(message: String) {
            hidePD()
            Toast.makeText(
                this@CompressVideoActivity,
                message,
                Toast.LENGTH_SHORT
            ).show()
            Log.e("TAG", "onError: $message")
        }
    }

    override fun onDestroy() {
        myRxFFmpegSubscriber.dispose()
        AdsUtils.destroyBanner()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (binding.videoView.isPlaying()) {
            binding.videoView.pause()
        }
        finish()
    }

    override fun onPause() {
        super.onPause()
        try {
            binding.videoView.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}