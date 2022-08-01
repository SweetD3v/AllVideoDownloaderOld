package com.example.allviddownloader.ui.activities

import android.app.DownloadManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.allviddownloader.databinding.ActivityFbmainBinding
import com.example.allviddownloader.utils.RootDirectoryFacebook
import java.io.File

class FBMainActivity : AppCompatActivity() {
    val binding by lazy { ActivityFbmainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {
            webView.settings.javaScriptEnabled = true
            webView.settings.pluginState = WebSettings.PluginState.ON
            webView.settings.builtInZoomControls = true
            webView.settings.displayZoomControls = true
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true
            webView.addJavascriptInterface(this@FBMainActivity, "FBDownloader")
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    webView.loadUrl(
                        "javascript:(function() { "
                                + "var el = document.querySelectorAll('div[data-sigil]');"
                                + "for(var i=0;i<el.length; i++)"
                                + "{"
                                + "var sigil = el[i].dataset.sigil;"
                                + "if(sigil.indexOf('inlineVideo') > -1){"
                                + "delete el[i].dataset.sigil;"
                                + "var jsonData = JSON.parse(el[i].dataset.store);"
                                + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');"
                                + "}" + "}" + "})()"
                    )

                    url?.let {
                        if (it.contains("scontent")) {
                            fabDownload.visibility = View.VISIBLE
                        } else fabDownload.visibility = View.GONE

                        fabDownload.setOnClickListener {
                            Log.e("TAG", "downloadUrl: ${url}")
                        }
                    }
                }

                override fun onLoadResource(view: WebView?, url: String?) {
                    webView.loadUrl(
                        ("javascript:(function prepareVideo() { "
                                + "var el = document.querySelectorAll('div[data-sigil]');"
                                + "for(var i=0;i<el.length; i++)"
                                + "{"
                                + "var sigil = el[i].dataset.sigil;"
                                + "if(sigil.indexOf('inlineVideo') > -1){"
                                + "delete el[i].dataset.sigil;"
                                + "console.log(i);"
                                + "var jsonData = JSON.parse(el[i].dataset.store);"
                                + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
                                + "}" + "}" + "})()")
                    )
                    webView.loadUrl(
                        ("javascript:( window.onload=prepareVideo;"
                                + ")()")
                    )
                }
            }

            CookieSyncManager.createInstance(this@FBMainActivity)
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            CookieSyncManager.getInstance().startSync()

            webView.loadUrl(
                if (intent.getIntExtra("fbtype", 0) == 0) "https://m.facebook.com/"
                else "https://m.facebook.com/watch/"
            )
        }
    }

    @JavascriptInterface
    fun processVideo(vidData: String?, vidID: String) {
        try {
            val mBaseFolderPath = RootDirectoryFacebook
            if (!File(mBaseFolderPath).exists()) {
                File(mBaseFolderPath).mkdir()
            }
            val mFilePath = "file://$mBaseFolderPath/$vidID.mp4"
            val downloadUri = Uri.parse(vidData)
            val req = DownloadManager.Request(downloadUri)
            req.setDestinationUri(Uri.parse(mFilePath))
            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(req)
            Toast.makeText(this, "Download Started", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Download Failed: $e", Toast.LENGTH_LONG).show()
        }
    }


//    @JavascriptInterface
//    fun processVideo(vidData: String?, vidID: String) {
//        Log.e("TAG", "processVideo: ${vidID}")
//        try {
//            val mBaseFolderPath: String = RootDirectoryFacebook
//            if (!File(mBaseFolderPath).exists()) {
//                File(mBaseFolderPath).mkdir()
//            }
//            val mFilePath = "file://$mBaseFolderPath/$vidID.mp4"
//            val downloadUri: Uri = Uri.parse(vidData)
//
//            val req: DownloadManager.Request = DownloadManager.Request(downloadUri)
//            req.setDestinationUri(Uri.parse(mFilePath))
//            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            val dm: DownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//            dm.enqueue(req)
//            Toast.makeText(this, "Download Started", Toast.LENGTH_LONG).show()
//        } catch (e: Exception) {
//            Toast.makeText(this, "Download Failed: $e", Toast.LENGTH_LONG).show()
//        }
//    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return
        }
        finish()
    }
}