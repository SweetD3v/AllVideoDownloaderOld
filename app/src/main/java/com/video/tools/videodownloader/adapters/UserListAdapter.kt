package com.video.tools.videodownloader.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.interfaces.UserListInterface
import com.video.tools.videodownloader.models.TrayModel
import de.hdodenhof.circleimageview.CircleImageView

class UserListAdapter(var ctx: Context, var trayModelArrayList: MutableList<TrayModel>) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    private var userListInterface: UserListInterface? = null

    fun setClick(userListInterface2: UserListInterface?) {
        userListInterface = userListInterface2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(ctx).inflate(R.layout.item_user_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        trayModelArrayList[holder.adapterPosition].user?.apply {
            holder.real_name.text = full_name
            Glide.with(ctx).load(profile_pic_url)
                .thumbnail(0.2f).into(holder.story_icon)
            holder.RLStoryLayout.setOnClickListener {
                userListInterface?.userListClick(
                    holder.adapterPosition,
                    trayModelArrayList[holder.adapterPosition]
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return trayModelArrayList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var story_icon: CircleImageView
        var real_name: TextView
        var user_name: TextView
        var RLStoryLayout: RelativeLayout

        init {
            story_icon = view.findViewById(R.id.story_icon)
            real_name = view.findViewById(R.id.real_name)
            user_name = view.findViewById(R.id.user_name)
            RLStoryLayout = view.findViewById(R.id.RLStoryLayout)
        }
    }
}