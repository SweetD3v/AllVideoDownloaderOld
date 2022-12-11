package com.example.allviddownloader.ui.activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.allviddownloader.R
import com.example.allviddownloader.databinding.ActivityWallpapersBinding
import com.example.allviddownloader.databinding.CategorySheetBinding
import com.example.allviddownloader.databinding.ItemWallCategoriesBinding
import com.example.allviddownloader.databinding.ItemWallpapersBinding
import com.example.allviddownloader.interfaces.CategorySelectionListener
import com.example.allviddownloader.models.WallModelPixabay
import com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity
import com.example.allviddownloader.utils.*
import com.example.allviddownloader.utils.apis.RestApi
import com.example.allviddownloader.widgets.BSFragmentBuilder
import com.example.allviddownloader.widgets.MarginItemDecoration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WallpapersActivity : FullScreenActivity() {
    val binding by lazy { ActivityWallpapersBinding.inflate(layoutInflater) }
    lateinit var wallpapersList: MutableList<WallModelPixabay.PhotoDetails>
    var lastVisiblesItems = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var loading: Boolean = true
    var mIsLastPage: Boolean = false
    var layoutManager = GridLayoutManager(this@WallpapersActivity, 2)
    var category: String? = "nature"
    var categorySheet: BSFragmentBuilder? = null
    var catSelected = 0
    var reset = true
    val wallType by lazy {
        if (intent.hasExtra("wallType")) intent.getStringExtra("wallType")
        else ""
    }

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

//            AdsUtils.loadBanner(
//                this, binding.bannerContainer,
//                getString(R.string.banner_id_details)
//            )

            AdsUtils.loadNative(
                this@WallpapersActivity,
                getString(R.string.admob_native_id),
                binding.adFrame
            )

            binding.toolbar.rlMain.adjustInsets(this@WallpapersActivity)

            binding.toolbar.imgDownloads.visible()
            binding.toolbar.imgDownloads.setOnClickListener {
                startActivity(
                    Intent(
                        this@WallpapersActivity,
                        MyCreationToolsActivity::class.java
                    ).apply {
                        putExtra(
                            MyCreationToolsActivity.CREATION_TYPE,
                            if (wallType == "wallpapers")
                                "wallpapers"
                            else "status"
                        )
                    })
            }

            loadWallpapers()
        }
    }

    private fun loadWallpapers() {
        binding.run {
            toolbar.imgBack.setOnClickListener {
                onBackPressed()
            }

            rvWallpapers.isNestedScrollingEnabled = false

            rvWallpapers.layoutManager = layoutManager
            rvWallpapers.addItemDecoration(
                MarginItemDecoration(
                    resources.getDimensionPixelSize(
                        R.dimen.rv_space
                    ), 2, RecyclerView.VERTICAL
                )
            )

            wallpapersList = mutableListOf()
            wallpapersAdapter = WallpapersAdapter(this@WallpapersActivity, wallType.toString())
            binding.rvWallpapers.adapter = wallpapersAdapter

            val arr: Array<String>
            if (wallType == "wallpapers") {
                binding.toolbar.txtTitle.text = getString(R.string.wallpapers)
                binding.toolbar.root.background = ContextCompat.getDrawable(
                    this@WallpapersActivity,
                    R.drawable.top_bar_gradient_yellow
                )
                arr = resources.getStringArray(R.array.wallp_arr)
            } else {
                binding.toolbar.txtTitle.text = getString(R.string.status_maker)
                binding.toolbar.root.background = ContextCompat.getDrawable(
                    this@WallpapersActivity,
                    R.drawable.top_bar_gradient_green
                )
                arr = resources.getStringArray(R.array.status_arr)
            }

            for (photo in arr) {
                val photoDetails = WallModelPixabay.PhotoDetails()
                photoDetails.largeImageURL = photo
                wallpapersList.add(photoDetails)
            }

            wallpapersList.shuffle()
            wallpapersAdapter.setList(wallpapersList)

        }
    }

    private fun initCategorySheet(catSheetBinding: CategorySheetBinding) {
        catSheetBinding.run {
            rvWallCategories.layoutManager = GridLayoutManager(this@WallpapersActivity, 2).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            rvWallCategories.addItemDecoration(MarginItemDecoration(dpToPx(4), 2))

            val categoryList = resources.getStringArray(R.array.category_arr)
            val categoryAdapter = CategoryAdapter(this@WallpapersActivity)
            rvWallCategories.adapter = categoryAdapter
            categoryAdapter.categorySelectionListener = object : CategorySelectionListener {
                override fun onCategorySelected(position: Int, category: String) {
                    pageIndex = 1
                    catSelected = position
                    this@WallpapersActivity.category = category
                    wallpapersList = mutableListOf()
                    reset = true
                    getNewWallpapers(category, pageIndex, perPage)
                    categorySheet?.dismiss()
                }
            }
            categoryAdapter.setList(categoryList.toMutableList())
            categoryAdapter.setCatSelectedPos(catSelected)
        }
    }

    private fun getNewWallpapers(category: String, page: Int, limit: Int) {
        loading = true
        binding.progressBar.visibility = View.VISIBLE
        Log.e("TAG", "getNewWallpapers: $category")
        val service = RestApi.newInstance(RestApi.BASE_URL_WALLPAPER_PIXABAY).service
        val call: Call<WallModelPixabay> =
            service.getAllWallpapersPixabay(
                RestApi.API_KEY_WALLPAPERS_PIXABAY,
                category,
                page,
                limit
            )
        call.enqueue(object : Callback<WallModelPixabay> {
            override fun onResponse(
                call: Call<WallModelPixabay>,
                response: Response<WallModelPixabay>
            ) {
                Log.e("TAG", "onResponse: ${response}")
                response.body()?.let { body ->
                    body.hits?.let { photosDetails ->
                        for (picDetails in photosDetails) {
                            Log.e("TAG", "onResponse: ${picDetails.previewURL}")
                            wallpapersList = photosDetails
                        }
                    }

                    Log.e("TAG", "onPostExecute: ${wallpapersList.size}")
                    wallpapersList.let { wallpapers ->
                        wallpapers.shuffle()
                        if (reset) {
                            reset = false
                            wallpapersAdapter.setList(wallpapers)
                        } else if (pageIndex != 1) {
                            wallpapersAdapter.addAll(wallpapers)
                        } else {
                            wallpapersAdapter.setList(wallpapers)
                        }

                        loading = false
                        mIsLastPage = pageIndex == 11
                    }
                }
                binding.progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<WallModelPixabay>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                wallpapersList = mutableListOf()
            }
        })

    }

    class WallpapersAdapter(
        var ctx: Context,
        var wallType: String
    ) :
        RecyclerView.Adapter<WallpapersAdapter.VH>() {
        lateinit var wallpapers: MutableList<WallModelPixabay.PhotoDetails>

        fun updateList(wallpapers: MutableList<WallModelPixabay.PhotoDetails>) {
            this.wallpapers = wallpapers
        }

        fun setList(list: MutableList<WallModelPixabay.PhotoDetails>) {
            wallpapers = list
            notifyDataSetChanged()
        }

        fun addAll(newList: MutableList<WallModelPixabay.PhotoDetails>) {
            val lastIndex: Int = wallpapers.size - 1
            wallpapers.addAll(newList)
            notifyItemRangeInserted(lastIndex, newList.size)
        }

        class VH(var binding: ItemWallpapersBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemWallpapersBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val wallpaper = wallpapers[holder.adapterPosition]
            Log.e("TAG", "onBindViewHolder: ${wallpaper.largeImageURL}")
            Glide.with(ctx).load(wallpaper.largeImageURL)
                .centerCrop()
                .into(holder.binding.ivThumbnail)

            holder.itemView.setOnClickListener {
                ctx.startActivity(
                    Intent(ctx, WallpapersDetailsActivity::class.java)
                        .putExtra("position", holder.adapterPosition)
                        .putExtra("wallType", wallType)
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


    class CategoryAdapter(
        var ctx: Context
    ) :
        RecyclerView.Adapter<CategoryAdapter.VH>() {
        lateinit var wallpapers: MutableList<String>
        var categorySelectionListener: CategorySelectionListener? = null
        var catSelected = 0

        fun setCatSelectedPos(catSelected: Int) {
            this.catSelected = catSelected
            notifyItemChanged(catSelected)
        }

        fun updateList(wallpapers: MutableList<String>) {
            this.wallpapers = wallpapers
        }

        fun setList(list: MutableList<String>) {
            wallpapers = list
            notifyDataSetChanged()
        }

        fun addAll(newList: MutableList<String>) {
            val lastIndex: Int = wallpapers.size - 1
            wallpapers.addAll(newList)
            notifyItemRangeInserted(lastIndex, newList.size)
        }

        class VH(var binding: ItemWallCategoriesBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemWallCategoriesBinding.inflate(LayoutInflater.from(ctx), parent, false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val category = wallpapers[holder.adapterPosition]
            holder.binding.txtCategory.text = category.toTitleCase()

            if (catSelected == holder.adapterPosition) {
                holder.binding.run {
                    txtCategory.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                    txtCategory.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent))
                }
            } else {
                holder.binding.run {
                    txtCategory.setTextColor(ContextCompat.getColor(ctx, R.color.colorAccent))
                    txtCategory.setBackgroundColor(ContextCompat.getColor(ctx, R.color.white))
                }
            }

            holder.itemView.setOnClickListener {
                if (catSelected != holder.adapterPosition) {
                    notifyItemChanged(catSelected)
                    catSelected = holder.adapterPosition

                    holder.binding.run {
                        txtCategory.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                        txtCategory.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.colorAccent))
                    }

                    categorySelectionListener?.onCategorySelected(holder.adapterPosition, category)
                }
            }
        }

        override fun getItemCount(): Int {
            return wallpapers.size
        }
    }

    override fun onDestroy() {
        AdsUtils.destroyBanner()
        super.onDestroy()
    }
}