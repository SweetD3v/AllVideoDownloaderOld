package com.video.tools.videodownloader.utils

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.annotation.UiThread
import com.video.tools.videodownloader.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


abstract class AsyncTaskRunner<Params, Result>(ctx: Context) {
    private val executors: ExecutorService =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    private var handler: Handler? = null
    private var params: Params? = null
    private var result: Result? = null
    private var showProgress = false
    val myProgressDialog: MyProgressDialog

    init {
        handler = Handler(Looper.getMainLooper())
        myProgressDialog = MyProgressDialog(ctx)
    }

    open fun execute(params: Params, showProgress: Boolean) {
        this.params = params
        this.showProgress = showProgress
        onPreExecute()
        executors.execute {
            result = doInBackground(this.params)
            handler!!.post(object : Runnable {
                override fun run() {
                    onPostExecute(result)
                    handler!!.removeCallbacks(this)
                    handler = null
                }
            })
        }
    }

    abstract fun doInBackground(params: Params?): Result?

    @MainThread
    @UiThread
    open fun onPostExecute(result: Result?) {
        try {
            if (showProgress) myProgressDialog.dismissDialog()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "onPostExecute Error: " + e.localizedMessage)
        }
    }

    @MainThread
    @UiThread
    open fun onPreExecute() {
        if (showProgress) {
            try {
                Log.e("TAG", "onPreExecute: ")
                myProgressDialog.showDialog("Downloading...", false)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TAG", "onPreExecute Error: " + e.localizedMessage)
            }
        }
    }

    open fun shutdown() {
        executors.shutdown()
    }

    open fun isShutdown(): Boolean {
        return executors.isShutdown
    }

    class MyProgressDialog(var context: Context) {
        var dialog: Dialog? = null
        fun showDialog(text: String, cancelable: Boolean) {
            dialog = Dialog(context, R.style.MyProgressDialog)
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
            progressBar.indeterminateDrawable.setTint(context.resources.getColor(R.color.primary))
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
                Log.e("TAG", "showDialog: ")
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
                    dp.toFloat(), Resources.getSystem().getDisplayMetrics()
                )
            )
        }
    }
}