package com.tools.videodownloader.ui.activities;

import static com.tools.videodownloader.utils.DisplayUtilsKt.adjustInsetsBoth;
import static com.tools.videodownloader.utils.DisplayUtilsKt.setBottomMargin;
import static com.tools.videodownloader.utils.DisplayUtilsKt.setTopMargin;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.tools.videodownloader.R;
import com.tools.videodownloader.collage_maker.features.DrawBitmapModel;
import com.tools.videodownloader.collage_maker.features.adjust.adjust.AdjustAdapter;
import com.tools.videodownloader.collage_maker.features.adjust.adjust.AdjustListener;
import com.tools.videodownloader.collage_maker.features.crop.PicsartCropDialogFragment;
import com.tools.videodownloader.collage_maker.features.draw.AdapterColor;
import com.tools.videodownloader.collage_maker.features.draw.AdapterMagicBrush;
import com.tools.videodownloader.collage_maker.features.draw.BrushColorListener;
import com.tools.videodownloader.collage_maker.features.draw.BrushMagicListener;
import com.tools.videodownloader.collage_maker.features.insta.InstaDialog;
import com.tools.videodownloader.collage_maker.features.mosaic.MosaicDialog;
import com.tools.videodownloader.collage_maker.features.splash.SplashDialog;
import com.tools.videodownloader.collage_maker.ui.EditingToolType;
import com.tools.videodownloader.collage_maker.ui.activities.CollageSaveShareActivity;
import com.tools.videodownloader.collage_maker.ui.adapters.AdapterFilterView;
import com.tools.videodownloader.collage_maker.ui.adapters.BottomToolsAdapter;
import com.tools.videodownloader.collage_maker.ui.interfaces.FilterListener;
import com.tools.videodownloader.collage_maker.ui.interfaces.OnPhotoEditorListener;
import com.tools.videodownloader.collage_maker.utils.FileUtils;
import com.tools.videodownloader.collage_maker.utils.SystemUtils;
import com.tools.videodownloader.collage_maker.utils.UtilsFilter;
import com.tools.videodownloader.databinding.ActivityPicEditBinding;
import com.tools.videodownloader.tools.photo_filters.PhotoFiltersUtils;
import com.tools.videodownloader.tools.photoeditor.BrushViewType;
import com.tools.videodownloader.tools.photoeditor.ImagePicEditorView;
import com.tools.videodownloader.tools.photoeditor.PicImageEditor;
import com.tools.videodownloader.ui.fragments.HomeFragment;
import com.tools.videodownloader.utils.AdsUtils;
import com.tools.videodownloader.utils.ExtensionsKt;
import com.tools.videodownloader.utils.FileUtilsss;
import com.tools.videodownloader.utils.NetworkState;
import com.tools.videodownloader.utils.remote_config.RemoteConfigUtils;

