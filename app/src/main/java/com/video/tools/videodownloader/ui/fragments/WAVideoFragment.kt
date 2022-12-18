package com.video.tools.videodownloader.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.video.tools.videodownloader.R
import com.video.tools.videodownloader.adapters.WAMediaAdapter
import com.video.tools.videodownloader.databinding.FragmentWaimagesBinding
import com.video.tools.videodownloader.models.Media
import com.video.tools.videodownloader.utils.addOuterGridSpacing
import com.video.tools.videodownloader.utils.getMediaWA


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
