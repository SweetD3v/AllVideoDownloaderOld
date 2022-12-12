package com.tools.videodownloader.ui.activities

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tools.videodownloader.R
import com.tools.videodownloader.databinding.ActivityRingtoneBinding
import com.tools.videodownloader.databinding.ItemRingtonesBinding
import com.tools.videodownloader.interfaces.RingToneSelectionListener
import com.tools.videodownloader.models.ringtone.RingPreviewModel
import com.tools.videodownloader.models.ringtone.RingtoneModel
import com.tools.videodownloader.utils.AsyncTaskRunner
import com.tools.videodownloader.utils.FileUtilsss
import com.tools.videodownloader.utils.apis.RestApi
import com.tools.videodownloader.utils.downloader.BasicImageDownloader
import com.tools.videodownloader.utils.originalPath
import com.tools.videodownloader.widgets.MarginItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException


class RingtoneActivity : BaseActivity(), RingToneSelectionListener {
    val binding by lazy { ActivityRingtoneBinding.inflate(layoutInflater) }
    lateinit var ringtonesList: MutableList<RingtoneModel.RingToneDetails>

    var pageIndex = 1
    var perPage: Int = 25
    var loading: Boolean = true
    var mIsLastPage: Boolean = false
    lateinit var ringtonesAdapter: RingtonesAdapter
    var category = "music"
    var layoutManager = LinearLayoutManager(this).apply {
        orientation = RecyclerView.VERTICAL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadRingtones()
    }

