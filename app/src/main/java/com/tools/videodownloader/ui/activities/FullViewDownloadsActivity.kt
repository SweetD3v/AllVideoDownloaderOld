package com.tools.videodownloader.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityFullviewBinding
import com.tools.videodownloader.databinding.ItemFullViewBinding
import com.tools.videodownloader.models.Media
import com.tools.videodownloader.utils.FileUtilsss
import com.tools.videodownloader.utils.RootDirectoryWhatsappShow
import com.tools.videodownloader.utils.getBitmapFromUri
import com.tools.videodownloader.utils.getMedia

class FullViewDownloadsActivity : AppCompatActivity() {
    val binding by lazy { ActivityFullviewBinding.inflate(layoutInflater) }
    val imagesList = mutableListOf<Media>()
    var position = 0
    var isVideo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra("type")) {
            isVideo = intent.getStringExtra("type").equals("video")
        }

        loadMedia()

        binding.fabDownload.setOnClickListener {
            imagesList.let { list ->
                val image = list[binding.viewPagerMedia.currentItem]
                Log.e("TAG", "onCreate: ${image.uri}")
                val bitmap = getBitmapFromUri(this, image.uri)

                FileUtilsss.saveBitmapAPI30(
                    this, bitmap,
                    "IMG_${System.currentTimeMillis()}.jpg",
                    "image/jpeg",
                    RootDirectoryWhatsappShow
                ) {
                    Toast.makeText(
                        this@FullViewDownloadsActivity,
                        "Saved!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun loadMedia() {
        getMedia(this) { list ->
            if (imagesList.size != list.size) {
                for (media in list) {
                    if (isVideo) {
                        if (media.isVideo) {
                            imagesList.add(media)
                        }
                    } else {
                        if (!media.isVideo) {
                            imagesList.add(media)
                        }
                    }
                }

                binding.run {
                    val waMediaAdapter =
                        ImageViewAdapter(this@FullViewDownloadsActivity, imagesList)
                    viewPagerMedia.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                    viewPagerMedia.offscreenPageLimit = 2
                    viewPagerMedia.registerOnPageChangeCallback(object :
                        ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                        }
                    })

                    viewPagerMedia.adapter = waMediaAdapter

                    position = intent.getIntExtra("position", 0)
                    viewPagerMedia.setCurrentItem(position, false)
                }
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
