package com.tools.videodownloader.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.tools.videodownloader.adapters.AllMediaAdapter
import com.tools.videodownloader.adapters.WAMediaAdapter
import com.tools.videodownloader.databinding.FragmentWaimagesBinding
import com.tools.videodownloader.models.Media
import com.tools.videodownloader.utils.dpToPx
import com.tools.videodownloader.utils.getMedia
import com.tools.videodownloader.widgets.MarginItemDecoration


class AllVideosFragment : BaseFragment<FragmentWaimagesBinding>() {
    override val binding by lazy { FragmentWaimagesBinding.inflate(layoutInflater) }
    var imagesList = mutableListOf<Media>()

    companion object {
        open fun newInstance(): AllVideosFragment {
            return AllVideosFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadVideos()
    }

    private fun loadVideos() {
        binding.run {
            rvWAImages.layoutManager = GridLayoutManager(ctx, 2)
            rvWAImages.addItemDecoration(MarginItemDecoration(dpToPx(8)))

            getMedia(ctx) { list ->
                if (imagesList.size != list.size) {
                    getMedia(ctx) { list ->
                        if (imagesList.size != list.size) {
                            for (media in list) {
                                if (media.isVideo) {
                                    imagesList.add(media)
                                }
                            }
                            val allMediaAdapter = AllMediaAdapter(ctx, imagesList)
                            rvWAImages.adapter = allMediaAdapter
                        }
                    }
                    val waMediaAdapter = WAMediaAdapter(ctx, imagesList)
                    rvWAImages.adapter = waMediaAdapter
                }
            }
        }
    }

    override fun onBackPressed() {
    }
}