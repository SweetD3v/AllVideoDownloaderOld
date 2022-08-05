package com.example.allviddownloader.ui.activities

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityRingtoneBinding
import com.example.allviddownloader.databinding.ItemRingtonesBinding
import com.example.allviddownloader.interfaces.RingToneSelectionListener
import com.example.allviddownloader.models.ringtone.RingtoneModel
import com.example.allviddownloader.utils.apis.RestApi
import com.example.allviddownloader.widgets.MarginItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RingtoneActivity : BaseActivity(), RingToneSelectionListener {
    val binding by lazy { ActivityRingtoneBinding.inflate(layoutInflater) }
    var ringtonesList: MutableList<RingtoneModel.RingToneDetails>? = mutableListOf()

    var pageIndex = 1
    var perPage: Int = 25
    lateinit var ringtonesAdapter: RingtonesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadRingtones()
    }

    private fun loadRingtones() {
        binding.run {
            rvRingtones.layoutManager = LinearLayoutManager(this@RingtoneActivity).apply {
                orientation = RecyclerView.VERTICAL
            }
            rvRingtones.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelSize(
                        R.dimen.rv_space
                    ), 1, RecyclerView.VERTICAL
                )
            )

            ringtonesList = mutableListOf()
            ringtonesAdapter = RingtonesAdapter(this@RingtoneActivity)
            rvRingtones.adapter = ringtonesAdapter
            ringtonesAdapter.updateList(mutableListOf())
            Log.e("TAG", "loadRingtones: ")
            getAllRingtones(pageIndex, perPage)
        }
    }

    private fun getAllRingtones(page: Int, limit: Int) {
        val service = RestApi.newInstance(RestApi.BASE_URL_RINGTONE).service
        val call: Call<RingtoneModel> =
            service.getAllRingtones(RestApi.API_KEY_RINGTONE, page, limit)

        call.enqueue(object : Callback<RingtoneModel> {
            override fun onResponse(call: Call<RingtoneModel>, response: Response<RingtoneModel>) {
                response.body()?.let { body ->
                    body.ringtonesListList?.let { ringtones ->
                        for (ringtone in ringtones) {
                            ringtonesList?.add(
                                RingtoneModel.RingToneDetails(
                                    ringtone.id,
                                    ringtone.name,
                                    ringtone.tags
                                )
                            )
                        }
                    }

                    ringtonesList?.let { ringtones ->
                        val startIndex = ringtonesAdapter.itemCount + 1
                        ringtonesAdapter.updateList(ringtones)
                        ringtonesAdapter.notifyItemRangeInserted(
                            startIndex,
                            ringtonesAdapter.itemCount
                        )
                    }
                }
                binding.progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<RingtoneModel>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                ringtonesList = mutableListOf()
            }
        })
    }

    class RingtonesAdapter(
        var ctx: Context
    ) :
        RecyclerView.Adapter<RingtonesAdapter.VH>() {
        lateinit var wallpapers: MutableList<RingtoneModel.RingToneDetails>

        var ringToneSelectionListener: RingToneSelectionListener? = null

        fun updateList(wallpapers: MutableList<RingtoneModel.RingToneDetails>) {
            this.wallpapers = wallpapers
        }

        class VH(var binding: ItemRingtonesBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemRingtonesBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val ringtone = wallpapers[holder.adapterPosition]
            holder.binding.run {
                txtTitle.text = ringtone.name
            }
            holder.itemView.setOnClickListener {
                ringToneSelectionListener?.onRingToneSelected(ringtone.id.toString())
//                ctx.startActivity(
//                    Intent(ctx, FullViewActivity::class.java)
//                        .putExtra("position", holder.adapterPosition)
//                        .putExtra(
//                            "type",
//                            if (wallpapers[holder.adapterPosition].isVideo) "video"
//                            else "photo"
//                        )
//                )
            }
        }

        override fun getItemCount(): Int {
            return wallpapers.size
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onRingToneSelected(url: String) {

        playMediaFromURL(url)
    }

    lateinit var mediaPlayer: MediaPlayer
    fun playMediaFromURL(url: String) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}