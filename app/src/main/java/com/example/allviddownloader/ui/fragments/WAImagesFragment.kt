package com.example.allviddownloader.ui.fragments

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allviddownloader.R
import com.example.allviddownloader.adapters.WAMediaAdapter
import com.example.allviddownloader.databinding.FragmentWaimagesBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.utils.addOuterGridSpacing
import com.example.allviddownloader.utils.getMediaWACoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class WAImagesFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }
    var decorationAdded: Boolean? = false

    var job = Job()
    var ioScope = CoroutineScope(Dispatchers.IO + job)
    var uiScope = CoroutineScope(Dispatchers.Main + job)

    var waMediaAdapter: WAMediaAdapter? = null

    companion object {
        var imagesList: MutableList<Media> = mutableListOf()
        open fun newInstance(): WAImagesFragment {
            return WAImagesFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
//            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val albumGridSpacing = resources.getDimension(R.dimen.rv_margin)
            if (decorationAdded == false) {
                decorationAdded = true
                rvWAImages.addOuterGridSpacing((albumGridSpacing).toInt())
                rvWAImages.addItemDecoration(GridMarginDecoration(albumGridSpacing.toInt()))
            }

            waMediaAdapter = WAMediaAdapter(ctx, imagesList)
            binding.rvWAImages.adapter = waMediaAdapter
        }
    }

    internal class GridMarginDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.left = space / 2
            outRect.top = space / 2
            outRect.right = space / 2
            outRect.bottom = space / 2
        }
    }


    override fun onResume() {
        super.onResume()

//        if (allPermissionsGranted()) {
//            onPermissionGranted()
//        } else {
//            permissionRequest.launch(permissions.toTypedArray())
//        }

        loadImages()
    }

    override fun onPermissionGranted() {
//        loadImages()
    }

    private fun loadImages() {
        binding.apply {
            val imageListNew = mutableListOf<Media>()

            job = Job()
            ioScope = CoroutineScope(Dispatchers.IO + job)
            uiScope = CoroutineScope(Dispatchers.Main + job)

            ioScope.launch {
                getMediaWACoroutine(ctx) { list ->
                    for (media in list) {
                        if (!media.path.contains(".nomedia", true)
                        ) {
                            imageListNew.add(media)
                        }
                        Log.e("TAG", "loadImagesWA: ${media.path}")
                    }
                    uiScope.launch {
                        imagesList = imageListNew
                        waMediaAdapter?.mediaList = imagesList
                        waMediaAdapter?.notifyDataSetChanged()
                    }
                }
            }

//            getMediaWAAll(ctx) { list ->
//                for (media in list) {
//                    if (!media.path.contains(".nomedia", true)
//                    ) {
//                        imageListNew.add(media)
//                    }
//                }
//                if (imageListNew.size != imagesList.size) {
//                    Log.e("TAG", "loadImagesNew: ${imageListNew.size}")
//                    Log.e("TAG", "loadImages: ${imagesList.size}")
//                    imagesList = imageListNew
//                    val waMediaAdapter = WAMediaAdapter(ctx, imagesList)
//                    binding.rvWAImages.adapter = waMediaAdapter
//                    waMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
//                }
//            }
        }
    }

    override fun onBackPressed() {
        requireActivity().finish()
    }
}
