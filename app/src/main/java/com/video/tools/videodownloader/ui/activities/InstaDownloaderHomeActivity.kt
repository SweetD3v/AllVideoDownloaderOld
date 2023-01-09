package com.video.tools.videodownloader.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityInstaDownloaderHomeBinding
import com.video.tools.videodownloader.databinding.DialogServerDownBinding
import com.video.tools.videodownloader.ui.mycreation.MyCreationToolsActivity
import com.video.tools.videodownloader.utils.*
import com.video.tools.videodownloader.utils.apis.InstaModel
import com.video.tools.videodownloader.utils.apis.POST_TYPE
import com.video.tools.videodownloader.utils.apis.RestApiClient
import com.video.tools.videodownloader.utils.downloader.BasicImageDownloader
import com.video.tools.videodownloader.utils.*
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InstaDownloaderHomeActivity : FullScreenActivity() {

    val binding by lazy { ActivityInstaDownloaderHomeBinding.inflate(layoutInflater) }

    val handler = Handler(Looper.getMainLooper())
    var count = 0
    val runnable = object : Runnable {
        override fun run() {
            count++
            handler.postDelayed(this, 1000)
            if (count >= 10) {
                handler.removeCallbacks(this)
                pd.dismissDialog()
            }
        }
    }
    val pd by lazy { MyProgressDialog }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            toolbar.rlMain.adjustInsets(this@InstaDownloaderHomeActivity)

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@InstaDownloaderHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@InstaDownloaderHomeActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame
                )
            }

            toolbar.root.background = ContextCompat.getDrawable(
                this@InstaDownloaderHomeActivity,
                R.drawable.top_bar_gradient_pink
            )

            toolbar.txtTitle.text = getString(R.string.instagram)

            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            toolbar.imgDownloads.visibility = View.VISIBLE
            toolbar.imgDownloads.setOnClickListener {
                startActivity(
                    Intent(
                        this@InstaDownloaderHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "insta_downloader")
                    }
                )
            }

            btnPaste.setOnClickListener {
                etText.setText(getClipboardItemsSpecific(SMType.INSTA))
            }

            btnDownload.setOnClickListener {
                if (etText.text.isNotEmpty()) {
                    if (NetworkState.isOnline()) {
                        AdsUtils.loadInterstitialAd(this@InstaDownloaderHomeActivity,
                            RemoteConfigUtils.adIdInterstital(),
                            object : AdsUtils.Companion.FullScreenCallback() {
                                override fun continueExecution() {
                                    startDownload(etText.text.trim().toString())
                                }
                            })
                    } else Toast.makeText(
                        this@InstaDownloaderHomeActivity,
                        "Please check your internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@InstaDownloaderHomeActivity,
                        "Please enter link",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            imgClear.setOnClickListener { etText.text.clear() }
        }
    }

    private fun startDownload(url: String) {
        Log.e("TAG", "startDownload: $url")
        pd.showDialog(this, "Please wait...", false)
        handler.post(runnable)

        val service = RestApiClient.getInstance(RestApiClient.Companion.SOCIAL_TYPE.INSTA).service

        val call: Call<InstaModel> = service.getMediaUrlInstagram(
            getString(R.string.rapid_api_key),
            getString(R.string.rapid_api_host_insta),
            url
        )

        call.enqueue(object : Callback<InstaModel> {
            override fun onResponse(call: Call<InstaModel>, response: Response<InstaModel>) {
                pd.dismissDialog()
                handler.removeCallbacks(runnable)
                Log.e("TAG", "isShowing: ${MyProgressDialog.dialog?.isShowing}")
                if (response.isSuccessful) {
                    val instaModel = response.body()

                    instaModel?.let { model ->
                        Log.e("TAG", "downLink: ${instaModel.media}")
                        if (instaModel.media.isNotEmpty()) {
                            if (model.getPostType() == POST_TYPE.PHOTO) {
                                BasicImageDownloader(this@InstaDownloaderHomeActivity)
                                    .saveImageToExternalInsta(
                                        instaModel.media
                                    ) {
                                        Toast.makeText(
                                            this@InstaDownloaderHomeActivity,
                                            "Image Downloaded.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                BasicImageDownloader(this@InstaDownloaderHomeActivity)
                                    .saveVideoToExternalInsta(
                                        instaModel.media
                                    ) {
                                        Toast.makeText(
                                            this@InstaDownloaderHomeActivity,
                                            "Video Downloaded.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            toastShort(this@InstaDownloaderHomeActivity, "Please enter valid url.")
                        }
                    } ?: run {
                        pd.dismissDialog()
                        handler.removeCallbacks(runnable)
                        showErrorDialog()
                    }
                } else {
                    Log.e("TAG", "onResponseError: ${response.errorBody()}")
                    pd.dismissDialog()
                    handler.removeCallbacks(runnable)
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<InstaModel>, t: Throwable) {
                pd.dismissDialog()
                handler.removeCallbacks(runnable)
                showErrorDialog()
            }
        })
    }

    private fun showErrorDialog() {
        val dialogServerDownBinding = DialogServerDownBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this, R.style.RoundedCornersDialog80).setCancelable(true)
            .setView(dialogServerDownBinding.root)

        val alertDialog = builder.create()
        alertDialog.show()

        dialogServerDownBinding.run {
            btnOk.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    fun getClipboardItemsSpecific(type: SMType): String {
        val clipboardItems = getClipBoardItems(this)

        if (clipboardItems.isNotEmpty()) {
            val fbList = mutableListOf<String>()
            val instaList = mutableListOf<String>()
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("facebook")) {
                    fbList.add(clipboardItems[i])
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("instagram")) {
                    instaList.add(clipboardItems[i])
                }
            }

            when (type) {
                SMType.INSTA -> {
                    if (instaList.isNotEmpty()) {
                        return instaList[0]
                    }
                    return ""
                }
                SMType.FACEBOOK -> {
                    if (fbList.isNotEmpty()) {
                        return fbList[0]
                    }
                    return ""
                }
                else -> {
                    return if (clipboardItems[0].contains("http"))
                        clipboardItems[0]
                    else ""
                }
            }
        }
        return ""
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}