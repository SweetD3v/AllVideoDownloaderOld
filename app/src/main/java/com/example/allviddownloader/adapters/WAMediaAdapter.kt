package com.example.allviddownloader.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.allviddownloader.databinding.ItemStatusBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.ui.activities.FullViewWhatsappActivity
import com.example.allviddownloader.utils.getBitmapFromUri
import com.squareup.picasso.Picasso
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class WAMediaAdapter(
    var ctx: Context,
    var mediaList: MutableList<Media>
) :
    RecyclerView.Adapter<WAMediaAdapter.VH>() {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    class VH(var binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        var loadingPosition = 0
        var future: Future<Bitmap>? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemStatusBinding.inflate(LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val media = mediaList[holder.adapterPosition]
        holder.loadingPosition = position;

//        if (holder.loadingPosition == position) {
//            Picasso.get().load(media.uri).into(holder.binding.ivThumbnail)
//        }

        val callback: Callable<Bitmap> = object : Callable<Bitmap> {
            override fun call(): Bitmap {
                var bmp = getBitmapFromUri(ctx, media.uri)
                holder.binding.ivThumbnail.setImageBitmap(bmp)
                return bmp!!
            }
        }

        holder.future = executor.submit(callback)

        if (media.isVideo) {
//            Glide.with(ctx).load(getVideoThumbnail(ctx, media.uri))
//                .into(holder.binding.ivThumbnail)
//            val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", File(media.path))
//            Picasso.get().load(media.uri).into(holder.binding.ivThumbnail)
            holder.binding.imgPlay.visibility = View.VISIBLE
        } else {
//            Glide.with(ctx).load(media.uri).into(holder.binding.ivThumbnail)
//            Picasso.get().load(media.uri).into(holder.binding.ivThumbnail)
            holder.binding.imgPlay.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            ctx.startActivity(
                Intent(ctx, FullViewWhatsappActivity::class.java)
                    .putExtra("position", holder.adapterPosition)
                    .putExtra(
                        "type",
                        if (mediaList[holder.adapterPosition].isVideo) "video"
                        else "photo"
                    )
            )
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }
}