package com.example.allviddownloader.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetMultipleContents
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ashudevs.facebookurlextractor.FacebookExtractor
import com.ashudevs.facebookurlextractor.FacebookFile
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.example.allviddownloader.R
import com.example.allviddownloader.adapters.StoriesListAdapter
import com.example.allviddownloader.collage_maker.ui.activities.CollageViewActivity
import com.example.allviddownloader.databinding.BottomsheetFbBinding
import com.example.allviddownloader.databinding.BottomsheetInstaBinding
import com.example.allviddownloader.databinding.FragmentHomeBinding
import com.example.allviddownloader.databinding.ViewDialogBinding
import com.example.allviddownloader.models.*
import com.example.allviddownloader.ui.activities.*
import com.example.allviddownloader.utils.*
import com.example.allviddownloader.utils.SMType.*
import com.example.allviddownloader.utils.apis.CommonClassForAPI
import com.example.allviddownloader.utils.downloader.BasicImageDownloader
import com.example.allviddownloader.widgets.BSFragmentBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.reactivex.observers.DisposableObserver
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.*
import java.net.*
import java.util.regex.Pattern


class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    companion object {
        val KEY_DATA_RESULT = "KEY_DATA_RESULT"
        val KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS"

        open fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private var photoUrl = ""
    private var videoUrl = ""

    var commonClassForAPI: CommonClassForAPI? = null
    var downloadDataArrayList: MutableList<DownloadData> = mutableListOf()
    private var storyItemModelList: MutableList<ItemModel> = mutableListOf()
    var storyModels: MutableList<TrayModel> = mutableListOf()
    var paths = mutableListOf<String>()
    lateinit var storiesListAdapter: StoriesListAdapter
    var position: Int = 0
    var urlDownload: String? = null
    var fbSheetBuilder: BSFragmentBuilder? = null
    var instaSheetBuilder: BSFragmentBuilder? = null

