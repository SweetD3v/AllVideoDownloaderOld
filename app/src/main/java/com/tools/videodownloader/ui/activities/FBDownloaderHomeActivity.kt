package com.tools.videodownloader.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityFbdownloaderHomeBinding
import com.tools.videodownloader.databinding.DialogServerDownBinding
import com.tools.videodownloader.ui.mycreation.MyCreationToolsActivity
import com.tools.videodownloader.utils.*
import com.tools.videodownloader.utils.apis.FBModel
import com.tools.videodownloader.utils.apis.RestApiClient
import com.tools.videodownloader.utils.downloader.BasicImageDownloader
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FBDownloaderHomeActivity : FullScreenActivity() {

    val binding by lazy { ActivityFbdownloaderHomeBinding.inflate(layoutInflater) }
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

            toolbar.rlMain.adjustInsets(this@FBDownloaderHomeActivity)

            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@FBDownloaderHomeActivity, getString(R.string.banner_id_details),
//                    bannerContainer
//                )

                AdsUtils.loadNative(
                    this@FBDownloaderHomeActivity,
                    RemoteConfigUtils.adIdNative(),
                    adFrame
                )
            }

            toolbar.txtTitle.text = getString(R.string.facebook)

            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            toolbar.imgDownloads.visibility = View.VISIBLE
            toolbar.imgDownloads.setOnClickListener {
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
                            RemoteConfigUtils.adIdInterstital(),
                            object : AdsUtils.Companion.FullScreenCallback() {
                                override fun continueExecution() {
                                    pd.showDialog(
                                        this@FBDownloaderHomeActivity,
                                        "Please wait...",
                                        false
                                    )
                                    startDownloadVolley(etText.text.trim().toString())
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

    private fun startDownloadVolley(url: String) {
        val baseUrl = getString(R.string.base_url_fb)
        val queue = Volley.newRequestQueue(this)

        handler.post(runnable)

        val request: StringRequest = object : StringRequest(
            Method.POST, baseUrl,
            com.android.volley.Response.Listener { response ->
                pd.dismissDialog()
                Toast.makeText(
                    this@FBDownloaderHomeActivity,
                    "Data added to API",
                    Toast.LENGTH_SHORT
                ).show()
                try {
                    val gson = Gson()
                    val fbModel: FBModel? = gson.fromJson(response, FBModel::class.java)

                    Log.e("TAG", "fbResponse: ${Gson().toJson(fbModel)}")

                    fbModel?.let { model ->
                        model.data?.data?.let { fbModelDetails ->
                            fbModelDetails.video.playable_url?.let { urlHD ->
                                BasicImageDownloader(this@FBDownloaderHomeActivity)
                                    .saveVideoToExternalFB(
                                        urlHD
                                    ) {
                                        Toast.makeText(
                                            this@FBDownloaderHomeActivity,
                                            "Video Downloaded.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    } ?: let {
                        pd.dismissDialog()
                        showErrorDialog()
                    }

                } catch (e: JSONException) {
                    Log.e("TAG", "fbExc: ${e.message}")
                    e.printStackTrace()
                    pd.dismissDialog()
                    showErrorDialog()
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                Log.e("TAG", "fbExcError: ${error.message}")
                pd.dismissDialog()
                showErrorDialog()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["URL"] = url
                return params
            }

            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["X-RapidAPI-Key"] = getString(R.string.rapid_api_key)
                headers["X-RapidAPI-Host"] = getString(R.string.rapid_api_host_fb)
                return headers
            }
        }
        queue.add(request)
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
                Log.e("TAG", "fbResponse: ${pd.dialog?.isShowing}")

                Log.e("TAG", "onResponseData: ${response.body()}")
                if (response.body()?.error == true) {
                    Log.e("TAG", "onResponseError: ${response.body()}")
                }

                if (response.isSuccessful) {
                    val fbModel = response.body()
                    fbModel?.let { model ->
                        model.data?.data?.let { fbModelDetails ->
                            fbModelDetails.video.playable_url?.let { urlHD ->
                                BasicImageDownloader(this@FBDownloaderHomeActivity)
                                    .saveVideoToExternalFB(
                                        urlHD
                                    ) {
                                        Toast.makeText(
                                            this@FBDownloaderHomeActivity,
                                            "Video Downloaded.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                } else {
                    pd.dismissDialog()
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<FBModel>, t: Throwable) {
                Log.e("TAG", "onFailure: ${t.message}")
                pd.dismissDialog()
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

        for (clip in clipboardItems) {
            Log.e("TAG", "getClips: $clip")
        }

        if (clipboardItems.isNotEmpty()) {
            val fbList = mutableListOf<String>()
            val instaList = mutableListOf<String>()
            val twitterList = mutableListOf<String>()
            val vimeoList = mutableListOf<String>()
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("fb.watch") || clipboardItems[i].contains("facebook")) {
                    fbList.add(clipboardItems[i])
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("instagram")) {
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