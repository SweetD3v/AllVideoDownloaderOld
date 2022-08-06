package com.example.allviddownloader.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityWallpapersBinding
import com.example.allviddownloader.databinding.ItemWallpapersBinding
import com.example.allviddownloader.models.WallModelPixabay
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.apis.RestApi
import com.example.allviddownloader.widgets.MarginItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WallpapersActivity : AppCompatActivity() {
    val binding by lazy { ActivityWallpapersBinding.inflate(layoutInflater) }
    var wallpapersList: MutableList<WallModelPixabay.PhotoDetails>? = mutableListOf()
    var pastVisiblesItems = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var loading: Boolean = true
    var loadingPage: Int = 0

    companion object {
        var pageIndex = 1
        var perPage: Int = 20
        const val WALLPAPER_ORIGINAL_URL: String = "wallUrl"
    }

    lateinit var wallpapersAdapter: WallpapersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (NetworkState.isOnline()) {
            loadWallpapers()
        }
    }

    private fun loadWallpapers() {
        binding.run {
            rvWallpapers.layoutManager = GridLayoutManager(this@WallpapersActivity, 2)
            rvWallpapers.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelSize(
                        R.dimen.rv_space
                    ), 2, RecyclerView.VERTICAL
                )
            )

            rvWallpapers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) { //check for scroll down
                        visibleItemCount = rvWallpapers.layoutManager!!.childCount
                        totalItemCount = rvWallpapers.layoutManager!!.itemCount
                        pastVisiblesItems =
                            (rvWallpapers.layoutManager!! as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                        if (loading) {
                            if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                                loading = false
                                Log.e("GG", "Last Item Wow !")

//                                if (loadingPage == 0) {
                                pageIndex++
                                loadingPage++
                                progressBar.visibility = View.VISIBLE
                                getNewWallpapers(pageIndex, perPage)
                                loading = true
//                                }
                            }
                        }
                    }
                }
            })

//            nScrollView.isNestedScrollingEnabled = false
//            nScrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
//                override fun onScrollChange(
//                    v: NestedScrollView?,
//                    scrollX: Int,
//                    scrollY: Int,
//                    oldScrollX: Int,
//                    oldScrollY: Int
//                ) {
//                    if (scrollY == v!!.getChildAt(0).measuredHeight - v.measuredHeight) {
//                        // in this method we are incrementing page number,
//                        // making progress bar visible and calling get data method.
//                        pageIndex++
//                        progressBar.visibility = View.VISIBLE
//                        getNewWallpapers(pageIndex, pageIndex)
//                    }
//                }
//
//            })
        }

        wallpapersList = mutableListOf()
        wallpapersAdapter = WallpapersAdapter(this@WallpapersActivity)
        binding.rvWallpapers.adapter = wallpapersAdapter
        wallpapersAdapter.updateList(mutableListOf())
        Log.e("TAG", "loadWallpapers: ")
        getNewWallpapers(pageIndex, perPage)
    }

    private fun getNewWallpapers(page: Int, limit: Int) {
        val service = RestApi.newInstance(RestApi.BASE_URL_WALLPAPER_PIXABAY).service
        val call: Call<WallModelPixabay> =
            service.getAllWallpapersPixabay(RestApi.API_KEY_WALLPAPERS_PIXABAY, page, limit)
        call.enqueue(object : Callback<WallModelPixabay> {
            override fun onResponse(
                call: Call<WallModelPixabay>,
                response: Response<WallModelPixabay>
            ) {
                loadingPage = 0
                Log.e("TAG", "onResponse: ${response}")
                response.body()?.let { body ->
                    body.hits?.let { photosDetails ->
                        for (picDetails in photosDetails) {
                            Log.e("TAG", "onResponse: ${picDetails.previewURL}")
                            wallpapersList = photosDetails
                        }
                    }

                    Log.e("TAG", "onPostExecute: ${wallpapersList?.size}")
                    wallpapersList?.let { wallpapers ->
                        val startIndex = wallpapersAdapter.itemCount + 1
                        wallpapersAdapter.updateList(wallpapers)
                        wallpapersAdapter.notifyItemRangeInserted(
                            startIndex,
                            wallpapersAdapter.itemCount
                        )
                    }
                }
                binding.progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<WallModelPixabay>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                wallpapersList = mutableListOf()
                loadingPage = 0
            }
        })

//        object : AsyncTaskRunner<String, MutableList<WallpaperModel.PhotosDetails>?>(this) {
//            override fun doInBackground(params: String?): MutableList<WallpaperModel.PhotosDetails>? {
//                val service = RestApi.service
//                val call: Call<WallpaperModel> = service.getAllWallpapers(20)
//                call.enqueue(object : Callback<WallpaperModel> {
//                    override fun onResponse(
//                        call: Call<WallpaperModel>,
//                        response: Response<WallpaperModel>
//                    ) {
//                        response.body()?.let { body ->
//                            body.photosDetails?.let { photosDetails ->
//                                wallpapersList = mutableListOf()
//                                for (picDetails in photosDetails) {
//                                    Log.e("TAG", "onResponse: ${picDetails.src?.small}")
//                                    wallpapersList?.add(picDetails)
//                                }
//
//                                onPostExecute(wallpapersList)
//                            }
//                        }
//                    }
//
//                    override fun onFailure(call: Call<WallpaperModel>, t: Throwable) {
//                        wallpapersList = mutableListOf()
//                    }
//                })
//                return wallpapersList
//            }
//
//            override fun onPostExecute(list: MutableList<WallpaperModel.PhotosDetails>?) {
//                super.onPostExecute(list)
//
//                list?.let { wallsList ->
//                    wallpapersList = wallsList
//                    Log.e("TAG", "onPostExecute: ${wallsList.size}")
//                    wallpapersList?.let { wallpapers ->
//                        val wallpapersAdapter =
//                            WallpapersAdapter(this@WallpapersActivity, wallpapers)
//                        binding.rvWallpapers.adapter = wallpapersAdapter
//                    }
//                }
//            }
//        }.execute("", true)
    }

    class WallpapersAdapter(
        var ctx: Context
    ) :
        RecyclerView.Adapter<WallpapersAdapter.VH>() {
        lateinit var wallpapers: MutableList<WallModelPixabay.PhotoDetails>

        fun updateList(wallpapers: MutableList<WallModelPixabay.PhotoDetails>) {
            this.wallpapers = wallpapers
        }

        class VH(var binding: ItemWallpapersBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemWallpapersBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val wallpaper = wallpapers[holder.adapterPosition]
            Log.e("TAG", "onBindViewHolder: ${wallpaper.largeImageURL}")
            Glide.with(ctx).load(wallpaper.previewURL)
                .centerCrop()
                .into(holder.binding.ivThumbnail)

            holder.itemView.setOnClickListener {
                ctx.startActivity(
                    Intent(ctx, WallpapersDetailsActivity::class.java)
                        .putExtra("position", holder.adapterPosition)
                        .putExtra(
                            WALLPAPER_ORIGINAL_URL,
                            wallpapers[holder.adapterPosition].largeImageURL
                        )
                )
            }
        }

        override fun getItemCount(): Int {
            return wallpapers.size
        }
    }

    override fun onBackPressed() {
        finish()
    }
}