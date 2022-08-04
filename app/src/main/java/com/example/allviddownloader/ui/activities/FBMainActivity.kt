package com.example.allviddownloader.ui.activities

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ashudevs.facebookurlextractor.FacebookExtractor
import com.ashudevs.facebookurlextractor.FacebookFile
import com.example.allviddownloader.databinding.ActivityFbmainBinding
import com.example.allviddownloader.utils.AppUtils
import com.example.allviddownloader.utils.AsyncTaskRunner
import com.example.allviddownloader.utils.downloader.BasicImageDownloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.regex.Pattern

class FBMainActivity : AppCompatActivity() {
    val binding by lazy { ActivityFbmainBinding.inflate(layoutInflater) }

    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        binding.run {
//            webView.settings.javaScriptEnabled = true
//            webView.settings.pluginState = WebSettings.PluginState.ON
//            webView.settings.builtInZoomControls = true
//            webView.settings.displayZoomControls = true
//            webView.settings.useWideViewPort = true
//            webView.settings.loadWithOverviewMode = true
//            webView.addJavascriptInterface(this@FBMainActivity, "FBDownloader")
//            webView.webViewClient = object : WebViewClient() {
//                override fun onPageFinished(view: WebView?, url: String?) {
//                    webView.loadUrl(
//                        "javascript:(function() { "
//                                + "var el = document.querySelectorAll('div[data-sigil]');"
//                                + "for(var i=0;i<el.length; i++)"
//                                + "{"
//                                + "var sigil = el[i].dataset.sigil;"
//                                + "if(sigil.indexOf('inlineVideo') > -1){"
//                                + "delete el[i].dataset.sigil;"
//                                + "var jsonData = JSON.parse(el[i].dataset.store);"
//                                + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');"
//                                + "}" + "}" + "})()"
//                    )
//
//                    url?.let {
//                        if (it.contains("scontent")) {
//                            fabDownload.visibility = View.VISIBLE
//                        } else fabDownload.visibility = View.GONE
//
//                        fabDownload.setOnClickListener {
//                            Log.e("TAG", "downloadUrl: ${url}")
//                        }
//                    }
//                }
//
//                override fun onLoadResource(view: WebView?, url: String?) {
//                    webView.loadUrl(
//                        ("javascript:(function prepareVideo() { "
//                                + "var el = document.querySelectorAll('div[data-sigil]');"
//                                + "for(var i=0;i<el.length; i++)"
//                                + "{"
//                                + "var sigil = el[i].dataset.sigil;" 
//                                + "if(sigil.indexOf('inlineVideo') > -1){"
//                                + "delete el[i].dataset.sigil;"
//                                + "console.log(i);"
//                                + "var jsonData = JSON.parse(el[i].dataset.store);"
//                                + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
//                                + "}" + "}" + "})()")
//                    )
//                    webView.loadUrl(
//                        ("javascript:( window.onload=prepareVideo;"
//                                + ")()")
//                    )
//                }
//            }
//
//            CookieSyncManager.createInstance(this@FBMainActivity)
//            val cookieManager = CookieManager.getInstance()
//            cookieManager.setAcceptCookie(true)
//            CookieSyncManager.getInstance().startSync()
//
//            webView.loadUrl(
//                if (intent.getIntExtra("fbtype", 0) == 0) "https://m.facebook.com/"
//                else "https://m.facebook.com/watch/"
//            )
//        }


        binding.fabDownload.setOnClickListener {
            object : AsyncTaskRunner<String, Void>(this) {
                override fun doInBackground(params: String?): Void? {
                    Log.e("TAG", "doInBackground: ${params}")
                    linkParsing(params!!) { item ->
                        Log.e("TAG", "onCreate: ${item.imageUrl}")
                        val downloadUrl = item.imageUrl!!

                        if (downloadUrl.contains(".jpg") || downloadUrl.contains(".png")) {
                            BasicImageDownloader(this@FBMainActivity).saveImageToExternal(
                                downloadUrl
                            )
                        } else {
                            BasicImageDownloader(this@FBMainActivity).saveVideoToExternal(
                                downloadUrl
                            )
                        }
                    }
                    return null
                }

            }.execute(binding.etFbLink.text.toString(), false)
        }
    }

    class DownloadItem {
        var author: String? = ""
        var filename: String? = ""
        var postLink: String? = ""
        var sdUrl: String? = ""
        var ext: String? = ""
        var thumbNailUrl: String? = ""
        var hdUrl: String? = ""
        var imageUrl: String? = ""
    }

