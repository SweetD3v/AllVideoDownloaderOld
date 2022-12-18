package com.video.tools.videodownloader.ui.mycreation

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.databinding.ActivityMyCreationFullViewBinding
import com.video.tools.videodownloader.databinding.ItemFullViewBinding
import com.video.tools.videodownloader.models.Media
import com.video.tools.videodownloader.ui.activities.VideoViewActivity
import com.video.tools.videodownloader.ui.mycreation.MyCreationToolsActivity.Companion.mediaList
import com.video.tools.videodownloader.utils.*
import com.squareup.picasso.Picasso
import com.video.tools.videodownloader.utils.remote_config.RemoteConfigUtils
import com.video.tools.videodownloader.utils.*
import java.io.File

class MyCreationFullViewActivity : AppCompatActivity() {

    val binding by lazy { ActivityMyCreationFullViewBinding.inflate(layoutInflater) }

    //    var imagesList: MutableList<Media>? = mutableListOf()
    var type: String? = "photo_cmp"
    var position = 0
    val extFile by lazy { File(getExternalFilesDir("Videos"), "video.mp4") }
    var isVideo = false
    var isAllVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        type = intent.getStringExtra(MyCreationToolsActivity.CREATION_TYPE)

        binding.run {

            if (NetworkState.isOnline())
                AdsUtils.loadBanner(
                    this@MyCreationFullViewActivity, RemoteConfigUtils.adIdBanner(),
                    bannerContainer
                )

            fabDelete.hide()
            fabShare.hide()
            fabSetWP.hide()
            txtDelete.visibility = View.GONE
            txtShare.visibility = View.GONE
            txtSetWp.visibility = View.GONE

            imgBack.setOnClickListener {
                onBackPressed()
            }

            fabMore.setOnClickListener {
                isAllVisible = if (!isAllVisible) {
                    fabDelete.show()
                    fabShare.show()
                    if (mediaList?.get(viewPagerMedia.currentItem)?.isVideo == false) {
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
                val builder = AlertDialog.Builder(
                    this@MyCreationFullViewActivity,
                    R.style.RoundedCornersDialog
                )
                    .setTitle("Delete")
                    .setMessage("Are you sure want to delete this file?")
                    .setCancelable(true)
                    .setPositiveButton("Delete") { dialog, _ ->
                        dialog.dismiss()
                        val image = mediaList!![binding.viewPagerMedia.currentItem]
                        val file = File(image.path)

                        StorageHelper.deleteFile(this@MyCreationFullViewActivity, file)
                        toastShort(this@MyCreationFullViewActivity, "File deleted.")
                        mediaList!!.remove(image)
                        refreshAdapter()
                        if (mediaList!!.size == 0)
                            finish()
                    }.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                val alertDialog = builder.create()
                alertDialog.show()
            }

            fabShare.setOnClickListener {
                val image = mediaList!![binding.viewPagerMedia.currentItem]
                if (contentResolver.getType(image.uri)?.contains("image", true) == true) {
                    val bitmap =
                        if (image.uri.toString().endsWith(".jpg")
                            or image.uri.toString().endsWith(".png")
                        ) getBitmapFromUri(this@MyCreationFullViewActivity, image.uri)
                        else getVideoThumbnail(this@MyCreationFullViewActivity, image.uri)

//                    saveImageTemp(bitmap)
                    Log.e("TAG", "shareImage: ${image.uri}")
                    shareMediaUri(this@MyCreationFullViewActivity, arrayListOf(image.uri))
                } else {
                    saveVideoTemp(image.uri)
                }
            }


            fabSetWP.setOnClickListener {
                val image = mediaList!![binding.viewPagerMedia.currentItem]
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
        loadMedia()

        if (extFile.exists())
            extFile.delete()
    }

    fun loadMedia() {
        var file: File? = RootDirectoryInstaDownlaoder
        if (type.equals("photo_cmp")) {
            file = RootDirectoryCompressedPhoto
        } else if (type.equals("video_cmp")) {
            file = RootDirectoryCompressedVideo
        } else if (type.equals("photo_editor")) {
            file = RootDirectoryPhotoEditor
        } else if (type.equals("collage_maker")) {
            file = RootDirectoryCollageMaker
        } else if (type.equals("cartoonify")) {
            file = RootDirectoryCartoonified
        } else if (type.equals("sketchify")) {
            file = RootDirectorySketchified
        } else if (type.equals("photo_filter")) {
            file = RootDirectoryPhotoFilter
        } else if (type.equals("photo_warp")) {
            file = RootDirectoryPhotoWarp
        } else if (type.equals("insta_downloader")) {
            file = RootDirectoryInstaDownlaoder
        } else if (type.equals("fb_downloader")) {
            file = RootDirectoryFBDownlaoder
        } else if (type.equals("all")) {
            file = originalPath
        } else if (type.equals("wallpapers")) {
            file = RootDirectoryWallpapers
        }

        Log.e("TAG", "loadMedia: ${file?.absolutePath}")

        file?.let {
//            getMediaByName(this, it) { imageListNew ->
//                for (media in imageListNew) {
//                    Log.e("TAG", "loadMedia: ${media.path}")
//                }
//                if (mediaList?.size != imageListNew.size) {
//                    mediaList = imageListNew
//
//                    if (type.equals("all"))
//                        mediaList = ArrayList(mediaList?.filter { mediaItem ->
//                            !mediaItem.path.contains("Insta Grid", true)
//                        } ?: arrayListOf())
//
//                    refreshAdapter()
//                }
//            }
            refreshAdapter()
        }
    }

    private fun refreshAdapter() {
        mediaList.let {
            binding.run {
                viewPagerMedia.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                viewPagerMedia.offscreenPageLimit = 2
                viewPagerMedia.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        this@MyCreationFullViewActivity.position = position
                        Log.e("TAG", "refreshAdapter: ${it?.get(position)?.isVideo}")
                        if (it?.get(position)?.isVideo == true) {
                            fabSetWP.hide()
                            fabSetWP.visibility = View.GONE
                            txtSetWp.visibility = View.GONE
                        }
                    }
                })

                viewPagerMedia.adapter = ImageViewAdapter(this@MyCreationFullViewActivity, it!!)

                position = intent.getIntExtra("position", 0)
                viewPagerMedia.setCurrentItem(position, false)
            }
        }
    }