import org.wysaid.myUtils.MsgUtil;
import org.wysaid.nativePort.CGENativeLibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class PicEditActivity extends FullScreenActivity implements OnPhotoEditorListener, View.OnClickListener,
        PicsartCropDialogFragment.OnCropPhoto, BrushColorListener, BrushMagicListener,
        InstaDialog.InstaSaveListener, SplashDialog.SplashDialogListener, MosaicDialog.MosaicDialogListener,
        BottomToolsAdapter.OnItemSelected, FilterListener, AdjustListener {
    boolean isSaved;
    private ConstraintLayout adjustLayout;
    private SeekBar adjustSeekBar;
    private TextView brush;
    private TextView brushBlur;
    private ConstraintLayout brushLayout;
    private SeekBar brushSize;
    private ImageView compareAdjust;
    public ImageView compareFilter;
    public ImageView compareOverlay;
    public EditingToolType currentMode = EditingToolType.NONE;
    private ImageView erase;
    private SeekBar eraseSize;
    public SeekBar filterIntensity;
    public ConstraintLayout filterLayout;
    private RelativeLayout loadingView;
    public ArrayList lstBitmapWithFilter = new ArrayList<>();
    public List<Bitmap> lstBitmapWithOverlay = new ArrayList<>();
    public AdjustAdapter mAdjustAdapter;
    private RecyclerView mColorBush;
    public static PicEditActivity activity;
    private BottomToolsAdapter mBottomToolsAdapter;
    public CGENativeLibrary.LoadImageCallback mLoadImageCallback = new CGENativeLibrary.LoadImageCallback() {
        public Bitmap loadImage(String str, Object obj) {
            try {
                return BitmapFactory.decodeStream(PicEditActivity.this.getAssets().open(str));
            } catch (IOException io) {
                return null;
            }
        }

        public void loadImageOK(Bitmap bitmap, Object obj) {
            bitmap.recycle();
        }
    };
    private RecyclerView mMagicBrush;
    public PicImageEditor mPicImageEditor;
    public ImagePicEditorView mImagePicEditorView;
    private ConstraintLayout mRootView;
    private RecyclerView mRvAdjust;
    public RecyclerView mRvFilters;
    public RecyclerView mRvOverlays;
    public RecyclerView mRvTools;
    private TextView magicBrush;
    ActivityPicEditBinding binding;

    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener onCompareTouchListener = (view, motionEvent) -> {
        switch (motionEvent.getAction()) {
            case 0:
                PicEditActivity.this.mImagePicEditorView.getGLSurfaceView().setAlpha(0.0f);
                return true;
            case 1:
                PicEditActivity.this.mImagePicEditorView.getGLSurfaceView().setAlpha(1.0f);
                return false;
            default:
                return true;
        }
    };

    public SeekBar overlayIntensity;

    public ConstraintLayout overlayLayout;
    private ImageView redo;
    private ImageView undo;
    public RelativeLayout wrapPhotoView;

    private NativeAd nativeAd;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityPicEditBinding.inflate(getLayoutInflater());

        binding.toolbar.txtTitle.setText(getString(R.string.photo_editor));
        binding.toolbar.imgSave.setTextColor(Color.parseColor("#efc67a"));
        ExtensionsKt.visible(binding.toolbar.imgSave);
        adjustInsetsBoth(PicEditActivity.this, topMargin -> {
            setTopMargin(binding.toolbar.rlMain, topMargin);
            return null;
        }, bottomMargin -> {
            setBottomMargin(binding.rlMainTop, bottomMargin);
            return null;
        });
        binding.toolbar.getRoot().setBackground(ContextCompat.getDrawable(
                PicEditActivity.this,
                R.drawable.top_bar_gradient_yellow
        ));

        if (NetworkState.Companion.isOnline())
            AdsUtils.Companion.loadBanner(this, RemoteConfigUtils.Companion.adIdBanner(),
                    binding.bannerContainer);

        setContentView(binding.getRoot());
        initViews();
        refreshAddialog();
        activity = this;
        if (getIntent().hasExtra(HomeFragment.KEY_SELECTED_PHOTOS)) {
            Log.e("TAG", "onCreateExtra: " + getIntent().getStringExtra(HomeFragment.KEY_SELECTED_PHOTOS));
            if (!TextUtils.isEmpty(getIntent().getStringExtra((HomeFragment.KEY_SELECTED_PHOTOS))))
                new OnLoadBitmapFromUri().execute(getIntent().getStringExtra(HomeFragment.KEY_SELECTED_PHOTOS));
            else {
                Uri receiveUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
                if (receiveUri != null) {
                    new OnLoadBitmapFromUri().execute(getRealPathFromUri(PicEditActivity.this, receiveUri));
                }
            }
        }

        CGENativeLibrary.setLoadImageCallback(this.mLoadImageCallback, null);

        PhotoFiltersUtils.string = "ABC";
        mBottomToolsAdapter = new BottomToolsAdapter();
        mBottomToolsAdapter.setmOnItemSelected(this);
        this.mRvTools.setLayoutManager(new GridLayoutManager(this, 4));
        this.mRvTools.setAdapter(this.mBottomToolsAdapter);
        this.mRvFilters.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.mRvFilters.setHasFixedSize(true);
        this.mRvOverlays.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.mRvOverlays.setHasFixedSize(true);
        new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        this.mRvAdjust.setLayoutManager(new GridLayoutManager(this, 4));
        this.mRvAdjust.setHasFixedSize(true);
        this.mAdjustAdapter = new AdjustAdapter(getApplicationContext(), this);
        this.mRvAdjust.setAdapter(this.mAdjustAdapter);
        this.mColorBush.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.mColorBush.setHasFixedSize(true);
        this.mColorBush.setAdapter(new AdapterColor(getApplicationContext(), this));
        this.mMagicBrush.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        this.mMagicBrush.setHasFixedSize(true);
        this.mMagicBrush.setAdapter(new AdapterMagicBrush(getApplicationContext(), this));
        this.mPicImageEditor = new PicImageEditor.Builder(this, this.mImagePicEditorView).setPinchTextScalable(true).build();
        this.mPicImageEditor.setOnPhotoEditorListener(this);
        toogleDrawBottomToolbar(false);
        this.brushLayout.setAlpha(0.0f);
        this.adjustLayout.setAlpha(0.0f);
        this.filterLayout.setAlpha(0.0f);
        this.overlayLayout.setAlpha(0.0f);
        findViewById(R.id.activitylayout).post(() -> {
            slideDown(brushLayout);
            slideDown(adjustLayout);
            slideDown(filterLayout);
            slideDown(overlayLayout);
        });
        new Handler().postDelayed(() -> {
            brushLayout.setAlpha(1.0f);
            adjustLayout.setAlpha(1.0f);
            filterLayout.setAlpha(1.0f);
            overlayLayout.setAlpha(1.0f);
        }, 1000);
        ((ConstraintLayout.LayoutParams) this.wrapPhotoView.getLayoutParams()).topMargin = ExtensionsKt.dpToPx(getApplicationContext(), 5);
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void toogleDrawBottomToolbar(boolean z) {
        int i = !z ? View.GONE : View.VISIBLE;
        this.brush.setVisibility(i);
//        this.magicBrush.setVisibility(i);
        this.brushBlur.setVisibility(i);
        this.erase.setVisibility(i);
        this.undo.setVisibility(i);
        this.redo.setVisibility(i);
    }

    public void showEraseBrush() {
        this.brushSize.setVisibility(View.GONE);
        this.mColorBush.setVisibility(View.GONE);
        this.eraseSize.setVisibility(View.VISIBLE);
        this.mMagicBrush.setVisibility(View.GONE);
        this.brush.setBackgroundResource(0);
        this.brush.setTextColor(ContextCompat.getColor(this, R.color.unselected_color));
        this.magicBrush.setBackgroundResource(0);
        this.magicBrush.setTextColor(ContextCompat.getColor(this, R.color.unselected_color));
        this.brushBlur.setBackgroundResource(0);
        this.brushBlur.setTextColor(ContextCompat.getColor(this, R.color.unselected_color));
        this.erase.setImageResource(R.drawable.eraser);
        this.mPicImageEditor.brushEraser();
        this.eraseSize.setProgress(20);
    }

    public void showColorBlurBrush() {
        this.brushSize.setVisibility(View.VISIBLE);
        this.mColorBush.setVisibility(View.VISIBLE);
        AdapterColor brushColorsAdapter = (AdapterColor) this.mColorBush.getAdapter();
        if (brushColorsAdapter != null) {
            brushColorsAdapter.setSelectedColorIndex(0);
        }
        this.mColorBush.scrollToPosition(0);
        if (brushColorsAdapter != null) {
            brushColorsAdapter.notifyDataSetChanged();
        }
        this.eraseSize.setVisibility(View.GONE);
        this.mMagicBrush.setVisibility(View.GONE);
        this.erase.setImageResource(R.drawable.eraser);
        this.magicBrush.setBackgroundResource(0);
        this.magicBrush.setTextColor(ContextCompat.getColor(this, R.color.unselected_color));
        this.brush.setBackgroundResource(0);
        this.brush.setTextColor(ContextCompat.getColor(this, R.color.unselected_color));
        this.brushBlur.setBackground(ContextCompat.getDrawable(this, R.drawable.border_bottom));
        this.brushBlur.setTextColor(ContextCompat.getColor(this, R.color.white));
        this.mPicImageEditor.setBrushMode(2);
        this.mPicImageEditor.setBrushDrawingMode(true);
        this.brushSize.setProgress(20);
    }

    public void showColorBrush() {
        this.brushSize.setVisibility(View.VISIBLE);
        this.mColorBush.setVisibility(View.VISIBLE);
        this.mColorBush.scrollToPosition(0);
        AdapterColor brushColorsAdapter = (AdapterColor) this.mColorBush.getAdapter();
        if (brushColorsAdapter != null) {
            brushColorsAdapter.setSelectedColorIndex(0);
        }
        if (brushColorsAdapter != null) {
            brushColorsAdapter.notifyDataSetChanged();
        }
        this.eraseSize.setVisibility(View.GONE);
        this.mMagicBrush.setVisibility(View.GONE);
        this.erase.setImageResource(R.drawable.eraser);
        this.magicBrush.setBackgroundResource(0);
        this.magicBrush.setTextColor(ContextCompat.getColor(this, R.color.unselected_color));
        this.brush.setBackground(ContextCompat.getDrawable(this, R.drawable.border_bottom));
        this.brush.setTextColor(ContextCompat.getColor(this, R.color.black));
        this.brushBlur.setBackgroundResource(0);
        this.brushBlur.setTextColor(ContextCompat.getColor(this, R.color.unselected_color));
        this.mPicImageEditor.setBrushMode(1);
        this.mPicImageEditor.setBrushDrawingMode(true);
        this.brushSize.setProgress(20);
    }

    public void showMagicBrush() {
        this.brushSize.setVisibility(View.VISIBLE);
        this.mColorBush.setVisibility(View.GONE);
        this.eraseSize.setVisibility(View.GONE);
        this.mMagicBrush.setVisibility(View.VISIBLE);
        this.erase.setImageResource(R.drawable.eraser);
        this.magicBrush.setBackground(ContextCompat.getDrawable(this, R.drawable.border_bottom));
        this.magicBrush.setTextColor(ContextCompat.getColor(this, R.color.black));
        this.brush.setBackgroundResource(0);
        this.brush.setTextColor(ContextCompat.getColor(this, R.color.unselected_color));
        this.brushBlur.setBackgroundResource(0);
        this.brushBlur.setTextColor(ContextCompat.getColor(this, R.color.unselected_color));
        this.mPicImageEditor.setBrushMagic(AdapterMagicBrush.lstDrawBitmapModel(getApplicationContext()).get(0));
        this.mPicImageEditor.setBrushMode(3);
        this.mPicImageEditor.setBrushDrawingMode(true);
        AdapterMagicBrush magicBrushAdapter = (AdapterMagicBrush) this.mMagicBrush.getAdapter();
        if (magicBrushAdapter != null) {
            magicBrushAdapter.setSelectedColorIndex(0);
        }
        this.mMagicBrush.scrollToPosition(0);
        if (magicBrushAdapter != null) {
            magicBrushAdapter.notifyDataSetChanged();
        }
    }

    private void initViews() {
        mImagePicEditorView = findViewById(R.id.imagePicEditorView);
        mImagePicEditorView.setVisibility(View.INVISIBLE);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRvOverlays = findViewById(R.id.rvOverlayView);
        mRvAdjust = findViewById(R.id.rvAdjustView);
        mRootView = findViewById(R.id.rootView);
        filterLayout = findViewById(R.id.filterLayout);
        overlayLayout = findViewById(R.id.overlayLayout);
        adjustLayout = findViewById(R.id.adjustLayout);
        filterIntensity = findViewById(R.id.filterIntensity);
        overlayIntensity = findViewById(R.id.overlayIntensity);
        brushLayout = findViewById(R.id.brushLayout);
        mColorBush = findViewById(R.id.rvColorBush);
        mMagicBrush = findViewById(R.id.rvMagicBush);
        wrapPhotoView = findViewById(R.id.wrap_photo_view);
        brush = findViewById(R.id.draw);
        magicBrush = findViewById(R.id.brush_magic);
        erase = findViewById(R.id.erase);
        undo = findViewById(R.id.undo);
        undo.setVisibility(View.GONE);
        redo = findViewById(R.id.redo);
        redo.setVisibility(View.GONE);
        brushBlur = findViewById(R.id.brush_blur);
        brushSize = findViewById(R.id.brushSize);
        eraseSize = findViewById(R.id.eraseSize);
        loadingView = findViewById(R.id.loadingView);
        loadingView.setVisibility(View.VISIBLE);
        binding.toolbar.imgSave.setVisibility(View.VISIBLE);

        binding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());

        binding.toolbar.imgSave.setOnClickListener(view -> {
            mImagePicEditorView.setLocked(true);
            savePhoto();
        });
        compareAdjust = findViewById(R.id.compareAdjust);
        compareAdjust.setOnTouchListener(onCompareTouchListener);
        compareAdjust.setVisibility(View.GONE);

        compareFilter = findViewById(R.id.compareFilter);
        compareFilter.setOnTouchListener(onCompareTouchListener);
        compareFilter.setVisibility(View.GONE);

        compareOverlay = findViewById(R.id.compareOverlay);
        compareOverlay.setOnTouchListener(onCompareTouchListener);
        compareOverlay.setVisibility(View.GONE);
        erase.setOnClickListener(view -> showEraseBrush());
        brush.setOnClickListener(view -> showColorBrush());
        magicBrush.setOnClickListener(view -> showMagicBrush());
        brushBlur.setOnClickListener(view -> showColorBlurBrush());
        eraseSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                mPicImageEditor.setBrushEraserSize((float) i);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                PicEditActivity.this.mPicImageEditor.brushEraser();
            }
        });
        brushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                PicEditActivity.this.mPicImageEditor.setBrushSize((float) (i + 10));
            }
        });
        adjustSeekBar = findViewById(R.id.adjustLevel);
        adjustSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                PicEditActivity.this.mAdjustAdapter.getCurrentAdjustModel().setIntensity(PicEditActivity.this.mPicImageEditor, ((float) i) / ((float) seekBar.getMax()), true);
            }
        });
        mImagePicEditorView.setBackgroundColor(-16777216);
        mImagePicEditorView.setLocked(false);
        mImagePicEditorView.setConstrained(true);
        filterIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                PicEditActivity.this.mImagePicEditorView.setFilterIntensity(((float) i) / 100.0f);
            }
        });
        overlayIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                PicEditActivity.this.mImagePicEditorView.setFilterIntensity(((float) i) / 100.0f);
            }
        });
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
    }

    public void showLoading(boolean z) {
        if (z) {
            getWindow().setFlags(16, 16);
            this.loadingView.setVisibility(View.VISIBLE);
            return;
        }
        getWindow().clearFlags(16);
        this.loadingView.setVisibility(View.GONE);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
    }

    @Override
    public void onAddViewListener(BrushViewType brushViewType, int i) {

    }

    public void onRemoveViewListener(int i) {
    }

    public void onRemoveViewListener(BrushViewType brushViewType, int i) {
    }

    public void onStartViewChangeListener(BrushViewType brushViewType) {
    }

    public void onStopViewChangeListener(BrushViewType brushViewType) {
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgCloseAdjust:
            case R.id.imgCloseFilter:
            case R.id.imgCloseOverlay:
            case R.id.imgSaveAdjust:
                new SaveFilterAsBitmap().execute();
                this.compareAdjust.setVisibility(View.GONE);
                slideDown(this.adjustLayout);
                slideUp(this.mRvTools);
                slideDownSaveView();
                this.currentMode = EditingToolType.NONE;
                return;
            case R.id.imgCloseBrush:
                new SaveFilterAsBitmap().execute();
                this.compareAdjust.setVisibility(View.GONE);
                slideDown(this.brushLayout);
                slideUp(this.mRvTools);
                slideDownSaveView();
                this.currentMode = EditingToolType.NONE;
                break;
            case R.id.imgSaveBrush:
                showLoading(true);
                runOnUiThread(() -> {
                    mPicImageEditor.setBrushDrawingMode(false);
                    undo.setVisibility(View.GONE);
                    redo.setVisibility(View.GONE);
                    erase.setVisibility(View.GONE);
                    slideDown(brushLayout);
                    slideUp(mRvTools);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(mRootView);
                    constraintSet.connect(wrapPhotoView.getId(), 3, mRootView.getId(), 3, ExtensionsKt.dpToPx(getApplicationContext(), 50));
                    constraintSet.connect(wrapPhotoView.getId(), 1, mRootView.getId(), 1, 0);
                    constraintSet.connect(wrapPhotoView.getId(), 4, binding.llTools.getId(), 3, 0);
                    constraintSet.connect(wrapPhotoView.getId(), 2, mRootView.getId(), 2, 0);
                    constraintSet.applyTo(mRootView);
                    mImagePicEditorView.setImageSource(mPicImageEditor.getBrushDrawingView().getDrawBitmap(mImagePicEditorView.getCurrentBitmap()));
                    mPicImageEditor.clearBrushAllViews();
                    showLoading(false);
                    updateLayout();
                });
                slideDownSaveView();
                this.currentMode = EditingToolType.NONE;
                return;
            case R.id.imgSaveFilter:
                new SaveFilterAsBitmap().execute();
                this.compareFilter.setVisibility(View.GONE);
                slideDown(this.filterLayout);
                slideUp(this.mRvTools);
                slideDownSaveView();
                this.currentMode = EditingToolType.NONE;
                return;
            case R.id.imgSaveOverlay:
                new SaveFilterAsBitmap().execute();
                slideDown(this.overlayLayout);
                slideUp(this.mRvTools);
                this.compareOverlay.setVisibility(View.GONE);
                slideDownSaveView();
                this.currentMode = EditingToolType.NONE;
                return;
            case R.id.redo:
                this.mPicImageEditor.redoBrush();
                return;
            case R.id.undo:
                this.mPicImageEditor.undoBrush();
                return;
            default:
        }
    }


    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void isPermissionGranted(boolean z, String str) {
        if (z) {
            new SaveBitmapAsFile().execute();
        }
    }

    @Override
    public void onFilterSelected(String str) {
        this.mPicImageEditor.setFilterEffect(str);
        this.filterIntensity.setProgress(100);
        this.overlayIntensity.setProgress(70);
        if (this.currentMode == EditingToolType.OVERLAY) {
            this.mImagePicEditorView.getGLSurfaceView().setFilterIntensity(0.7f);
        }
    }

    public void onToolSelected(EditingToolType editingToolType) {
        this.currentMode = editingToolType;
        switch (editingToolType) {
            case BRUSH:
                showColorBrush();
                this.mPicImageEditor.setBrushDrawingMode(true);
                slideDown(this.mRvTools);
                slideUp(this.brushLayout);
                slideUpSaveControl();
                toogleDrawBottomToolbar(true);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(this.mRootView);
                constraintSet.connect(this.wrapPhotoView.getId(), 3, this.mRootView.getId(), 3, ExtensionsKt.dpToPx(getApplicationContext(), 50));
                constraintSet.connect(this.wrapPhotoView.getId(), 1, this.mRootView.getId(), 1, 0);
                constraintSet.connect(this.wrapPhotoView.getId(), 4, this.brushLayout.getId(), 3, 0);
                constraintSet.connect(this.wrapPhotoView.getId(), 2, this.mRootView.getId(), 2, 0);
                constraintSet.applyTo(this.mRootView);
                this.mPicImageEditor.setBrushMode(1);
                updateLayout();
                break;
            case ADJUST:
                slideUpSaveView();
                this.compareAdjust.setVisibility(View.VISIBLE);
                this.mAdjustAdapter = new AdjustAdapter(getApplicationContext(), this);
                this.mRvAdjust.setAdapter(this.mAdjustAdapter);
                this.mAdjustAdapter.setSelectedAdjust(0);
                this.mPicImageEditor.setAdjustFilter(this.mAdjustAdapter.getFilterConfig());
                slideUp(this.adjustLayout);
                slideDown(this.mRvTools);
                break;
            case FILTER:
                slideUpSaveView();
                new LoadFilterBitmap().execute();
                break;
            case OVERLAY:
                slideUpSaveView();
                new LoadOverlayBitmap().execute();
                break;
            case INSTA:
                new ShowInstaDialog().execute();
                break;
            case SPLASH:
                new ShowSplashDialog(true).execute();
                break;
            case BLUR:
                new ShowSplashDialog(false).execute();
                break;
            case MOSAIC:
                new ShowMosaicDialog().execute();
                break;
            case CROP:
                PicsartCropDialogFragment.show(this, this, this.mImagePicEditorView.getCurrentBitmap());
                break;
//            case BEAUTY:
//                BeautyDialog.show(this, this.mPhotoEditorView.getCurrentBitmap(), this);
//                break;
        }
        this.mImagePicEditorView.setHandlingSticker(null);
    }

    public void slideUp(View view) {
        ObjectAnimator.ofFloat(view, "translationY", (float) view.getHeight(), 0.0f).start();
    }

    public void slideUpSaveView() {
        binding.toolbar.getRoot().setVisibility(View.GONE);
    }

    public void slideUpSaveControl() {
        binding.toolbar.getRoot().setVisibility(View.GONE);
    }

    public void slideDownSaveControl() {
        binding.toolbar.getRoot().setVisibility(View.VISIBLE);
    }

    public void slideDownSaveView() {
        binding.toolbar.getRoot().setVisibility(View.VISIBLE);
    }

    public void slideDown(View view) {
        ObjectAnimator.ofFloat(view, "translationY", 0.0f, (float) view.getHeight()).start();
    }


    public void onBackPressed() {
        if (this.currentMode != null) {
            try {
                switch (this.currentMode) {
                    case BRUSH:
                        slideDown(this.brushLayout);
                        slideUp(this.mRvTools);
                        slideDownSaveControl();
                        this.undo.setVisibility(View.GONE);
                        this.redo.setVisibility(View.GONE);
                        this.erase.setVisibility(View.GONE);
                        this.mPicImageEditor.setBrushDrawingMode(false);
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(this.mRootView);
                        constraintSet.connect(this.wrapPhotoView.getId(), 3, this.mRootView.getId(), 3, ExtensionsKt.dpToPx(getApplicationContext(), 50));
                        constraintSet.connect(this.wrapPhotoView.getId(), 1, this.mRootView.getId(), 1, 0);
                        constraintSet.connect(this.wrapPhotoView.getId(), 4, this.binding.llTools.getId(), 3, 0);
                        constraintSet.connect(this.wrapPhotoView.getId(), 2, this.mRootView.getId(), 2, 0);
                        constraintSet.applyTo(this.mRootView);
                        this.mPicImageEditor.clearBrushAllViews();
                        slideDownSaveView();
                        this.currentMode = EditingToolType.NONE;
                        updateLayout();
                        return;
                    case ADJUST:
                        this.mPicImageEditor.setFilterEffect("");
                        this.compareAdjust.setVisibility(View.GONE);
                        slideDown(this.adjustLayout);
                        slideUp(this.mRvTools);
                        slideDownSaveView();
                        this.currentMode = EditingToolType.NONE;
                        return;
                    case FILTER:
                        slideDown(this.filterLayout);
                        slideUp(this.mRvTools);
                        slideDownSaveView();
                        this.mPicImageEditor.setFilterEffect("");
                        this.compareFilter.setVisibility(View.GONE);
                        this.lstBitmapWithFilter.clear();
                        if (this.mRvFilters.getAdapter() != null) {
                            this.mRvFilters.getAdapter().notifyDataSetChanged();
                        }
                        this.currentMode = EditingToolType.NONE;
                        return;
                    case OVERLAY:
                        this.mPicImageEditor.setFilterEffect("");
                        this.compareOverlay.setVisibility(View.GONE);
                        this.lstBitmapWithOverlay.clear();
                        slideUp(this.mRvTools);
                        slideDown(this.overlayLayout);
                        slideDownSaveView();
                        this.mRvOverlays.getAdapter().notifyDataSetChanged();
                        this.currentMode = EditingToolType.NONE;
                        return;
                    case SPLASH:
                    case BLUR:
                    case MOSAIC:
                    case CROP:
                        showDiscardDialog();
                        return;
//                    case BEAUTY:
//                        showDiscardDialog();
                    case NONE:
                        if (!isSaved)
                            showDiscardDialog();
                        else finish();
                        return;
                    default:
                        Intent intent = new Intent(PicEditActivity.this, MainActivity.class);
                        setResult(RESULT_CANCELED, intent);
                        super.onBackPressed();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDiscardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyProgressDialog)
                .setMessage(R.string.dialog_discard_title).setPositiveButton(R.string.discard, (dialogInterface, i) -> {
                    currentMode = null;
                    finish();
                }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    public void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        AdsUtils.Companion.destroyBanner();
        super.onDestroy();
    }

    public void onAdjustSelected(AdjustAdapter.AdjustModel adjustModel) {
        this.adjustSeekBar.setProgress((int) (adjustModel.slierIntensity * ((float) this.adjustSeekBar.getMax())));
    }

    public void finishCrop(Bitmap bitmap) {
        this.mImagePicEditorView.setImageSource(bitmap);
        this.currentMode = EditingToolType.NONE;
        updateLayout();
    }

    public void onColorChanged(String str) {
        this.mPicImageEditor.setBrushColor(Color.parseColor(str));
    }

    public void instaSavedBitmap(Bitmap bitmap) {
        this.mImagePicEditorView.setImageSource(bitmap);
        this.currentMode = EditingToolType.NONE;
        updateLayout();
    }

    public void onMagicChanged(DrawBitmapModel drawBitmapModel) {
        this.mPicImageEditor.setBrushMagic(drawBitmapModel);
    }

    public void onSaveSplash(Bitmap bitmap) {
        this.mImagePicEditorView.setImageSource(bitmap);
        this.currentMode = EditingToolType.NONE;
    }

    public void onSaveMosaic(Bitmap bitmap) {
        this.mImagePicEditorView.setImageSource(bitmap);
        this.currentMode = EditingToolType.NONE;
    }

    class LoadFilterBitmap extends AsyncTask<Void, Void, Void> {
        LoadFilterBitmap() {
        }

        public void onPreExecute() {
            PicEditActivity.this.showLoading(true);
        }

        public Void doInBackground(Void... voidArr) {
            lstBitmapWithFilter.clear();
            lstBitmapWithFilter.addAll(UtilsFilter.getLstBitmapWithFilter(ThumbnailUtils.extractThumbnail(PicEditActivity.this.mImagePicEditorView.getCurrentBitmap(), 100, 100)));
            return null;
        }

        public void onPostExecute(Void voidR) {
            mRvFilters.setAdapter(new AdapterFilterView(lstBitmapWithFilter,
                    PicEditActivity.this, PicEditActivity.this, Arrays.asList(UtilsFilter.EFFECT_CONFIGS)));
            slideDown(mRvTools);
            slideUp(filterLayout);
            compareFilter.setVisibility(View.VISIBLE);
            filterIntensity.setProgress(100);
            showLoading(false);
        }
    }

    class ShowInstaDialog extends AsyncTask<Void, Bitmap, Bitmap> {
        ShowInstaDialog() {
        }

        public void onPreExecute() {
            PicEditActivity.this.showLoading(true);
        }

        public Bitmap doInBackground(Void... voidArr) {
            return UtilsFilter.getBlurImageFromBitmap(PicEditActivity.this.mImagePicEditorView.getCurrentBitmap(), 5.0f);
        }

        public void onPostExecute(Bitmap bitmap) {
            PicEditActivity.this.showLoading(false);
            InstaDialog.show(PicEditActivity.this, PicEditActivity.this, PicEditActivity.this.mImagePicEditorView.getCurrentBitmap(), bitmap);
        }
    }

    class ShowMosaicDialog extends AsyncTask<Void, List<Bitmap>, List<Bitmap>> {
        ShowMosaicDialog() {
        }

        public void onPreExecute() {
            PicEditActivity.this.showLoading(true);
        }

        public List<Bitmap> doInBackground(Void... voidArr) {
            List<Bitmap> arrayList = new ArrayList<>();
            arrayList.add(UtilsFilter.cloneBitmap(PicEditActivity.this.mImagePicEditorView.getCurrentBitmap()));
            arrayList.add(UtilsFilter.getBlurImageFromBitmap(PicEditActivity.this.mImagePicEditorView.getCurrentBitmap(), 8.0f));
            return arrayList;
        }

        public void onPostExecute(List<Bitmap> list) {
            PicEditActivity.this.showLoading(false);
            MosaicDialog.show(PicEditActivity.this, list.get(0), list.get(1), PicEditActivity.this);
        }
    }

    class LoadOverlayBitmap extends AsyncTask<Void, Void, Void> {
        LoadOverlayBitmap() {
        }

        public void onPreExecute() {
            PicEditActivity.this.showLoading(true);
        }

        public Void doInBackground(Void... voidArr) {
            PicEditActivity.this.lstBitmapWithOverlay.clear();
            PicEditActivity.this.lstBitmapWithOverlay.addAll(UtilsFilter.getLstBitmapWithOverlay(ThumbnailUtils.extractThumbnail(PicEditActivity.this.mImagePicEditorView.getCurrentBitmap(), 100, 100)));
            return null;
        }

        public void onPostExecute(Void voidR) {
            PicEditActivity.this.mRvOverlays.setAdapter(new AdapterFilterView(PicEditActivity.this.lstBitmapWithOverlay, PicEditActivity.this, PicEditActivity.this,
                    Arrays.asList(UtilsFilter.OVERLAY_CONFIG)));
            PicEditActivity.this.slideDown(PicEditActivity.this.mRvTools);
            PicEditActivity.this.slideUp(PicEditActivity.this.overlayLayout);
            PicEditActivity.this.compareOverlay.setVisibility(View.VISIBLE);
            PicEditActivity.this.overlayIntensity.setProgress(100);
            PicEditActivity.this.showLoading(false);
        }
    }

    class ShowSplashDialog extends AsyncTask<Void, List<Bitmap>, List<Bitmap>> {
        boolean isSplash;

        public ShowSplashDialog(boolean z) {
            this.isSplash = z;
        }

        public void onPreExecute() {
            PicEditActivity.this.showLoading(true);
        }

        public List<Bitmap> doInBackground(Void... voidArr) {
            Bitmap currentBitmap = PicEditActivity.this.mImagePicEditorView.getCurrentBitmap();
            List<Bitmap> arrayList = new ArrayList<>();
            arrayList.add(currentBitmap);
            if (this.isSplash) {
                arrayList.add(UtilsFilter.getBlackAndWhiteImageFromBitmap(currentBitmap));
            } else {
                arrayList.add(UtilsFilter.getBlurImageFromBitmap(currentBitmap, 3.0f));
            }
            return arrayList;
        }

        public void onPostExecute(List<Bitmap> list) {
            if (this.isSplash) {
                SplashDialog.show(PicEditActivity.this, list.get(0), null, list.get(1), PicEditActivity.this, true);
            } else {
                SplashDialog.show(PicEditActivity.this, list.get(0), list.get(1), null, PicEditActivity.this, false);
            }
            PicEditActivity.this.showLoading(false);
        }
    }

    class SaveFilterAsBitmap extends AsyncTask<Void, Void, Bitmap> {
        SaveFilterAsBitmap() {
        }

        public void onPreExecute() {
            PicEditActivity.this.showLoading(true);
        }

        public Bitmap doInBackground(Void... voidArr) {
            final Bitmap[] bitmapArr = {null};
            PicEditActivity.this.mImagePicEditorView.saveGLSurfaceViewAsBitmap(bitmap -> bitmapArr[0] = bitmap);
            while (bitmapArr[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return bitmapArr[0];
        }

        public void onPostExecute(Bitmap bitmap) {
            PicEditActivity.this.mImagePicEditorView.setImageSource(bitmap);
            PicEditActivity.this.mImagePicEditorView.setFilterEffect("");
            PicEditActivity.this.showLoading(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActivityResult(int i, int i2, @Nullable Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 123) {
            if (i2 == -1) {
                try {
                    InputStream openInputStream = getContentResolver().openInputStream(intent.getData());
                    Bitmap decodeStream = BitmapFactory.decodeStream(openInputStream);
                    float width = (float) decodeStream.getWidth();
                    float height = (float) decodeStream.getHeight();
                    float max = Math.max(width / 1280.0f, height / 1280.0f);
                    if (max > 1.0f) {
                        decodeStream = Bitmap.createScaledBitmap(decodeStream, (int) (width / max), (int) (height / max), false);
                    }
                    if (SystemUtils.rotateBitmap(decodeStream, new ExifInterface(openInputStream).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)) != decodeStream) {
                        decodeStream.recycle();
                        decodeStream = null;
                    }
                    this.mImagePicEditorView.setImageSource(decodeStream);
                    updateLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                    MsgUtil.toastMsg(this, "Error: Can not open image");
                }
            } else {
                finish();
            }
        }
    }

    class OnLoadBitmapFromUri extends AsyncTask<String, Bitmap, Bitmap> {
        OnLoadBitmapFromUri() {
        }

        public void onPreExecute() {
            PicEditActivity.this.showLoading(true);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public Bitmap doInBackground(String... strArr) {
            try {
                Uri fromFile = Uri.parse(strArr[0]);
                Bitmap bitmap = FileUtilsss.Companion.getBitmap(PicEditActivity.this, fromFile);
                float width = (float) bitmap.getWidth();
                float height = (float) bitmap.getHeight();
                float max = Math.max(width / 1280.0f, height / 1280.0f);
                if (max > 1.0f) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width / max), (int) (height / max), false);
                }
                Bitmap rotateBitmap = SystemUtils.rotateBitmap(bitmap, new ExifInterface(PicEditActivity.this.getContentResolver().openInputStream(fromFile)).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1));
                if (rotateBitmap != bitmap) {
                    bitmap.recycle();
                }
                return rotateBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public void onPostExecute(Bitmap bitmap) {
            PicEditActivity.this.mImagePicEditorView.setImageSource(bitmap);
            PicEditActivity.this.updateLayout();
        }
    }

    public void updateLayout() {
        this.mImagePicEditorView.postDelayed(() -> {
            try {
                Display defaultDisplay = PicEditActivity.this.getWindowManager().getDefaultDisplay();
                Point point = new Point();
                defaultDisplay.getSize(point);
                int i = point.x;
                int height = PicEditActivity.this.wrapPhotoView.getHeight();
                int i2 = PicEditActivity.this.mImagePicEditorView.getGLSurfaceView().getRenderViewport().width;
                float f = (float) PicEditActivity.this.mImagePicEditorView.getGLSurfaceView().getRenderViewport().height;
                float f2 = (float) i2;
                if (((int) ((((float) i) * f) / f2)) <= height) {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
                    layoutParams.addRule(13);
                    PicEditActivity.this.mImagePicEditorView.setLayoutParams(layoutParams);
                    PicEditActivity.this.mImagePicEditorView.setVisibility(View.VISIBLE);
                } else {
                    RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams((int) ((((float) height) * f2) / f), -1);
                    layoutParams2.addRule(13);
                    PicEditActivity.this.mImagePicEditorView.setLayoutParams(layoutParams2);
                    PicEditActivity.this.mImagePicEditorView.setVisibility(View.VISIBLE);
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            PicEditActivity.this.showLoading(false);
        }, 300);
    }


    class SaveBitmapAsFile extends AsyncTask<Void, String, String> {
        SaveBitmapAsFile() {
        }

        @Override
        public void onPreExecute() {
            PicEditActivity.this.showLoading(true);
        }

        @Override
        public String doInBackground(Void... voidArr) {
            File saveBitmapAsFile = FileUtils.saveBitmapAsFile(mImagePicEditorView.getCurrentBitmap(), "Photo Editor");
            try {
                MediaScannerConnection.scanFile(PicEditActivity.this.getApplicationContext(), new String[]{saveBitmapAsFile.getAbsolutePath()}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                    @Override
                    public void onMediaScannerConnected() {

                    }

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });

                return saveBitmapAsFile.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(String str) {
            isSaved = true;
            showLoading(false);
            if (str == null) {
                Toast.makeText(getApplicationContext(), "Oop! Something went wrong", Toast.LENGTH_LONG).show();
                return;
            }

            AdsUtils.Companion.loadInterstitialAd(PicEditActivity.this, RemoteConfigUtils.Companion.adIdInterstital(),
                    new AdsUtils.Companion.FullScreenCallback() {
                        @Override
                        public void continueExecution() {
                            Intent intent = new Intent(PicEditActivity.this, CollageSaveShareActivity.class);
                            intent.putExtra("path", str);
                            intent.putExtra("type", 0);
                            startActivity(intent);
                        }
                    });
        }
    }

    public void savePhoto() {
        new SaveBitmapAsFile().execute();
    }

    private void refreshAddialog() {
        AdLoader.Builder builder = new AdLoader.Builder(this, RemoteConfigUtils.Companion.adIdNative());

        builder.forNativeAd(unifiedNativeAd -> {
            if (nativeAd != null) {
                nativeAd.destroy();
            }
            nativeAd = unifiedNativeAd;
        });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }
}