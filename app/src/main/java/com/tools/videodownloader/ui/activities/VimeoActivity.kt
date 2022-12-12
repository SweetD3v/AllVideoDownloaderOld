package com.tools.videodownloader.ui.activities

import android.os.Bundle
import android.util.Log
import android.webkit.*
import com.tools.videodownloader.databinding.ActivityVimeoBinding

class VimeoActivity : BaseActivity() {
    val binding by lazy { ActivityVimeoBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadVideos()
    }

    private fun loadVideos() {
        binding.run {
            webView.settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                loadsImagesAutomatically = true
                domStorageEnabled = true
            }
            webView.webViewClient = WVClient()
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(webView: WebView, i: Int) {

                }
            }
            webView.loadUrl("https://vimeo.com/watch/")
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