    private fun saveImageTemp(bitmap: Bitmap?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileUtilsss.saveBitmapAsFileCache(
                this@MyCreationFullViewActivity,
                bitmap,
                "IMG_${System.currentTimeMillis()}.jpg"
            ) { path ->
                val uri = FileProvider.getUriForFile(this, "${packageName}.provider", File(path))
                shareMediaUri(this, arrayListOf(uri))
            }
        } else {
            FileUtilsss.saveBitmapAsFileCache(
                this@MyCreationFullViewActivity,
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
            this@MyCreationFullViewActivity,
            uri,
            File(cachePathWA, "TEMP_VID_${System.currentTimeMillis()}.mp4")
        ) { path ->
            Log.e("TAG", "filePath: ${path}")
            val uri1 = FileProvider.getUriForFile(this, "${packageName}.provider", path)
            shareMediaUri(this, arrayListOf(uri1))
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
            val item = itemList[holder.bindingAdapterPosition]

            Log.e("TAG", "onBindViewHolder: ${item.path}")

//            Glide.with(ctx).load(item.uri)
//                .placeholder(R.drawable.error_placeholder).into(holder.binding.imgView)
            Picasso.get().load(item.uri).into(holder.binding.imgView)

            holder.binding.imgPlay.visibility = if (item.isVideo) View.VISIBLE else View.GONE
            holder.binding.rlRoot.setBackgroundColor(
                ContextCompat.getColor(
                    ctx,
                    R.color.black
                )
            )

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
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}