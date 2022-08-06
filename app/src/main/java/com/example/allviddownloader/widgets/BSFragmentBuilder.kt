package com.example.allviddownloader.widgets

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import com.example.allviddownloader.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BSFragmentBuilder : BottomSheetDialogFragment() {
    var fManager: FragmentManager? = null
    var title: String? = null
    private var exploreMode = false
    private var canGoBack: Boolean = false
    private var onSearchClick: OnSearchClick? = null
    private var fullHeight = true
    var layout: View? = null

    fun with(manager: FragmentManager): BSFragmentBuilder {
        val fragment = BSFragmentBuilder()
        fragment.fManager = manager
        return fragment
    }

    fun title(title: String?): BSFragmentBuilder {
        this.title = title
        return this
    }

    fun exploreMode(enabled: Boolean): BSFragmentBuilder {
        exploreMode = enabled
        return this
    }

    fun fullHeight(fullHeight: Boolean): BSFragmentBuilder {
        this.fullHeight = fullHeight
        return this
    }

    fun onSearchClick(callback: OnSearchClick?): BSFragmentBuilder {
        onSearchClick = callback
        return this
    }

    fun setupLayout(
        context: Context?,
        layoutResId: Int
    ): BSFragmentBuilder {
        layout = LayoutInflater.from(context).inflate(layoutResId, null)
        return this
    }

    fun show() {
        fManager?.let { show(it, tag) }
    }

    private fun canGoBack(): Boolean {
        return canGoBack
    }

    interface OnSearchClick {
        fun onSearchApplied()
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        var contentView: View? = null
        if (layout != null) {
            contentView = layout
        }
        dialog.setContentView(contentView!!)
        val layoutParams =
            (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.height = if (fullHeight) {
            WindowManager.LayoutParams.MATCH_PARENT
        } else {
            WindowManager.LayoutParams.WRAP_CONTENT
        }
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
            behavior.state = STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }
}