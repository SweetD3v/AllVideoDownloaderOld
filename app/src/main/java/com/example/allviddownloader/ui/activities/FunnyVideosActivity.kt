package com.example.allviddownloader.ui.activities

import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityFunnyVideosBinding
import com.example.allviddownloader.utils.setDarkStatusBarColor

class FunnyVideosActivity : AppCompatActivity() {
    val binding by lazy { ActivityFunnyVideosBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDarkStatusBarColor(this, R.color.black)
        setContentView(binding.root)

        loadVideos()
    }

    private fun loadVideos() {


        binding.run {
            webView.settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                loadsImagesAutomatically = true
                domStorageEnabled = true
            }
            webView.webViewClient = WVClient()
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(webView: WebView, i: Int) {

                }
            }
            webView.loadUrl("https://myvideo.fun/")
        }
    }

    class WVClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            Log.e("TAG", "onReceivedError: ${error}")
        }
    }
}