package com.video.tools.videodownloader.utils

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.video.tools.videodownloader.R

class MyProgressDialog {
    companion object {
        var dialog: Dialog? = null
        fun showDialog(context: Context?, text: String, cancelable: Boolean) {
            dialog = Dialog(context!!, R.style.RoundedCornersDialog)
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            linearLayout.layoutParams = layoutParams
            val progressBar = ProgressBar(context)
            progressBar.indeterminateDrawable.setTint(context.resources.getColor(R.color.colorPrimary))
            val layoutParams_progress = LinearLayout.LayoutParams(dpToPx(48), dpToPx(48))
            layoutParams_progress.gravity = Gravity.CENTER_VERTICAL
            val linearlayoutParams_progress = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            linearLayout.setPadding(40, 48, 24, 48)
            linearlayoutParams_progress.gravity = Gravity.CENTER
            progressBar.layoutParams = layoutParams_progress
            linearLayout.addView(progressBar)
            val textView = TextView(context)
            textView.textSize = 15f
            textView.text = text
            textView.setTextColor(Color.GRAY)
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setPadding(40, 0, 0, 0)
            val linearlayoutParams_text = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            textView.layoutParams = linearlayoutParams_text
            linearLayout.addView(textView)
            dialog?.window?.setContentView(linearLayout, layoutParams)
            dialog?.setCancelable(cancelable)
            if (dialog != null && dialog?.isShowing == false) {
                dialog?.show()
            }
        }

        fun dismissDialog() {
            if (dialog != null && dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        }

        private fun dpToPx(dp: Int): Int {
            return Math.round(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp.toFloat(), Resources.getSystem().displayMetrics
                )
            )
        }
    }
}