    fun linkParsing(url: String, loaded: (item: DownloadItem) -> Unit) {
        val showLogs = true
        Log.e("post_url", url)
        return try {
            val getUrl = URL(url)
            val urlConnection =
                getUrl.openConnection() as HttpURLConnection
            var reader: BufferedReader? = null
            urlConnection.setRequestProperty("User-Agent", AppUtils.USER_AGENT)
            urlConnection.setRequestProperty("Accept", "*/*")
            val streamMap = StringBuilder()
            try {
                reader =
                    BufferedReader(InputStreamReader(urlConnection.inputStream))
                var line: String?
                while (reader.readLine().also {
                        line = it
                    } != null) {
                    streamMap.append(line)
                }
            } catch (E: Exception) {
                E.printStackTrace()
                reader?.close()
                urlConnection.disconnect()
            } finally {
                reader?.close()
                urlConnection.disconnect()
            }
            if (streamMap.toString().contains("You must log in to continue.")) {
            } else {
                val metaTAGTitle =
                    Pattern.compile("<meta property=\"og:title\"(.+?)\" />")
                val metaTAGTitleMatcher = metaTAGTitle.matcher(streamMap)
                val metaTAGDescription =
                    Pattern.compile("<meta property=\"og:description\"(.+?)\" />")
                val metaTAGDescriptionMatcher =
                    metaTAGDescription.matcher(streamMap)
                var authorName: String? = ""
                var fileName: String? = ""
                if (metaTAGTitleMatcher.find()) {
                    var author =
                        streamMap.substring(metaTAGTitleMatcher.start(), metaTAGTitleMatcher.end())
                    Log.e("Extractor", "AUTHOR :: $author")
                    author = author.replace("<meta property=\"og:title\" content=\"", "")
                        .replace("\" />", "")
                    authorName = author
                } else {
                    authorName = "N/A"
                }
                if (metaTAGDescriptionMatcher.find()) {
                    var name = streamMap.substring(
                        metaTAGDescriptionMatcher.start(),
                        metaTAGDescriptionMatcher.end()
                    )
                    Log.e("Extractor", "FILENAME :: $name")
                    name = name.replace("<meta property=\"og:description\" content=\"", "")
                        .replace("\" />", "")
                    fileName = name
                } else {
                    fileName = "N/A"
                }
                val sdVideo =
                    Pattern.compile("<meta property=\"og:video\"(.+?)\" />")
                val sdVideoMatcher = sdVideo.matcher(streamMap)
                val imagePattern =
                    Pattern.compile("<meta property=\"og:image\"(.+?)\" />")
                val imageMatcher = imagePattern.matcher(streamMap)
                val thumbnailPattern =
                    Pattern.compile("<img class=\"_3chq\" src=\"(.+?)\" />")
                val thumbnailMatcher = thumbnailPattern.matcher(streamMap)
                val hdVideo = Pattern.compile("(hd_src):\"(.+?)\"")
                val hdVideoMatcher = hdVideo.matcher(streamMap)
                val facebookFile = DownloadItem()
                facebookFile.author = authorName
                facebookFile.filename = fileName
                facebookFile.postLink = url
                if (sdVideoMatcher.find()) {
                    var vUrl = sdVideoMatcher.group()
                    vUrl = vUrl.substring(8, vUrl.length - 1) //sd_scr: 8 char
                    facebookFile.sdUrl = vUrl
                    facebookFile.ext = "mp4"
                    var imageUrl = streamMap.substring(sdVideoMatcher.start(), sdVideoMatcher.end())
                    imageUrl = imageUrl.replace("<meta property=\"og:video\" content=\"", "")
                        .replace("\" />", "").replace("&amp;", "&")
                    Log.e("Extractor", "FILENAME :: NULL")
                    Log.e("Extractor", "FILENAME :: $imageUrl")
                    facebookFile.sdUrl = URLDecoder.decode(imageUrl, "UTF-8")
                    if (showLogs) {
                        Log.e("Extractor", "SD_URL :: Null")
                        Log.e("Extractor", "SD_URL :: $imageUrl")
                    }
                    if (thumbnailMatcher.find()) {
                        var thumbNailUrl =
                            streamMap.substring(thumbnailMatcher.start(), thumbnailMatcher.end())
                        thumbNailUrl = thumbNailUrl.replace("<img class=\"_3chq\" src=\"", "")
                            .replace("\" />", "").replace("&amp;", "&")
                        Log.e("Extractor", "Thumbnail :: NULL")
                        Log.e("Extractor", "Thumbnail :: $thumbNailUrl")
                        facebookFile.thumbNailUrl = URLDecoder.decode(thumbNailUrl, "UTF-8")
                    }

                }
                if (hdVideoMatcher.find()) {
                    var vUrl1 = hdVideoMatcher.group()
                    vUrl1 = vUrl1.substring(8, vUrl1.length - 1) //hd_scr: 8 char
                    facebookFile.hdUrl = vUrl1

                    if (showLogs) {
                        Log.e("Extractor", "HD_URL :: Null")
                        Log.e("Extractor", "HD_URL :: $vUrl1")
                    }

                } else {
                    facebookFile.hdUrl = null
                }
                if (imageMatcher.find()) {
                    var imageUrl =
                        streamMap.substring(imageMatcher.start(), imageMatcher.end())
                    imageUrl = imageUrl.replace("<meta property=\"og:image\" content=\"", "")
                        .replace("\" />", "").replace("&amp;", "&")
                    Log.e("Extractor", "FILENAME :: NULL")
                    Log.e("Extractor", "FILENAME :: $imageUrl")
                    facebookFile.imageUrl = URLDecoder.decode(imageUrl, "UTF-8")
                }
                if (facebookFile.sdUrl == null && facebookFile.hdUrl == null) {
                }
                loaded(facebookFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("JavascriptInterface")
    private fun loadWebView() {
        binding.run {
            webView.settings.javaScriptEnabled = true
            webView.settings.userAgentString = AppUtils.USER_AGENT
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true
            webView.addJavascriptInterface(this, "mJava")
            webView.post {
                run {
                    Log.e("TAG", "onCreate: ${etFbLink.text}")
                    webView.loadUrl(etFbLink.text.toString())
                }
            }
            object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url == "https://m.facebook.com/login.php" || url.contains("https://m.facebook.com/login.php")
                    ) {
                        webView.loadUrl("url of your video")
                    }
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }
            }
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
//                    if (progressBarBottomSheet != null) {
//                        if (newProgress == 100) {
//                            progressBarBottomSheet.visibility = View.GONE
//                        } else {
//                            progressBarBottomSheet.visibility = View.VISIBLE
//                        }
//                        progressBarBottomSheet.progress = newProgress
//                    }
                }
            }
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                        if (webView.progress == 100) {
                            var original = webView.originalUrl
                            var post_link = "url of your video"
                            if (original.equals(post_link)) {
                                var listOfResolutions = arrayListOf<ResolutionDetail>()
                                val progressDialog = ProgressDialog(this@FBMainActivity)
                                progressDialog.show()

                                //Fetch resoultions
                                webView.evaluateJavascript(
                                    "(function(){return window.document.body.outerHTML})();"
                                ) { value ->
                                    val reader = JsonReader(StringReader(value))
                                    reader.isLenient = true
                                    try {
                                        if (reader.peek() == JsonToken.STRING) {
                                            val domStr = reader.nextString()
                                            domStr?.let {
                                                val xmlString = it
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    CoroutineScope(Dispatchers.IO).async {
                                                        try {
                                                            getVideoResolutionsFromPageSource((xmlString)) {
                                                                listOfResolutions = it
                                                            }
                                                        } catch (e: java.lang.Exception) {
                                                            e.printStackTrace()
                                                            Log.e("Exception", e.message!!)
                                                        }
                                                    }.await()
                                                    progressDialog.hide()
                                                    if (listOfResolutions.size > 0) {
                                                        Log.e(
                                                            "TAG",
                                                            "listOfResolutions: ${listOfResolutions.size}"
                                                        )
//                                                        setupResolutionsListDialog(listOfResolutions)
                                                    } else {
                                                        Toast.makeText(
                                                            this@FBMainActivity,
                                                            "No Resolutions Found",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    } finally {
                                        reader.close()
                                    }
                                }
                            }
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                    super.onPageFinished(view, url)
                }

                @TargetApi(android.os.Build.VERSION_CODES.M)
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError
                ) {

                }

                @SuppressWarnings("deprecation")
                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                }

                override fun onLoadResource(view: WebView?, url: String?) {
                    Log.e("getData", "onLoadResource")

                    super.onLoadResource(view, url)
                }
            }
        }
    }

