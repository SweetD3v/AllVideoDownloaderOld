package com.example.allviddownloader.collage_maker.ui.activities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaScannerConnection
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.example.allviddownloader.R
import com.example.allviddownloader.collage_maker.features.college.CollageLayout
import com.example.allviddownloader.collage_maker.features.college.CollegeLayoutParser
import com.example.allviddownloader.collage_maker.features.college.adapter.CollegeAdapter
import com.example.allviddownloader.collage_maker.ui.ToolType
import com.example.allviddownloader.collage_maker.ui.ToolType.*
import com.example.allviddownloader.collage_maker.ui.adapters.*
import com.example.allviddownloader.collage_maker.ui.fragments.FilterDialogFragment
import com.example.allviddownloader.collage_maker.ui.fragments.PicsartCropDialogFragment
import com.example.allviddownloader.collage_maker.ui.interfaces.FilterListener
import com.example.allviddownloader.collage_maker.ui.interfaces.Sticker_interfce
import com.example.allviddownloader.collage_maker.utils.FileUtils
import com.example.allviddownloader.collage_maker.utils.SystemUtils
import com.example.allviddownloader.collage_maker.utils.UtilsFilter
import com.example.allviddownloader.databinding.ActivityCollageBinding
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.utils.toastShort
import com.steelkiwi.cropiwa.AspectRatio
import org.wysaid.nativePort.CGENativeLibrary.LoadImageCallback
import java.io.File
import java.io.IOException

