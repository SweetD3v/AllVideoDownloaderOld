package com.example.allviddownloader.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.allviddownloader.adapters.WAMediaAdapter
import com.example.allviddownloader.databinding.FragmentWaimagesBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.utils.dpToPx
import com.example.allviddownloader.utils.getMediaWA
import com.example.allviddownloader.widgets.MarginItemDecoration


class WAImagesFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }

    companion object {
        var imagesList: MutableList<Media> = mutableListOf()

        open fun newInstance(): WAImagesFragment {
            return WAImagesFragment()
        }

        open fun newInstance(mediaList: MutableList<Media>): WAImagesFragment {
            imagesList = mediaList
            Log.e("TAG", "newInstanceI: ${imagesList.size}")
            return WAImagesFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        if (allPermissionsGranted()) {
//            onPermissionGranted()
//        } else {
//            permissionRequest.launch(permissions.toTypedArray())
//        }
        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            Log.e("TAG", "onPermissionGranted: ${imagesList.size}")
            waMediaAdapter = WAMediaAdapter(ctx, imagesList, binding.rlMain)
            binding.rvWAImages.adapter = waMediaAdapter
            waMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
        }
    }

    lateinit var waMediaAdapter: WAMediaAdapter
    override fun onPermissionGranted() {
        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            Log.e("TAG", "onPermissionGranted: ${imagesList.size}")
            waMediaAdapter = WAMediaAdapter(ctx, imagesList, binding.rlMain)
            binding.rvWAImages.adapter = waMediaAdapter
            waMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
        }
    }

    private fun loadImages() {
        binding.apply {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val imageListNew = mutableListOf<Media>()
            getMediaWA(ctx) { list ->
                for (media in list) {
                    if (!media.isVideo and !media.uri.toString().contains(".nomedia", true)
                    ) {
                        imageListNew.add(media)
                    }
                }
                if (imageListNew.size != imagesList.size) {
                    imagesList = imageListNew
                    val waMediaAdapter = WAMediaAdapter(ctx, imagesList, binding.rlMain)
                    binding.rvWAImages.adapter = waMediaAdapter
                    waMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
                }
            }
        }
    }

    fun refreshImages(){
        waMediaAdapter.notifyItemRangeChanged(0, imagesList.size)
    }

    override fun onBackPressed() {
    }
}