    lateinit var permissionsList: MutableList<String>
    var permissionsStr = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var permissionsCount = 0

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }

    var alertDialog: AlertDialog? = null

    private fun showPermissionDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)
        builder.setTitle("Permission required")
            .setMessage("Some permissions are needed to be allowed to use this app without any problems.")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
        if (alertDialog == null) {
            alertDialog = builder.create()
            if (alertDialog?.isShowing == false) {
                alertDialog?.show()
            }
        }
    }

    private fun askForPermissions(permissionsList: MutableList<String>) {
        val newPermissionStr = arrayOfNulls<String>(permissionsList.size)
        for (i in newPermissionStr.indices) {
            newPermissionStr[i] = permissionsList[i]
        }
        if (newPermissionStr.isNotEmpty()) {
            permissionsLauncher.launch(newPermissionStr)
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
            showPermissionDialog()
        }
    }

    val permissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            val list: ArrayList<Boolean> = ArrayList(result!!.values)
            permissionsList = ArrayList()
            permissionsCount = 0
            for (i in 0 until list.size) {
                if (shouldShowRequestPermissionRationale(permissionsStr.get(i))) {
                    permissionsList.add(permissionsStr[i])
                } else if (!hasPermission(ctx, permissionsStr.get(i))) {
                    permissionsCount++
                }
            }
            if (permissionsList.size > 0) {
                //Some permissions are denied and can be asked again.
                askForPermissions(permissionsList)
            } else if (permissionsCount > 0) {
                //Show alert dialog
                showPermissionDialog()
            } else {
                //All permissions granted. Do your stuff 🤞
                //                        downloadSingleImage(urlDownload)
                if (urlDownload.toString().contains("instagram")) {
                    callDownload(urlDownload!!)
                } else if (urlDownload.toString().contains("facebook")) {
                    //                            GetFacebookData().execute(binding.etText.text.toString())
                    var txtUrl = binding.etText.text.toString()
                    if (txtUrl.contains("/?app=fbl"))
                        txtUrl = txtUrl.split("/?app=fbl")[0]
                    getFacebookData(txtUrl)
                }
            }
        }

    var multiImagePicker = registerForActivityResult(
        GetMultipleContents()
    ) { uriList ->
        if (uriList.size > 0) {
            if (uriList.size > 1) {
                val pathList =
                    java.util.ArrayList<String>()
                for (uri in uriList) {
                    pathList.add(uri.toString())
                }
                val intent = Intent(
                    ctx,
                    CollageViewActivity::class.java
                )
                intent.putStringArrayListExtra(KEY_DATA_RESULT, pathList)
                startActivity(intent)
            } else {
                toastShort(ctx, "Please select more than 1 images.")
            }
        }
    }

    private fun launchCollage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (hasPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE))
                multiImagePicker.launch("image/*")
        } else {
            if (hasPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE)
                && hasPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ) {
                multiImagePicker.launch("image/*")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commonClassForAPI = CommonClassForAPI()

        binding.run {
            btnPasteInstagram.setOnClickListener {
//                binding.etText.setText(getClipboardItemsSpecific(INSTA))
                selectTab(INSTA)
            }
            btnPasteFacebook.setOnClickListener {
//                binding.etText.setText(getClipboardItemsSpecific(FACEBOOK))
                selectTab(FACEBOOK)
            }
            btnPasteTwitter.setOnClickListener {
//                binding.etText.setText(getClipboardItemsSpecific(TWITTER))
                selectTab(TWITTER)
            }
            btnPasteVimeo.setOnClickListener {
//                binding.etText.setText(getClipboardItemsSpecific(VIMEO))
                selectTab(VIMEO)
            }

            llInstagram.setOnClickListener {
                val instaSheetBinding = BottomsheetInstaBinding.inflate(layoutInflater)
//                instaSheetBuilder = BSFragmentBuilder().with(childFragmentManager)
//                    .title("Instagram")
//                    .setupLayout(ctx, R.layout.bottomsheet_insta)


//                instaSheetBuilder?.show()
//                val instaSheetView = instaSheetBuilder?.layout
//                initInstaSheet(instaSheetBinding)
                startActivity(Intent(ctx, InstaDownloaderHomeActivity::class.java))
            }

            llFacebook.setOnClickListener {
//                val fbSheetBinding = BottomsheetFbBinding.inflate(layoutInflater)
//                fbSheetBuilder = BSFragmentBuilder().with(childFragmentManager)
//                    .title("Facebook")
//                    .apply {
//                        layout = fbSheetBinding.root
//                    }
//
//
//                fbSheetBuilder?.show()
//                initFBSheet(fbSheetBinding)
                startActivity(Intent(ctx, FBDownloaderHomeActivity::class.java))
            }

            llFacebookWatch.setOnClickListener {
                startActivity(
                    Intent(ctx, FBMainActivity::class.java)
                        .putExtra("fbtype", 0)
                )
            }

            llWhatsappSide.setOnClickListener {
                startActivity(Intent(ctx, WAStatusActivity::class.java))
            }

            llWallpaper.setOnClickListener {
                startActivity(Intent(ctx, WallpapersActivity::class.java)
                    .putExtra("walpType", "wallpapers"))
            }

            llFunnyNew.setOnClickListener {
                startActivity(Intent(ctx, FunnyVideosActivity::class.java))
            }

            llCleaner.setOnClickListener {
                startActivity(Intent(ctx, CleanerActivity::class.java))
            }

//            llVimeo.setOnClickListener {
//                startActivity(Intent(ctx, VimeoActivity::class.java))
//            }
//
//            llRingtone.setOnClickListener {
//                startActivity(Intent(ctx, RingtoneActivity::class.java))
//            }

            llStatusMaker.setOnClickListener {

            }

            llCollageMakerNew.setOnClickListener {
                launchCollage()
            }

            llFunnyNew.setOnClickListener {
                startActivity(Intent(ctx, FunnyVideosActivity::class.java))
            }

            btnDownload.setOnClickListener {
                urlDownload = etText.text.toString()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!hasPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        permissionsList = mutableListOf()
                        permissionsList.addAll(permissionsStr)
                        askForPermissions(permissionsList)
                    } else {
                        if (urlDownload.toString().contains("instagram")) {
                            callDownload(urlDownload!!)
                        } else if (urlDownload.toString().contains("facebook")) {
                            Log.e("TAG", "onPostExecute1: ${binding.etText.text}")
                            var txtUrl = binding.etText.text.toString()
                            if (txtUrl.contains("/?app=fbl"))
                                txtUrl = txtUrl.split("/?app=fbl")[0]
//                            GetFacebookData().execute(binding.etText.text.toString())
                            Log.e("TAG", "onPostExecute1: ${txtUrl}")
                            getFacebookData(txtUrl)
                        }
                    }
                } else if (!hasPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE)
                    and !hasPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ) {
                    permissionsList = mutableListOf()
                    permissionsList.addAll(permissionsStr)
                    askForPermissions(permissionsList)
                } else {
                    if (urlDownload.toString().contains("instagram")) {
                        callDownload(urlDownload!!)
                    } else if (urlDownload.toString().contains("facebook")) {
//                        GetFacebookData().execute(binding.etText.text.toString())
                        var txtUrl = binding.etText.text.toString()
                        if (txtUrl.contains("/?app=fbl"))
                            txtUrl = txtUrl.split("/?app=fbl")[0]
                        getFacebookData(txtUrl)
                    }
                }
            }
        }
    }

    private fun initInstaSheet(instaSheetBinding: BottomsheetInstaBinding) {
        instaSheetBinding.run {
            imgBack.setOnClickListener {
                instaSheetBuilder?.dismiss()
            }
        }
    }

    private fun initFBSheet(fbSheetBinding: BottomsheetFbBinding) {
        fbSheetBinding.run {
            imgBack.setOnClickListener {
                fbSheetBuilder?.dismiss()
            }

            btnPaste.setOnClickListener {
                Log.e("TAG", "initFBSheet: ${getClipboardItemsSpecific(FACEBOOK)}")
                etText.setText(getClipboardItemsSpecific(FACEBOOK))
            }

            btnDownload.setOnClickListener {
                etText.text.toString().let { text ->
                    Log.e("TAG", "initFBSheet: $text")
                    object : AsyncTaskRunner<String, Void>(ctx) {
                        override fun doInBackground(params: String?): Void? {
                            Log.e("TAG", "doInBackground: ${params}")
                            linkParsing(params!!) { item ->
                                Log.e("TAG", "onCreate: ${item.imageUrl}")
                                val downloadUrl = item.imageUrl!!

                                if (downloadUrl.contains(".jpg") || downloadUrl.contains(".png")) {
                                    BasicImageDownloader(ctx).saveImageToExternal(
                                        downloadUrl,
                                        RootDirectoryFacebookShow
                                    )
                                } else {
                                    BasicImageDownloader(ctx).saveVideoToExternal(
                                        downloadUrl
                                    )
                                }
                            }
                            return null
                        }

                    }.execute(text, false)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.e("TAG", "onResume: Called ${getClipboardItemsSpecific()}")
        binding.run {
            etText.setText(getClipboardItemsSpecific())
        }
    }

    fun getClipboardItemsSpecific(type: SMType): String {
        val clipboardItems = getClipBoardItems(ctx)

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
                INSTA -> {
                    if (instaList.isNotEmpty()) {
                        selectTab(type)
                        return instaList[0]
                    }
                    return ""
                }
                FACEBOOK -> {
                    if (fbList.isNotEmpty()) {
                        selectTab(type)
                        return fbList[0]
                    }
                    return ""
                }
                TWITTER -> {
                    if (twitterList.isNotEmpty()) {
                        selectTab(type)
                        return twitterList[0]
                    }
                    return ""
                }
                VIMEO -> {
                    if (vimeoList.isNotEmpty()) {
                        selectTab(type)
                        return vimeoList[0]
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

    fun getClipboardItemsSpecific(): String {
        val clipboardItems = getClipBoardItems(ctx)

        Log.e("TAG", "clipboardItems1: ${clipboardItems.size}")
        if (clipboardItems.isNotEmpty()) {
            val fbList = mutableListOf<String>()
            val instaList = mutableListOf<String>()
            val twitterList = mutableListOf<String>()
            val vimeoList = mutableListOf<String>()
            var type: SMType = INSTA

            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.instagram.com")) {
                    instaList.add(clipboardItems[i])
                    type = INSTA
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.facebook.com")) {
                    fbList.add(clipboardItems[i])
                    type = FACEBOOK
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.twitter.com")) {
                    twitterList.add(clipboardItems[i])
                    type = TWITTER
                }
            }
            for (i in clipboardItems.indices) {
                if (clipboardItems[i].contains("www.vimeo.com")) {
                    vimeoList.add(clipboardItems[i])
                    type = VIMEO
                }
            }
            Log.e("TAG", "itemType: $type")
            when (type) {
                INSTA -> {
                    if (instaList.isNotEmpty()) {
                        selectTab(type)
                        return instaList[0]
                    }
                    return ""
                }
                FACEBOOK -> {
                    if (fbList.isNotEmpty()) {
                        selectTab(type)
                        return fbList[0]
                    }
                    return ""
                }
                TWITTER -> {
                    if (twitterList.isNotEmpty()) {
                        selectTab(type)
                        return twitterList[0]
                    }
                    return ""
                }
                VIMEO -> {
                    if (vimeoList.isNotEmpty()) {
                        selectTab(type)
                        return vimeoList[0]
                    }
                    return ""
                }
                else -> {
                    return if (clipboardItems[0].contains("http"))
                        clipboardItems[0]
                    else ""
                }
            }
        } else {
            selectTab(INSTA)
        }
        return ""
    }

    private fun selectTab(type: SMType) {
        when (type) {
            INSTA -> {
                binding.btnPasteInstagram.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.white
                    )
                )
                binding.btnPasteInstagram.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.primarydark))
                binding.btnPasteFacebook.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteFacebook.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
                binding.btnPasteTwitter.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteTwitter.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
                binding.btnPasteVimeo.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteVimeo.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
            }
            FACEBOOK -> {
                binding.btnPasteInstagram.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteInstagram.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
                binding.btnPasteFacebook.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.white
                    )
                )
                binding.btnPasteFacebook.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.primarydark))
                binding.btnPasteTwitter.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteTwitter.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
                binding.btnPasteVimeo.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteVimeo.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
            }
            TWITTER -> {
                binding.btnPasteInstagram.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteInstagram.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
                binding.btnPasteFacebook.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteFacebook.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
                binding.btnPasteTwitter.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                binding.btnPasteTwitter.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.primarydark))
                binding.btnPasteVimeo.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteVimeo.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
            }
            VIMEO -> {
                binding.btnPasteInstagram.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteInstagram.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
                binding.btnPasteFacebook.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteFacebook.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
                binding.btnPasteTwitter.setTextColor(
                    ContextCompat.getColor(
                        ctx,
                        R.color.primary
                    )
                )
                binding.btnPasteTwitter.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.transparent))
                binding.btnPasteVimeo.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                binding.btnPasteVimeo.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.primarydark))
            }
            else -> {}
        }
    }

    private fun getUrlWithoutParameters(str: String): String? {
        return try {
            val uri = URI(str)
            URI(uri.scheme, uri.authority, uri.path, null, uri.fragment).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(ctx, "Enter valid url", Toast.LENGTH_SHORT).show()
            ""
        }
    }

    fun getImageFilenameFromURL(str: String?): String? {
        return try {
            File(URL(str).path).name
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            System.currentTimeMillis().toString() + ".png"
        }
    }

    fun getVideoFilenameFromURL(str: String?): String? {
        return try {
            File(URL(str).path).name
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            System.currentTimeMillis().toString() + ".mp4"
        }
    }

    var pd: AsyncTaskRunner.MyProgressDialog? = null
    private val instaObserver: DisposableObserver<JsonObject> =
        object : DisposableObserver<JsonObject>() {
            override fun onNext(jsonObject: JsonObject) {
                try {
                    val responseModel: ResponseModel = Gson().fromJson(
                        jsonObject.toString(),
                        object : TypeToken<ResponseModel?>() {}.type
                    )
                    val edge_sidecar_to_children: EdgeSidecarToChildren? =
                        responseModel.graphql!!.shortcode_media!!.edge_sidecar_to_children
                    pd?.dismissDialog()

                    if (edge_sidecar_to_children != null) {
                        edge_sidecar_to_children.edges?.apply {
                            val edges: List<Edge> = this
                            for (i in edges.indices) {
                                if (edges[i].node!!.is_video) {
                                    videoUrl = edges[i].node!!.video_url.toString()
                                    val download_data =
                                        DownloadData(
                                            videoUrl,
                                            getVideoFilenameFromURL(videoUrl)!!,
                                            ""
                                        )
                                    downloadDataArrayList.add(download_data)
                                } else {
                                    photoUrl = edges[i].node?.display_resources?.get(
                                        edges[i].node?.display_resources!!.size - 1
                                    )?.src!!
                                    Log.e("TAG", "onNext: ${photoUrl}")
                                    val download_data =
                                        DownloadData(
                                            photoUrl,
                                            getImageFilenameFromURL(photoUrl)!!,
                                            ""
                                        )
                                    downloadDataArrayList.add(download_data)
                                }
                            }
                            pd?.dismissDialog()
                            if (videoUrl != "") {
                                val file: File = File(
                                    RootDirectoryWhatsappShow,
                                    getVideoFilenameFromURL(videoUrl)
                                )
                                if (!file.exists()) {
                                    paths = java.util.ArrayList()
                                    downloadDataArrayList = arrayListOf()
                                    //                                DownloadImageMultiple().execute(
                                    //                                    videoUrl,
                                    //                                    getVideoFilenameFromURL(videoUrl)
                                    //                                )
                                    //                            downloadMultiple();
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "Already Downloaded",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } else {
                                val file = File(
                                    RootDirectoryWhatsappShow,
                                    getImageFilenameFromURL(photoUrl)
                                )
                                if (!file.exists()) {
                                    paths = mutableListOf()
                                    //                                DownloadImageMultiple()
                                    //                                    .execute(photoUrl, getImageFilenameFromURL(photoUrl))
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "Already Downloaded",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }
                    } else if (responseModel.graphql.shortcode_media!!.is_video) {
                        videoUrl = responseModel.graphql.shortcode_media.video_url.toString()
                        val download_data =
                            DownloadData(videoUrl, getVideoFilenameFromURL(videoUrl)!!, "")
                        downloadDataArrayList.add(download_data)
                        val file = File(
                            RootDirectoryWhatsappShow,
                            getVideoFilenameFromURL(videoUrl)
                        )
                        if (!file.exists()) {
                            callDownload(videoUrl)
                        } else {
                            Toast.makeText(activity, "Already Downloaded", Toast.LENGTH_SHORT)
                                .show()
                        }
                        videoUrl = ""
                        binding.etText.setText("")
                    } else {
                        photoUrl =
                            responseModel.graphql.shortcode_media.display_resources!![responseModel.graphql.shortcode_media.display_resources.size - 1].src!!
                        val download_data =
                            DownloadData(photoUrl, getImageFilenameFromURL(photoUrl)!!, "")
                        downloadDataArrayList.add(download_data)
                        val file = File(
                            RootDirectoryWhatsappShow,
                            getImageFilenameFromURL(photoUrl)
                        )
                        Log.e("TAG", "onNext: ${photoUrl}")
                        if (!file.exists()) {
                            Log.e("TAG", "onNext1: ${photoUrl}")
//                            callDownload(photoUrl)
                            downloadSingleImage(photoUrl)
                        } else {
                            Toast.makeText(activity, "Already Downloaded", Toast.LENGTH_SHORT)
                                .show()
                        }
                        photoUrl = ""
                        binding.etText.setText("")
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "onNextCatch: ${e.localizedMessage}")
                    e.printStackTrace()
                }
            }

            override fun onError(th: Throwable) {
                pd?.dismissDialog()
                Log.e("TAG", "onError: ${th.localizedMessage}")
                th.printStackTrace()
            }

            override fun onComplete() {
                pd?.dismissDialog()
            }
        }

    private fun callDownload(str: String) {
        val str2 = "${getUrlWithoutParameters(str)}?__a=1"
        Log.e("TAG", "callDownload: $str2")
        try {
            if (!NetworkState.isOnline()) {

            } else if (commonClassForAPI != null) {
                pd = AsyncTaskRunner.MyProgressDialog(ctx)
                pd?.showDialog("Downloading...", false)
                commonClassForAPI!!.callResult(
                    this.instaObserver,
                    str2,
                    "ds_user_id=" + PrefsManager.newInstance(ctx)
                        .getString(PrefsManager.USERID, "")
                        .toString() + "; sessionid=" + PrefsManager.newInstance(ctx)
                        .getString(PrefsManager.SESSIONID, "")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun downloadSingleImage(urlSingle: String?) {
        Log.e("TAG", "downloadSingleImage: $urlSingle")
        object : AsyncTaskRunner<String, String>(ctx) {
            override fun doInBackground(params: String?): String? {
                var input: InputStream? = null
                var output: OutputStream? = null
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(urlSingle)
                    connection = url.openConnection() as HttpURLConnection
                    connection.connect()
                    if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                        return ("Server returned HTTP " + connection.responseCode
                                + " " + connection.responseMessage)
                    }
                    val fileLength = connection.contentLength
                    input = connection.inputStream
                    var filename = File(
                        RootDirectoryInstaShow,
                        "story_" + storyItemModelList[position].id.toString() + ".png"
                    )
                    output = FileOutputStream(filename)
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        if (this.isShutdown()) {
                            input.close()
                            return null
                        }
                        total += count.toLong()
//                        if (fileLength > 0) publishProgress((total * 100 / fileLength).toInt())
                        output.write(data, 0, count)
                    }
                } catch (e: java.lang.Exception) {
                    return e.toString()
                } finally {
                    try {
                        output?.close()
                        input?.close()
                    } catch (ignored: IOException) {
                    }
                    connection?.disconnect()
                }
                return null
            }

        }.execute(urlSingle!!, true)
    }

    fun getFilenameFromURL(str: String?): String? {
        return try {
            File(URL(str).path).name + ".mp4"
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            System.currentTimeMillis().toString() + ".mp4"
        }
    }


    inner class GetFacebookData internal constructor() :
        AsyncTask<String?, Void?, Document?>() {
        var facebookDoc: Document? = null
        public override fun onPreExecute() {
            super.onPreExecute()
            AsyncTaskRunner.MyProgressDialog(ctx).showDialog("Loading...", false)
        }

        override fun doInBackground(vararg strArr: String?): Document? {
            Log.e("TAG", "onPostExecute: ${strArr[0]}")
            try {
                facebookDoc = Jsoup.connect(strArr[0]).get()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return facebookDoc
        }

        public override fun onPostExecute(document: Document?) {
            AsyncTaskRunner.MyProgressDialog(ctx).dismissDialog()
            try {
                videoUrl =
                    document!!.select("meta[property=\"og:video\"]").last().attr("content")
                if (videoUrl != "") {
                    try {
                        download_data(
                            videoUrl,
                            RootDirectoryFacebookShow.absolutePath,
                            getFilenameFromURL(videoUrl)!!
                        )
                        videoUrl = ""
                        binding.etText.setText("")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                }
            } catch (e2: NullPointerException) {
                e2.printStackTrace()
            }
        }
    }

    var downloadId = 0

    fun download_data(url: String?, dirPath: String, fileName: String) {
        val viewDialogBinding = ViewDialogBinding.inflate(LayoutInflater.from(ctx))
        downloadId = PRDownloader.download(url, dirPath, fileName)
            .build()
            .setOnStartOrResumeListener {
                val builder =
                    AlertDialog.Builder(
                        ctx,
                        R.style.Downloader_DialogTheme
                    ).setView(viewDialogBinding.root)
                        .setCancelable(true)
                alertDialog = builder.create()
                alertDialog!!.show()
                viewDialogBinding.rhProgressBar.max = 100
                alertDialog!!.setOnDismissListener {
                    PRDownloader.cancel(
                        downloadId
                    )
                }
            }
            .setOnProgressListener { progress ->
                val perc: Double =
                    progress.currentBytes / progress.totalBytes as Double * 100.0f
                viewDialogBinding.rhProgressBar.progress = perc.toInt()
                viewDialogBinding.txtPerc.text = perc.toInt().toString() + " %"
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Toast.makeText(activity, "Download Complete", Toast.LENGTH_SHORT).show()
//                    refreshGallery("$dirPath/$fileName")
                    alertDialog!!.dismiss()
                }

                override fun onError(error: Error?) {
                    Toast.makeText(activity, "Download Failed", Toast.LENGTH_SHORT).show()
                    alertDialog!!.dismiss()
                }
            })
    }

    var pd1: AsyncTaskRunner.MyProgressDialog? = null

    @SuppressLint("StaticFieldLeak")
    private fun getFacebookData(txtUrl1: String) {
//        var txtUrl = "https://scontent.fstv8-2.fna.fbcdn.net/v/t39.25447-2/291149301_723262768982245_7691970269610478645_n.mp4?_nc_cat=105&vs=7b7a538ed4563428&_nc_vs=HBksFQAYJEdQV1ZXaEhscUd5OHpaRUNBRFdNLTlZMFhyOXFibWRqQUFBRhUAAsgBABUAGCRHQkVoWWhGTkxDRWJQSTBDQU5wZlJIRDc1bGdqYnJGcUFBQUYVAgLIAQBLBogScHJvZ3Jlc3NpdmVfcmVjaXBlATENc3Vic2FtcGxlX2ZwcwAQdm1hZl9lbmFibGVfbnN1YgAgbWVhc3VyZV9vcmlnaW5hbF9yZXNvbHV0aW9uX3NzaW0AKGNvbXB1dGVfc3NpbV9vbmx5X2F0X29yaWdpbmFsX3Jlc29sdXRpb24AEWRpc2FibGVfcG9zdF9wdnFzABUAJQAcAAAm8KiBqqHW%2FgEVAigCQzMYC3Z0c19wcmV2aWV3HBdAFXbItDlYEBg5ZGFzaF9pNGxpdGViYXNpY181c2VjZ29wXzQ4MF9jcmZfMjhfbWFpbl8zLjBfZnJhZ18yX3ZpZGVvEgAYGHZpZGVvcy52dHMuY2FsbGJhY2sucHJvZDgSVklERU9fVklFV19SRVFVRVNUGwqIFW9lbV90YXJnZXRfZW5jb2RlX3RhZwZvZXBfc2QTb2VtX3JlcXVlc3RfdGltZV9tcwEwDG9lbV9jZmdfcnVsZQpzZF91bm11dGVkE29lbV9yb2lfcmVhY2hfY291bnQFODE0ODARb2VtX2lzX2V4cGVyaW1lbnQADG9lbV92aWRlb19pZA81NzgzOTUwMDA1MjcyNDUSb2VtX3ZpZGVvX2Fzc2V0X2lkDzU1MzY4MTQ2OTU2OTY0MBVvZW1fdmlkZW9fcmVzb3VyY2VfaWQPNTYwMDMzODQ4ODk2MDU2HG9lbV9zb3VyY2VfdmlkZW9fZW5jb2RpbmdfaWQQNDk1MDg0MzYxNTAyNzEyNA52dHNfcmVxdWVzdF9pZAAlAhwAJcQBGweIAXMENjQ0NAJjZAoyMDIyLTA3LTA2A3JjYgU4MTQwMANhcHAURmFjZWJvb2sgZm9yIEFuZHJvaWQCY3QORkJfU0hPUlRTX1BPU1QTb3JpZ2luYWxfZHVyYXRpb25fcwU1LjQzNAJ0cxRwcm9ncmVzc2l2ZV9vcmRlcmluZwA%3D&ccb=1-7&_nc_sid=a165d7&efg=eyJ2ZW5jb2RlX3RhZyI6Im9lcF9zZCJ9&_nc_ohc=XmC0ZJ882CkAX_AHDD_&_nc_rml=0&_nc_ht=scontent.fstv8-2.fna&oh=00_AT8nx-d_jpBnVJQehyk7-RQa_F0zq_1-WMyZPqAEQXHauQ&oe=62E7B0D9&_nc_rid=657445796061240"
        var txtUrl = "https://www.facebook.com/102438284758534/videos/536904160301855"
        pd1 = AsyncTaskRunner.MyProgressDialog(ctx)
        pd1?.showDialog("Loading...", false)
        object : FacebookExtractor(ctx, txtUrl, true) {
            override fun onExtractionComplete(facebookFile: FacebookFile) {
                pd1?.dismissDialog()
                var videoUrl: String = facebookFile.sdUrl
                if (videoUrl.contains("/?app=fbl"))
                    videoUrl = videoUrl.split("/?app=fbl")[0]
                Log.e("TAG", "onExtractionComplete: ${txtUrl}")
                AppUtils.download(
                    txtUrl,
                    RootDirectoryFacebook,
                    ctx,
                    facebookFile.author + ".mp4"
                )
                binding.etText.setText("")
            }

            override fun onExtractionFail(error: Exception?) {
                pd1?.dismissDialog()
                Log.e("myapp", "Error :  $error")
                Toast.makeText(activity, "Not valid URL", Toast.LENGTH_SHORT).show()
                binding.etText.setText("")
            }
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
                        streamMap.substring(
                            metaTAGTitleMatcher.start(),
                            metaTAGTitleMatcher.end()
                        )
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
                    var imageUrl =
                        streamMap.substring(sdVideoMatcher.start(), sdVideoMatcher.end())
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
                            streamMap.substring(
                                thumbnailMatcher.start(),
                                thumbnailMatcher.end()
                            )
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

    override fun onBackPressed() {

    }
}