class CollageViewActivity : BaseActivity(), EditingToolsAdapter.OnItemSelected,
    AspectRatioPreviewAdapter.OnNewSelectedListener,
    CollegeBGAdapter.BackgroundChangeListener, FilterListener,
    PicsartCropDialogFragment.OnCropPhoto,
    FilterDialogFragment.OnFilterSavePhoto,
    PieceToolsAdapter.OnPieceFuncItemSelected, CollegeAdapter.OnItemClickListener,
    Sticker_interfce {
    val binding by lazy { ActivityCollageBinding.inflate(layoutInflater) }

    private val instance: CollageViewActivity? = null
    var puzzle: CollageViewActivity? = null
    var currentAspect: AspectRatio? = null
    var currentBackgroundState: CollegeBGAdapter.SquareView? = null
    var currentMode: ToolType? = null
    var deviceWidth = 0
    var lstBitmapWithFilter: MutableList<Bitmap> = ArrayList()
    var lstOriginalDrawable: MutableList<Drawable> = ArrayList()
    var lstPaths: List<String>? = null
    var pieceBorderRadius = 0f
    var piecePadding = 0f
    private val pieceToolsAdapter = PieceToolsAdapter(this)

    var collegeLayout: CollageLayout? = null

    var targets: List<Target> = ArrayList()
    var collageViewActivity: CollageViewActivity? = null

    var builder: AlertDialog.Builder? = null
    var view: View? = null
    var isSaveDialog = false

    var singleImagePicker = registerForActivityResult(
        GetContent()
    ) { uri -> replaceCurrentPic(SystemUtils.getRealPathFromUri(this@CollageViewActivity, uri)) }

    private val mEditingToolsAdapter = EditingToolsAdapter(this, true)
    var mLoadImageCallback: LoadImageCallback = object : LoadImageCallback {
        override fun loadImage(str: String, obj: Any): Bitmap? {
            return try {
                BitmapFactory.decodeStream(assets.open(str))
            } catch (io: IOException) {
                io.printStackTrace()
                return null
            }
        }

        override fun loadImageOK(bitmap: Bitmap, obj: Any) {
            bitmap.recycle()
        }
    }

    fun slideUpSaveView() {
        binding.toolbar.toolbar.visibility = View.GONE
    }

    fun slideDownSaveView() {
        binding.toolbar.toolbar.visibility = View.VISIBLE
    }

    fun slideUp(view: View) {
        ObjectAnimator.ofFloat(view, "translationY", *floatArrayOf(view.height.toFloat(), 0.0f))
            .start()
    }

    fun slideDown(view: View) {
        ObjectAnimator.ofFloat(view, "translationY", 0.0f, view.height.toFloat()).start()
    }

    var onClickListener = View.OnClickListener { view: View ->
        when (view.id) {
            R.id.imgCloseBackground, R.id.imgCloseFilter, R.id.imgCloseLayout, R.id.imgCloseSticker, R.id.imgCloseText -> {
                slideDownSaveView()
                onBackPressed()
            }
            R.id.imgSaveBackground -> {
                slideDown(binding.changeBackgroundLayout)
                slideUp(binding.rvConstraintTools)
                slideDownSaveView()
                showDownFunction()
                binding.puzzleView.setLocked(true)
                binding.puzzleView.setTouchEnable(true)
                if (binding.puzzleView.backgroundResourceMode === 0) {
                    currentBackgroundState!!.isColor = true
                    currentBackgroundState!!.isBitmap = false
                    currentBackgroundState!!.drawableId =
                        (binding.puzzleView.background as ColorDrawable).color
                    currentBackgroundState!!.drawable = null
                } else if (binding.puzzleView.backgroundResourceMode === 1) {
                    currentBackgroundState!!.isColor = false
                    currentBackgroundState!!.isBitmap = false
                    currentBackgroundState!!.drawable = binding.puzzleView.background
                } else {
                    currentBackgroundState!!.isColor = false
                    currentBackgroundState!!.isBitmap = true
                    currentBackgroundState!!.drawable = binding.puzzleView.background
                }
                currentMode = NONE
            }
            R.id.imgSaveFilter -> {
                slideDown(binding.filterLayout)
                slideUp(binding.rvConstraintTools)
                currentMode = NONE
                slideDownSaveView()
                binding.puzzleView.setTouchEnable(true)
            }
            R.id.imgSaveLayout -> {
                slideUp(binding.rvConstraintTools)
                slideDown(binding.changeLayoutLayout)
                slideDownSaveView()
                showDownFunction()
                collegeLayout = binding.puzzleView.collegeLayout
                pieceBorderRadius = binding.puzzleView.pieceRadian
                piecePadding = binding.puzzleView.piecePadding
                binding.puzzleView.setLocked(true)
                binding.puzzleView.setTouchEnable(true)
                currentAspect = binding.puzzleView.aspectRatio
                currentMode = NONE
            }
            R.id.imgSaveSticker -> {
                binding.puzzleView.setHandlingSticker(null)
                slideDown(binding.stickerLayout)
                slideUp(binding.rvConstraintTools)
                slideDownSaveView()
                binding.puzzleView.setLocked(true)
                binding.puzzleView.setTouchEnable(true)
                currentMode = NONE
                binding.stickerGridFragmentContainer.visibility = View.GONE
            }
            R.id.imgSaveText -> {
                binding.puzzleView.setHandlingSticker(null)
                binding.puzzleView.setLocked(true)
                binding.addNewText.visibility = View.GONE
                slideDown(binding.textControl)
                slideUp(binding.rvConstraintTools)
                slideDownSaveView()
                binding.puzzleView.setLocked(true)
                binding.puzzleView.setTouchEnable(true)
                currentMode = NONE
            }
            R.id.tv_blur -> {
                selectBackgroundBlur()
            }
            R.id.tv_change_border -> {
                selectBorderTool()
            }
            R.id.tv_change_layout -> {
                selectLayoutTool()
            }
            R.id.tv_change_ratio -> {
                selectRadiusTool()
            }
            R.id.tv_color -> {
                selectBackgroundColorTab()
            }
            R.id.tv_radian -> {
                selectBackgroundGradientTab()
            }
            else -> {}
        }
    }

    override fun onToolSelected(toolType: ToolType?) {
        currentMode = toolType
        when (toolType) {
            LAYOUT -> {
                collegeLayout = binding.puzzleView.collegeLayout
                currentAspect = binding.puzzleView.aspectRatio
                pieceBorderRadius = binding.puzzleView.pieceRadian
                piecePadding = binding.puzzleView.piecePadding
                binding.puzzleList.scrollToPosition(0)
                (binding.puzzleList.adapter as CollegeAdapter?)!!.setSelectedIndex(-1)
                binding.puzzleList.adapter!!.notifyDataSetChanged()
                binding.radioLayout.scrollToPosition(0)
                (binding.radioLayout.adapter as AspectRatioPreviewAdapter?)!!.setLastSelectedView(-1)
                binding.radioLayout.adapter!!.notifyDataSetChanged()
                selectLayoutTool()
                slideUpSaveView()
                slideUp(binding.changeLayoutLayout)
                slideDown(binding.rvConstraintTools)
                showUpFunction(binding.changeLayoutLayout)
                binding.puzzleView.setLocked(false)
                binding.puzzleView.setTouchEnable(false)
                return
            }
            BORDER -> {
                collegeLayout = binding.puzzleView.collegeLayout
                currentAspect = binding.puzzleView.aspectRatio
                pieceBorderRadius = binding.puzzleView.pieceRadian
                piecePadding = binding.puzzleView.piecePadding
                binding.puzzleList.scrollToPosition(0)
                (binding.puzzleList.adapter as CollegeAdapter?)!!.setSelectedIndex(-1)
                binding.puzzleList.adapter!!.notifyDataSetChanged()
                binding.radioLayout.scrollToPosition(0)
                (binding.radioLayout.adapter as AspectRatioPreviewAdapter?)!!.setLastSelectedView(-1)
                binding.radioLayout.adapter!!.notifyDataSetChanged()
                selectBorderTool()
                slideUpSaveView()
                slideUp(binding.changeLayoutLayout)
                slideDown(binding.rvConstraintTools)
                showUpFunction(binding.changeLayoutLayout)
                binding.puzzleView.setLocked(false)
                binding.puzzleView.setTouchEnable(false)
                return
            }
            RATIO -> {
                collegeLayout = binding.puzzleView.collegeLayout
                currentAspect = binding.puzzleView.aspectRatio
                pieceBorderRadius = binding.puzzleView.pieceRadian
                piecePadding = binding.puzzleView.piecePadding
                binding.puzzleList.scrollToPosition(0)
                (binding.puzzleList.adapter as CollegeAdapter?)!!.setSelectedIndex(-1)
                binding.puzzleList.adapter!!.notifyDataSetChanged()
                binding.radioLayout.scrollToPosition(0)
                (binding.radioLayout.adapter as AspectRatioPreviewAdapter?)!!.setLastSelectedView(-1)
                binding.radioLayout.adapter!!.notifyDataSetChanged()
                selectRadiusTool()
                slideUpSaveView()
                slideUp(binding.changeLayoutLayout)
                slideDown(binding.rvConstraintTools)
                showUpFunction(binding.changeLayoutLayout)
                binding.puzzleView.setLocked(false)
                binding.puzzleView.setTouchEnable(false)
                return
            }
            FILTER -> {
                if (lstOriginalDrawable.isEmpty()) {
                    for (drawable in binding.puzzleView.getCollegePieces()) {
                        lstOriginalDrawable.add(drawable.drawable)
                    }
                }
                LoadFilterBitmap().execute()
                slideUpSaveView()
                return
            }
            BACKGROUND -> {
                binding.puzzleView.setLocked(false)
                binding.puzzleView.setTouchEnable(false)
                slideUpSaveView()
                selectBackgroundColorTab()
                slideDown(binding.rvConstraintTools)
                slideUp(binding.changeBackgroundLayout)
                showUpFunction(binding.changeBackgroundLayout)
                if (binding.puzzleView.backgroundResourceMode === 0) {
                    currentBackgroundState!!.isColor = true
                    currentBackgroundState!!.isBitmap = false
                    currentBackgroundState!!.drawableId =
                        (binding.puzzleView.background as ColorDrawable).color
                    return
                } else if (binding.puzzleView.backgroundResourceMode === 2 || binding.puzzleView.background is ColorDrawable) {
                    currentBackgroundState!!.isBitmap = true
                    currentBackgroundState!!.isColor = false
                    currentBackgroundState!!.drawable = binding.puzzleView.background
                    return
                } else if (binding.puzzleView.background is GradientDrawable) {
                    currentBackgroundState!!.isBitmap = false
                    currentBackgroundState!!.isColor = false
                    currentBackgroundState!!.drawable = binding.puzzleView.background
                    return
                } else {
                    return
                }
            }
            else -> {}
        }
    }

    fun selectBackgroundBlur() {
        val arrayList: ArrayList<Drawable> = ArrayList()
        for (drawable in binding.puzzleView.getCollegePieces()) {
            arrayList.add(drawable.drawable)
        }
        val collegeBGAdapter =
            CollegeBGAdapter(applicationContext, this, arrayList as List<Drawable?>)
        collegeBGAdapter.setSelectedSquareIndex(-1)
        binding.backgroundList.adapter = collegeBGAdapter
        binding.backgroundList.visibility = View.VISIBLE
        binding.tvBlur.setBackgroundResource(R.drawable.border_bottom)
        binding.tvBlur.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.radianList.visibility = View.GONE
        binding.tvRadian.setBackgroundResource(0)
        binding.tvRadian.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
        binding.colorList.visibility = View.GONE
        binding.tvColor.setBackgroundResource(0)
        binding.tvColor.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
    }

    fun selectBackgroundColorTab() {
        binding.colorList.visibility = View.VISIBLE
        binding.tvColor.setBackgroundResource(R.drawable.border_bottom)
        binding.tvColor.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.colorList.scrollToPosition(0)
        (binding.colorList.adapter as CollegeBGAdapter?)!!.setSelectedSquareIndex(-1)
        binding.colorList.adapter!!.notifyDataSetChanged()
        binding.radianList.visibility = View.GONE
        binding.tvRadian.setBackgroundResource(0)
        binding.tvRadian.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
        binding.backgroundList.visibility = View.GONE
        binding.tvBlur.setBackgroundResource(0)
        binding.tvBlur.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
    }

    fun selectBackgroundGradientTab() {
        binding.radianList.visibility = View.VISIBLE
        binding.tvRadian.setBackgroundResource(R.drawable.border_bottom)
        binding.tvRadian.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.radianList.scrollToPosition(0)
        (binding.radianList.adapter as CollegeBGAdapter?)!!.setSelectedSquareIndex(-1)
        binding.radianList.adapter!!.notifyDataSetChanged()
        binding.colorList.visibility = View.GONE
        binding.tvColor.setBackgroundResource(0)
        binding.tvColor.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
        binding.backgroundList.visibility = View.GONE
        binding.tvBlur.setBackgroundResource(0)
        binding.tvBlur.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
    }


    fun selectLayoutTool() {
        binding.puzzleList.visibility = View.VISIBLE
        binding.tvChangeLayout.setBackgroundResource(R.drawable.border_bottom)
        binding.tvChangeLayout.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary))
        binding.changeBorder.visibility = View.GONE
        binding.tvChangeBorder.setBackgroundResource(0)
        binding.tvChangeBorder.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
        binding.radioLayout.visibility = View.GONE
        binding.tvChangeRatio.setBackgroundResource(0)
        binding.tvChangeRatio.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
    }


    fun selectRadiusTool() {
        binding.radioLayout.visibility = View.VISIBLE
        binding.tvChangeRatio.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary))
        binding.tvChangeRatio.setBackgroundResource(R.drawable.border_bottom)
        binding.puzzleList.visibility = View.GONE
        binding.tvChangeLayout.setBackgroundResource(0)
        binding.tvChangeLayout.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
        binding.changeBorder.visibility = View.GONE
        binding.tvChangeBorder.setBackgroundResource(0)
        binding.tvChangeBorder.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
    }

    fun selectBorderTool() {
        binding.changeBorder.visibility = View.VISIBLE
        binding.tvChangeBorder.setBackgroundResource(R.drawable.border_bottom)
        binding.tvChangeBorder.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary))
        binding.puzzleList.visibility = View.GONE
        binding.tvChangeLayout.setBackgroundResource(0)
        binding.tvChangeLayout.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
        binding.radioLayout.visibility = View.GONE
        binding.tvChangeRatio.setBackgroundResource(0)
        binding.tvChangeRatio.setTextColor(ContextCompat.getColor(this, R.color.unselected_color))
        binding.skBorderRadius.progress = binding.puzzleView.pieceRadian.toInt()
        binding.skBorder.progress = binding.puzzleView.piecePadding.toInt()
    }

    private fun showUpFunction(view: View) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.puzzleLayout)
        constraintSet.connect(binding.wrapPuzzleView.id, 1, binding.puzzleLayout.id, 1, 0)
        constraintSet.connect(binding.wrapPuzzleView.id, 4, view.id, 3, 0)
        constraintSet.connect(binding.wrapPuzzleView.id, 2, binding.puzzleLayout.id, 2, 0)
        constraintSet.applyTo(binding.puzzleLayout)
    }


    fun showDownFunction() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.puzzleLayout)
        constraintSet.connect(binding.wrapPuzzleView.id, 1, binding.puzzleLayout.id, 1, 0)
        constraintSet.connect(binding.wrapPuzzleView.id, 4, binding.rvConstraintTools.id, 3, 0)
        constraintSet.connect(binding.wrapPuzzleView.id, 2, binding.puzzleLayout.id, 2, 0)
        constraintSet.applyTo(binding.puzzleLayout)
    }

    var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, i: Int, z: Boolean) {
                when (seekBar.id) {
                    R.id.sk_border -> binding.puzzleView.piecePadding = i.toFloat()
                    R.id.sk_border_radius -> binding.puzzleView.pieceRadian = i.toFloat()
                }
                binding.puzzleView.invalidate()
            }
        }

    fun getInstance(): CollageViewActivity? {
        return instance
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun init() {
        deviceWidth = resources.displayMetrics.widthPixels
        binding.rvConstraintTools.adapter = mEditingToolsAdapter
        binding.rvPieceControl.adapter = pieceToolsAdapter
        view = LayoutInflater.from(this).inflate(R.layout.save_dialog, null)
    }

    fun replaceCurrentPic(str: String) {
        loadBitmapFromUri()
            .execute(str)
    }

    fun showLoading(z: Boolean) {
        if (z) {
            window.setFlags(16, 16)
            binding.loadingView.visibility = View.VISIBLE
            return
        }
        window.clearFlags(16)
        binding.loadingView.visibility = View.GONE
    }

    inner class LoadFilterBitmap :
        AsyncTask<Void?, Void?, Void?>() {
        public override fun onPreExecute() {
            showLoading(true)
        }

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg voidArr: Void?): Void? {
            lstBitmapWithFilter.clear()
            lstBitmapWithFilter.addAll(
                UtilsFilter.getLstBitmapWithFilter(
                    ThumbnailUtils.extractThumbnail(
                        (binding.puzzleView.getCollegePieces().get(0)
                            .drawable as BitmapDrawable).bitmap,
                        100,
                        100
                    )
                )
            )
            return null
        }

        public override fun onPostExecute(voidR: Void?) {
            binding.rvFilterView.adapter = AdapterFilterView(
                lstBitmapWithFilter,
                this@CollageViewActivity,
                this@CollageViewActivity,
                UtilsFilter.EFFECT_CONFIGS.asList()
            )
            slideDown(binding.rvConstraintTools)
            slideUp(binding.filterLayout)
            showLoading(false)
            binding.puzzleView.setLocked(false)
            binding.puzzleView.setTouchEnable(false)
        }
    }

    inner class LoadFilterBitmapForCurrentPiece :
        AsyncTask<Void?, List<Bitmap?>?, List<Bitmap>>() {
        public override fun onPreExecute() {
            showLoading(true)
        }

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg voidArr: Void?): List<Bitmap> {
            return UtilsFilter.getLstBitmapWithFilter(
                ThumbnailUtils.extractThumbnail(
                    (binding.puzzleView.getHandlingPiece().drawable as BitmapDrawable).bitmap,
                    100,
                    100
                )
            )
        }

        public override fun onPostExecute(list: List<Bitmap>) {
            showLoading(false)
            if (binding.puzzleView.getHandlingPiece() != null) {
                FilterDialogFragment.show(
                    this@CollageViewActivity,
                    this@CollageViewActivity,
                    (binding.puzzleView.getHandlingPiece().drawable as BitmapDrawable).bitmap,
                    list
                )
            }
        }
    }

    inner class LoadBitmapWithFilter :
        AsyncTask<String?, List<Bitmap?>?, List<Bitmap>>() {
        public override fun onPreExecute() {
            showLoading(true)
        }

        override fun doInBackground(vararg strArr: String?): List<Bitmap> {
            val arrayList: ArrayList<Bitmap> = ArrayList()
            for (drawable in lstOriginalDrawable) {
                arrayList.add(
                    UtilsFilter.getBitmapWithFilter(
                        (drawable as BitmapDrawable).bitmap,
                        strArr[0]
                    )
                )
            }
            return arrayList
        }

        public override fun onPostExecute(list: List<Bitmap>) {
            for (i in list.indices) {
                val bitmapDrawable = BitmapDrawable(resources, list[i])
                bitmapDrawable.setAntiAlias(true)
                bitmapDrawable.isFilterBitmap = true
                binding.puzzleView.getCollegePieces().get(i).drawable = bitmapDrawable
            }
            binding.puzzleView.invalidate()
            showLoading(false)
        }
    }

    inner class loadBitmapFromUri :
        AsyncTask<String?, Bitmap?, Bitmap?>() {
        public override fun onPreExecute() {
            showLoading(true)
        }

        override fun doInBackground(vararg strArr: String?): Bitmap? {
            return try {
                val fromFile = Uri.fromFile(File(strArr[0]!!))
                val rotateBitmap = SystemUtils.rotateBitmap(
                    MediaStore.Images.Media.getBitmap(contentResolver, fromFile),
                    ExifInterface(contentResolver.openInputStream(fromFile)!!).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                )
                val width = rotateBitmap.width.toFloat()
                val height = rotateBitmap.height.toFloat()
                val max = (width / 1280.0f).coerceAtLeast(height / 1280.0f)
                if (max > 1.0f) Bitmap.createScaledBitmap(
                    rotateBitmap,
                    (width / max).toInt(),
                    (height / max).toInt(),
                    false
                ) else rotateBitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        public override fun onPostExecute(bitmap: Bitmap?) {
            showLoading(false)
            binding.puzzleView.replace(bitmap, "")
        }
    }

    override fun onBackPressed() {
        if (currentMode == null) {
            super.onBackPressed()
            return
        }
        try {
            when (currentMode) {
                LAYOUT, BORDER, RATIO -> {
                    slideDown(binding.changeLayoutLayout)
                    slideUp(binding.rvConstraintTools)
                    slideDownSaveView()
                    showDownFunction()
                    binding.puzzleView.updateLayout(collegeLayout)
                    binding.puzzleView.piecePadding = piecePadding
                    binding.puzzleView.pieceRadian = pieceBorderRadius
                    currentMode = NONE
                    windowManager.defaultDisplay.getSize(Point())
                    onNewAspectRatioSelected(currentAspect!!)
                    binding.puzzleView.aspectRatio = currentAspect
                    binding.puzzleView.setLocked(true)
                    binding.puzzleView.setTouchEnable(true)
                }
                FILTER -> {
                    slideUp(binding.rvConstraintTools)
                    slideDown(binding.filterLayout)
                    binding.puzzleView.setLocked(true)
                    binding.puzzleView.setTouchEnable(true)
                    var i = 0
                    while (i < lstOriginalDrawable.size) {
                        binding.puzzleView.getCollegePieces()[i].drawable = lstOriginalDrawable[i]
                        i++
                    }
                    binding.puzzleView.invalidate()
                    slideDownSaveView()
                    currentMode = NONE
                }
                STICKER -> {
                }
                TEXT -> {
                    if (!binding.puzzleView.stickers.isEmpty()) {
                        binding.puzzleView.stickers.clear()
                        binding.puzzleView.setHandlingSticker(null)
                    }
                    slideDown(binding.textControl)
                    binding.addNewText.visibility = View.GONE
                    binding.puzzleView.setHandlingSticker(null)
                    slideUp(binding.rvConstraintTools)
                    slideDownSaveView()
                    binding.puzzleView.setLocked(true)
                    currentMode = NONE
                    binding.puzzleView.setTouchEnable(true)
                }
                BACKGROUND -> {
                    slideUp(binding.rvConstraintTools)
                    slideDown(binding.changeBackgroundLayout)
                    binding.puzzleView.setLocked(true)
                    binding.puzzleView.setTouchEnable(true)
                    if (currentBackgroundState!!.isColor) {
                        binding.puzzleView.backgroundResourceMode = 0
                        binding.puzzleView.setBackgroundColor(currentBackgroundState!!.drawableId)
                    } else if (currentBackgroundState!!.isBitmap) {
                        binding.puzzleView.backgroundResourceMode = 2
                        binding.puzzleView.background = currentBackgroundState!!.drawable
                    } else {
                        binding.puzzleView.backgroundResourceMode = 1
                        if (currentBackgroundState!!.drawable != null) {
                            binding.puzzleView.background = currentBackgroundState!!.drawable
                        } else {
                            binding.puzzleView.setBackgroundResource(currentBackgroundState!!.drawableId)
                        }
                    }
                    slideDownSaveView()
                    showDownFunction()
                    currentMode = NONE
                }
                PIECE -> {
                    slideDown(binding.rvPieceControl)
                    slideUp(binding.rvConstraintTools)
                    slideDownSaveView()
                    val layoutParams =
                        binding.rvPieceControl.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.bottomMargin = 0
                    binding.rvPieceControl.layoutParams = layoutParams
                    currentMode = NONE
                    binding.puzzleView.setHandlingPiece(null)
                    binding.puzzleView.setPreviousHandlingPiece(null)
                    binding.puzzleView.invalidate()
                    currentMode = NONE
                    return
                }
                NONE -> {
                    isSaveDialog = false
                    saveDiscardDialog(isSaveDialog)
                }
                else -> super.onBackPressed()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    inner class SavePuzzleAsFile :
        AsyncTask<Bitmap?, String?, String?>() {
        public override fun onPreExecute() {
            showLoading(true)
        }

        override fun doInBackground(vararg bitmapArr: Bitmap?): String? {
            val bitmap = bitmapArr[0]
            val bitmap2 = bitmapArr[1]
            val createBitmap =
                Bitmap.createBitmap(bitmap!!.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(createBitmap)
            canvas.drawBitmap(
                bitmap,
                null,
                RectF(0.0f, 0.0f, bitmap.width.toFloat(), bitmap.height.toFloat()),
                null
            )
            canvas.drawBitmap(
                bitmap2!!,
                null,
                RectF(0.0f, 0.0f, bitmap.width.toFloat(), bitmap.height.toFloat()),
                null
            )
            bitmap.recycle()
            bitmap2.recycle()
            val saveBitmapAsFile = FileUtils.saveBitmapAsFile(createBitmap) ?: return null
            return try {
                MediaScannerConnection.scanFile(
                    applicationContext, arrayOf(saveBitmapAsFile.absolutePath), null
                ) { str, uri -> }
                createBitmap.recycle()
                saveBitmapAsFile.absolutePath
            } catch (e: java.lang.Exception) {
                createBitmap.recycle()
                null
            } catch (th: Throwable) {
                createBitmap.recycle()
                throw th
            }
        }

        public override fun onPostExecute(str: String?) {
            showLoading(false)
            toastShort(this@CollageViewActivity, str!!)
        }
    }

    fun saveDiscardDialog(isSaveDialog: Boolean) {
        val ok = view!!.findViewById<TextView>(R.id.btn_okay)
        val cancel = view!!.findViewById<ImageView>(R.id.btn_cancel)
        if (isSaveDialog) {
            (view!!.findViewById<View>(R.id.txtTitle) as TextView).setText(R.string.dialog_save_text)
        } else {
            (view!!.findViewById<View>(R.id.txtTitle) as TextView).text =
                resources.getString(R.string.dialog_discard_text)
        }
        builder = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(true)
        val alertDialog = builder?.create()!!
        alertDialog.show()
        ok.setOnClickListener { arg0: View? ->
            if (alertDialog.isShowing) alertDialog.dismiss()
            if (isSaveDialog) {
                binding.puzzleView.setHandlingSticker(null)
                slideDown(binding.stickerLayout)
                slideUp(binding.rvConstraintTools)
                slideDownSaveView()
                binding.puzzleView.setLocked(true)
                binding.puzzleView.setTouchEnable(true)
                currentMode = NONE
                binding.stickerGridFragmentContainer.visibility = View.GONE
                val createBitmap: Bitmap =
                    FileUtils.createBitmap(binding.puzzleView, 1920)
                val createBitmap2 = binding.puzzleView.createBitmap()
                SavePuzzleAsFile().execute(createBitmap, createBitmap2)
            } else {
                currentMode = null
                finish()
            }
        }
        cancel.setOnClickListener { v: View? ->
            if (alertDialog.isShowing) alertDialog.dismiss()
            binding.puzzleView.setLocked(false)
        }
        alertDialog.setOnDismissListener { dialog1: DialogInterface? ->
            if (view!!.findViewById<View>(R.id.container_main).parent != null) {
                (view!!.findViewById<View>(R.id.container_main)
                    .parent as ViewGroup).removeView(
                    view!!.findViewById(R.id.container_main)
                )
            }
        }
    }

    override fun onNewAspectRatioSelected(aspectRatio: AspectRatio) {
        val defaultDisplay = windowManager.defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        val calculateWidthAndHeight = calculateWidthAndHeight(aspectRatio, point)
        binding.puzzleView.layoutParams =
            ConstraintLayout.LayoutParams(calculateWidthAndHeight[0], calculateWidthAndHeight[1])
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.wrapPuzzleView)
        constraintSet.connect(binding.puzzleView.id, 3, binding.wrapPuzzleView.id, 3, 0)
        constraintSet.connect(binding.puzzleView.id, 1, binding.wrapPuzzleView.id, 1, 0)
        constraintSet.connect(binding.puzzleView.id, 4, binding.wrapPuzzleView.id, 4, 0)
        constraintSet.connect(binding.puzzleView.id, 2, binding.wrapPuzzleView.id, 2, 0)
        constraintSet.applyTo(binding.wrapPuzzleView)
        binding.puzzleView.aspectRatio = aspectRatio
    }

    private fun calculateWidthAndHeight(aspectRatio: AspectRatio, point: Point): IntArray {
        val height = binding.wrapPuzzleView.height
        if (aspectRatio.height > aspectRatio.width) {
            val ratio = (aspectRatio.ratio * height.toFloat()).toInt()
            return if (ratio < point.x) {
                intArrayOf(ratio, height)
            } else intArrayOf(point.x, (point.x.toFloat() / aspectRatio.ratio).toInt())
        }
        val ratio2 = (point.x.toFloat() / aspectRatio.ratio).toInt()
        return if (ratio2 > height) {
            intArrayOf((height.toFloat() * aspectRatio.ratio).toInt(), height)
        } else intArrayOf(point.x, ratio2)
    }

    override fun onItemClick(collegeLayout: CollageLayout, i: Int) {
        val parse: CollageLayout = CollegeLayoutParser.parse(collegeLayout!!.generateInfo())
        collegeLayout.radian = binding.puzzleView.pieceRadian
        collegeLayout.padding = binding.puzzleView.piecePadding
        binding.puzzleView.updateLayout(parse)
    }

    @SuppressLint("StaticFieldLeak")
    override fun onBackgroundSelected(squareView: CollegeBGAdapter.SquareView) {
        if (squareView.isColor) {
            binding.puzzleView.setBackgroundColor(squareView.drawableId)
            binding.puzzleView.backgroundResourceMode = 0
        } else if (squareView.drawable != null) {
            binding.puzzleView.backgroundResourceMode = 2
            object : AsyncTask<Void?, Bitmap?, Bitmap?>() {
                override fun onPreExecute() {
                    showLoading(true)
                }

                override fun doInBackground(vararg voidArr: Void?): Bitmap? {
                    return UtilsFilter.getBlurImageFromBitmap(
                        (squareView.drawable as BitmapDrawable).bitmap,
                        5.0f
                    )
                }

                override fun onPostExecute(bitmap: Bitmap?) {
                    showLoading(false)
                    binding.puzzleView.background =
                        BitmapDrawable(resources, bitmap)
                }
            }.execute()
        } else {
            binding.puzzleView.setBackgroundResource(squareView.drawableId)
            binding.puzzleView.backgroundResourceMode = 1
        }
    }

    override fun onFilterSelected(str: String) {
        LoadBitmapWithFilter().execute(str)
    }

    override fun finishCrop(bitmap: Bitmap?) {
        binding.puzzleView.replace(bitmap, "")
    }

    override fun onSaveFilter(bitmap: Bitmap?) {
        binding.puzzleView.replace(bitmap, "")
    }

    override fun onPieceFuncSelected(toolType: ToolType?) {
        when (toolType) {
            REPLACE_IMG -> {
                singleImagePicker.launch("image/*")
                return
            }
            H_FLIP -> {
                binding.puzzleView.flipHorizontally()
                return
            }
            V_FLIP -> {
                binding.puzzleView.flipVertically()
                return
            }
            ROTATE -> {
                binding.puzzleView.rotate(90.0f)
                return
            }
            CROP -> {
                PicsartCropDialogFragment.show(
                    this,
                    this,
                    (binding.puzzleView.getHandlingPiece().drawable as BitmapDrawable).bitmap
                )
                return
            }
            FILTER -> LoadFilterBitmapForCurrentPiece().execute()
            else -> {

            }
        }
    }

    override fun getsticker() {
    }
}