package com.example.allviddownloader.ui.mycreation

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityMyCreationToolsBinding
import com.example.allviddownloader.databinding.ItemMyCreationBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.utils.*
import java.io.File

class MyCreationToolsActivity : BaseActivity() {
    companion object {
        const val CREATION_TYPE = "creation_type"
    }

    var mediaList: MutableList<Media>? = mutableListOf()
    var type: String? = "photo_cmp"
    var decorationAdded: Boolean? = false

    val binding by lazy { ActivityMyCreationToolsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        type = intent.getStringExtra(CREATION_TYPE)

        binding.run {
            if (NetworkState.isOnline()) {
//                AdsUtils.loadBanner(
//                    this@MyCreationToolsActivity, bannerContainer,
//                    getString(R.string.banner_id_details)
//                )

                AdsUtils.loadNative(
                    this@MyCreationToolsActivity,
                    getString(R.string.admob_native_id),
                    adFrame
                )
            }

            imgBack.setOnClickListener { onBackPressed() }
            rvMyCreation.isNestedScrollingEnabled = false
            rvMyCreation.layoutManager = GridLayoutManager(this@MyCreationToolsActivity, 2)
//            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val albumGridSpacing = resources.getDimension(R.dimen.rv_margin)

            if (decorationAdded == false) {
                decorationAdded = true
                rvMyCreation.addOuterGridSpacing((albumGridSpacing).toInt())
                rvMyCreation.addItemDecoration(
                    GridMarginDecoration(
                        albumGridSpacing.toInt()
                    )
                )
            }
        }
    }

    internal class GridMarginDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.left = space / 2
            outRect.top = space / 2
            outRect.right = space / 2
            outRect.bottom = space / 2
        }
    }

    override fun onResume() {
        super.onResume()

        loadMedia()
    }

    fun loadMedia() {
        var file: File? = RootDirectoryInstaDownlaoder
//        if (type.equals("photo_cmp")) {
//            file = RootDirectoryCompressedPhoto
//        } else
        if (type.equals("video_cmp")) {
            file = RootDirectoryCompressedVideo
        } else
//        if (type.equals("photo_editor")) {
//            file = RootDirectoryPhotoEditor
//        } else if (type.equals("video_editor")) {
//            file = RootDirectoryVideoEditor
//        } else
            if (type.equals("collage_maker")) {
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
            }

        Log.e("TAG", "loadMedia: ${file?.exists()}")

        file?.let {
            getMediaByName(this, it) { mediaList ->
                for (media in mediaList) {
                    Log.e("TAG", "loadMedia: ${media.path}")
                }
                if (this.mediaList?.size != mediaList.size) {
                    this.mediaList = mediaList

                    val myCreationAdapter = MyCreationAdapter(this, mediaList)
                    binding.rvMyCreation.adapter = myCreationAdapter
                    myCreationAdapter.notifyItemRangeChanged(0, mediaList.size)
                }
            }
        }
    }

    inner class MyCreationAdapter(
        var ctx: Context,
        var mediaList: MutableList<Media>
    ) : RecyclerView.Adapter<MyCreationAdapter.VH>() {
        inner class VH(var binding: ItemMyCreationBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemMyCreationBinding.inflate(LayoutInflater.from(ctx)))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val mediaItem = mediaList[holder.adapterPosition]
            Glide.with(ctx)
                .load(mediaItem.uri)
                .into(holder.binding.ivThumbnail)

            if (mediaItem.isVideoFile()) {
                holder.binding.imgPlay.visibility = View.VISIBLE
            } else {
                holder.binding.imgPlay.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                ctx.startActivity(
                    Intent(ctx, MyCreationFullViewActivity::class.java)
                        .putExtra(CREATION_TYPE, type)
                        .putExtra("position", holder.adapterPosition)
                )
            }
        }

        override fun getItemCount(): Int {
            return mediaList.size
        }
    }

    override fun onBackPressed() {
        AdsUtils.loadInterstitialAd(
            this,
            getString(R.string.interstitial_id),
            object : AdsUtils.Companion.FullScreenCallback() {
                override fun continueExecution() {
                    finish()
                }
            })
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}