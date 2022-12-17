package com.tools.videodownloader.ui.activities

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityFullviewWaBinding
import com.tools.videodownloader.databinding.ItemFullViewBinding
import com.tools.videodownloader.models.Media
import com.tools.videodownloader.ui.fragments.WAImagesFragment.Companion.imagesList
import com.tools.videodownloader.utils.*
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import java.io.File

class FullViewWhatsappActivity : AppCompatActivity() {
    val binding by lazy { ActivityFullviewWaBinding.inflate(layoutInflater) }

    //    var imagesList = mutableListOf<Media>()
    var position = 0
    val extFile by lazy { File(getExternalFilesDir("Videos"), "video.mp4") }
    var isVideo = false
    var isAllVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            Log.e("TAG", "imagesList: ${imagesList.size}")

            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@FullViewWhatsappActivity, RemoteConfigUtils.adIdBanner(),
                    bannerContainer
                )

            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }

            fabDownload.hide()
            fabShare.hide()
            fabSetWP.hide()
            txtDownload.visibility = View.GONE
            txtShare.visibility = View.GONE
            txtSetWp.visibility = View.GONE

            fabMore.setOnClickListener {
                isAllVisible = if (!isAllVisible) {
                    fabDownload.show()
                    fabShare.show()
                    if (!imagesList[viewPagerMedia.currentItem].isVideo) {
                        fabSetWP.show()
                        txtSetWp.visibility = View.VISIBLE
                    }
                    txtDownload.visibility = View.VISIBLE
                    txtShare.visibility = View.VISIBLE

                    true
                } else {
                    fabDownload.hide()
                    fabShare.hide()
                    fabSetWP.hide()
                    txtDownload.visibility = View.GONE
                    txtShare.visibility = View.GONE
                    txtSetWp.visibility = View.GONE

                    false
                }
            }

            fabShare.setOnClickListener {
                val image = imagesList[binding.viewPagerMedia.currentItem]
                if (contentResolver.getType(image.uri)?.contains("image", true) == true) {
                    val bitmap =
                        if (image.uri.toString().endsWith(".jpg")
                            or image.uri.toString().endsWith(".png")
                        ) getBitmapFromUri(this@FullViewWhatsappActivity, image.uri)
                        else getVideoThumbnail(this@FullViewWhatsappActivity, image.uri)

                    saveImageTemp(bitmap)
                } else {
                    saveVideoTemp(image.uri)
                }
            }

            fabSetWP.setOnClickListener {
                val image = imagesList[binding.viewPagerMedia.currentItem]
                val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                val uri = image.uri
                Log.e("TAG", "uriWP: ${uri}")
                val intent = Intent(Intent.ACTION_ATTACH_DATA)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(uri, contentResolver.getType(uri))
                intent.putExtra("mimeType", contentResolver.getType(uri))

                val resInfoList: List<ResolveInfo> = packageManager.queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                for (resolveInfo in resInfoList) {
                    val packageName: String = resolveInfo.activityInfo.packageName
                    grantUriPermission(
                        packageName,
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                startActivity(Intent.createChooser(intent, "Set as:"))
            }

//            fabShare.setOnClickListener {
//                shareMedia(
//                    this@FullViewWhatsappActivity, imagesList[viewPagerMedia.currentItem].uri,
//                    imagesList[viewPagerMedia.currentItem].path
//                )
//            }

            fabDownload.setOnClickListener {
                AdsUtils.loadInterstitialAd(
                    this@FullViewWhatsappActivity,
                    RemoteConfigUtils.adIdInterstital(),
                    object : AdsUtils.Companion.FullScreenCallback() {
                        override fun continueExecution() {
                            val image = imagesList[binding.viewPagerMedia.currentItem]
                            val bitmap =
                                if (image.uri.toString().endsWith(".jpg")
                                    or image.uri.toString().endsWith(".png")
                                ) getBitmapFromUri(this@FullViewWhatsappActivity, image.uri)
                                else getVideoThumbnail(this@FullViewWhatsappActivity, image.uri)
                            if (image.uri.toString().endsWith(".jpg")
                                or image.uri.toString().endsWith(".png")
                            ) {
                                saveImage(bitmap)
                            } else {
                                saveVideo(imagesList[position].uri)
                            }
                        }
                    })
            }
        }

        if (intent.hasExtra("type")) {
            isVideo = intent.getStringExtra("type").equals("video")
        }

//        if (!isVideo) {
//            binding.fabSetWP.visibility = View.GONE
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                loadImages()
//            } else {
//                executeImageOld()
//            }
//        } else {
//            binding.fabSetWP.visibility = View.VISIBLE
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                loadVideo()
//            } else {
//                executeVideoOld()
//            }
//        }
        loadStatus()

        if (extFile.exists())
            extFile.delete()
    }

    private fun loadStatus() {
//        binding.apply {
//            val imageListNew = mutableListOf<Media>()
//            getMediaWAAll(this@FullViewWhatsappActivity) { list ->
//                for (media in list) {
//                    if (!media.path.contains(".nomedia", true)
//                    ) {
//                        imageListNew.add(media)
//                    }
//                }
//                Log.e("TAG", "loadImagesNew: ${imageListNew.size}")
//                Log.e("TAG", "loadImages: ${imagesList.size}")
//                if (imageListNew.size != imagesList.size) {
//                    imagesList = imageListNew
//                    refreshAdapter()
//                }
//            }
//        }
        refreshAdapter()
    }

    private fun saveVideo(uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val extFile = File(getExternalFilesDir("Videos"), "video.mp4")
            if (extFile.exists())
                extFile.delete()
            FileUtilsss.copyFileAPI30(
                this@FullViewWhatsappActivity,
                uri,
                File(getExternalFilesDir("Videos"), "video.mp4")
            ) { file ->
                Log.e("TAG", "filePath: ${file.absolutePath}")

                FileUtilsss.saveVideoQ(
                    this@FullViewWhatsappActivity, uri,
                    "VID_${System.currentTimeMillis()}",
                    RootDirectoryWhatsappShow
                ) {
                    MediaScannerConnection.scanFile(
                        this@FullViewWhatsappActivity, arrayOf(
                            it.absolutePath
                        ), null
                    ) { _: String?, uri: Uri? ->
                        Toast.makeText(
                            this@FullViewWhatsappActivity,
                            getString(R.string.saved),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Toast.makeText(
                        this@FullViewWhatsappActivity,
                        getString(R.string.saved),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            val file = File(imagesList[position].path)
            val dest =
                File(RootDirectoryWhatsappShow, "VID${System.currentTimeMillis()}.mp4")
            try {
                Log.e("TAG", "uri: $uri")
//                            copy(file, File("${dest}.mp4"))
                FileUtilsss.copyFileAPI30(
                    this@FullViewWhatsappActivity,
                    uri,
                    dest
                ) { file1 ->
                    Log.e("TAG", "onCopied: ${file1.absolutePath}")
                    MediaScannerConnection.scanFile(
                        this@FullViewWhatsappActivity, arrayOf(
                            file1.absolutePath
                        ), null
                    ) { _: String?, uri: Uri? ->
                        Handler(Looper.getMainLooper())
                            .post {
                                Toast.makeText(
                                    this@FullViewWhatsappActivity,
                                    getString(R.string.saved),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            } catch (e: Exception) {
                Log.e("TAG", "copyExc: ${e.message}")
            }
        }
    }

    private fun saveImage(bitmap: Bitmap?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtilsss.saveBitmapAPI30(
                this@FullViewWhatsappActivity, bitmap,
                "IMG_${System.currentTimeMillis()}.jpg",
                "image/jpeg",
                RootDirectoryWhatsappShow
            ) {
                Toast.makeText(
                    this@FullViewWhatsappActivity,
                    "Saved!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val file1 = FileUtilsss.saveBitmapAsFileWA(
                this@FullViewWhatsappActivity,
                bitmap,
                "IMG_${System.currentTimeMillis()}.jpg"
            )

            file1.let { file ->
                MediaScannerConnection.scanFile(
                    this@FullViewWhatsappActivity, arrayOf(
                        file.absolutePath
                    ), null
                ) { _: String?, _: Uri? ->

                }
            }
        }
    }

    private fun saveImageTemp(bitmap: Bitmap?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtilsss.saveBitmapAsFileCache(
                this@FullViewWhatsappActivity,
                bitmap,
                "IMG_${System.currentTimeMillis()}.jpg"
            ) { path ->
                val uri = FileProvider.getUriForFile(this, "${packageName}.provider", File(path))
                shareMediaUri(this, arrayListOf(uri))
            }
        } else {
            FileUtilsss.saveBitmapAsFileCache(
                this@FullViewWhatsappActivity,
                bitmap,
                "IMG_${System.currentTimeMillis()}.jpg"
            ) { path ->
                val uri = FileProvider.getUriForFile(this, "${packageName}.provider", File(path))
                shareMediaUri(this, arrayListOf(uri))
            }
        }
    }

    private fun saveVideoTemp(uri: Uri) {
        FileUtilsss.copyFileAPI30(
            this@FullViewWhatsappActivity,
            uri,
            File(cachePathWA, "TEMP_VID_${System.currentTimeMillis()}.mp4")
        ) { path ->
            Log.e("TAG", "filePath: ${path}")
            val uri1 = FileProvider.getUriForFile(this, "${packageName}.provider", path)
            shareMediaUri(this, arrayListOf(uri1))
        }
    }

    fun executeImageOld() {
        val handler = Handler(Looper.getMainLooper())
        imagesList.clear()
        if (AppUtils.STATUS_DIRECTORY.exists()) {
            val imagesListNew = getMediaQMinus(this, AppUtils.STATUS_DIRECTORY)
            for (media in imagesListNew) {
                if (!media.path.contains(".nomedia", true)) {
                    imagesList.add(media)
                }
            }
            Log.e("TAG", "executeOld: ${imagesList}")
            handler.post {
                refreshAdapter()
            }
        }
    }

    fun executeVideoOld() {
        val handler = Handler(Looper.getMainLooper())
        imagesList.clear()
        if (AppUtils.STATUS_DIRECTORY.exists()) {
            val imagesListNew = getMediaQMinus(this, AppUtils.STATUS_DIRECTORY)
            for (media in imagesListNew) {
                if (!media.path.contains(".nomedia", true)) {
                    imagesList.add(media)
                }
            }
            Log.e("TAG", "executeOld: ${imagesList}")
            handler.post {
                refreshAdapter()
            }
        }
    }

    fun loadImages() {
        val handler = Handler(Looper.getMainLooper())
        val fromTreeUri = DocumentFile.fromTreeUri(
            this,
            contentResolver.persistedUriPermissions[0].uri
        )
        Log.e("TAG", "loadImagesA30: ${fromTreeUri}")
        imagesList.clear()
        if (fromTreeUri == null) {
            handler.post { }
            return
        }
        val listFiles = fromTreeUri.listFiles()
        if (listFiles.isEmpty()) {
            handler.post { }
            return
        }
        for (documentFile in listFiles) {
            Log.e("TAG", "loadImagesA30: ${documentFile.uri}")
            val media = Media(
                documentFile.uri,
                documentFile.uri.toString(),
                contentResolver.getType(documentFile.uri)!!.contains("video"),
                documentFile.lastModified()
            )
//            if (!media.isVideo) {
            if (!media.uri.toString().contains(".nomedia"))
                imagesList.add(media)
//            }
        }
        Log.e("HEY: ", "${imagesList}")
        handler.post { refreshAdapter() }
    }

    fun loadVideo() {
        val handler = Handler(Looper.getMainLooper())
        val fromTreeUri = DocumentFile.fromTreeUri(
            this,
            contentResolver.persistedUriPermissions[0].uri
        )
        Log.e("TAG", "loadImagesA30: ${fromTreeUri}")
        imagesList.clear()
        if (fromTreeUri == null) {
            handler.post { }
            return
        }
        val listFiles = fromTreeUri.listFiles()
        if (listFiles.isEmpty()) {
            handler.post { }
            return
        }
        for (documentFile in listFiles) {
            Log.e("TAG", "loadImagesA30: ${documentFile.uri}")
            val media = Media(
                documentFile.uri,
                documentFile.uri.toString(),
                contentResolver.getType(documentFile.uri)!!.contains("video"),
                documentFile.lastModified()
            )
//            if (media.isVideo) {
            if (!media.uri.toString().contains(".nomedia"))
                imagesList.add(media)
//            }
        }
        Log.e("HEY: ", "${imagesList}")
        handler.post { refreshAdapter() }
    }

    private fun refreshAdapter() {
        imagesList.let {
            binding.run {
                viewPagerMedia.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                viewPagerMedia.offscreenPageLimit = 2
                viewPagerMedia.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        this@FullViewWhatsappActivity.position = position
                        if (it[position].isVideo) {
                            fabSetWP.hide()
                            fabSetWP.visibility = View.GONE
                            txtSetWp.visibility = View.GONE
                        }
                    }
                })

                viewPagerMedia.adapter = ImageViewAdapter(this@FullViewWhatsappActivity, it)

                position = intent.getIntExtra("position", 0)
                viewPagerMedia.setCurrentItem(position, false)
            }
        }
    }

    inner class ImageViewAdapter(
        var ctx: Context,
        var itemList: MutableList<Media>
    ) : RecyclerView.Adapter<ImageViewAdapter.VH>() {
        inner class VH(var binding: ItemFullViewBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemFullViewBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = itemList[holder.adapterPosition]

            Glide.with(ctx).load(item.uri)
                .placeholder(R.drawable.error_placeholder).into(holder.binding.imgView)

            holder.binding.imgPlay.visibility = if (item.isVideo) View.VISIBLE else View.GONE

            holder.binding.imgPlay.setOnClickListener {
                if (item.isVideo)
                    ctx.startActivity(
                        Intent(ctx, VideoViewActivity::class.java)
                            .putExtra("path", item.uri.toString())
                    )
            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
//        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}