    fun getVideoResolutionsFromPageSource(
        pageSourceXmlString: String?,
        finished: (listOfRes: ArrayList<ResolutionDetail>) -> Unit
    ) {
        //pageSourceXmlString is the Page Source of WebPage of that specific copied video
        //We need to find list of Base URLs from pageSourceXmlString
        //Base URLs are inside an attribute named data-store which is inside a div whose class name starts with  '_53mw;
        //We need to find that div then get data-store which has a JSON as string
        //Parse that JSON and we will get list of adaptationset
        //Each adaptationset has list of representation tags
        // representation is the actual div which contains BASE URLs
        //Note that: BASE URLs have a specific attribute called mimeType
        //mimeType has audio/mp4 and video/mp4 which helps us to figure out whether the url is of an audio or a video
        val listOfResolutions = arrayListOf<ResolutionDetail>()
        if (!pageSourceXmlString?.isEmpty()!!) {
            val document: org.jsoup.nodes.Document = Jsoup.parse(pageSourceXmlString)
            val sampleDiv = document.getElementsByTag("body")
            if (!sampleDiv.isEmpty()) {
                val bodyDocument: org.jsoup.nodes.Document = Jsoup.parse(sampleDiv.html())
                val dataStoreDiv: org.jsoup.nodes.Element? =
                    bodyDocument.select("div._53mw").first()
                val dataStoreAttr = dataStoreDiv?.attr("data-store")
                val jsonObject = JSONObject(dataStoreAttr)
                if (jsonObject.has("dashManifest")) {
                    val dashManifestString: String = jsonObject.getString("dashManifest")
                    val dashManifestDoc: org.jsoup.nodes.Document = Jsoup.parse(dashManifestString)
                    val mdpTagVal = dashManifestDoc.getElementsByTag("MPD")
                    val mdpDoc: org.jsoup.nodes.Document = Jsoup.parse(mdpTagVal.html())
                    val periodTagVal = mdpDoc.getElementsByTag("Period")
                    val periodDocument: org.jsoup.nodes.Document = Jsoup.parse(periodTagVal.html())
                    val subBodyDiv: org.jsoup.nodes.Element? = periodDocument.select("body").first()
                    subBodyDiv?.children()?.forEach {
                        val adaptionSetDiv: org.jsoup.nodes.Element? =
                            it.select("adaptationset").first()
                        adaptionSetDiv?.children()?.forEach {
                            if (it is org.jsoup.nodes.Element) {
                                val representationDiv: org.jsoup.nodes.Element? =
                                    it.select("representation").first()
                                val resolutionDetail = ResolutionDetail()
                                if (representationDiv?.hasAttr("mimetype")!!) {
                                    resolutionDetail.mimetype = representationDiv.attr("mimetype")
                                }
                                if (representationDiv.hasAttr("width")) {
                                    resolutionDetail.width =
                                        representationDiv.attr("width")?.toLong()!!
                                }
                                if (representationDiv.hasAttr("height")) {
                                    resolutionDetail.height =
                                        representationDiv.attr("height").toLong()
                                }
                                if (representationDiv.hasAttr("FBDefaultQuality")) {
                                    resolutionDetail.FBDefaultQuality =
                                        representationDiv.attr("FBDefaultQuality")
                                }
                                if (representationDiv.hasAttr("FBQualityClass")) {
                                    resolutionDetail.FBQualityClass =
                                        representationDiv.attr("FBQualityClass")
                                }
                                if (representationDiv.hasAttr("FBQualityLabel")) {
                                    resolutionDetail.FBQualityLabel =
                                        representationDiv.attr("FBQualityLabel")
                                }
                                val representationDoc: org.jsoup.nodes.Document =
                                    Jsoup.parse(representationDiv.html())
                                val baseUrlTag = representationDoc.getElementsByTag("BaseURL")
                                if (!baseUrlTag.isEmpty() && !resolutionDetail.FBQualityLabel.equals(
                                        "Source",
                                        ignoreCase = true
                                    )
                                ) {
                                    resolutionDetail.videoQualityURL = baseUrlTag[0].text()
                                    listOfResolutions.add(resolutionDetail)
                                }
                            }
                        }
                    }
                }
            }
        }
        finished(listOfResolutions)
    }

    class ResolutionDetail {
        var width: Long = 0
        var height: Long = 0
        var FBQualityLabel = ""
        var FBDefaultQuality = ""
        var FBQualityClass = ""
        var videoQualityURL = ""
        var mimetype = ""  // [audio/mp3 for audios and video/mp4 for videos]
    }

//    @JavascriptInterface
//    fun processVideo(vidData: String?, vidID: String) {
//        try {
//            val mBaseFolderPath = RootDirectoryFacebook
//            if (!File(mBaseFolderPath).exists()) {
//                File(mBaseFolderPath).mkdir()
//            }
//            val mFilePath = "file://$mBaseFolderPath/$vidID.mp4"
//            val downloadUri = Uri.parse(vidData)
//            val req = DownloadManager.Request(downloadUri)
//            req.setDestinationUri(Uri.parse(mFilePath))
//            req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//            dm.enqueue(req)
//            Toast.makeText(this, "Download Started", Toast.LENGTH_LONG).show()
//        } catch (e: Exception) {
//            Toast.makeText(this, "Download Failed: $e", Toast.LENGTH_LONG).show()
//        }
//    }


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