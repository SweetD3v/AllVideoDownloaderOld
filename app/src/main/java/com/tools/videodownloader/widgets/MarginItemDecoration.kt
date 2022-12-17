package com.tools.videodownloader.widgets

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
//            if (parent.getChildAdapterPosition(view) == 0) {
            top = spaceSize / 2
//            }
            left = spaceSize / 2
            right = spaceSize / 2
            bottom = spaceSize
        }
    }
}
