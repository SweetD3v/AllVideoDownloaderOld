package com.example.allviddownloader.collage_maker.ui.activities

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaScannerConnection
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allviddownloader.R
import com.example.allviddownloader.collage_maker.features.college.CollegeUtils
import com.example.allviddownloader.collage_maker.features.college.PuzzleLayout
import com.example.allviddownloader.collage_maker.features.college.PuzzleLayoutParser
import com.example.allviddownloader.collage_maker.features.college.PuzzleView
import com.example.allviddownloader.collage_maker.features.college.adapter.PuzzleAdapter
import com.example.allviddownloader.collage_maker.ui.EditingToolType
import com.example.allviddownloader.collage_maker.ui.EditingToolType.*
import com.example.allviddownloader.collage_maker.ui.adapters.AspectRatioPreviewAdapter
import com.example.allviddownloader.collage_maker.ui.adapters.BottomToolsAdapter
import com.example.allviddownloader.collage_maker.ui.adapters.CollegeBGAdapter
import com.example.allviddownloader.collage_maker.ui.adapters.PieceToolsAdapter
import com.example.allviddownloader.collage_maker.ui.fragments.FilterDialogFragment
import com.example.allviddownloader.collage_maker.ui.fragments.PicsartCropDialogFragment
import com.example.allviddownloader.collage_maker.ui.interfaces.FilterListener
import com.example.allviddownloader.collage_maker.ui.interfaces.Sticker_interfce
import com.example.allviddownloader.collage_maker.utils.FileUtils
import com.example.allviddownloader.collage_maker.utils.SystemUtils
import com.example.allviddownloader.collage_maker.utils.UtilsFilter
import com.example.allviddownloader.databinding.ActivityPuzzleBinding
import com.example.allviddownloader.tools.photo_filters.PhotoFiltersUtils
import com.example.allviddownloader.ui.activities.BaseActivity
import com.example.allviddownloader.ui.activities.MainActivity
import com.example.allviddownloader.ui.fragments.HomeFragment
import com.example.allviddownloader.utils.AdsUtils
import com.example.allviddownloader.utils.NetworkState
import com.example.allviddownloader.utils.dpToPx
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.steelkiwi.cropiwa.AspectRatio
import gun0912.tedimagepicker.builder.TedImagePicker.Companion.with
import org.wysaid.nativePort.CGENativeLibrary
import org.wysaid.nativePort.CGENativeLibrary.LoadImageCallback
import java.io.IOException

