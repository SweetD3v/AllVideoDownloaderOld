package com.example.allviddownloader.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.allviddownloader.databinding.ItemStatusBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.ui.activities.FullViewWhatsappActivity
import com.example.allviddownloader.utils.MediaDiffCallback
import com.example.allviddownloader.utils.dpToPx


class WAMediaAdapter(
    var ctx: Context,
    var oldMediaList: MutableList<Media>
) :
    RecyclerView.Adapter<WAMediaAdapter.VH>() {

    class VH(var binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        var loadingPosition = 0
    }

    fun updateEmployeeListItems(employees: MutableList<Media>) {
        val diffCallback = MediaDiffCallback(this.oldMediaList, employees)
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(diffCallback)
        this.oldMediaList.clear()
        this.oldMediaList.addAll(employees)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemStatusBinding.inflate(LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val media = oldMediaList[holder.bindingAdapterPosition]
        holder.loadingPosition = position

//        if (holder.loadingPosition == position) {
//            Picasso.get().load(media.uri).into(holder.binding.ivThumbnail)
//        }

        if (media.isVideoFile(ctx)) {
            Glide.with(ctx).load(media.uri)
                .override(holder.binding.root.width, dpToPx(250))
                .into(holder.binding.ivThumbnail)
//            val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", File(media.path))
//            Picasso.get().load(media.uri).into(holder.binding.ivThumbnail)
            holder.binding.imgPlay.visibility = View.VISIBLE
        } else {
            Glide.with(ctx).load(media.uri)
                .override(holder.binding.root.width, dpToPx(250))
                .into(holder.binding.ivThumbnail)
//            Picasso.get().load(media.uri).into(holder.binding.ivThumbnail)
            holder.binding.imgPlay.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            ctx.startActivity(
                Intent(ctx, FullViewWhatsappActivity::class.java)
                    .putExtra("position", holder.bindingAdapterPosition)
                    .putExtra(
                        "type",
                        if (oldMediaList[holder.bindingAdapterPosition].isVideo) "video"
                        else "photo"
                    )
            )
        }
    }

    override fun getItemCount(): Int {
        return oldMediaList.size
    }
}