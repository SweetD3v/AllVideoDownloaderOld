package com.tools.videodownloader.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tools.videodownloader.models.Media
import com.tools.videodownloader.ui.activities.FullViewDownloadsActivity
import com.tools.videodownloader.utils.getVideoThumbnail
import com.tools.videodownloader.databinding.ItemStatusBinding

class AllMediaAdapter(
    var ctx: Context,
    var mediaList: MutableList<Media>
) :
    RecyclerView.Adapter<AllMediaAdapter.VH>() {
    class VH(var binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemStatusBinding.inflate(LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val media = mediaList[holder.adapterPosition]
        Log.e("TAG", "onBindViewHolder: ${media.uri}")
        if (media.isVideo) {
            Glide.with(ctx).load(getVideoThumbnail(ctx, media.uri))
                .into(holder.binding.ivThumbnail)
            holder.binding.imgPlay.visibility = View.VISIBLE
        } else {
            Glide.with(ctx).load(media.uri).into(holder.binding.ivThumbnail)
            holder.binding.imgPlay.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            Log.e("TAG", "onBindViewHolder: ${media.isVideo}")
            ctx.startActivity(
                Intent(ctx, FullViewDownloadsActivity::class.java)
                    .putExtra("position", holder.adapterPosition)
                    .putExtra(
                        "type",
                        if (media.isVideo) "video"
                        else "photo"
                    )
            )
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }
}