class CollageViewActivity : BaseActivity(), BottomToolsAdapter.OnItemSelected,
    AspectRatioPreviewAdapter.OnNewSelectedListener,
    CollegeBGAdapter.BackgroundChangeListener, FilterListener,
    PicsartCropDialogFragment.OnCropPhoto,
    FilterDialogFragment.OnFilterSavePhoto,
    PieceToolsAdapter.OnPieceFuncItemSelected, PuzzleAdapter.OnItemClickListener,
    Sticker_interfce {
    val binding by lazy { ActivityPuzzleBinding.inflate(layoutInflater) }
    var isSaved = false

    lateinit var instance: CollageViewActivity
    lateinit var puzzle: CollageViewActivity
    lateinit var changeBackgroundLayout: ConstraintLayout
    lateinit var changeBorder: LinearLayout
    lateinit var changeLayoutLayout: ConstraintLayout
    lateinit var currentAspect: AspectRatio
    lateinit var currentBackgroundState: CollegeBGAdapter.SquareView
    var currentMode: EditingToolType? = null
    var deviceWidth = 0
    lateinit var loadingView: RelativeLayout
    var lstBitmapWithFilter: MutableList<Bitmap> = arrayListOf()
    var lstOriginalDrawable: MutableList<Drawable> = arrayListOf()
    var lstPaths: MutableList<String>? = null
    var isActivityLeft = false

    var pieceBorderRadius = 0f

    var piecePadding = 0f
    private val bottomSubToolsAdapter: PieceToolsAdapter = PieceToolsAdapter(this)

    lateinit var puzzleLayout: PuzzleLayout
    lateinit var puzzleList: RecyclerView

    lateinit var puzzleView: PuzzleView
    lateinit var radiusLayout: RecyclerView
    lateinit var rvBackgroundList: RecyclerView
    lateinit var rvBackgroundColor: RecyclerView
    lateinit var rvBackgroundGradient: RecyclerView

    lateinit var rvPieceControl: RecyclerView
    lateinit var sbChangeBorderRadius: SeekBar
    lateinit var sbChangeBorderSize: SeekBar
    lateinit var toolbar: Toolbar

    var targets: MutableList<Target> = arrayListOf()
    lateinit var tvChangeBackgroundBlur: TextView
    lateinit var tvChangeBackgroundColor: TextView
    lateinit var tvChangeBackgroundGradient: TextView
    lateinit var tvChangeBorder: TextView
    lateinit var tvChangeLayout: TextView
    lateinit var tvChangeRatio: TextView
    lateinit var wrapPuzzleView: ConstraintLayout
    lateinit var puzzleViewActivity: CollageViewActivity

    lateinit var mBottomToolsAdapter: BottomToolsAdapter
    var mLoadImageCallback: LoadImageCallback = object : LoadImageCallback {
        override fun loadImage(str: String, obj: Any): Bitmap? {
            return try {
                BitmapFactory.decodeStream(assets.open(str))
            } catch (io: IOException) {
                null
            }
        }

        override fun loadImageOK(bitmap: Bitmap, obj: Any) {
            bitmap.recycle()
        }
    }

    lateinit var mRvTools: RecyclerView
    lateinit var mainActivity: ConstraintLayout

    var onClickListener = View.OnClickListener { view: View ->
        when (view.id) {
            R.id.imgCloseBackground, R.id.imgCloseLayout -> {
                slideDownSaveView()
                slideDown(changeLayoutLayout)
                onBackPressed()
                return@OnClickListener
            }
            R.id.imgSaveBackground -> {
                slideDown(changeBackgroundLayout)
                slideUp(mRvTools)
                slideDownSaveView()
                showDownFunction()
                puzzleView.setLocked(true)
                puzzleView.setTouchEnable(true)
                if (puzzleView.backgroundResourceMode == 0) {
                    currentBackgroundState.isColor = true
                    currentBackgroundState.isBitmap = false
                    currentBackgroundState.drawableId =
                        (puzzleView.background as ColorDrawable).color
                    currentBackgroundState.drawable = null
                } else if (puzzleView.backgroundResourceMode == 1) {
                    currentBackgroundState.isColor = false
                    currentBackgroundState.isBitmap = false
                    currentBackgroundState.drawable = puzzleView.background
                } else {
                    currentBackgroundState.isColor = false
                    currentBackgroundState.isBitmap = true
                    currentBackgroundState.drawable = puzzleView.background
                }
                currentMode = NONE
                return@OnClickListener
            }
//            R.id.imgSaveFilter -> {
//                slideUp(mRvTools)
//                currentMode = NONE
//                slideDownSaveView()
//                puzzleView.setTouchEnable(true)
//                return@OnClickListener
//            }
            R.id.imgSaveLayout -> {
                slideUp(mRvTools)
                slideDown(changeLayoutLayout)
                slideDownSaveView()
                showDownFunction()
                puzzleLayout = puzzleView.puzzleLayout
                pieceBorderRadius = puzzleView.pieceRadian
                piecePadding = puzzleView.piecePadding
                puzzleView.setLocked(true)
                puzzleView.setTouchEnable(true)
                currentAspect = puzzleView.aspectRatio
                currentMode = NONE
                return@OnClickListener
            }
            R.id.tv_blur -> {
                selectBackgroundBlur()
                return@OnClickListener
            }
            R.id.tv_change_border -> {
                selectBorderTool()
                return@OnClickListener
            }
            R.id.tv_change_layout -> {
                selectLayoutTool()
                return@OnClickListener
            }
            R.id.tv_change_ratio -> {
                selectRadiusTool()
                return@OnClickListener
            }
            R.id.tv_color -> {
                selectBackgroundColorTab()
                return@OnClickListener
            }
            R.id.tv_radian -> {
                selectBackgroundGradientTab()
                return@OnClickListener
            }
            else -> {}
        }
    }

    var onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, i: Int, z: Boolean) {
                when (seekBar.id) {
                    R.id.sk_border -> puzzleView.piecePadding = i.toFloat()
                    R.id.sk_border_radius -> puzzleView.pieceRadian = i.toFloat()
                }
                puzzleView.invalidate()
            }
        }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        requestWindowFeature(1)
        window.setFlags(1024, 1024)
        setContentView(binding.root)
        if (NetworkState.isOnline()) AdsUtils.loadBanner(
            this, getString(R.string.banner_id_details),
            binding.bannerContainer
        )
        toolbar = findViewById(R.id.toolbar)
        (toolbar.findViewById<View>(R.id.app_title) as TextView).text = ""
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener({ v: View? -> onBackPressed() })
        PhotoFiltersUtils.string = "XYZ"
        puzzleViewActivity = this
        deviceWidth = resources.displayMetrics.widthPixels
        loadingView = findViewById(R.id.loadingView)
        puzzleView = findViewById(R.id.puzzle_view)
        wrapPuzzleView = findViewById(R.id.wrapPuzzleView)
        mRvTools = findViewById(R.id.rvConstraintTools)
        mRvTools.layoutManager = GridLayoutManager(this, 4)
        mBottomToolsAdapter =
            BottomToolsAdapter(
                true
            )
        mBottomToolsAdapter.setmOnItemSelected(this@CollageViewActivity)
        mRvTools.adapter = mBottomToolsAdapter
        rvPieceControl = findViewById(R.id.rvPieceControl)
        rvPieceControl.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvPieceControl.adapter = bottomSubToolsAdapter
        sbChangeBorderSize = findViewById(R.id.sk_border)
        sbChangeBorderSize.setOnSeekBarChangeListener(onSeekBarChangeListener)
        sbChangeBorderRadius = findViewById(R.id.sk_border_radius)
        sbChangeBorderRadius.setOnSeekBarChangeListener(onSeekBarChangeListener)
        lstPaths = intent.getStringArrayListExtra(HomeFragment.KEY_DATA_RESULT)
        puzzleLayout = CollegeUtils.getPuzzleLayouts(lstPaths!!.size)[0]
        puzzleView.puzzleLayout = puzzleLayout
        puzzleView.setTouchEnable(true)
        puzzleView.setNeedDrawLine(false)
        puzzleView.setNeedDrawOuterLine(false)
        puzzleView.setLineSize(4)
        puzzleView.piecePadding = 6.0f
        puzzleView.pieceRadian = 15.0f
        puzzleView.setLineColor(ContextCompat.getColor(this, R.color.white))
        puzzleView.setSelectedLineColor(ContextCompat.getColor(this, R.color.colorAccent))
        puzzleView.setHandleBarColor(ContextCompat.getColor(this, R.color.colorAccent))
        puzzleView.setAnimateDuration(300)
        puzzleView.setOnPieceSelectedListener { puzzlePiece, i ->
            Log.e("TAG", "pieceSelected: ")
            val layoutParams =
                rvPieceControl.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.bottomMargin = dpToPx(applicationContext, 10)
            rvPieceControl.layoutParams = layoutParams
            currentMode = PIECE
            slideDown(mRvTools)
            slideUp(rvPieceControl)
        }
        puzzleView.setOnPieceUnSelectedListener {
            Log.e("TAG", "pieceSelectedNot: ")
            slideDown(rvPieceControl)
            slideUp(mRvTools)
            slideDownSaveView()
            val layoutParams =
                rvPieceControl.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.bottomMargin = 0
            rvPieceControl.layoutParams = layoutParams
            currentMode = NONE
        }
        puzzleView.post { PhotoFiltersUtils.loadPhoto(this,
        lstPaths, puzzleLayout, puzzleView, targets, deviceWidth) }
        findViewById<ImageView>(R.id.imgCloseLayout).setOnClickListener(onClickListener)
        findViewById<ImageView>(R.id.imgSaveLayout).setOnClickListener(onClickListener)
        findViewById<ImageView>(R.id.imgCloseBackground).setOnClickListener(onClickListener)
        findViewById<ImageView>(R.id.imgSaveBackground).setOnClickListener(onClickListener)
        changeLayoutLayout = findViewById(R.id.changeLayoutLayout)
        changeBorder = findViewById(R.id.change_border)
        tvChangeLayout = findViewById(R.id.tv_change_layout)
        tvChangeLayout.setOnClickListener(onClickListener)
        tvChangeBorder = findViewById(R.id.tv_change_border)
        tvChangeBorder.setOnClickListener(onClickListener)
        tvChangeRatio = findViewById(R.id.tv_change_ratio)
        tvChangeRatio.setOnClickListener(onClickListener)
        tvChangeBackgroundColor = findViewById(R.id.tv_color)
        tvChangeBackgroundColor.setOnClickListener(onClickListener)
        tvChangeBackgroundGradient = findViewById(R.id.tv_radian)
        tvChangeBackgroundGradient.setOnClickListener(onClickListener)
        tvChangeBackgroundBlur = findViewById(R.id.tv_blur)
        tvChangeBackgroundBlur.setOnClickListener(onClickListener)
        puzzleList = findViewById(R.id.puzzleList)
        puzzleList.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val puzzleAdapter = PuzzleAdapter()
        puzzleAdapter.setOnItemClickListener(this)
        puzzleList.adapter = puzzleAdapter
        puzzleAdapter.refreshData(CollegeUtils.getPuzzleLayouts(lstPaths!!.size), null)
        val previewAspectRatioAdapter = AspectRatioPreviewAdapter()
        previewAspectRatioAdapter.setListener(this)
        radiusLayout = findViewById(R.id.radioLayout)
        radiusLayout.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        radiusLayout.adapter = previewAspectRatioAdapter
        val saveBitmap: MaterialButton = findViewById(R.id.save)
        saveBitmap.visibility = View.VISIBLE
        saveBitmap.setOnClickListener { view: View? ->
            saveImage()
        }
        puzzleView.setConstrained(true)
        changeBackgroundLayout = findViewById(R.id.changeBackgroundLayout)
        mainActivity = findViewById(R.id.puzzle_layout)
        changeLayoutLayout.alpha = 0.0f
        changeBackgroundLayout.alpha = 0.0f
        rvPieceControl.alpha = 0.0f
        mainActivity.post {
            slideDown(changeLayoutLayout)
            slideDown(changeBackgroundLayout)
            slideDown(rvPieceControl)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            changeLayoutLayout.alpha = 1.0f
            changeBackgroundLayout.alpha = 1.0f
            rvPieceControl.alpha = 1.0f
        }, 1000)
        showLoading(false)
        currentBackgroundState = CollegeBGAdapter.SquareView(Color.parseColor("#ffffff"), "", true)
        rvBackgroundColor = findViewById(R.id.colorList)
        rvBackgroundColor.layoutManager = LinearLayoutManager(
            applicationContext, RecyclerView.HORIZONTAL, false
        )
        rvBackgroundColor.setHasFixedSize(true)
        rvBackgroundColor.adapter = CollegeBGAdapter(applicationContext, this)
        rvBackgroundGradient = findViewById(R.id.gradientList)
        rvBackgroundGradient.layoutManager = LinearLayoutManager(
            applicationContext, RecyclerView.HORIZONTAL, false
        )
        rvBackgroundGradient.setHasFixedSize(true)
        rvBackgroundGradient.adapter = CollegeBGAdapter(
            applicationContext,
            this as CollegeBGAdapter.BackgroundChangeListener,
            true
        )
        rvBackgroundList = findViewById(R.id.rvBackgroundList)
        rvBackgroundList.layoutManager = LinearLayoutManager(
            applicationContext, RecyclerView.HORIZONTAL, false
        )
        rvBackgroundList.setHasFixedSize(true)
        val defaultDisplay = windowManager.defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        val layoutParams = puzzleView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.height = point.x
        layoutParams.width = point.x
        puzzleView.layoutParams = layoutParams
        currentAspect = AspectRatio(1, 1)
        puzzleView.aspectRatio = AspectRatio(1, 1)
        puzzle = this
        currentMode = NONE
        CGENativeLibrary.setLoadImageCallback(mLoadImageCallback, null as Any?)
        instance = this
    }

    private fun saveImage() {
        puzzleView.setHandlingSticker(null)
        slideUp(mRvTools)
        slideDownSaveView()
        puzzleView.setLocked(true)
        puzzleView.setTouchEnable(true)
        currentMode = NONE
        val createBitmap = FileUtils.createBitmap(puzzleView, 1920)
        val createBitmap2: Bitmap = puzzleView.createBitmap()
        SavePuzzleAsFile().execute(createBitmap, createBitmap2)
    }

    fun showLoading(z: Boolean) {
        if (z) {
            window.setFlags(16, 16)
            loadingView.visibility = View.VISIBLE
            return
        }
        window.clearFlags(16)
        loadingView.visibility = View.GONE
    }

    fun selectBackgroundBlur() {
        val arrayList: ArrayList<Drawable> = arrayListOf()
        for (drawable in puzzleView.getPuzzlePieces()) {
            arrayList.add(drawable.drawable)
        }
        val puzzleBackgroundAdapter = CollegeBGAdapter(
            applicationContext, this, arrayList as List<Drawable?>
        )
        puzzleBackgroundAdapter.setSelectedSquareIndex(-1)
        rvBackgroundList.adapter = puzzleBackgroundAdapter
        rvBackgroundList.visibility = View.VISIBLE
        tvChangeBackgroundBlur.setBackgroundResource(R.drawable.border_bottom)
        tvChangeBackgroundBlur.setTextColor(ContextCompat.getColor(this, R.color.white))
        rvBackgroundGradient.visibility = View.GONE
        tvChangeBackgroundGradient.setBackgroundResource(0)
        tvChangeBackgroundGradient.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.unselected_color
            )
        )
        rvBackgroundColor.visibility = View.GONE
        tvChangeBackgroundColor.setBackgroundResource(0)
        tvChangeBackgroundColor.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.unselected_color
            )
        )
    }

    fun selectBackgroundColorTab() {
        rvBackgroundColor.visibility = View.VISIBLE
        tvChangeBackgroundColor.setBackgroundResource(R.drawable.border_bottom)
        tvChangeBackgroundColor.setTextColor(ContextCompat.getColor(this, R.color.white))
        rvBackgroundColor.scrollToPosition(0)
        (rvBackgroundColor.adapter as CollegeBGAdapter?)?.setSelectedSquareIndex(-1)
        rvBackgroundColor.adapter?.notifyDataSetChanged()
        rvBackgroundGradient.visibility = View.GONE
        tvChangeBackgroundGradient.setBackgroundResource(0)
        tvChangeBackgroundGradient.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.unselected_color
            )
        )
        rvBackgroundList.visibility = View.GONE
        tvChangeBackgroundBlur.setBackgroundResource(0)
        tvChangeBackgroundBlur.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.unselected_color
            )
        )
    }

    fun selectBackgroundGradientTab() {
        rvBackgroundGradient.visibility = View.VISIBLE
        tvChangeBackgroundGradient.setBackgroundResource(R.drawable.border_bottom)
        tvChangeBackgroundGradient.setTextColor(ContextCompat.getColor(this, R.color.white))
        rvBackgroundGradient.scrollToPosition(0)
        (rvBackgroundGradient.adapter as CollegeBGAdapter?)?.setSelectedSquareIndex(-1)
        rvBackgroundGradient.adapter?.notifyDataSetChanged()
        rvBackgroundColor.visibility = View.GONE
        tvChangeBackgroundColor.setBackgroundResource(0)
        tvChangeBackgroundColor.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.unselected_color
            )
        )
        rvBackgroundList.visibility = View.GONE
        tvChangeBackgroundBlur.setBackgroundResource(0)
        tvChangeBackgroundBlur.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.unselected_color
            )
        )
    }


    fun selectLayoutTool() {
        puzzleList.visibility = View.VISIBLE
        tvChangeLayout.visibility = View.VISIBLE
        changeBorder.visibility = View.GONE
        tvChangeBorder.visibility = View.GONE
        radiusLayout.visibility = View.GONE
        tvChangeRatio.visibility = View.GONE
    }


    fun selectRadiusTool() {
        radiusLayout.visibility = View.VISIBLE
        tvChangeRatio.visibility = View.VISIBLE
        puzzleList.visibility = View.GONE
        tvChangeLayout.visibility = View.GONE
        changeBorder.visibility = View.GONE
        tvChangeBorder.visibility = View.GONE
    }


    fun selectBorderTool() {
        changeBorder.visibility = View.VISIBLE
        tvChangeBorder.visibility = View.VISIBLE
        rvBackgroundList.visibility = View.GONE
        puzzleList.visibility = View.GONE
        tvChangeLayout.visibility = View.GONE
        radiusLayout.visibility = View.GONE
        tvChangeRatio.visibility = View.GONE
        sbChangeBorderRadius.progress = puzzleView.pieceRadian.toInt()
        sbChangeBorderSize.progress = puzzleView.piecePadding.toInt()
    }

    private fun showUpFunction(view: View?) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(mainActivity)
        constraintSet.connect(wrapPuzzleView.id, 1, mainActivity.id, 1, 0)
        constraintSet.connect(wrapPuzzleView.id, 4, view!!.id, 3, 0)
        constraintSet.connect(wrapPuzzleView.id, 2, mainActivity.id, 2, 0)
        constraintSet.applyTo(mainActivity)
    }


    fun showDownFunction() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(mainActivity)
        constraintSet.connect(wrapPuzzleView.id, 1, mainActivity.id, 1, 0)
        constraintSet.connect(wrapPuzzleView.id, 4, mRvTools.id, 3, 0)
        constraintSet.connect(wrapPuzzleView.id, 2, mainActivity.id, 2, 0)
        constraintSet.applyTo(mainActivity)
    }

    override fun onToolSelected(editingToolType: EditingToolType?) {
        currentMode = editingToolType
        when (editingToolType) {
            LAYOUT -> {
                puzzleLayout = puzzleView.puzzleLayout
                currentAspect = puzzleView.aspectRatio
                pieceBorderRadius = puzzleView.pieceRadian
                piecePadding = puzzleView.piecePadding
                puzzleList.scrollToPosition(0)
                (puzzleList.adapter as PuzzleAdapter?)?.setSelectedIndex(-1)
                puzzleList.adapter?.notifyDataSetChanged()
                radiusLayout.scrollToPosition(0)
                (radiusLayout.adapter as AspectRatioPreviewAdapter?)?.setLastSelectedView(-1)
                radiusLayout.adapter?.notifyDataSetChanged()
                selectLayoutTool()
                slideUpSaveView()
                slideUp(changeLayoutLayout)
                slideDown(mRvTools)
                showUpFunction(changeLayoutLayout)
                puzzleView.setLocked(false)
                puzzleView.setTouchEnable(false)
                return
            }
            BORDER -> {
                puzzleLayout = puzzleView.puzzleLayout
                currentAspect = puzzleView.aspectRatio
                pieceBorderRadius = puzzleView.pieceRadian
                piecePadding = puzzleView.piecePadding
                puzzleList.scrollToPosition(0)
                (puzzleList.adapter as PuzzleAdapter?)?.setSelectedIndex(-1)
                puzzleList.adapter?.notifyDataSetChanged()
                radiusLayout.scrollToPosition(0)
                (radiusLayout.adapter as AspectRatioPreviewAdapter?)?.setLastSelectedView(-1)
                radiusLayout.adapter?.notifyDataSetChanged()
                selectBorderTool()
                slideUpSaveView()
                slideUp(changeLayoutLayout)
                slideDown(mRvTools)
                showUpFunction(changeLayoutLayout)
                puzzleView.setLocked(false)
                puzzleView.setTouchEnable(false)
                return
            }
            RATIO -> {
                puzzleLayout = puzzleView.puzzleLayout
                currentAspect = puzzleView.aspectRatio
                pieceBorderRadius = puzzleView.pieceRadian
                piecePadding = puzzleView.piecePadding
                puzzleList.scrollToPosition(0)
                (puzzleList.adapter as PuzzleAdapter?)?.setSelectedIndex(-1)
                puzzleList.adapter?.notifyDataSetChanged()
                radiusLayout.scrollToPosition(0)
                (radiusLayout.adapter as AspectRatioPreviewAdapter?)?.setLastSelectedView(-1)
                radiusLayout.adapter?.notifyDataSetChanged()
                selectRadiusTool()
                slideUpSaveView()
                slideUp(changeLayoutLayout)
                slideDown(mRvTools)
                showUpFunction(changeLayoutLayout)
                puzzleView.setLocked(false)
                puzzleView.setTouchEnable(false)
                return
            }
            FILTER -> {
                if (lstOriginalDrawable.isEmpty()) {
                    for (drawable in puzzleView.getPuzzlePieces()) {
                        lstOriginalDrawable.add(drawable.drawable)
                    }
                }
                slideUpSaveView()
                return
            }
            TEXT -> {
                puzzleView.setTouchEnable(false)
                slideUpSaveView()
                puzzleView.setLocked(false)
                slideDown(mRvTools)
                return
            }
            BACKGROUND -> {
                puzzleView.setLocked(false)
                puzzleView.setTouchEnable(false)
                slideUpSaveView()
                selectBackgroundColorTab()
                slideDown(mRvTools)
                slideUp(changeBackgroundLayout)
                showUpFunction(changeBackgroundLayout)
                if (puzzleView.backgroundResourceMode == 0) {
                    currentBackgroundState.isColor = true
                    currentBackgroundState.isBitmap = false
                    currentBackgroundState.drawableId =
                        (puzzleView.background as ColorDrawable).color
                    return
                } else if (puzzleView.backgroundResourceMode == 2 || puzzleView.background is ColorDrawable) {
                    currentBackgroundState.isBitmap = true
                    currentBackgroundState.isColor = false
                    currentBackgroundState.drawable = puzzleView.background
                    return
                } else if (puzzleView.background is GradientDrawable) {
                    currentBackgroundState.isBitmap = false
                    currentBackgroundState.isColor = false
                    currentBackgroundState.drawable = puzzleView.background
                    return
                } else {
                    return
                }
            }
            else -> return
        }
    }


    fun loadPhoto() {
        val i: Int
        val arrayList: MutableList<Bitmap> = mutableListOf()
        Log.e("TAG", "loadPhoto: ${lstPaths?.size}")
        i = if (lstPaths!!.size > puzzleLayout.areaCount) {
            puzzleLayout.areaCount
        } else {
            lstPaths!!.size
        }
        for (i1 in 0 until i) {
            val target: Target = object : Target {
                override fun onBitmapFailed(exc: Exception, drawable: Drawable) {}
                override fun onPrepareLoad(drawable: Drawable) {}
                override fun onBitmapLoaded(bitmap: Bitmap, loadedFrom: LoadedFrom) {
                    var bmp = bitmap
                    val width = bmp.width
                    val f = width.toFloat()
                    val height = bmp.height.toFloat()
                    val max = Math.max(f / f, height / f)
                    if (max > 1.0f) {
                        bmp = Bitmap.createScaledBitmap(
                            bmp,
                            (f / max).toInt(),
                            (height / max).toInt(),
                            false
                        )
                    }
                    arrayList.add(bmp)
                    if (arrayList.size == i) {
                        if (lstPaths!!.size < puzzleLayout.areaCount) {
                            for (i2 in 0 until puzzleLayout.areaCount) {
                                try {
                                    puzzleView.addPiece(arrayList[i2 % i2])
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@CollageViewActivity,
                                        "An error occurred while loading image",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            puzzleView.addPieces(arrayList)
                        }
                    }
                    targets.remove(this)
                }
            }
            Log.e("TAG", "loadPhoto1: ${Uri.parse(lstPaths?.get(i1))}")

            try {
                Picasso.get().load(Uri.parse(lstPaths?.get(i1))).resize(deviceWidth, deviceWidth)
                    .centerInside()
                    .config(
                        Bitmap.Config.RGB_565
                    ).into(
                        target
                    )
                targets.add(target)
            } catch (e: Exception) {
                Log.e("TAG", "loadPhotoExc: ${e.message}")
            }
        }
    }

    fun slideUp(view: View?) {
        ObjectAnimator.ofFloat(view, "translationY", view!!.height.toFloat(), 0.0f)
            .start()
    }


    override fun onDestroy() {
        isActivityLeft = true
        try {
            puzzleView.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        AdsUtils.destroyBanner()
        super.onDestroy()
    }

    fun slideUpSaveView() {
        toolbar.visibility = View.GONE
    }

    fun slideDownSaveView() {
        toolbar.visibility = View.VISIBLE
    }

    fun slideDown(view: View?) {
        ObjectAnimator.ofFloat(view, "translationY", 0.0f, view!!.height.toFloat()).start()
    }

    override fun onBackPressed() {
        if (currentMode == null) {
            super.onBackPressed()
            return
        }
        try {
            when (currentMode) {
                LAYOUT, BORDER, RATIO -> {
                    slideDown(changeLayoutLayout)
                    slideUp(mRvTools)
                    slideDownSaveView()
                    showDownFunction()
                    puzzleView.updateLayout(puzzleLayout)
                    puzzleView.piecePadding = piecePadding
                    puzzleView.pieceRadian = pieceBorderRadius
                    currentMode = NONE
                    windowManager.defaultDisplay.getSize(Point())
                    onNewAspectRatioSelected(currentAspect)
                    puzzleView.aspectRatio = currentAspect
                    puzzleView.setLocked(true)
                    puzzleView.setTouchEnable(true)
                    return
                }
                BACKGROUND -> {
                    slideUp(mRvTools)
                    slideDown(changeBackgroundLayout)
                    puzzleView.setLocked(true)
                    puzzleView.setTouchEnable(true)
                    if (currentBackgroundState.isColor) {
                        puzzleView.backgroundResourceMode = 0
                        puzzleView.setBackgroundColor(currentBackgroundState.drawableId)
                    } else if (currentBackgroundState.isBitmap) {
                        puzzleView.backgroundResourceMode = 2
                        puzzleView.background = currentBackgroundState.drawable
                    } else {
                        puzzleView.backgroundResourceMode = 1
                        if (currentBackgroundState.drawable != null) {
                            puzzleView.background = currentBackgroundState.drawable
                        } else {
                            puzzleView.setBackgroundResource(currentBackgroundState.drawableId)
                        }
                    }
                    slideDownSaveView()
                    showDownFunction()
                    currentMode = NONE
                    return
                }
                PIECE -> {
                    slideDown(rvPieceControl)
                    slideUp(mRvTools)
                    slideDownSaveView()
                    val layoutParams =
                        rvPieceControl.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.bottomMargin = 0
                    rvPieceControl.layoutParams = layoutParams
                    currentMode = NONE
                    puzzleView.setHandlingPiece(null)
                    puzzleView.setPreviousHandlingPiece(null)
                    puzzleView.invalidate()
                    currentMode = NONE
                    return
                }
                NONE -> {
                    if (!isSaved) showDiscardDialog() else finish()
                    return
                }
                else -> {
                    val intent = Intent(
                        this@CollageViewActivity,
                        MainActivity::class.java
                    )
                    setResult(RESULT_CANCELED, intent)
                    super.onBackPressed()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDiscardDialog() {
        AlertDialog.Builder(this).setMessage(R.string.dialog_discard_title)
            .setPositiveButton(R.string.discard) { dialogInterface, i ->
                currentMode = null
                finish()
            }.setNegativeButton("Cancel",
                { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() })
            .create().show()
    }

    override fun onItemClick(puzzleLayout2: PuzzleLayout, i: Int) {
        val parse: PuzzleLayout = PuzzleLayoutParser.parse(puzzleLayout2.generateInfo())
        puzzleLayout2.radian = puzzleView.pieceRadian
        puzzleLayout2.padding = puzzleView.piecePadding
        puzzleView.updateLayout(parse)
    }

    override fun onNewAspectRatioSelected(aspectRatio: AspectRatio?) {
        val defaultDisplay = windowManager.defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        val calculateWidthAndHeight = calculateWidthAndHeight(aspectRatio, point)
        puzzleView.layoutParams = ConstraintLayout.LayoutParams(
            calculateWidthAndHeight[0], calculateWidthAndHeight[1]
        )
        val constraintSet = ConstraintSet()
        constraintSet.clone(wrapPuzzleView)
        constraintSet.connect(puzzleView.id, 3, wrapPuzzleView.id, 3, 0)
        constraintSet.connect(puzzleView.id, 1, wrapPuzzleView.id, 1, 0)
        constraintSet.connect(puzzleView.id, 4, wrapPuzzleView.id, 4, 0)
        constraintSet.connect(puzzleView.id, 2, wrapPuzzleView.id, 2, 0)
        constraintSet.applyTo(wrapPuzzleView)
        puzzleView.aspectRatio = aspectRatio
    }

    private fun calculateWidthAndHeight(aspectRatio: AspectRatio?, point: Point): IntArray {
        val height = wrapPuzzleView.height
        if (aspectRatio!!.height > aspectRatio.width) {
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

    override fun onPause() {
        isActivityLeft = true
        super.onPause()
    }

    override fun onResume() {
        isActivityLeft = false
        super.onResume()
    }


    @SuppressLint("StaticFieldLeak")
    override fun onBackgroundSelected(squareView: CollegeBGAdapter.SquareView) {
        if (squareView.isColor) {
            puzzleView.setBackgroundColor(squareView.drawableId)
            puzzleView.backgroundResourceMode = 0
        } else if (squareView.drawable != null) {
            puzzleView.backgroundResourceMode = 2

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
                    puzzleView.background = BitmapDrawable(resources, bitmap)
                }
            }.execute(null)
        } else {
            puzzleView.setBackgroundResource(squareView.drawableId)
            puzzleView.backgroundResourceMode = 1
        }
    }

    override fun onFilterSelected(str: String) {
        LoadBitmapWithFilter().execute(str)
    }


    override fun finishCrop(bitmap: Bitmap?) {
        puzzleView.replace(bitmap, "")
    }

    override fun onSaveFilter(bitmap: Bitmap?) {
        puzzleView.replace(bitmap, "")
    }

    override fun onPieceFuncSelected(editingToolType: EditingToolType?) {
        when (editingToolType) {
            REPLACE_IMG -> {
                with(this)
                    .showCameraTile(false)
                    .dropDownAlbum()
                    .imageCountTextFormat("%s images")
                    .start { uri: Uri ->
                        OnLoadBitmapFromUri().execute(
                            uri.toString()
                        )
                    }
                return
            }
            H_FLIP -> {
                puzzleView.flipHorizontally()
                return
            }
            V_FLIP -> {
                puzzleView.flipVertically()
                return
            }
            ROTATE -> {
                puzzleView.rotate(90.0f)
                return
            }
            CROP -> {
                PicsartCropDialogFragment.show(
                    this,
                    this,
                    (puzzleView.getHandlingPiece().drawable as BitmapDrawable).bitmap
                )
                return
            }
            FILTER -> LoadFilterBitmapForCurrentPiece().execute()
            else -> {}
        }
    }

    inner class OnLoadBitmapFromUri : AsyncTask<String?, Bitmap?, Bitmap?>() {
        public override fun onPreExecute() {
            showLoading(true)
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        override fun doInBackground(vararg strArr: String?): Bitmap? {
            return try {
                val fromFile = Uri.parse(strArr[0])
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fromFile)
                val width = bitmap.width.toFloat()
                val height = bitmap.height.toFloat()
                val max = (width / 1280.0f).coerceAtLeast(height / 1280.0f)
                if (max > 1.0f) {
                    bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        (width / max).toInt(),
                        (height / max).toInt(),
                        false
                    )
                }
                val rotateBitmap: Bitmap = SystemUtils.rotateBitmap(
                    bitmap,
                    android.media.ExifInterface(contentResolver.openInputStream(fromFile)!!)
                        .getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1)
                )
                if (rotateBitmap != bitmap) {
                    bitmap.recycle()
                }
                rotateBitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            showLoading(false)
            puzzleView.replace(bitmap, "")
        }
    }

    inner class LoadFilterBitmapForCurrentPiece :
        AsyncTask<Void?, List<Bitmap?>?, List<Bitmap?>?>() {
        public override fun onPreExecute() {
            showLoading(true)
        }

        @SuppressLint("WrongThread")
        override fun doInBackground(vararg voidArr: Void?): List<Bitmap>? {
            return UtilsFilter.getLstBitmapWithFilter(
                ThumbnailUtils.extractThumbnail(
                    (puzzleView.getHandlingPiece().drawable as BitmapDrawable).bitmap, 100, 100
                )
            )
        }

        override fun onPostExecute(list: List<Bitmap?>?) {
            showLoading(false)
            if (puzzleView.getHandlingPiece() != null) {
                FilterDialogFragment.show(
                    this@CollageViewActivity,
                    this@CollageViewActivity,
                    (puzzleView.getHandlingPiece().drawable as BitmapDrawable).bitmap,
                    list
                )
                showLoading(false)
            }
        }
    }

    inner class LoadBitmapWithFilter :
        AsyncTask<String?, List<Bitmap?>?, List<Bitmap?>?>() {
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

        override fun onPostExecute(list: List<Bitmap?>?) {
            list?.let { lists ->
                for (i in lists.indices) {
                    val bitmapDrawable = BitmapDrawable(resources, lists[i])
                    bitmapDrawable.setAntiAlias(true)
                    bitmapDrawable.isFilterBitmap = true
                    puzzleView.getPuzzlePieces()[i].drawable = bitmapDrawable
                }
            }
            puzzleView.invalidate()
            showLoading(false)
        }
    }

    inner class SavePuzzleAsFile :
        AsyncTask<Bitmap?, String?, String?>() {
        public override fun onPreExecute() {
            showLoading(true)
        }

        override fun doInBackground(vararg bitmapArr: Bitmap?): String? {
            val bitmap = bitmapArr[0]!!
            val bitmap2 = bitmapArr[1]!!
            val createBitmap =
                Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(createBitmap)
            canvas.drawBitmap(
                bitmap,
                null,
                RectF(0.0f, 0.0f, bitmap.width.toFloat(), bitmap.height.toFloat()),
                null as Paint?
            )
            canvas.drawBitmap(
                bitmap2,
                null,
                RectF(0.0f, 0.0f, bitmap.width.toFloat(), bitmap.height.toFloat()),
                null as Paint?
            )
            bitmap.recycle()
            bitmap2.recycle()
            val saveBitmapAsFile =
                FileUtils.saveBitmapAsFile(createBitmap, "Collage Maker") ?: return null
            return try {
                MediaScannerConnection.scanFile(
                    applicationContext,
                    arrayOf(saveBitmapAsFile.absolutePath),
                    null as Array<String?>?
                ) { str: String?, uri: Uri? -> }
                createBitmap.recycle()
                saveBitmapAsFile.absolutePath
            } catch (e: Exception) {
                createBitmap.recycle()
                null
            } catch (th: Throwable) {
                createBitmap.recycle()
                throw th
            }
        }

        override fun onPostExecute(str: String?) {
            showLoading(false)
            isSaved = true
            AdsUtils.loadInterstitialAd(this@CollageViewActivity,
                getString(R.string.interstitial_id),
                object : AdsUtils.Companion.FullScreenCallback() {
                    override fun continueExecution() {
                        val intent = Intent(
                            this@CollageViewActivity,
                            CollageSaveShareActivity::class.java
                        )
                        intent.putExtra("path", str)
                        intent.putExtra("type", 1)
                        startActivity(intent)
                    }
                })
        }
    }

    override fun getsticker() {

    }
}