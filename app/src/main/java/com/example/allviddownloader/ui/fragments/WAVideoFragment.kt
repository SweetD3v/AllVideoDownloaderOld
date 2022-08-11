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


class WAVideoFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }

    companion object {
        var videosList = mutableListOf<Media>()
        open fun newInstance(): WAVideoFragment {
            return WAVideoFragment()
        }

        open fun newInstance(mediaList: MutableList<Media>): WAVideoFragment {
            videosList = mediaList
            Log.e("TAG", "newInstance: ${videosList.size}")
            return WAVideoFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val waMediaAdapter = WAMediaAdapter(ctx, videosList, binding.rlMain)
            binding.rvWAImages.adapter = waMediaAdapter
            waMediaAdapter.notifyItemRangeChanged(0, videosList.size)
        }
    }

    private fun loadVideos() {
        binding.apply {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))
            val imageListNew = mutableListOf<Media>()
            getMediaWA(ctx) { list ->
                for (media in list) {
                    if (media.isVideo and !media.uri.toString().contains(".nomedia", true)
                    ) {
                        imageListNew.add(media)
                    }
                }
                if (imageListNew.size != videosList.size) {
                    videosList = imageListNew
                    val waMediaAdapter = WAMediaAdapter(ctx, videosList, binding.rlMain)
                    binding.rvWAImages.adapter = waMediaAdapter
                    waMediaAdapter.notifyItemRangeChanged(0, videosList.size)
                }
            }
        }
    }

    override fun onBackPressed() {
    }
}
