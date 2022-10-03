package com.example.allviddownloader.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityFbdownloaderHomeBinding
import com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity
import com.example.allviddownloader.utils.*
import com.example.allviddownloader.utils.apis.FBModel
import com.example.allviddownloader.utils.apis.RestApiClient
import com.example.allviddownloader.utils.downloader.BasicImageDownloader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FBDownloaderHomeActivity : BaseActivity() {

    val binding by lazy { ActivityFbdownloaderHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@FBDownloaderHomeActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@FBDownloaderHomeActivity,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            imgBack.setOnClickListener {
                onBackPressed()
            }

            imgDownloads.setOnClickListener {
                startActivity(
                    Intent(
                        this@FBDownloaderHomeActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(MyCreationToolsActivity.CREATION_TYPE, "fb_downloader")
                    }
                )
            }

            btnPaste.setOnClickListener {
                etText.setText(getClipboardItemsSpecific(SMType.FACEBOOK))
            }

            btnDownload.setOnClickListener {
                if (etText.text.isNotEmpty()) {
                    if (NetworkState.isOnline()) {
                        AdsUtils.loadInterstitialAd(this@FBDownloaderHomeActivity,
                            getString(R.string.interstitial_id),
                            object : AdsUtils.Companion.FullScreenCallback() {
                                override fun continueExecution() {
                                    startDownload(etText.text.trim().toString())
                                }
                            })
                    } else Toast.makeText(
                        this@FBDownloaderHomeActivity,
                        "Please check your internet connection",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@FBDownloaderHomeActivity,
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
        val pd = MyProgressDialog
        pd.showDialog(this@FBDownloaderHomeActivity, "Please wait...", false)
        val service = RestApiClient.getInstance(RestApiClient.Companion.SOCIAL_TYPE.FB).service

        val call: Call<FBModel> = service.getMediaUrlFacebook(
            getString(R.string.rapid_api_key),
            getString(R.string.rapid_api_host_fb),
            url
        )

        call.enqueue(object : Callback<FBModel> {
            override fun onResponse(call: Call<FBModel>, response: Response<FBModel>) {
                pd.dismissDialog()
                Log.e("TAG", "response: ${pd.dialog?.isShowing}")

                if (response.body()?.hasError == true) {
                    Log.e("TAG", "onResponseError: ${response.body()?.errorMessage}")
                }

                if (response.isSuccessful) {
                    val fbModel = response.body()
                    fbModel?.let { model ->
                        model.fbModelDetails?.let { fbModelDetails ->
                            fbModelDetails.videoHD?.let { urlHD ->
                                BasicImageDownloader(this@FBDownloaderHomeActivity)
                                    .saveVideoToExternal(
                                        urlHD,
                                        RootDirectoryFBDownlaoder
                                    ) {
                                        Toast.makeText(
                                            this@FBDownloaderHomeActivity,
                                            "Video Downloaded.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } ?: let {
                                fbModelDetails.video?.let { sdUrl ->
                                    BasicImageDownloader(this@FBDownloaderHomeActivity)
                                        .saveVideoToExternal(
                                            sdUrl,
                                            RootDirectoryFBDownlaoder
                                        ) {
                                            Toast.makeText(
                                                this@FBDownloaderHomeActivity,
                                                "Video Downloaded.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } ?: toastShort(
                                    this@FBDownloaderHomeActivity,
                                    "Failed to download Video"
                                )
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this@FBDownloaderHomeActivity,
                        "Unable to download. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                    pd.dismissDialog()
                }
            }

            override fun onFailure(call: Call<FBModel>, t: Throwable) {
                Log.e("TAG", "onFailure: ${t.message}")
                pd.dismissDialog()
                toastShort(this@FBDownloaderHomeActivity, t.message.toString())
            }
        })
    }

    fun getClipboardItemsSpecific(type: SMType): String {
        val clipboardItems = getClipBoardItems(this)

        for (clip in clipboardItems) {
            Log.e("TAG", "getClips: $clip")
        }

        if (clipboardItems.isNotEmpty()) {
            val fbList = mutableListOf<String>()
            val instaList = mutableListOf<String>()
            val twitterList = mutableListOf<String>()
            val vimeoList = mutableListOf<String>()
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.facebook.com")) {
                    fbList.add(clipboardItems[i])
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.instagram.com")) {
                    instaList.add(clipboardItems[i])
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.twitter.com")) {
                    twitterList.add(clipboardItems[i])
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.vimeo.com")) {
                    vimeoList.add(clipboardItems[i])
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