    private fun loadRingtones() {
        binding.run {
            imgBack.setOnClickListener {
                onBackPressed()
            }
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

            rvRingtones.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition =
                        layoutManager.findFirstCompletelyVisibleItemPosition()

                    val isNotLoadingAndNotLastPage = !loading && !mIsLastPage
                    val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                    val isValidFirstItem = firstVisibleItemPosition >= 0
                    val totalIsMoreThanVisible = totalItemCount >= WallpapersActivity.perPage
                    val shouldLoadMore =
                        isValidFirstItem && isAtLastItem && totalIsMoreThanVisible && isNotLoadingAndNotLastPage
                    Log.e("TAG", "onScrolled: ${isAtLastItem}")

                    if (shouldLoadMore) {
                        progressBar.visibility = View.VISIBLE
                        WallpapersActivity.pageIndex++
                        getAllRingtones(
                            pageIndex,
                            perPage
                        )
                    }
                }
            })

            ringtonesList = mutableListOf()
            ringtonesAdapter = RingtonesAdapter(this@RingtoneActivity)
            ringtonesAdapter.ringToneSelectionListener = this@RingtoneActivity
            rvRingtones.adapter = ringtonesAdapter
            ringtonesAdapter.updateList(mutableListOf())
            Log.e("TAG", "loadRingtones: ")
            getAllRingtones(pageIndex, perPage)
        }
    }

    private fun getAllRingtones(page: Int, limit: Int) {
        loading = true
        binding.progressBar.visibility = View.VISIBLE
        val service = RestApi.newInstance(RestApi.BASE_URL_RINGTONE).service
        val call: Call<RingtoneModel> =
            service.getAllRingtones(RestApi.API_KEY_RINGTONE, category, page, limit)

        call.enqueue(object : Callback<RingtoneModel> {
            override fun onResponse(call: Call<RingtoneModel>, response: Response<RingtoneModel>) {
                response.body()?.let { body ->
                    body.ringtonesListList?.let { ringtones ->
                        for (ringtone in ringtones) {
                            ringtonesList.add(
                                RingtoneModel.RingToneDetails(
                                    ringtone.id,
                                    ringtone.name,
                                    ringtone.tags
                                )
                            )
                        }
                    }

                    ringtonesList.let { ringtones ->
                        ringtones.shuffle()
                        if (WallpapersActivity.pageIndex != 1) {
                            ringtonesAdapter.addAll(ringtones)
                        } else {
                            ringtonesAdapter.updateList(ringtones)
                        }

                        loading = false
                        mIsLastPage = WallpapersActivity.pageIndex == 11
                        ringtonesList.let { ringtones1 ->
                            val startIndex = ringtonesAdapter.itemCount + 1
                            ringtonesAdapter.updateList(ringtones1)
                            ringtonesAdapter.notifyItemRangeInserted(
                                startIndex,
                                ringtonesAdapter.itemCount
                            )
                        }
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<RingtoneModel>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                ringtonesList = mutableListOf()
            }
        })
    }

    inner class RingtonesAdapter(
        var ctx: Context
    ) :
        RecyclerView.Adapter<RingtonesAdapter.VH>() {
        lateinit var wallpapers: MutableList<RingtoneModel.RingToneDetails>

        var ringToneSelectionListener: RingToneSelectionListener? = null

        var selectedPos = -1

        fun setRingSelectedPos(selectedPos: Int) {
            this.selectedPos = selectedPos
            notifyItemChanged(selectedPos)
        }

        fun setRingSelectedPos(selectedPos: Int, ringToneDetails: RingtoneModel.RingToneDetails) {
            this.selectedPos = selectedPos
            notifyItemChanged(selectedPos, ringToneDetails)
        }

        fun addAll(ringtones: MutableList<RingtoneModel.RingToneDetails>) {
            val lastIndex: Int = wallpapers.size - 1
            wallpapers.addAll(ringtones)
            notifyItemRangeInserted(lastIndex, ringtones.size)
        }

        fun updateList(wallpapers: MutableList<RingtoneModel.RingToneDetails>) {
            this.wallpapers = wallpapers
            notifyDataSetChanged()
        }

        inner class VH(var binding: ItemRingtonesBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemRingtonesBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val ringtone = wallpapers[holder.adapterPosition]
            holder.binding.run {
                txtTitle.text = ringtone.name
            }

            if (selectedPos != -1) {
                if (selectedPos == holder.adapterPosition) {
                    holder.binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                } else {
                    holder.binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                }
            }

            holder.binding.imgMore.setOnClickListener {
                val popupMenu = PopupMenu(this@RingtoneActivity, it)

                // Inflating popup menu from popup_menu.xml file

                // Inflating popup menu from popup_menu.xml file
                popupMenu.menuInflater.inflate(R.menu.menu_ringtone, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_download -> {
                            getRingToneFromId(ringtone.id!!, "storage")
                        }

                        R.id.action_set_as -> {
                            Toast.makeText(
                                this@RingtoneActivity,
                                "Set as Ringtone...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    true
                }
                // Showing the popup menu
                // Showing the popup menu
                popupMenu.show()
            }

            holder.itemView.setOnClickListener {
                if (selectedPos != holder.adapterPosition) {
                    notifyItemChanged(selectedPos, Any())
                    selectedPos = holder.adapterPosition
                    holder.binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                    ringToneSelectionListener?.onRingToneSelected(ringtone.id!!)
                } else {
                    stopMediaPlayer()
                    holder.binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                }
                holder.itemView.setOnClickListener {
                    ringToneSelectionListener?.onRingToneSelected(ringtone.id!!)
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
        }

        override fun getItemCount(): Int {
            return wallpapers.size
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onRingToneSelected(id: Long) {
        Log.e("TAG", "onRingToneSelected: $id")
        getRingToneFromId(id, "temp")
    }

    var id: Long = 0
    private fun getRingToneFromId(id: Long, downloadType: String) {
        if (this.id != id) {
            this.id = id

            val dialog = AsyncTaskRunner.MyProgressDialog(this)
            dialog.showDialog("Loading...", false)
            stopMediaPlayer()

            val service = RestApi.newInstance(RestApi.BASE_URL_RINGTONE).service
            val call: Call<RingPreviewModel> =
                service.getRingtoneFromId(id, RestApi.API_KEY_RINGTONE)
            call.enqueue(object : Callback<RingPreviewModel> {
                override fun onResponse(
                    call: Call<RingPreviewModel>,
                    response: Response<RingPreviewModel>
                ) {
                    response.body()?.let { body ->

                        if (downloadType == "temp") {
                            BasicImageDownloader(this@RingtoneActivity).saveRingtoneToTemp(
                                body.previews!!.previewLQMp3!!,
                                File(cacheDir, "temp_ringtone"),
                                false
                            ) { uri ->
                                dialog.dismissDialog()
                                playMediaFromURL(uri.toString())
                            }
                        } else {
                            BasicImageDownloader(this@RingtoneActivity).saveRingtoneToTemp(
                                body.previews!!.previewLQMp3!!,
                                File(cacheDir, "temp_ringtone"),
                                false
                            ) { uri ->
                                dialog.dismissDialog()
                                val dest =
                                    File(
                                        originalPath,
                                        "RINGTONE_${System.currentTimeMillis()}.mp3"
                                    )
                                FileUtilsss.copyFileAPI30(this@RingtoneActivity, uri, dest) {
                                    Toast.makeText(
                                        this@RingtoneActivity,
                                        "Ringtone downloaded.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

//                            BasicImageDownloader(this@RingtoneActivity)
//                                .downloadRingtone(body.previews!!.previewLQMp3!!, true) {
//                                    Toast.makeText(
//                                        this@RingtoneActivity,
//                                        "Ringtone downloaded.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
                        }
                    }
                }

                override fun onFailure(call: Call<RingPreviewModel>, t: Throwable) {

                }
            })
        } else {
            stopMediaPlayer()
        }
    }

    var mediaPlayer: MediaPlayer? = MediaPlayer()

    fun playMediaFromURL(url: String?) {
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopMediaPlayer() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
        } catch (e: Exception) {
            Log.e("TAG", "stopMediaPlayerException: ${e.message}")
        }
    }

    override fun onPause() {
        stopMediaPlayer()
        super.onPause()
    }
}