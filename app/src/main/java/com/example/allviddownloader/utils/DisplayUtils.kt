package com.example.allviddownloader.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.media.CamcorderProfile
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.allviddownloader.AllVidApp
import com.example.allviddownloader.R
import kotlin.math.roundToInt

inline fun View.afterMeasured(crossinline block: () -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }
}

fun View.adjustInsets(activity: Activity) {
    ViewCompat.setOnApplyWindowInsetsListener(
        activity.window.decorView
    ) { _, insets ->
        val statusbarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        val navbarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        Log.e("TAG", "adjustInsets: ${statusbarHeight}")
        (this.layoutParams as ViewGroup.MarginLayoutParams).topMargin = statusbarHeight
        insets
    }
}

fun adjustInsetsBoth(activity: Activity, marginTop: (Int) -> Unit, marginBottom: (Int) -> Unit) {
    ViewCompat.setOnApplyWindowInsetsListener(
        activity.window.decorView
    ) { _, insets ->
        val statusbarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        val navbarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
        Log.e("TAG", "adjustInsets: ${statusbarHeight}")
        marginTop(statusbarHeight)
        marginBottom(navbarHeight)
        insets
    }
}

fun View.onWindowInsets(action: (View, WindowInsetsCompat) -> Unit) {
    ViewCompat.requestApplyInsets(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        action(v, insets)
        insets
    }
}

fun setLightStatusBarColor(context: Context, window: Window, colorId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    window.statusBarColor = ContextCompat.getColor(context, colorId)
}

fun setLightStatusBar(view: View, activity: AppCompatActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//        var flags = view.systemUiVisibility
//        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        view.systemUiVisibility = flags
        activity.window.statusBarColor = ResourcesCompat.getColor(
            AllVidApp.getInstance().resources, R.color.statusbar, null
        )
    }
}

fun setDarkStatusBar(view: View, activity: AppCompatActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        activity.window.statusBarColor = ResourcesCompat.getColor(
            AllVidApp.getInstance().resources, R.color.statusbar, null
        )
    }
}

fun setDarkStatusBarColor(activity: AppCompatActivity, color: Int) {
    activity.window.statusBarColor = ResourcesCompat.getColor(
        AllVidApp.getInstance().resources, color, null
    )
}

fun dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(), Resources.getSystem().displayMetrics
    ).roundToInt()
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).roundToInt()
}

fun isTablet(context: Context): Boolean {
    return ((context.resources.configuration.screenLayout
            and Configuration.SCREENLAYOUT_SIZE_MASK)
            >= Configuration.SCREENLAYOUT_SIZE_LARGE)
}

fun getMaxSupportedWidth(context: Context): Int {
    val recordingInfo: RecordingInfo = getRecordingInfo(context)
    return recordingInfo.width
}

fun getMaxSupportedHeight(context: Context): Int {
    val recordingInfo: RecordingInfo = getRecordingInfo(context)
    return recordingInfo.height
}

fun getRecordingInfo(context: Context): RecordingInfo {
    val displayMetrics = DisplayMetrics()
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    wm.defaultDisplay.getRealMetrics(displayMetrics)
    val displayWidth = displayMetrics.widthPixels
    val displayHeight = displayMetrics.heightPixels
    val displayDensity = displayMetrics.densityDpi
    val configuration = context.resources.configuration
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
    val cameraWidth = camcorderProfile?.videoFrameWidth ?: -1
    val cameraHeight = camcorderProfile?.videoFrameHeight ?: -1
    val cameraFrameRate = camcorderProfile?.videoFrameRate ?: 60
    return calculateRecordingInfo(
        displayWidth, displayHeight, displayDensity, isLandscape,
        cameraWidth, cameraHeight, cameraFrameRate, 100
    )
}

class RecordingInfo(val width: Int, val height: Int, val frameRate: Int, val density: Int)

fun calculateRecordingInfo(
    displayWidth: Int,
    displayHeight: Int,
    displayDensity: Int,
    isLandscapeDevice: Boolean,
    cameraWidth: Int,
    cameraHeight: Int,
    cameraFrameRate: Int,
    sizePercentage: Int
): RecordingInfo {
    var displayWidth = displayWidth
    var displayHeight = displayHeight
    displayWidth = displayWidth * sizePercentage / 100
    displayHeight = displayHeight * sizePercentage / 100
    if (cameraWidth == -1 && cameraHeight == -1) {
        return RecordingInfo(displayWidth, displayHeight, cameraFrameRate, displayDensity)
    }
    var frameWidth = if (isLandscapeDevice) cameraWidth else cameraHeight
    var frameHeight = if (isLandscapeDevice) cameraHeight else cameraWidth
    if (frameWidth >= displayWidth && frameHeight >= displayHeight) {
        return RecordingInfo(displayWidth, displayHeight, cameraFrameRate, displayDensity)
    }
    if (isLandscapeDevice) {
        frameWidth = displayWidth * frameHeight / displayHeight
    } else {
        frameHeight = displayHeight * frameWidth / displayWidth
    }
    return RecordingInfo(frameWidth, frameHeight, cameraFrameRate, displayDensity)
}

fun Window.fitSystemWindows() {
    WindowCompat.setDecorFitsSystemWindows(this, false)
}

var View.topMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).topMargin
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = value }
    }

var View.topPadding: Int
    get() = paddingTop
    set(value) {
        updateLayoutParams { setPaddingRelative(paddingStart, value, paddingEnd, paddingBottom) }
    }

var View.bottomMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = value }
    }

var View.endMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> { marginEnd = value }
    }

var View.startMargin: Int
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams).marginStart
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> { marginStart = value }
    }

fun hideKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}