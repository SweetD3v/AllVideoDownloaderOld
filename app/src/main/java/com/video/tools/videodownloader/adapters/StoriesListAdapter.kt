package com.video.tools.videodownloader.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.interfaces.Instagram_Story_Click
import com.video.tools.videodownloader.models.ItemModel
import com.video.tools.videodownloader.ui.activities.ImageViewActivity
import com.video.tools.videodownloader.ui.activities.VideoViewActivity
import com.video.tools.videodownloader.utils.RootDirectoryInstaShow
import java.io.File

class StoriesListAdapter(
    var ctx: Context,
    var storyItemModelList: MutableList<ItemModel>,
    var instagram_story_click: Instagram_Story_Click
) : RecyclerView.Adapter<StoriesListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(ctx).inflate(R.layout.items_whatsapp_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val itemModel: ItemModel = storyItemModelList[i]
        try {
            if (itemModel.media_type == 2) {
                viewHolder.ivPlay.visibility = View.VISIBLE
            } else {
                viewHolder.ivPlay.visibility = View.GONE
            }
            Glide.with(ctx)
                .load(itemModel.image_versions2?.candidates?.get(0)?.url)
                .into(viewHolder.pcw)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (itemModel.media_type == 2) {
            val file: File = File(
                RootDirectoryInstaShow,
                "story_" + storyItemModelList[i].id.toString() + ".mp4"
            )
            if (file.exists()) {
                viewHolder.tv_download.text = "Downloaded"
            } else {
                viewHolder.tv_download.text = "Download"
            }
        } else {
            val file: File = File(
                RootDirectoryInstaShow,
                "story_" + storyItemModelList[i].id.toString() + ".png"
            )
            if (file.exists()) {
                viewHolder.tv_download.text = "Downloaded"
            } else {
                viewHolder.tv_download.text = "Download"
            }
        }
        viewHolder.itemView.setOnClickListener {
            if (storyItemModelList[i].media_type == 2) {
                val intent = Intent(ctx, VideoViewActivity::class.java)
                intent.putExtra("path", storyItemModelList[i].video_versions?.get(0)?.url)
                ctx.startActivity(intent)
            } else {
                val intent = Intent(ctx, ImageViewActivity::class.java)
                intent.putExtra(
                    "path",
                    itemModel.image_versions2?.candidates?.get(0)?.url
                )
                ctx.startActivity(intent)
            }
        }
        viewHolder.tv_download.setOnClickListener { v: View? ->
            instagram_story_click.clickInsta(
                i
            )
        }
    }

    override fun getItemCount(): Int {
        return storyItemModelList.size
    }

    class ViewHolder(itemsWhatsappViewBinding: View) :
        RecyclerView.ViewHolder(itemsWhatsappViewBinding) {
        var pcw: ImageView
        var ivPlay: ImageView
        var tv_download: TextView

        init {
            tv_download = itemsWhatsappViewBinding.findViewById(R.id.tv_download)
            pcw = itemsWhatsappViewBinding.findViewById(R.id.pcw)
            ivPlay = itemsWhatsappViewBinding.findViewById(R.id.iv_play)
            itemView.findViewById<View>(R.id.popup).visibility = View.GONE
        }
    }
}