package com.example.allviddownloader.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ItemStatusBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.ui.activities.FullViewWASavedActivity
import com.example.allviddownloader.utils.getBitmapFromUri
import com.example.allviddownloader.utils.getVideoThumbnail
import com.example.allviddownloader.utils.toastShort
import java.io.File

class WAMediaSavedAdapter(
    var ctx: Context,
    var mediaList: MutableList<Media>
) :
    RecyclerView.Adapter<WAMediaSavedAdapter.VH>() {
    class VH(var binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemStatusBinding.inflate(LayoutInflater.from(ctx), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val media = mediaList[holder.bindingAdapterPosition]
        if (media.path.endsWith(".mp4")) {
            val bmp = getVideoThumbnail(ctx, media.uri)
            Log.e("TAG", "onVid: ${bmp?.width} || ${bmp?.height}")
        }

        Log.e("TAG", "onVid: ${media.uri}")

        if (media.isVideo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Glide.with(ctx).load(media.uri)
                .into(holder.binding.ivThumbnail)
        } else {
            Glide.with(ctx).load(media.uri)
                .into(holder.binding.ivThumbnail)
        }

        if (media.isVideo) {
            holder.binding.imgPlay.visibility = View.VISIBLE
        } else {
            holder.binding.imgPlay.visibility = View.GONE
        }
        holder.binding.imgDelete.visibility = View.VISIBLE

        holder.binding.imgDelete.setOnClickListener {
            val builder = AlertDialog.Builder(ctx, R.style.RoundedCornersDialog)
                .setTitle("Delete")
                .setMessage("Are you sure want to delete this file?")
                .setCancelable(true)
                .setPositiveButton("Delete") { dialog, _ ->
                    dialog.dismiss()
                    val image = mediaList[holder.bindingAdapterPosition]
                    val file = File(image.path)
                    mediaList.remove(image)
                    notifyItemRemoved(holder.bindingAdapterPosition)
                    file.delete()
                    toastShort(ctx, "File deleted.")
                }.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            val alertDialog = builder.create()
            alertDialog.show()
        }

        holder.itemView.setOnClickListener {
            ctx.startActivity(
                Intent(ctx, FullViewWASavedActivity::class.java)
                    .putExtra("position", holder.bindingAdapterPosition)
                    .putExtra(
                        "type",
                        if (mediaList[holder.bindingAdapterPosition].isVideo) "video"
                        else "photo"
                    )
            )
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }
}