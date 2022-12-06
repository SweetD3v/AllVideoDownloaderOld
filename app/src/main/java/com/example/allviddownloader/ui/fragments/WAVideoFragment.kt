package com.example.allviddownloader.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.allviddownloader.R
import com.example.allviddownloader.adapters.WAMediaAdapter
import com.example.allviddownloader.databinding.FragmentWaimagesBinding
import com.example.allviddownloader.models.Media
import com.example.allviddownloader.utils.addOuterGridSpacing
import com.example.allviddownloader.utils.getMediaWA


class WAVideoFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }
    var videosList = mutableListOf<Media>()

    companion object {
        open fun newInstance(): WAVideoFragment {
            return WAVideoFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
//            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val albumGridSpacing = resources.getDimension(R.dimen.rv_margin)
            rvWAImages.addOuterGridSpacing((albumGridSpacing).toInt())
            rvWAImages.addItemDecoration(WAImagesFragment.GridMarginDecoration(albumGridSpacing.toInt()))
        }
    }

    override fun onResume() {
        super.onResume()

        loadVideos()
    }

    private fun loadVideos() {
        binding.apply {
            val imageListNew = mutableListOf<Media>()
            getMediaWA(ctx) { list ->
                for (media in list) {
                    if (media.isVideo and !media.uri.toString().contains(".nomedia", true)
                    ) {
                        imageListNew.add(media)
                    }
                }
                Log.e("TAG", "loadVideosNew: ${imageListNew.size}")
                Log.e("TAG", "loadVideos: ${videosList.size}")
                if (imageListNew.size != videosList.size) {
                    videosList = imageListNew
                    val waMediaAdapter = WAMediaAdapter(ctx, videosList)
                    binding.rvWAImages.adapter = waMediaAdapter
                    waMediaAdapter.notifyItemRangeChanged(0, videosList.size)
                }
            }
        }
    }

    override fun onBackPressed() {
        requireActivity().finish()
    }
}
