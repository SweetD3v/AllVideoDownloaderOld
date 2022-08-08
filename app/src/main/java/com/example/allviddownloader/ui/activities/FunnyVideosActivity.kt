package com.example.allviddownloader.ui.activities

import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityFunnyVideosBinding
import com.example.allviddownloader.utils.setDarkStatusBarColor
import okhttp3.internal.userAgent

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
            webView.isSaveEnabled = true
            webView.setNetworkAvailable(true)
            webView.settings.apply {
                userAgentString = "Mozilla/5.0 (Linux; U; Android 4.4; en-us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                loadsImagesAutomatically = true
                domStorageEnabled = true
            }
            webView.webViewClient = WVClient()
            webView.webChromeClient = WebChromeClient()
            webView.loadUrl("https://myvideo.fun")
        }
    }

    class WVClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Log.e("TAG", "shouldOverrideUrlLoading: ${request?.url}")
            return true
        }

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