package com.example.allviddownloader.ui.activities

import android.content.Intent
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.example.allviddownloader.databinding.ActivityLoginBinding
import com.example.allviddownloader.utils.PrefsManager

class InstaLoginActivity : AppCompatActivity() {
    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    var cookies: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadPage()
        binding.swipeRefreshLayout.setOnRefreshListener { loadPage() }
    }

    private fun loadPage() {
        binding.apply {
            webView.settings.javaScriptEnabled = true
            webView.clearCache(true)
            webView.webViewClient = MyBrowser()
            CookieSyncManager.createInstance(this@InstaLoginActivity)
            CookieManager.getInstance().removeAllCookie()
            webView.loadUrl("https://www.instagram.com/accounts/login/")
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(webView: WebView, i: Int) {
                    binding.swipeRefreshLayout.isRefreshing = i != 100
                }
            }
        }
    }

    fun getCookie(str: String?, str2: String?): String? {
        val cookie = CookieManager.getInstance().getCookie(str)
        if (cookie != null && !cookie.isEmpty()) {
            val split = cookie.split(";").toTypedArray()
            for (str3 in split) {
                if (str3.contains(str2!!)) {
                    return str3.split("=").toTypedArray()[1]
                }
            }
        }
        return null
    }

    inner class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(webView: WebView, str: String): Boolean {
            webView.loadUrl(str)
            return true
        }

        override fun onLoadResource(webView: WebView, str: String) {
            super.onLoadResource(webView, str)
        }

        override fun onPageFinished(webView: WebView, str: String) {
            super.onPageFinished(webView, str)
            this@InstaLoginActivity.cookies = CookieManager.getInstance().getCookie(str)
            try {
                val cookie: String? = this@InstaLoginActivity.getCookie(str, "sessionid")
                val cookie2: String? = this@InstaLoginActivity.getCookie(str, "csrftoken")
                val cookie3: String? = this@InstaLoginActivity.getCookie(str, "ds_user_id")
                if (cookie != null && cookie2 != null && cookie3 != null) {
                    PrefsManager.newInstance(this@InstaLoginActivity)
                        .putString(PrefsManager.COOKIES, this@InstaLoginActivity.cookies!!)
                    PrefsManager.newInstance(this@InstaLoginActivity)
                        .putString(PrefsManager.CSRF, cookie2)
                    PrefsManager.newInstance(this@InstaLoginActivity)
                        .putString(PrefsManager.SESSIONID, cookie)
                    PrefsManager.newInstance(this@InstaLoginActivity)
                        .putString(PrefsManager.USERID, cookie3)
                    PrefsManager.newInstance(this@InstaLoginActivity)
                        .putBoolean(PrefsManager.ISINSTALOGIN, true)
                    webView.destroy()
                    val intent = Intent()
                    intent.putExtra("result", "result")
                    this@InstaLoginActivity.setResult(RESULT_OK, intent)
                    this@InstaLoginActivity.finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onReceivedError(webView: WebView, i: Int, str: String, str2: String) {
            super.onReceivedError(webView, i, str, str2)
        }

        override fun shouldInterceptRequest(
            webView: WebView,
            webResourceRequest: WebResourceRequest
        ): WebResourceResponse? {
            return super.shouldInterceptRequest(webView, webResourceRequest)
        }

        override fun shouldOverrideUrlLoading(
            webView: WebView,
            webResourceRequest: WebResourceRequest
        ): Boolean {
            return super.shouldOverrideUrlLoading(webView, webResourceRequest)
        }
    }
}