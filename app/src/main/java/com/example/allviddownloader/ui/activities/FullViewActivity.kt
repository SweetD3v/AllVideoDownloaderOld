package com.example.allviddownloader.ui.activities

import android.content.Context
import android.content.Intent
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
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityFullviewBinding
import com.example.allviddownloader.databinding.ItemFullViewBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.utils.*
import java.io.File

class FullViewActivity : AppCompatActivity() {
    val binding by lazy { ActivityFullviewBinding.inflate(layoutInflater) }
    val imagesList = mutableListOf<Media>()
    var position = 0
    val extFile by lazy { File(getExternalFilesDir("Videos"), "video.mp4") }
    var isVideo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra("type")) {
            isVideo = intent.getStringExtra("type").equals("video")
        }

        if (!isVideo) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                loadImages()
            } else {
                executeImageOld()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                loadVideo()
            } else {
                executeVideoOld()
            }
        }

        if (extFile.exists())
            extFile.delete()

        binding.fabDownload.setOnClickListener {
            imagesList.let { list ->
                val image = list[binding.viewPagerMedia.currentItem]
                val bitmap =
                    if (image.uri.toString().endsWith(".jpg")
                        or image.uri.toString().endsWith(".png")
                    ) getBitmapFromUri(this, image.uri)
                    else getVideoThumbnail(this, image.uri)

                //                val file = File(
                //                    RootDirectoryWhatsappShow, "IMG_${System.currentTimeMillis()}${
                //                        if (image.uri.toString().endsWith(".jpg")) ".jpg" else ".png"
                //                    }"
                //                )
                //                FileUtilsss.copyFileAPI30(this, image.uri, null, file,
                //                    if (image.uri.toString().endsWith(".jpg")) ".jpg" else ".png") {
                //                    Toast.makeText(
                //                        this@FullViewActivity,
                //                        "Saved!",
                //                        Toast.LENGTH_SHORT
                //                    ).show()
                //                }

                if (image.uri.toString().endsWith(".jpg")
                    or image.uri.toString().endsWith(".png")
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtilsss.saveBitmapAPI30(
                            this, bitmap,
                            "IMG_${System.currentTimeMillis()}.jpg",
                            "image/jpeg",
                            RootDirectoryWhatsappShow
                        ) {
                            Toast.makeText(
                                this@FullViewActivity,
                                "Saved!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val file1 = FileUtilsss.saveBitmapAsFile(
                            this, bitmap, "IMG_${System.currentTimeMillis()}.jpg"
                        )

                        file1.let { file ->
                            MediaScannerConnection.scanFile(
                                this, arrayOf(
                                    file.absolutePath
                                ), null
                            ) { _: String?, _: Uri? ->

                            }
                        }
                    }

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val extFile = File(getExternalFilesDir("Videos"), "video.mp4")
                        if (extFile.exists())
                            extFile.delete()
                        FileUtilsss.copyFileAPI30(
                            this, image.uri, null,
                            File(getExternalFilesDir("Videos"), "video.mp4"), "video/mp4"
                        ) { file ->
                            Log.e("TAG", "filePath: ${file.absolutePath}")
                            FileUtilsss.saveVideoAPI30(
                                this, file,
                                "VID_${System.currentTimeMillis()}",
                                RootDirectoryWhatsappShow
                            ) {
                                Toast.makeText(
                                    this@FullViewActivity,
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
                            Log.e("TAG", "uri: ${imagesList[position].uri}")
//                            copy(file, File("${dest}.mp4"))
                            FileUtilsss.copyFileAPI30(
                                this,
                                imagesList[position].uri,
                                null,
                                dest,
                                "video/mp4"
                            ) { file1 ->
                                Log.e("TAG", "onCopied: ${file1.absolutePath}")
                                MediaScannerConnection.scanFile(
                                    this@FullViewActivity, arrayOf(
                                        file1.absolutePath
                                    ), null
                                ) { _: String?, uri: Uri? ->
                                    Handler(Looper.getMainLooper())
                                        .post {
                                            Toast.makeText(
                                                this@FullViewActivity,
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
            }
        }
    }

    fun executeImageOld() {
        val handler = Handler(Looper.getMainLooper())
        imagesList.clear()
        if (AppUtils.STATUS_DIRECTORY.exists()) {
            val imagesListNew = getMediaQMinus(this, AppUtils.STATUS_DIRECTORY)
            for (media in imagesListNew) {
                if (!media.isVideo) {
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
                if (media.isVideo) {
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
            if (!media.isVideo) {
                if (!media.uri.toString().contains(".nomedia"))
                    imagesList.add(media)
            }
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
            if (media.isVideo) {
                if (!media.uri.toString().contains(".nomedia"))
                    imagesList?.add(media)
            }
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
                        this@FullViewActivity.position = position
                    }
                })

                viewPagerMedia.adapter = ImageViewAdapter(this@FullViewActivity, it)

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
                .placeholder(R.drawable.ic_whatsapp_svg).into(holder.binding.imgView)


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
}
