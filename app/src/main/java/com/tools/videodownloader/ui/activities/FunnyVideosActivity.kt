package com.tools.videodownloader.ui.activities

import android.app.Activity
import android.content.Context
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityFunnyVideosBinding
import com.tools.videodownloader.databinding.ItemAttitudeStatusBinding
import com.tools.videodownloader.models.PopularVids
import com.tools.videodownloader.utils.*
import com.tools.videodownloader.utils.downloader.BasicImageDownloader

class FunnyVideosActivity : AppCompatActivity() {
    val binding by lazy { ActivityFunnyVideosBinding.inflate(layoutInflater) }

    var lastUrl: String? = null
    var counts = 0
    val customUrl by lazy { if (intent.hasExtra("customUrl")) intent.getStringExtra("customUrl") else "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkStatusBarColor(this, R.color.black)
        setContentView(binding.root)

        binding.run {
            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@FunnyVideosActivity, getString(R.string.banner_id_details),
                    bannerContainer
                )

            initMyPopularVideos()

            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

//    private fun loadVideos() {
//        binding.run {
//            webView.isSaveEnabled = true
//            webView.setNetworkAvailable(true)
//            webView.settings.apply {
//                javaScriptEnabled = true
//                loadWithOverviewMode = true
//                useWideViewPort = true
//                builtInZoomControls = false
//                loadsImagesAutomatically = true
//                domStorageEnabled = true
//
//                setAppCacheEnabled(true)
//                layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
//                domStorageEnabled = true
//                mediaPlaybackRequiresUserGesture = false
//                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//
//                setSupportZoom(true)
//                builtInZoomControls = true
//                displayZoomControls = false
//
//                webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
//            }
//            webView.webViewClient = WVClient()
//            webView.webChromeClient = ChromeClient()
//            if (intent.hasExtra("customUrl"))
//                webView.loadUrl(intent.getStringExtra("customUrl").toString())
//            else webView.loadUrl("https://myvideo.fun")
//        }
//    }

    private fun initMyPopularVideos() {
        binding.run {
            viewPagerVid.orientation = ViewPager2.ORIENTATION_VERTICAL
            val popularAdapter = PopularVideoAdapter(this@FunnyVideosActivity)
            val titleArr =
                this@FunnyVideosActivity.resources.getStringArray(R.array.myfun_titles_array)
            val thumbArr =
                this@FunnyVideosActivity.resources.getStringArray(R.array.myfun_thumbs_array)
            val videoArr = this@FunnyVideosActivity.resources.getStringArray(R.array.fun_videos)
            var popularList = mutableListOf<PopularVids>()
            for (i in videoArr.indices) {
                popularList.add(PopularVids("Title${i}", videoArr[i], videoArr[i]))
            }
            popularList.shuffle()
            if (customUrl != "")
                popularList.add(
                    0,
                    PopularVids(
                        "Title0",
                        customUrl.toString(),
                        customUrl.toString()
                    )
                )
            popularList = popularList.distinctBy { it.videoUrl }.toMutableList()
//            popularList.add(0, PopularVids(titleArr[i], thumbArr[i], videoArr[i]))
            popularAdapter.popularList = popularList
            viewPagerVid.adapter = popularAdapter

            viewPagerVid.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    Log.e("TAG", "onPageSelected: $position")
                }
            })
        }
    }

    class PopularVideoAdapter(var ctx: Context) :
        RecyclerView.Adapter<PopularVideoAdapter.VH>() {

        var popularList = mutableListOf<PopularVids>()
        var popularItemClickListener: PopularItemClickListener? = null

        inner class VH(var binding: ItemAttitudeStatusBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemAttitudeStatusBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.binding.run {
                val popularVid = popularList[holder.bindingAdapterPosition]

                Glide.with(ctx).load(popularVid.videoUrl)
                    .into(imgWallpaper)

                fabDownload.setOnClickListener {
                    AdsUtils.loadInterstitialAd(
                        ctx as Activity,
                        ctx.getString(R.string.interstitial_id),
                        object : AdsUtils.Companion.FullScreenCallback() {
                            override fun continueExecution() {
                                popularVid.videoUrl.let { url ->
                                    BasicImageDownloader(ctx)
                                        .saveImageToExternal(
                                            url,
                                            RootDirectoryFacts
                                        )
                                }
                            }
                        })
                }

//                videoView.setVideoURI(Uri.parse(popularVid.videoUrl))
                imgPlay.setOnClickListener {
                    videoView.setOnPreparedListener {
                        imgPlay.invisible()

                        // Restore saved position, if available.
                        if (videoView.currentPosition > 0) {
                            videoView.seekTo(videoView.currentPosition)
                        } else {
                            // Skipping to 1 shows the first frame of the video.
                            videoView.seekTo(1)
                        }

                        // Start playing!
                        videoView.start()
                    }
                }

                root.setOnClickListener {
                    popularItemClickListener?.onItemClick(popularVid.videoUrl)
                }
            }
        }

        override fun getItemCount(): Int {
            return popularList.size
        }

        interface PopularItemClickListener {
            fun onItemClick(url: String)
        }
    }

    private fun startDownload(downloadUrl: String?) {
        downloadUrl?.let { url ->
            Log.e("TAG", "startDownload: $url")
            BasicImageDownloader(this@FunnyVideosActivity)
                .saveVideoToExternalFunny(
                    url
                ) {
                    Toast.makeText(
                        this@FunnyVideosActivity,
                        "Video Downloaded.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    inner class ChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

//            if (view?.url.toString() != "https://myvideo.fun/") {
//                if (view?.url.toString() != lastUrl) {
//                    lastUrl = view?.url.toString()
//                    counts++
//
//                    if (counts == 3) {
//                        counts = 0
//                        AdsUtils.loadInterstitialAd(this@FunnyVideosActivity,
//                            getString(R.string.interstitial_id),
//                            object : AdsUtils.Companion.FullScreenCallback() {
//                                override fun continueExecution() {
//                                    Log.e("TAG", "fullScreenAdClosed: ")
//                                }
//                            })
//                    }
//                }
//            }

            Log.e("TAG", "onProgressChanged: ${view?.url} | Position: $counts")
        }
    }

    inner class WVClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Log.e("TAG", "shouldOverrideUrlLoading: ${request?.url}")
            return true
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
            Log.e("TAG", "onReceivedSslError: ${error.toString()}")
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("TAG", "onReceivedError: ${error?.description}")
            }
        }
    }

    override fun onBackPressed() {
        if (intent.hasExtra("customUrl")) {
            AdsUtils.loadInterstitialAd(this@FunnyVideosActivity,
                getString(R.string.interstitial_id),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        finish()
                    }
                })
        } else finish()
    }
}