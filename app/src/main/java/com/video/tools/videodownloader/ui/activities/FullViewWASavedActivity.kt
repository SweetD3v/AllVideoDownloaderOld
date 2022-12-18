package com.video.tools.videodownloader.ui.activities

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityFullviewWaSavedBinding
import com.video.tools.videodownloader.databinding.ItemFullViewBinding
import com.video.tools.videodownloader.models.Media
import com.video.tools.videodownloader.utils.*
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import com.video.tools.videodownloader.utils.*
import java.io.File

class FullViewWASavedActivity : AppCompatActivity() {
    val binding by lazy { ActivityFullviewWaSavedBinding.inflate(layoutInflater) }
    var imagesList = mutableListOf<Media>()
    var position = 0
    val extFile by lazy { File(getExternalFilesDir("Videos"), "video.mp4") }
    var isVideo = false
    var isAllVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.run {

            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@FullViewWASavedActivity, RemoteConfigUtils.adIdBanner(),
                    bannerContainer
                )

            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }

            fabDelete.hide()
            fabShare.hide()
            fabSetWP.hide()
            txtDelete.visibility = View.GONE
            txtShare.visibility = View.GONE
            txtSetWp.visibility = View.GONE

            fabMore.setOnClickListener {
                isAllVisible = if (!isAllVisible) {
                    fabDelete.show()
                    fabShare.show()
                    if (!imagesList[viewPagerMedia.currentItem].isVideo) {
                        fabSetWP.show()
                        txtSetWp.visibility = View.VISIBLE
                    }
                    txtDelete.visibility = View.VISIBLE
                    txtShare.visibility = View.VISIBLE

                    true
                } else {
                    fabDelete.hide()
                    fabShare.hide()
                    fabSetWP.hide()
                    txtDelete.visibility = View.GONE
                    txtShare.visibility = View.GONE
                    txtSetWp.visibility = View.GONE

                    false
                }
            }

            fabDelete.setOnClickListener {
                val builder =
                    AlertDialog.Builder(this@FullViewWASavedActivity, R.style.RoundedCornersDialog)
                        .setTitle("Delete")
                        .setMessage("Are you sure want to delete this file?")
                        .setCancelable(true)
                        .setPositiveButton("Delete") { dialog, _ ->
                            dialog.dismiss()
                            val image = imagesList[binding.viewPagerMedia.currentItem]
                            val file = File(image.path)

                            file.delete()
                            toastShort(this@FullViewWASavedActivity, "File deleted.")
                            imagesList.remove(image)
                            refreshAdapter()
                            if (imagesList.size == 0)
                                finish()
                        }.setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }

                val alertDialog = builder.create()
                alertDialog.show()
            }

            fabShare.setOnClickListener {
                val image = imagesList[binding.viewPagerMedia.currentItem]
                if (contentResolver.getType(image.uri)?.contains("image", true) == true) {
                    val bitmap =
                        if (image.uri.toString().endsWith(".jpg")
                            or image.uri.toString().endsWith(".png")
                        ) getBitmapFromUri(this@FullViewWASavedActivity, image.uri)
                        else getVideoThumbnail(this@FullViewWASavedActivity, image.uri)

                    Log.e("TAG", "onCreateimage: ${image.uri}")
                    shareMediaUri(this@FullViewWASavedActivity, arrayListOf(image.uri))
//                    saveImageTemp(bitmap)
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

        }

        if (intent.hasExtra("type")) {
            isVideo = intent.getStringExtra("type").equals("video")
        }

        binding.fabSetWP.visibility = View.GONE
        loadImages()

        if (extFile.exists())
            extFile.delete()
    }

    private fun saveVideo(uri: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val extFile = File(getExternalFilesDir("Videos"), "video.mp4")
            if (extFile.exists())
                extFile.delete()
            FileUtilsss.copyFileAPI30(
                this@FullViewWASavedActivity,
                uri,
                File(getExternalFilesDir("Videos"), "video.mp4")
            ) { file ->
                Log.e("TAG", "filePath: ${file.absolutePath}")
                FileUtilsss.saveVideoAPI30(
                    this@FullViewWASavedActivity, file,
                    "VID_${System.currentTimeMillis()}",
                    RootDirectoryWhatsappShow
                ) {
                    Toast.makeText(
                        this@FullViewWASavedActivity,
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
                    this@FullViewWASavedActivity,
                    uri,
                    dest
                ) { file1 ->
                    Log.e("TAG", "onCopied: ${file1.absolutePath}")
                    MediaScannerConnection.scanFile(
                        this@FullViewWASavedActivity, arrayOf(
                            file1.absolutePath
                        ), null
                    ) { _: String?, uri: Uri? ->
                        Handler(Looper.getMainLooper())
                            .post {
                                Toast.makeText(
                                    this@FullViewWASavedActivity,
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
                this@FullViewWASavedActivity, bitmap,
                "IMG_${System.currentTimeMillis()}.jpg",
                "image/jpeg",
                RootDirectoryWhatsappShow
            ) {
                Toast.makeText(
                    this@FullViewWASavedActivity,
                    "Saved!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val file1 = FileUtilsss.saveBitmapAsFile(
                this@FullViewWASavedActivity,
                bitmap,
                "IMG_${System.currentTimeMillis()}.jpg"
            )

            file1.let { file ->
                MediaScannerConnection.scanFile(
                    this@FullViewWASavedActivity, arrayOf(
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
                this@FullViewWASavedActivity,
                bitmap,
                "IMG_${System.currentTimeMillis()}.jpg"
            ) { path ->
                val uri = FileProvider.getUriForFile(this, "${packageName}.provider", File(path))
                shareMediaUri(this, arrayListOf(uri))
            }
        } else {
            FileUtilsss.saveBitmapAsFileCache(
                this@FullViewWASavedActivity,
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
            this@FullViewWASavedActivity,
            uri,
            File(cachePathWA, "TEMP_VID_${System.currentTimeMillis()}.mp4")
        ) { path ->
            Log.e("TAG", "filePath: ${path}")
            val uri1 = FileProvider.getUriForFile(this, "${packageName}.provider", path)
            shareMediaUri(this, arrayListOf(uri1))
        }
    }

    fun loadImages() {
        binding.apply {
            val imageListNew = mutableListOf<Media>()
            getMedia(this@FullViewWASavedActivity, RootDirectoryWhatsappShow) { list ->
                for (media in list) {
                    imageListNew.add(media)
                }
                Log.e("TAG", "loadImagesNew: ${imageListNew.size}")
                Log.e("TAG", "loadImages: ${imagesList.size}")
                if (imageListNew.size != imagesList.size) {
                    imagesList = imageListNew
                    refreshAdapter()
                }
            }
        }
    }

    private fun refreshAdapter() {
        imagesList.let {
            binding.run {
                viewPagerMedia.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                viewPagerMedia.offscreenPageLimit = 2
                viewPagerMedia.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        this@FullViewWASavedActivity.position = position
                        if (it[position].isVideo) {
                            fabSetWP.hide()
                            fabSetWP.visibility = View.GONE
                            txtSetWp.visibility = View.GONE
                        }
                    }
                })

                viewPagerMedia.adapter = ImageViewAdapter(this@FullViewWASavedActivity, it)

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
        AdsUtils.clicksCountWA++
        if (AdsUtils.clicksCountWA == 4) {
            AdsUtils.clicksCountWA = 0

            AdsUtils.loadInterstitialAd(
                this,
                RemoteConfigUtils.adIdInterstital(),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        finish()
                    }
                })

            return
        }
        finish()
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}