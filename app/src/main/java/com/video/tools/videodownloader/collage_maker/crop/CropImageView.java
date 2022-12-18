package com.video.tools.videodownloader.collage_maker.crop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.video.tools.videodownloader.R;
import com.video.tools.videodownloader.collage_maker.crop.animation.SimpleValueAnimator;
import com.video.tools.videodownloader.collage_maker.crop.animation.SimpleValueAnimatorListener;
import com.video.tools.videodownloader.collage_maker.crop.animation.ValueAnimatorV14;
import com.video.tools.videodownloader.collage_maker.crop.animation.ValueAnimatorV8;
import com.video.tools.videodownloader.collage_maker.crop.callback.Callback;
import com.video.tools.videodownloader.collage_maker.crop.callback.CropCallback;
import com.video.tools.videodownloader.collage_maker.crop.callback.LoadCallback;
import com.video.tools.videodownloader.collage_maker.crop.callback.SaveCallback;
import com.video.tools.videodownloader.collage_maker.crop.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

@SuppressWarnings("unused")
public class CropImageView extends ImageView {
    private static final String TAG = CropImageView.class.getSimpleName();

    private static final int HANDLE_SIZE_IN_DP = 14;
    private static final int MIN_FRAME_SIZE_IN_DP = 50;
    private static final int FRAME_STROKE_WEIGHT_IN_DP = 1;
    private static final int GUIDE_STROKE_WEIGHT_IN_DP = 1;
    private static final float DEFAULT_INITIAL_FRAME_SCALE = 1f;
    private static final int DEFAULT_ANIMATION_DURATION_MILLIS = 100;
    private static final int DEBUG_TEXT_SIZE_IN_DP = 15;

    private static final int TRANSPARENT = 0x00000000;
    private static final int TRANSLUCENT_WHITE = 0xBBFFFFFF;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int TRANSLUCENT_BLACK = 0xBB000000;


    private int mViewWidth = 0;
    private int mViewHeight = 0;
    private float mScale = 1.0f;
    private float mAngle = 0.0f;
    private float mImgWidth = 0.0f;
    private float mImgHeight = 0.0f;

    private boolean mIsInitialized = false;
    private Matrix mMatrix = null;
    private Paint mPaintTranslucent;
    private Paint mPaintFrame;
    private Paint mPaintBitmap;
    private Paint mPaintDebug;
    private RectF mFrameRect;
    private RectF mInitialFrameRect;
    private RectF mImageRect;
    private PointF mCenter = new PointF();
    private float mLastX, mLastY;
    private boolean mIsRotating = false;
    private boolean mIsAnimating = false;
    private SimpleValueAnimator mAnimator = null;
    private final Interpolator DEFAULT_INTERPOLATOR = new DecelerateInterpolator();
    private Interpolator mInterpolator = DEFAULT_INTERPOLATOR;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Uri mSourceUri = null;
    private Uri mSaveUri = null;
    private int mExifRotation = 0;
    private int mOutputMaxWidth;
    private int mOutputMaxHeight;
    private int mOutputWidth = 0;
    private int mOutputHeight = 0;
    private boolean mIsDebug = false;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;
    private int mCompressQuality = 100;
    private int mInputImageWidth = 0;
    private int mInputImageHeight = 0;
    private int mOutputImageWidth = 0;
    private int mOutputImageHeight = 0;
    private AtomicBoolean mIsLoading = new AtomicBoolean(false);
    private AtomicBoolean mIsCropping = new AtomicBoolean(false);
    private AtomicBoolean mIsSaving = new AtomicBoolean(false);
    private ExecutorService mExecutor;

    private TouchArea mTouchArea = TouchArea.OUT_OF_BOUNDS;

    private CropMode mCropMode = CropMode.SQUARE;
    private ShowMode mGuideShowMode = ShowMode.SHOW_ALWAYS;
    private ShowMode mHandleShowMode = ShowMode.SHOW_ALWAYS;
    private float mMinFrameSize;
    private int mHandleSize;
    private int mTouchPadding = 0;
    private boolean mShowGuide = true;
    private boolean mShowHandle = true;
    private boolean mIsCropEnabled = true;
    private boolean mIsEnabled = true;
    private PointF mCustomRatio = new PointF(1.0f, 1.0f);
    private float mFrameStrokeWeight = 2.0f;
    private float mGuideStrokeWeight = 2.0f;
    private int mBackgroundColor;
    private int mOverlayColor;
    private int mFrameColor;
    private int mHandleColor;
    private int mGuideColor;
    private float mInitialFrameScale; // 0.01 ~ 1.0, 0.75 is default value
    private boolean mIsAnimationEnabled = true;
    private int mAnimationDurationMillis = DEFAULT_ANIMATION_DURATION_MILLIS;
    private boolean mIsHandleShadowEnabled = true;

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mExecutor = Executors.newSingleThreadExecutor();

        float density = getDensity();
        mHandleSize = (int) (density * HANDLE_SIZE_IN_DP);
        mMinFrameSize = density * MIN_FRAME_SIZE_IN_DP;
        mFrameStrokeWeight = density * FRAME_STROKE_WEIGHT_IN_DP;
        mGuideStrokeWeight = density * GUIDE_STROKE_WEIGHT_IN_DP;

        mPaintFrame = new Paint();
        mPaintTranslucent = new Paint();
        mPaintBitmap = new Paint();
        mPaintBitmap.setFilterBitmap(true);
        mPaintDebug = new Paint();
        mPaintDebug.setAntiAlias(true);
        mPaintDebug.setStyle(Paint.Style.STROKE);
        mPaintDebug.setColor(WHITE);
        mPaintDebug.setTextSize((float) DEBUG_TEXT_SIZE_IN_DP * density);

        mMatrix = new Matrix();
        mScale = 1.0f;
        mBackgroundColor = TRANSPARENT;
        mFrameColor = WHITE;
        mOverlayColor = TRANSLUCENT_BLACK;
        mHandleColor = WHITE;
        mGuideColor = TRANSLUCENT_WHITE;

        handleStyleable(context, attrs, defStyle, density);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mode = this.mCropMode;
        ss.backgroundColor = this.mBackgroundColor;
        ss.overlayColor = this.mOverlayColor;
        ss.frameColor = this.mFrameColor;
        ss.guideShowMode = this.mGuideShowMode;
        ss.handleShowMode = this.mHandleShowMode;
        ss.showGuide = this.mShowGuide;
        ss.showHandle = this.mShowHandle;
        ss.handleSize = this.mHandleSize;
        ss.touchPadding = this.mTouchPadding;
        ss.minFrameSize = this.mMinFrameSize;
        ss.customRatioX = this.mCustomRatio.x;
        ss.customRatioY = this.mCustomRatio.y;
        ss.frameStrokeWeight = this.mFrameStrokeWeight;
        ss.guideStrokeWeight = this.mGuideStrokeWeight;
        ss.isCropEnabled = this.mIsCropEnabled;
        ss.handleColor = this.mHandleColor;
        ss.guideColor = this.mGuideColor;
        ss.initialFrameScale = this.mInitialFrameScale;
        ss.angle = this.mAngle;
        ss.isAnimationEnabled = this.mIsAnimationEnabled;
        ss.animationDuration = this.mAnimationDurationMillis;
        ss.exifRotation = this.mExifRotation;
        ss.sourceUri = this.mSourceUri;
        ss.saveUri = this.mSaveUri;
        ss.compressFormat = this.mCompressFormat;
        ss.compressQuality = this.mCompressQuality;
        ss.isDebug = this.mIsDebug;
        ss.outputMaxWidth = this.mOutputMaxWidth;
        ss.outputMaxHeight = this.mOutputMaxHeight;
        ss.outputWidth = this.mOutputWidth;
        ss.outputHeight = this.mOutputHeight;
        ss.isHandleShadowEnabled = this.mIsHandleShadowEnabled;
        ss.inputImageWidth = this.mInputImageWidth;
        ss.inputImageHeight = this.mInputImageHeight;
        ss.outputImageWidth = this.mOutputImageWidth;
        ss.outputImageHeight = this.mOutputImageHeight;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mCropMode = ss.mode;
        this.mBackgroundColor = ss.backgroundColor;
        this.mOverlayColor = ss.overlayColor;
        this.mFrameColor = ss.frameColor;
        this.mGuideShowMode = ss.guideShowMode;
        this.mHandleShowMode = ss.handleShowMode;
        this.mShowGuide = ss.showGuide;
        this.mShowHandle = ss.showHandle;
        this.mHandleSize = ss.handleSize;
        this.mTouchPadding = ss.touchPadding;
        this.mMinFrameSize = ss.minFrameSize;
        this.mCustomRatio = new PointF(ss.customRatioX, ss.customRatioY);
        this.mFrameStrokeWeight = ss.frameStrokeWeight;
        this.mGuideStrokeWeight = ss.guideStrokeWeight;
        this.mIsCropEnabled = ss.isCropEnabled;
        this.mHandleColor = ss.handleColor;
        this.mGuideColor = ss.guideColor;
        this.mInitialFrameScale = ss.initialFrameScale;
        this.mAngle = ss.angle;
        this.mIsAnimationEnabled = ss.isAnimationEnabled;
        this.mAnimationDurationMillis = ss.animationDuration;
        this.mExifRotation = ss.exifRotation;
        this.mSourceUri = ss.sourceUri;
        this.mSaveUri = ss.saveUri;
        this.mCompressFormat = ss.compressFormat;
        this.mCompressQuality = ss.compressQuality;
        this.mIsDebug = ss.isDebug;
        this.mOutputMaxWidth = ss.outputMaxWidth;
        this.mOutputMaxHeight = ss.outputMaxHeight;
        this.mOutputWidth = ss.outputWidth;
        this.mOutputHeight = ss.outputHeight;
        this.mIsHandleShadowEnabled = ss.isHandleShadowEnabled;
        this.mInputImageWidth = ss.inputImageWidth;
        this.mInputImageHeight = ss.inputImageHeight;
        this.mOutputImageWidth = ss.outputImageWidth;
        this.mOutputImageHeight = ss.outputImageHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(viewWidth, viewHeight);

        mViewWidth = viewWidth - getPaddingLeft() - getPaddingRight();
        mViewHeight = viewHeight - getPaddingTop() - getPaddingBottom();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getDrawable() != null) setupLayout(mViewWidth, mViewHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);

        if (mIsInitialized) {
            setMatrix();
            Bitmap bm = getBitmap();
            if (bm != null) {
                canvas.drawBitmap(bm, mMatrix, mPaintBitmap);
                drawCropFrame(canvas);
            }

            if (mIsDebug) {
                drawDebugInfo(canvas);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mExecutor.shutdown();
        super.onDetachedFromWindow();
    }

    private void handleStyleable(Context context, AttributeSet attrs, int defStyle, float mDensity) {
        TypedArray ta =
                context.obtainStyledAttributes(attrs, R.styleable.scv_CropImageView, defStyle, 0);
        Drawable drawable;
        mCropMode = CropMode.SQUARE;
        try {
            drawable = ta.getDrawable(R.styleable.scv_CropImageView_scv_img_src);
            if (drawable != null) setImageDrawable(drawable);
            for (CropMode mode : CropMode.values()) {
                if (ta.getInt(R.styleable.scv_CropImageView_scv_crop_mode, 3) == mode.getId()) {
                    mCropMode = mode;
                    break;
                }
            }
            mBackgroundColor =
                    ta.getColor(R.styleable.scv_CropImageView_scv_background_color, TRANSPARENT);
            mOverlayColor =
                    ta.getColor(R.styleable.scv_CropImageView_scv_overlay_color, TRANSLUCENT_BLACK);
            mFrameColor = ta.getColor(R.styleable.scv_CropImageView_scv_frame_color, WHITE);
            mHandleColor = ta.getColor(R.styleable.scv_CropImageView_scv_handle_color, WHITE);
            mGuideColor = ta.getColor(R.styleable.scv_CropImageView_scv_guide_color, TRANSLUCENT_WHITE);
            for (ShowMode mode : ShowMode.values()) {
                if (ta.getInt(R.styleable.scv_CropImageView_scv_guide_show_mode, 1) == mode.getId()) {
                    mGuideShowMode = mode;
                    break;
                }
            }

            for (ShowMode mode : ShowMode.values()) {
                if (ta.getInt(R.styleable.scv_CropImageView_scv_handle_show_mode, 1) == mode.getId()) {
                    mHandleShowMode = mode;
                    break;
                }
            }
            setGuideShowMode(mGuideShowMode);
            setHandleShowMode(mHandleShowMode);
            mHandleSize = ta.getDimensionPixelSize(R.styleable.scv_CropImageView_scv_handle_size,
                    (int) (HANDLE_SIZE_IN_DP * mDensity));
            mTouchPadding = ta.getDimensionPixelSize(R.styleable.scv_CropImageView_scv_touch_padding, 0);
            mMinFrameSize = ta.getDimensionPixelSize(R.styleable.scv_CropImageView_scv_min_frame_size,
                    (int) (MIN_FRAME_SIZE_IN_DP * mDensity));
            mFrameStrokeWeight =
                    ta.getDimensionPixelSize(R.styleable.scv_CropImageView_scv_frame_stroke_weight,
                            (int) (FRAME_STROKE_WEIGHT_IN_DP * mDensity));
            mGuideStrokeWeight =
                    ta.getDimensionPixelSize(R.styleable.scv_CropImageView_scv_guide_stroke_weight,
                            (int) (GUIDE_STROKE_WEIGHT_IN_DP * mDensity));
            mIsCropEnabled = ta.getBoolean(R.styleable.scv_CropImageView_scv_crop_enabled, true);
            mInitialFrameScale = constrain(
                    ta.getFloat(R.styleable.scv_CropImageView_scv_initial_frame_scale,
                            DEFAULT_INITIAL_FRAME_SCALE), 0.01f, 1.0f, DEFAULT_INITIAL_FRAME_SCALE);
            mIsAnimationEnabled =
                    ta.getBoolean(R.styleable.scv_CropImageView_scv_animation_enabled, true);
            mAnimationDurationMillis = ta.getInt(R.styleable.scv_CropImageView_scv_animation_duration,
                    DEFAULT_ANIMATION_DURATION_MILLIS);
            mIsHandleShadowEnabled =
                    ta.getBoolean(R.styleable.scv_CropImageView_scv_handle_shadow_enabled, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ta.recycle();
        }
    }

    private void drawDebugInfo(Canvas canvas) {
        Paint.FontMetrics fontMetrics = mPaintDebug.getFontMetrics();
        mPaintDebug.measureText("W");
        int textHeight = (int) (fontMetrics.descent - fontMetrics.ascent);
        int x = (int) (mImageRect.left + (float) mHandleSize * 0.5f * getDensity());
        int y = (int) (mImageRect.top + textHeight + (float) mHandleSize * 0.5f * getDensity());
        StringBuilder builder = new StringBuilder();
        builder.append("LOADED FROM: ").append(mSourceUri != null ? "Uri" : "Bitmap");
        canvas.drawText(builder.toString(), x, y, mPaintDebug);
        builder = new StringBuilder();

        if (mSourceUri == null) {
            builder.append("INPUT_IMAGE_SIZE: ")
                    .append((int) mImgWidth)
                    .append("x")
                    .append((int) mImgHeight);
            y += textHeight;
            canvas.drawText(builder.toString(), x, y, mPaintDebug);
            builder = new StringBuilder();
        } else {
            builder = new StringBuilder().append("INPUT_IMAGE_SIZE: ")
                    .append(mInputImageWidth)
                    .append("x")
                    .append(mInputImageHeight);
            y += textHeight;
            canvas.drawText(builder.toString(), x, y, mPaintDebug);
            builder = new StringBuilder();
        }
        builder.append("LOADED_IMAGE_SIZE: ")
                .append(getBitmap().getWidth())
                .append("x")
                .append(getBitmap().getHeight());
        y += textHeight;
        canvas.drawText(builder.toString(), x, y, mPaintDebug);
        builder = new StringBuilder();
        if (mOutputImageWidth > 0 && mOutputImageHeight > 0) {
            builder.append("OUTPUT_IMAGE_SIZE: ")
                    .append(mOutputImageWidth)
                    .append("x")
                    .append(mOutputImageHeight);
            y += textHeight;
            canvas.drawText(builder.toString(), x, y, mPaintDebug);
            builder = new StringBuilder().append("EXIF ROTATION: ").append(mExifRotation);
            y += textHeight;
            canvas.drawText(builder.toString(), x, y, mPaintDebug);
            builder = new StringBuilder().append("CURRENT_ROTATION: ").append((int) mAngle);
            y += textHeight;
            canvas.drawText(builder.toString(), x, y, mPaintDebug);
        }
        builder = new StringBuilder();
        builder.append("FRAME_RECT: ").append(mFrameRect.toString());
        y += textHeight;
        canvas.drawText(builder.toString(), x, y, mPaintDebug);
        builder = new StringBuilder();
        builder.append("ACTUAL_CROP_RECT: ").append(getActualCropRect() != null ? getActualCropRect().toString() : "");
        y += textHeight;
        canvas.drawText(builder.toString(), x, y, mPaintDebug);
    }

    private void drawCropFrame(Canvas canvas) {
        if (!mIsCropEnabled) return;
        if (mIsRotating) return;
        drawOverlay(canvas);
        drawFrame(canvas);
        if (mShowGuide) drawGuidelines(canvas);
        if (mShowHandle) drawHandles(canvas);
    }

    private void drawOverlay(Canvas canvas) {
        mPaintTranslucent.setAntiAlias(true);
        mPaintTranslucent.setFilterBitmap(true);
        mPaintTranslucent.setColor(mOverlayColor);
        mPaintTranslucent.setStyle(Paint.Style.FILL);
        Path path = new Path();
        RectF overlayRect =
                new RectF((float) Math.floor(mImageRect.left), (float) Math.floor(mImageRect.top),
                        (float) Math.ceil(mImageRect.right), (float) Math.ceil(mImageRect.bottom));
        if (!mIsAnimating && (mCropMode == CropMode.CIRCLE || mCropMode == CropMode.CIRCLE_SQUARE)) {
            path.addRect(overlayRect, Path.Direction.CW);
            PointF circleCenter = new PointF((mFrameRect.left + mFrameRect.right) / 2,
                    (mFrameRect.top + mFrameRect.bottom) / 2);
            float circleRadius = (mFrameRect.right - mFrameRect.left) / 2;
            path.addCircle(circleCenter.x, circleCenter.y, circleRadius, Path.Direction.CCW);
            canvas.drawPath(path, mPaintTranslucent);
        } else {
            path.addRect(overlayRect, Path.Direction.CW);
            path.addRect(mFrameRect, Path.Direction.CCW);
            canvas.drawPath(path, mPaintTranslucent);
        }
    }

    private void drawFrame(Canvas canvas) {
        mPaintFrame.setAntiAlias(true);
        mPaintFrame.setFilterBitmap(true);
        mPaintFrame.setStyle(Paint.Style.STROKE);
        mPaintFrame.setColor(mFrameColor);
        mPaintFrame.setStrokeWidth(mFrameStrokeWeight);
        canvas.drawRect(mFrameRect, mPaintFrame);
    }

    private void drawGuidelines(Canvas canvas) {
        mPaintFrame.setColor(mGuideColor);
        mPaintFrame.setStrokeWidth(mGuideStrokeWeight);
        float h1 = mFrameRect.left + (mFrameRect.right - mFrameRect.left) / 3.0f;
        float h2 = mFrameRect.right - (mFrameRect.right - mFrameRect.left) / 3.0f;
        float v1 = mFrameRect.top + (mFrameRect.bottom - mFrameRect.top) / 3.0f;
        float v2 = mFrameRect.bottom - (mFrameRect.bottom - mFrameRect.top) / 3.0f;
        canvas.drawLine(h1, mFrameRect.top, h1, mFrameRect.bottom, mPaintFrame);
        canvas.drawLine(h2, mFrameRect.top, h2, mFrameRect.bottom, mPaintFrame);
        canvas.drawLine(mFrameRect.left, v1, mFrameRect.right, v1, mPaintFrame);
        canvas.drawLine(mFrameRect.left, v2, mFrameRect.right, v2, mPaintFrame);
    }

    private void drawHandles(Canvas canvas) {
        mPaintFrame.setStyle(Paint.Style.FILL);
        mPaintFrame.setStrokeWidth(8);
        mPaintFrame.setColor(mHandleColor);
//        canvas.drawCircle(mFrameRect.left, mFrameRect.top, mHandleSize, mPaintFrame);
//        canvas.drawCircle(mFrameRect.right, mFrameRect.top, mHandleSize, mPaintFrame);
//        canvas.drawCircle(mFrameRect.left, mFrameRect.bottom, mHandleSize, mPaintFrame);
//        canvas.drawCircle(mFrameRect.right, mFrameRect.bottom, mHandleSize, mPaintFrame);
//        canvas.drawCircle(mFrameRect.centerX(), mFrameRect.bottom, mHandleSize, mPaintFrame);
//        canvas.drawCircle(mFrameRect.centerX(), mFrameRect.top, mHandleSize, mPaintFrame);
//        canvas.drawCircle(mFrameRect.left, mFrameRect.centerY(), mHandleSize, mPaintFrame);
//        canvas.drawCircle(mFrameRect.right, mFrameRect.centerY(), mHandleSize, mPaintFrame);

        //top - left
        canvas.drawLine(mFrameRect.left - 4, mFrameRect.top - 4, mFrameRect.left + 56, mFrameRect.top - 4, mPaintFrame);
        canvas.drawLine(mFrameRect.left - 4, mFrameRect.top - 8, mFrameRect.left - 4, mFrameRect.top + 56, mPaintFrame);

        //top - right
        canvas.drawLine(mFrameRect.right - 56, mFrameRect.top - 4, mFrameRect.right + 8, mFrameRect.top - 4, mPaintFrame);
        canvas.drawLine(mFrameRect.right + 4, mFrameRect.top - 4, mFrameRect.right + 4, mFrameRect.top + 56, mPaintFrame);

        //left - bottom
        canvas.drawLine(mFrameRect.left - 4, mFrameRect.bottom + 4, mFrameRect.left + 56, mFrameRect.bottom + 4, mPaintFrame);
        canvas.drawLine(mFrameRect.left - 4, mFrameRect.bottom - 56, mFrameRect.left - 4, mFrameRect.bottom + 8, mPaintFrame);

        //right - bottom
        canvas.drawLine(mFrameRect.right - 56, mFrameRect.bottom + 4, mFrameRect.right + 8, mFrameRect.bottom + 4, mPaintFrame);
        canvas.drawLine(mFrameRect.right + 4, mFrameRect.bottom - 56, mFrameRect.right + 4, mFrameRect.bottom + 8, mPaintFrame);

        //center - top
        canvas.drawLine(mFrameRect.centerX() - 36, mFrameRect.top - 4, mFrameRect.centerX() + 36, mFrameRect.top - 4, mPaintFrame);

        //center - bottom
        canvas.drawLine(mFrameRect.centerX() - 36, mFrameRect.bottom + 4, mFrameRect.centerX() + 36, mFrameRect.bottom + 4, mPaintFrame);

        //center - left
        canvas.drawLine(mFrameRect.left - 4, mFrameRect.centerY() - 36, mFrameRect.left - 4, mFrameRect.centerY() + 36, mPaintFrame);

        //center - right
        canvas.drawLine(mFrameRect.right + 4, mFrameRect.centerY() - 36, mFrameRect.right + 4, mFrameRect.centerY() + 36, mPaintFrame);
    }

    private void drawHandleShadows(Canvas canvas) {
        mPaintFrame.setStyle(Paint.Style.FILL);
        mPaintFrame.setColor(TRANSLUCENT_BLACK);
        RectF rect = new RectF(mFrameRect);
        rect.offset(0, 1);
        canvas.drawCircle(rect.left, rect.top, mHandleSize, mPaintFrame);
        canvas.drawCircle(rect.right, rect.top, mHandleSize, mPaintFrame);
        canvas.drawCircle(rect.left, rect.bottom, mHandleSize, mPaintFrame);
        canvas.drawCircle(rect.right, rect.bottom, mHandleSize, mPaintFrame);
    }

    private void setMatrix() {
        mMatrix.reset();
        mMatrix.setTranslate(mCenter.x - mImgWidth * 0.5f, mCenter.y - mImgHeight * 0.5f);
        mMatrix.postScale(mScale, mScale, mCenter.x, mCenter.y);
        mMatrix.postRotate(mAngle, mCenter.x, mCenter.y);
    }

    private void setupLayout(int viewW, int viewH) {
        if (viewW == 0 || viewH == 0) return;
        setCenter(new PointF(getPaddingLeft() + viewW * 0.5f, getPaddingTop() + viewH * 0.5f));
        setScale(calcScale(viewW, viewH, mAngle));
        setMatrix();
        mImageRect = calcImageRect(new RectF(0f, 0f, mImgWidth, mImgHeight), mMatrix);

        if (mInitialFrameRect != null) {
            mFrameRect = applyInitialFrameRect(mInitialFrameRect);
        } else {
            mFrameRect = calcFrameRect(mImageRect);
        }

        mIsInitialized = true;
        invalidate();
    }

    private float calcScale(int viewW, int viewH, float angle) {
        mImgWidth = getDrawable().getIntrinsicWidth();
        mImgHeight = getDrawable().getIntrinsicHeight();
        if (mImgWidth <= 0) mImgWidth = viewW;
        if (mImgHeight <= 0) mImgHeight = viewH;
        float viewRatio = (float) viewW / (float) viewH;
        float imgRatio = getRotatedWidth(angle) / getRotatedHeight(angle);
        float scale = 1.0f;
        if (imgRatio >= viewRatio) {
            scale = viewW / getRotatedWidth(angle);
        } else if (imgRatio < viewRatio) {
            scale = viewH / getRotatedHeight(angle);
        }
        return scale;
    }

    private RectF calcImageRect(RectF rect, Matrix matrix) {
        RectF applied = new RectF();
        matrix.mapRect(applied, rect);
        return applied;
    }

    private RectF calcFrameRect(RectF imageRect) {
        float frameW = getRatioX(imageRect.width());
        float frameH = getRatioY(imageRect.height());
        float imgRatio = imageRect.width() / imageRect.height();
        float frameRatio = frameW / frameH;
        float l = imageRect.left, t = imageRect.top, r = imageRect.right, b = imageRect.bottom;
        if (frameRatio >= imgRatio) {
            l = imageRect.left;
            r = imageRect.right;
            float hy = (imageRect.top + imageRect.bottom) * 0.5f;
            float hh = (imageRect.width() / frameRatio) * 0.5f;
            t = hy - hh;
            b = hy + hh;
        } else if (frameRatio < imgRatio) {
            t = imageRect.top;
            b = imageRect.bottom;
            float hx = (imageRect.left + imageRect.right) * 0.5f;
            float hw = imageRect.height() * frameRatio * 0.5f;
            l = hx - hw;
            r = hx + hw;
        }
        float w = r - l;
        float h = b - t;
        float cx = l + w / 2;
        float cy = t + h / 2;
        float sw = w * mInitialFrameScale;
        float sh = h * mInitialFrameScale;
        return new RectF(cx - sw / 2, cy - sh / 2, cx + sw / 2, cy + sh / 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsInitialized) return false;
        if (!mIsCropEnabled) return false;
        if (!mIsEnabled) return false;
        if (mIsRotating) return false;
        if (mIsAnimating) return false;
        if (mIsLoading.get()) return false;
        if (mIsCropping.get()) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onDown(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                onMove(event);
                if (mTouchArea != TouchArea.OUT_OF_BOUNDS) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onCancel();
                return true;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                onUp(event);
                return true;
        }
        return false;
    }

    private void onDown(MotionEvent e) {
        invalidate();
        mLastX = e.getX();
        mLastY = e.getY();
        checkTouchArea(e.getX(), e.getY());
    }

    private void onMove(MotionEvent e) {
        float diffX = e.getX() - mLastX;
        float diffY = e.getY() - mLastY;
        switch (mTouchArea) {
            case CENTER:
                moveFrame(diffX, diffY);
                break;
            case LEFT_TOP:
                moveHandleLT(diffX, diffY);
                break;
            case RIGHT_TOP:
                moveHandleRT(diffX, diffY);
                break;
            case LEFT_BOTTOM:
                moveHandleLB(diffX, diffY);
                break;
            case CENTER_LEFT:
                if (mCropMode == CropMode.FREE)
                    moveHandleLeftCenter(diffX, diffY);
                break;
            case CENTER_RIGHT:
                if (mCropMode == CropMode.FREE)
                    moveHandleRightCenter(diffX, diffY);
                break;
            case RIGHT_BOTTOM:
                moveHandleRB(diffX, diffY);
                break;
            case CENTER_TOP:
                if (mCropMode == CropMode.FREE)
                    moveHandleTOP(diffX, diffY);
                break;
            case CENTER_BOTTOM:
                if (mCropMode == CropMode.FREE)
                    moveHandleBOTTOM(diffX, diffY);
                break;
            case OUT_OF_BOUNDS:
                break;
        }
        invalidate();
        mLastX = e.getX();
        mLastY = e.getY();
    }

    private void onUp(MotionEvent e) {
        if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = false;
        if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = false;
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
        invalidate();
    }

    private void onCancel() {
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
        invalidate();
    }


    private void checkTouchArea(float x, float y) {
        if (isInsideCornerLeftTop(x, y)) {
            mTouchArea = TouchArea.LEFT_TOP;
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isTop(x, y)) {
            mTouchArea = TouchArea.CENTER_TOP;
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isBottom(x, y)) {
            mTouchArea = TouchArea.CENTER_BOTTOM;
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isInsideCornerRightTop(x, y)) {
            mTouchArea = TouchArea.RIGHT_TOP;
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isInsideCornerLeftBottom(x, y)) {
            mTouchArea = TouchArea.LEFT_BOTTOM;
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isInsideCornerRightBottom(x, y)) {
            mTouchArea = TouchArea.RIGHT_BOTTOM;
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }

        if (isCenterLeft(x, y)) {
            mTouchArea = TouchArea.CENTER_LEFT;
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isCenterRight(x, y)) {
            mTouchArea = TouchArea.CENTER_RIGHT;
            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isInsideFrame(x, y)) {
            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            mTouchArea = TouchArea.CENTER;
            return;
        }
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
    }

    private boolean isInsideFrame(float x, float y) {
        if (mFrameRect.left <= x && mFrameRect.right >= x) {
            if (mFrameRect.top <= y && mFrameRect.bottom >= y) {
                mTouchArea = TouchArea.CENTER;
                return true;
            }
        }
        return false;
    }

    private boolean isInsideCornerLeftTop(float x, float y) {
        float dx = x - mFrameRect.left;
        float dy = y - mFrameRect.top;
        float d = dx * dx + dy * dy;
        return sq(mHandleSize + mTouchPadding) >= d;
    }

    private boolean isTop(float x, float y) {
        float dx = x - mFrameRect.centerX();
        float dy = y - mFrameRect.top;
        float d = dx * dx + dy * dy;
        return sq(mHandleSize + mTouchPadding) >= d;
    }

    private boolean isBottom(float x, float y) {
        float dx = x - mFrameRect.centerX();
        float dy = y - mFrameRect.bottom;
        float d = dx * dx + dy * dy;
        return sq(mHandleSize + mTouchPadding) >= d;
    }


    private boolean isCenterLeft(float x, float y) {
        float dx = x - mFrameRect.left;
        float dy = y - mFrameRect.centerY();
        float d = dx * dx + dy * dy;
        return sq(mHandleSize + mTouchPadding) >= d;
    }

    private boolean isCenterRight(float x, float y) {
        float dx = x - mFrameRect.right;
        float dy = y - mFrameRect.centerY();
        float d = dx * dx + dy * dy;
        return sq(mHandleSize + mTouchPadding) >= d;
    }

    private boolean isInsideCornerRightTop(float x, float y) {
        float dx = x - mFrameRect.right;
        float dy = y - mFrameRect.top;
        float d = dx * dx + dy * dy;
        return sq(mHandleSize + mTouchPadding) >= d;
    }

    private boolean isInsideCornerLeftBottom(float x, float y) {
        float dx = x - mFrameRect.left;
        float dy = y - mFrameRect.bottom;
        float d = dx * dx + dy * dy;
        return sq(mHandleSize + mTouchPadding) >= d;
    }

    private boolean isInsideCornerRightBottom(float x, float y) {
        float dx = x - mFrameRect.right;
        float dy = y - mFrameRect.bottom;
        float d = dx * dx + dy * dy;
        return sq(mHandleSize + mTouchPadding) >= d;
    }

    private void moveFrame(float x, float y) {
        mFrameRect.left += x;
        mFrameRect.right += x;
        mFrameRect.top += y;
        mFrameRect.bottom += y;
        checkMoveBounds();
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void moveHandleLT(float diffX, float diffY) {
        if (mCropMode == CropMode.FREE) {
            mFrameRect.left += diffX;
            mFrameRect.top += diffY;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.top -= offsetY;
            }
            checkScaleBounds();
        } else {
            float dx = diffX;
            float dy = diffX * getRatioY() / getRatioX();
            mFrameRect.left += dx;
            mFrameRect.top += dy;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
                float offsetY = offsetX * getRatioY() / getRatioX();
                mFrameRect.top -= offsetY;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.top -= offsetY;
                float offsetX = offsetY * getRatioX() / getRatioY();
                mFrameRect.left -= offsetX;
            }
            float ox, oy;
            if (!isInsideHorizontal(mFrameRect.left)) {
                ox = mImageRect.left - mFrameRect.left;
                mFrameRect.left += ox;
                oy = ox * getRatioY() / getRatioX();
                mFrameRect.top += oy;
            }
            if (!isInsideVertical(mFrameRect.top)) {
                oy = mImageRect.top - mFrameRect.top;
                mFrameRect.top += oy;
                ox = oy * getRatioX() / getRatioY();
                mFrameRect.left += ox;
            }
        }
    }

    private void moveHandleTOP(float diffX, float diffY) {
        if (mCropMode == CropMode.FREE) {
            mFrameRect.top += diffY;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.top -= offsetY;
            }
            checkScaleBounds();
        } else {
            float dx = diffX;
            float dy = diffX * getRatioY() / getRatioX();
            mFrameRect.left += dx;
            mFrameRect.top += dy;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
                float offsetY = offsetX * getRatioY() / getRatioX();
                mFrameRect.top -= offsetY;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.top -= offsetY;
                float offsetX = offsetY * getRatioX() / getRatioY();
                mFrameRect.left -= offsetX;
            }
            float ox, oy;
            if (!isInsideHorizontal(mFrameRect.left)) {
                ox = mImageRect.left - mFrameRect.left;
                mFrameRect.left += ox;
                oy = ox * getRatioY() / getRatioX();
                mFrameRect.top += oy;
            }
            if (!isInsideVertical(mFrameRect.top)) {
                oy = mImageRect.top - mFrameRect.top;
                mFrameRect.top += oy;
                ox = oy * getRatioX() / getRatioY();
                mFrameRect.left += ox;
            }
        }
    }

    private void moveHandleBOTTOM(float diffX, float diffY) {
        if (mCropMode == CropMode.FREE) {
            mFrameRect.bottom += diffY;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.bottom += offsetY;
            }
            checkScaleBounds();
        } else {
            float dx = diffX;
            float dy = diffX * getRatioY() / getRatioX();
            mFrameRect.left += dx;
            mFrameRect.top += dy;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
                float offsetY = offsetX * getRatioY() / getRatioX();
                mFrameRect.top -= offsetY;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.top -= offsetY;
                float offsetX = offsetY * getRatioX() / getRatioY();
                mFrameRect.left -= offsetX;
            }
            float ox, oy;
            if (!isInsideHorizontal(mFrameRect.left)) {
                ox = mImageRect.left - mFrameRect.left;
                mFrameRect.left += ox;
                oy = ox * getRatioY() / getRatioX();
                mFrameRect.top += oy;
            }
            if (!isInsideVertical(mFrameRect.top)) {
                oy = mImageRect.top - mFrameRect.top;
                mFrameRect.top += oy;
                ox = oy * getRatioX() / getRatioY();
                mFrameRect.left += ox;
            }
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void moveHandleRT(float diffX, float diffY) {
        if (mCropMode == CropMode.FREE) {
            mFrameRect.right += diffX;
            mFrameRect.top += diffY;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.right += offsetX;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.top -= offsetY;
            }
            checkScaleBounds();
        } else {
            float dx = diffX;
            float dy = diffX * getRatioY() / getRatioX();
            mFrameRect.right += dx;
            mFrameRect.top -= dy;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.right += offsetX;
                float offsetY = offsetX * getRatioY() / getRatioX();
                mFrameRect.top -= offsetY;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.top -= offsetY;
                float offsetX = offsetY * getRatioX() / getRatioY();
                mFrameRect.right += offsetX;
            }
            float ox, oy;
            if (!isInsideHorizontal(mFrameRect.right)) {
                ox = mFrameRect.right - mImageRect.right;
                mFrameRect.right -= ox;
                oy = ox * getRatioY() / getRatioX();
                mFrameRect.top += oy;
            }
            if (!isInsideVertical(mFrameRect.top)) {
                oy = mImageRect.top - mFrameRect.top;
                mFrameRect.top += oy;
                ox = oy * getRatioX() / getRatioY();
                mFrameRect.right -= ox;
            }
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void moveHandleLB(float diffX, float diffY) {
        if (mCropMode == CropMode.FREE) {
            mFrameRect.left += diffX;
            mFrameRect.bottom += diffY;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.bottom += offsetY;
            }
            checkScaleBounds();
        } else {
            float dx = diffX;
            float dy = diffX * getRatioY() / getRatioX();
            mFrameRect.left += dx;
            mFrameRect.bottom -= dy;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
                float offsetY = offsetX * getRatioY() / getRatioX();
                mFrameRect.bottom += offsetY;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.bottom += offsetY;
                float offsetX = offsetY * getRatioX() / getRatioY();
                mFrameRect.left -= offsetX;
            }
            float ox, oy;
            if (!isInsideHorizontal(mFrameRect.left)) {
                ox = mImageRect.left - mFrameRect.left;
                mFrameRect.left += ox;
                oy = ox * getRatioY() / getRatioX();
                mFrameRect.bottom -= oy;
            }
            if (!isInsideVertical(mFrameRect.bottom)) {
                oy = mFrameRect.bottom - mImageRect.bottom;
                mFrameRect.bottom -= oy;
                ox = oy * getRatioX() / getRatioY();
                mFrameRect.left += ox;
            }
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void moveHandleLeftCenter(float diffX, float diffY) {
        if (mCropMode == CropMode.FREE) {
            mFrameRect.left += diffX;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        } else {
            float dx = diffX;
            float dy = diffX * getRatioY() / getRatioX();
            mFrameRect.left += dx;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.left -= offsetX;
                float offsetY = offsetX * getRatioY() / getRatioX();
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                float offsetX = offsetY * getRatioX() / getRatioY();
                mFrameRect.left -= offsetX;
            }
            float ox, oy;
            if (!isInsideHorizontal(mFrameRect.left)) {
                ox = mImageRect.left - mFrameRect.left;
                mFrameRect.left += ox;
                oy = ox * getRatioY() / getRatioX();
            }
            if (!isInsideVertical(mFrameRect.bottom)) {
                oy = mFrameRect.bottom - mImageRect.bottom;
                ox = oy * getRatioX() / getRatioY();
                mFrameRect.left += ox;
            }
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void moveHandleRightCenter(float diffX, float diffY) {
        if (mCropMode == CropMode.FREE) {
            mFrameRect.right += diffX;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.right += offsetX;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
            }
            checkScaleBounds();
        } else {
            float dx = diffX;
            float dy = diffX * getRatioY() / getRatioX();
            mFrameRect.right += dx;
            mFrameRect.bottom += dy;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.right += offsetX;
                float offsetY = offsetX * getRatioY() / getRatioX();
                mFrameRect.bottom += offsetY;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.bottom += offsetY;
                float offsetX = offsetY * getRatioX() / getRatioY();
                mFrameRect.right += offsetX;
            }
            float ox, oy;
            if (!isInsideHorizontal(mFrameRect.right)) {
                ox = mFrameRect.right - mImageRect.right;
                mFrameRect.right -= ox;
                oy = ox * getRatioY() / getRatioX();
                mFrameRect.bottom -= oy;
            }
            if (!isInsideVertical(mFrameRect.bottom)) {
                oy = mFrameRect.bottom - mImageRect.bottom;
                mFrameRect.bottom -= oy;
                ox = oy * getRatioX() / getRatioY();
                mFrameRect.right -= ox;
            }
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void moveHandleRB(float diffX, float diffY) {
        if (mCropMode == CropMode.FREE) {
            mFrameRect.right += diffX;
            mFrameRect.bottom += diffY;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.right += offsetX;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.bottom += offsetY;
            }
            checkScaleBounds();
        } else {
            float dx = diffX;
            float dy = diffX * getRatioY() / getRatioX();
            mFrameRect.right += dx;
            mFrameRect.bottom += dy;
            if (isWidthTooSmall()) {
                float offsetX = mMinFrameSize - getFrameW();
                mFrameRect.right += offsetX;
                float offsetY = offsetX * getRatioY() / getRatioX();
                mFrameRect.bottom += offsetY;
            }
            if (isHeightTooSmall()) {
                float offsetY = mMinFrameSize - getFrameH();
                mFrameRect.bottom += offsetY;
                float offsetX = offsetY * getRatioX() / getRatioY();
                mFrameRect.right += offsetX;
            }
            float ox, oy;
            if (!isInsideHorizontal(mFrameRect.right)) {
                ox = mFrameRect.right - mImageRect.right;
                mFrameRect.right -= ox;
                oy = ox * getRatioY() / getRatioX();
                mFrameRect.bottom -= oy;
            }
            if (!isInsideVertical(mFrameRect.bottom)) {
                oy = mFrameRect.bottom - mImageRect.bottom;
                mFrameRect.bottom -= oy;
                ox = oy * getRatioX() / getRatioY();
                mFrameRect.right -= ox;
            }
        }
    }

    private void checkScaleBounds() {
        float lDiff = mFrameRect.left - mImageRect.left;
        float rDiff = mFrameRect.right - mImageRect.right;
        float tDiff = mFrameRect.top - mImageRect.top;
        float bDiff = mFrameRect.bottom - mImageRect.bottom;

        if (lDiff < 0) {
            mFrameRect.left -= lDiff;
        }
        if (rDiff > 0) {
            mFrameRect.right -= rDiff;
        }
        if (tDiff < 0) {
            mFrameRect.top -= tDiff;
        }
        if (bDiff > 0) {
            mFrameRect.bottom -= bDiff;
        }
    }

    private void checkMoveBounds() {
        float diff = mFrameRect.left - mImageRect.left;
        if (diff < 0) {
            mFrameRect.left -= diff;
            mFrameRect.right -= diff;
        }
        diff = mFrameRect.right - mImageRect.right;
        if (diff > 0) {
            mFrameRect.left -= diff;
            mFrameRect.right -= diff;
        }
        diff = mFrameRect.top - mImageRect.top;
        if (diff < 0) {
            mFrameRect.top -= diff;
            mFrameRect.bottom -= diff;
        }
        diff = mFrameRect.bottom - mImageRect.bottom;
        if (diff > 0) {
            mFrameRect.top -= diff;
            mFrameRect.bottom -= diff;
        }
    }

    private boolean isInsideHorizontal(float x) {
        return mImageRect.left <= x && mImageRect.right >= x;
    }

    private boolean isInsideVertical(float y) {
        return mImageRect.top <= y && mImageRect.bottom >= y;
    }

    private boolean isWidthTooSmall() {
        return getFrameW() < mMinFrameSize;
    }

    private boolean isHeightTooSmall() {
        return getFrameH() < mMinFrameSize;
    }


    private void recalculateFrameRect(int durationMillis) {
        if (mImageRect == null) return;
        if (mIsAnimating) {
            getAnimator().cancelAnimation();
        }
        final RectF currentRect = new RectF(mFrameRect);
        final RectF newRect = calcFrameRect(mImageRect);
        final float diffL = newRect.left - currentRect.left;
        final float diffT = newRect.top - currentRect.top;
        final float diffR = newRect.right - currentRect.right;
        final float diffB = newRect.bottom - currentRect.bottom;
        if (mIsAnimationEnabled) {
            SimpleValueAnimator animator = getAnimator();
            animator.addAnimatorListener(new SimpleValueAnimatorListener() {
                @Override
                public void onAnimationStarted() {
                    mIsAnimating = true;
                }

                @Override
                public void onAnimationUpdated(float scale) {
                    mFrameRect = new RectF(currentRect.left + diffL * scale, currentRect.top + diffT * scale,
                            currentRect.right + diffR * scale, currentRect.bottom + diffB * scale);
                    invalidate();
                }

                @Override
                public void onAnimationFinished() {
                    mFrameRect = newRect;
                    invalidate();
                    mIsAnimating = false;
                }
            });
            animator.startAnimation(durationMillis);
        } else {
            mFrameRect = calcFrameRect(mImageRect);
            invalidate();
        }
    }

    private float getRatioX(float w) {
        switch (mCropMode) {
            case FIT_IMAGE:
                return mImageRect.width();
            case FREE:
                return w;
            case RATIO_4_3:
                return 4;
            case RATIO_3_4:
                return 3;
            case RATIO_16_9:
                return 16;
            case RATIO_9_16:
                return 9;
            case SQUARE:
            case CIRCLE:
            case CIRCLE_SQUARE:
                return 1;
            case CUSTOM:
                return mCustomRatio.x;
            default:
                return w;
        }
    }

    private float getRatioY(float h) {
        switch (mCropMode) {
            case FIT_IMAGE:
                return mImageRect.height();
            case FREE:
                return h;
            case RATIO_4_3:
                return 3;
            case RATIO_3_4:
                return 4;
            case RATIO_16_9:
                return 9;
            case RATIO_9_16:
                return 16;
            case SQUARE:
            case CIRCLE:
            case CIRCLE_SQUARE:
                return 1;
            case CUSTOM:
                return mCustomRatio.y;
            default:
                return h;
        }
    }

    private float getRatioX() {
        switch (mCropMode) {
            case FIT_IMAGE:
                return mImageRect.width();
            case RATIO_4_3:
                return 4;
            case RATIO_3_4:
                return 3;
            case RATIO_16_9:
                return 16;
            case RATIO_9_16:
                return 9;
            case SQUARE:
            case CIRCLE:
            case CIRCLE_SQUARE:
                return 1;
            case CUSTOM:
                return mCustomRatio.x;
            default:
                return 1;
        }
    }

    private float getRatioY() {
        switch (mCropMode) {
            case FIT_IMAGE:
                return mImageRect.height();
            case RATIO_4_3:
                return 3;
            case RATIO_3_4:
                return 4;
            case RATIO_16_9:
                return 9;
            case RATIO_9_16:
                return 16;
            case SQUARE:
            case CIRCLE:
            case CIRCLE_SQUARE:
                return 1;
            case CUSTOM:
                return mCustomRatio.y;
            default:
                return 1;
        }
    }

    private float getDensity() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.density;
    }

    private float sq(float value) {
        return value * value;
    }

    private float constrain(float val, float min, float max, float defaultVal) {
        if (val < min || val > max) return defaultVal;
        return val;
    }

    private void postErrorOnMainThread(final Callback callback, final Throwable e) {
        if (callback == null) return;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            callback.onError(e);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(e);
                }
            });
        }
    }

    private Bitmap getBitmap() {
        Bitmap bm = null;
        Drawable d = getDrawable();
        if (d != null && d instanceof BitmapDrawable) bm = ((BitmapDrawable) d).getBitmap();
        return bm;
    }

    private float getRotatedWidth(float angle) {
        return getRotatedWidth(angle, mImgWidth, mImgHeight);
    }

    private float getRotatedWidth(float angle, float width, float height) {
        return angle % 180 == 0 ? width : height;
    }

    private float getRotatedHeight(float angle) {
        return getRotatedHeight(angle, mImgWidth, mImgHeight);
    }

    private float getRotatedHeight(float angle, float width, float height) {
        return angle % 180 == 0 ? height : width;
    }

    private Bitmap getRotatedBitmap(Bitmap bitmap) {
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(mAngle, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix,
                true);
    }

    private SimpleValueAnimator getAnimator() {
        setupAnimatorIfNeeded();
        return mAnimator;
    }

    private void setupAnimatorIfNeeded() {
        if (mAnimator == null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mAnimator = new ValueAnimatorV8(mInterpolator);
            } else {
                mAnimator = new ValueAnimatorV14(mInterpolator);
            }
        }
    }

    private Bitmap getCroppedBitmapFromUri() throws IOException {
        Bitmap cropped = null;
        InputStream is = null;
        try {
            is = getContext().getContentResolver().openInputStream(mSourceUri);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            final int originalImageWidth = decoder.getWidth();
            final int originalImageHeight = decoder.getHeight();
            Rect cropRect = calcCropRect(originalImageWidth, originalImageHeight);
            if (mAngle != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(-mAngle);
                RectF rotated = new RectF();
                matrix.mapRect(rotated, new RectF(cropRect));
                rotated.offset(rotated.left < 0 ? originalImageWidth : 0,
                        rotated.top < 0 ? originalImageHeight : 0);
                cropRect = new Rect((int) rotated.left, (int) rotated.top, (int) rotated.right,
                        (int) rotated.bottom);
            }
            cropped = decoder.decodeRegion(cropRect, new BitmapFactory.Options());
            if (mAngle != 0) {
                Bitmap rotated = getRotatedBitmap(cropped);
                if (cropped != getBitmap() && cropped != rotated) {
                    cropped.recycle();
                }
                cropped = rotated;
            }
        } finally {
            Utils.closeQuietly(is);
        }
        return cropped;
    }

    private Rect calcCropRect(int originalImageWidth, int originalImageHeight) {
        float scaleToOriginal =
                getRotatedWidth(mAngle, originalImageWidth, originalImageHeight) / mImageRect.width();
        float offsetX = mImageRect.left * scaleToOriginal;
        float offsetY = mImageRect.top * scaleToOriginal;
        int left = Math.round(mFrameRect.left * scaleToOriginal - offsetX);
        int top = Math.round(mFrameRect.top * scaleToOriginal - offsetY);
        int right = Math.round(mFrameRect.right * scaleToOriginal - offsetX);
        int bottom = Math.round(mFrameRect.bottom * scaleToOriginal - offsetY);
        int imageW = Math.round(getRotatedWidth(mAngle, originalImageWidth, originalImageHeight));
        int imageH = Math.round(getRotatedHeight(mAngle, originalImageWidth, originalImageHeight));
        return new Rect(Math.max(left, 0), Math.max(top, 0), Math.min(right, imageW),
                Math.min(bottom, imageH));
    }

    private Bitmap scaleBitmapIfNeeded(Bitmap cropped) {
        int width = cropped.getWidth();
        int height = cropped.getHeight();
        int outWidth = 0;
        int outHeight = 0;
        float imageRatio = getRatioX(mFrameRect.width()) / getRatioY(mFrameRect.height());

        if (mOutputWidth > 0) {
            outWidth = mOutputWidth;
            outHeight = Math.round(mOutputWidth / imageRatio);
        } else if (mOutputHeight > 0) {
            outHeight = mOutputHeight;
            outWidth = Math.round(mOutputHeight * imageRatio);
        } else {
            if (mOutputMaxWidth > 0 && mOutputMaxHeight > 0 && (width > mOutputMaxWidth
                    || height > mOutputMaxHeight)) {
                float maxRatio = (float) mOutputMaxWidth / (float) mOutputMaxHeight;
                if (maxRatio >= imageRatio) {
                    outHeight = mOutputMaxHeight;
                    outWidth = Math.round((float) mOutputMaxHeight * imageRatio);
                } else {
                    outWidth = mOutputMaxWidth;
                    outHeight = Math.round((float) mOutputMaxWidth / imageRatio);
                }
            }
        }

        if (outWidth > 0 && outHeight > 0) {
            Bitmap scaled = Utils.getScaledBitmap(cropped, outWidth, outHeight);
            if (cropped != getBitmap() && cropped != scaled) {
                cropped.recycle();
            }
            cropped = scaled;
        }
        return cropped;
    }

    private Uri saveImage(Bitmap bitmap, final Uri uri) throws IOException, IllegalStateException {
        mSaveUri = uri;
        if (mSaveUri == null) {
            throw new IllegalStateException("Save uri must not be null.");
        }

        OutputStream outputStream = null;
        try {
            outputStream = getContext().getContentResolver().openOutputStream(uri);
            bitmap.compress(mCompressFormat, mCompressQuality, outputStream);
            Utils.copyExifInfo(getContext(), mSourceUri, uri, bitmap.getWidth(), bitmap.getHeight());
            Utils.updateGalleryInfo(getContext(), uri);
            return uri;
        } finally {
            Utils.closeQuietly(outputStream);
        }
    }

    public Bitmap getImageBitmap() {
        return getBitmap();
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap); // calls setImageDrawable internally
    }

    @Override
    public void setImageResource(int resId) {
        mIsInitialized = false;
        resetImageInfo();
        super.setImageResource(resId);
        updateLayout();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mIsInitialized = false;
        resetImageInfo();
        setImageDrawableInternal(drawable);
    }

    private void setImageDrawableInternal(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateLayout();
    }

    @Override
    public void setImageURI(Uri uri) {
        mIsInitialized = false;
        super.setImageURI(uri);
        updateLayout();
    }

    private void updateLayout() {
        Drawable d = getDrawable();
        if (d != null) {
            setupLayout(mViewWidth, mViewHeight);
        }
    }

    private void resetImageInfo() {
        if (mIsLoading.get()) return;
        mSourceUri = null;
        mSaveUri = null;
        mInputImageWidth = 0;
        mInputImageHeight = 0;
        mOutputImageWidth = 0;
        mOutputImageHeight = 0;
        mAngle = mExifRotation;
    }

    public void startLoad(final Uri sourceUri, final LoadCallback callback) {
        loadAsync(sourceUri, callback);
    }

    public void loadAsync(final Uri sourceUri, final LoadCallback callback) {
        loadAsync(sourceUri, false, null, callback);
    }


    public void loadAsync(final Uri sourceUri, final boolean useThumbnail,
                          final RectF initialFrameRect, final LoadCallback callback) {

        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mIsLoading.set(true);

                    mSourceUri = sourceUri;
                    mInitialFrameRect = initialFrameRect;

                    if (useThumbnail) {
                        applyThumbnail(sourceUri);
                    }

                    final Bitmap sampled = getImage(sourceUri);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAngle = mExifRotation;
                            setImageDrawableInternal(new BitmapDrawable(getResources(), sampled));
                            if (callback != null) callback.onSuccess();
                        }
                    });
                } catch (Exception e) {
                    postErrorOnMainThread(callback, e);
                } finally {
                    mIsLoading.set(false);
                }
            }
        });
    }


    public Completable loadAsCompletable(final Uri sourceUri) {
        return loadAsCompletable(sourceUri, false, null);
    }


    public Completable loadAsCompletable(final Uri sourceUri, final boolean useThumbnail,
                                         final RectF initialFrameRect) {
        return Completable.create(new CompletableOnSubscribe() {

            @Override
            public void subscribe(@NonNull final CompletableEmitter emitter) throws Exception {

                mInitialFrameRect = initialFrameRect;
                mSourceUri = sourceUri;

                if (useThumbnail) {
                    applyThumbnail(sourceUri);
                }

                final Bitmap sampled = getImage(sourceUri);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAngle = mExifRotation;
                        setImageDrawableInternal(new BitmapDrawable(getResources(), sampled));
                        emitter.onComplete();
                    }
                });
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                mIsLoading.set(true);
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                mIsLoading.set(false);
            }
        });
    }


    public LoadRequest load(Uri sourceUri) {
        return new LoadRequest(this, sourceUri);
    }

    private void applyThumbnail(Uri sourceUri) {
        final Bitmap thumb = getThumbnail(sourceUri);
        if (thumb == null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAngle = mExifRotation;
                setImageDrawableInternal(new BitmapDrawable(getResources(), thumb));
            }
        });
    }

    private Bitmap getImage(Uri sourceUri) {

        if (sourceUri == null) {
            throw new IllegalStateException("Source Uri must not be null.");
        }

        mExifRotation = Utils.getExifOrientation(getContext(), mSourceUri);
        int maxSize = Utils.getMaxSize();
        int requestSize = Math.max(mViewWidth, mViewHeight);
        if (requestSize == 0) requestSize = maxSize;

        final Bitmap sampledBitmap =
                Utils.decodeSampledBitmapFromUri(getContext(), mSourceUri, requestSize);
        mInputImageWidth = Utils.sInputImageWidth;
        mInputImageHeight = Utils.sInputImageHeight;
        return sampledBitmap;
    }

    private Bitmap getThumbnail(Uri sourceUri) {

        if (sourceUri == null) {
            throw new IllegalStateException("Source Uri must not be null.");
        }

        mExifRotation = Utils.getExifOrientation(getContext(), mSourceUri);
        int requestSize = (int) (Math.max(mViewWidth, mViewHeight) * 0.1f);
        if (requestSize == 0) return null;

        final Bitmap sampledBitmap =
                Utils.decodeSampledBitmapFromUri(getContext(), mSourceUri, requestSize);
        mInputImageWidth = Utils.sInputImageWidth;
        mInputImageHeight = Utils.sInputImageHeight;
        return sampledBitmap;
    }


    public void rotateImage(RotateDegrees degrees, int durationMillis) {
        if (mIsRotating) {
            getAnimator().cancelAnimation();
        }

        final float currentAngle = mAngle;
        final float newAngle = (mAngle + degrees.getValue());
        final float angleDiff = newAngle - currentAngle;
        final float currentScale = mScale;
        final float newScale = calcScale(mViewWidth, mViewHeight, newAngle);

        if (mIsAnimationEnabled) {
            final float scaleDiff = newScale - currentScale;
            SimpleValueAnimator animator = getAnimator();
            animator.addAnimatorListener(new SimpleValueAnimatorListener() {
                @Override
                public void onAnimationStarted() {
                    mIsRotating = true;
                }

                @Override
                public void onAnimationUpdated(float scale) {
                    mAngle = currentAngle + angleDiff * scale;
                    mScale = currentScale + scaleDiff * scale;
                    setMatrix();
                    invalidate();
                }

                @Override
                public void onAnimationFinished() {
                    mAngle = newAngle % 360;
                    mScale = newScale;
                    mInitialFrameRect = null;
                    setupLayout(mViewWidth, mViewHeight);
                    mIsRotating = false;
                }
            });
            animator.startAnimation(durationMillis);
        } else {
            mAngle = newAngle % 360;
            mScale = newScale;
            setupLayout(mViewWidth, mViewHeight);
        }
    }


    public void rotateImage(RotateDegrees degrees) {
        rotateImage(degrees, mAnimationDurationMillis);
    }


    public Bitmap getCroppedBitmap() {
        Bitmap source = getBitmap();
        if (source == null) return null;

        Bitmap rotated = getRotatedBitmap(source);
        Rect cropRect = calcCropRect(source.getWidth(), source.getHeight());
        Bitmap cropped = Bitmap.createBitmap(rotated, cropRect.left, cropRect.top, cropRect.width(),
                cropRect.height(), null, false);
        if (rotated != cropped && rotated != source) {
            rotated.recycle();
        }

        if (mCropMode == CropMode.CIRCLE) {
            Bitmap circle = getCircularBitmap(cropped);
            if (cropped != getBitmap()) {
                cropped.recycle();
            }
            cropped = circle;
        }
        return cropped;
    }


    public Bitmap getCircularBitmap(Bitmap square) {
        if (square == null) return null;
        Bitmap output =
                Bitmap.createBitmap(square.getWidth(), square.getHeight(), Bitmap.Config.ARGB_8888);

        final Rect rect = new Rect(0, 0, square.getWidth(), square.getHeight());
        Canvas canvas = new Canvas(output);

        int halfWidth = square.getWidth() / 2;
        int halfHeight = square.getHeight() / 2;

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        canvas.drawCircle(halfWidth, halfHeight, Math.min(halfWidth, halfHeight), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(square, rect, rect, paint);
        return output;
    }


    public void startCrop(final Uri saveUri, final CropCallback cropCallback,
                          final SaveCallback saveCallback) {

        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap croppedImage = null;

                try {
                    mIsCropping.set(true);

                    croppedImage = cropImage();

                    final Bitmap cropped = croppedImage;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (cropCallback != null) cropCallback.onSuccess(cropped);
                            if (mIsDebug) invalidate();
                        }
                    });

                    saveImage(croppedImage, saveUri);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (saveCallback != null) saveCallback.onSuccess(saveUri);
                        }
                    });
                } catch (Exception e) {
                    if (croppedImage == null) {
                        postErrorOnMainThread(cropCallback, e);
                    } else {
                        postErrorOnMainThread(saveCallback, e);
                    }
                } finally {
                    mIsCropping.set(false);
                }
            }
        });
    }


    public void cropAsync(final Uri sourceUri, final CropCallback cropCallback) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mIsCropping.set(true);

                    if (sourceUri != null) mSourceUri = sourceUri;

                    final Bitmap cropped = cropImage();

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (cropCallback != null) cropCallback.onSuccess(cropped);
                            if (mIsDebug) invalidate();
                        }
                    });
                } catch (Exception e) {
                    postErrorOnMainThread(cropCallback, e);
                } finally {
                    mIsCropping.set(false);
                }
            }
        });
    }

    public void cropAsync(final CropCallback cropCallback) {
        cropAsync(null, cropCallback);
    }


    public Single<Bitmap> cropAsSingle(final Uri sourceUri) {
        return Single.fromCallable(new Callable<Bitmap>() {

            @Override
            public Bitmap call() throws Exception {
                if (sourceUri != null) mSourceUri = sourceUri;
                return cropImage();
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                mIsCropping.set(true);
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                mIsCropping.set(false);
            }
        });
    }

    public Single<Bitmap> cropAsSingle() {
        return cropAsSingle(null);
    }


    public CropRequest crop(Uri sourceUri) {
        return new CropRequest(this, sourceUri);
    }


    public void saveAsync(final Uri saveUri, final Bitmap image, final SaveCallback saveCallback) {
        mExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    mIsSaving.set(true);
                    saveImage(image, saveUri);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (saveCallback != null) saveCallback.onSuccess(saveUri);
                        }
                    });
                } catch (Exception e) {
                    postErrorOnMainThread(saveCallback, e);
                } finally {
                    mIsSaving.set(false);
                }
            }
        });
    }


    public Single<Uri> saveAsSingle(final Bitmap bitmap, final Uri saveUri) {
        return Single.fromCallable(new Callable<Uri>() {

            @Override
            public Uri call() throws Exception {
                return saveImage(bitmap, saveUri);
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                mIsSaving.set(true);
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                mIsSaving.set(false);
            }
        });
    }


    public SaveRequest save(Bitmap bitmap) {
        return new SaveRequest(this, bitmap);
    }

    private Bitmap cropImage() throws IOException, IllegalStateException {
        Bitmap cropped;

        if (mSourceUri == null) {
            cropped = getCroppedBitmap();
        } else {
            cropped = getCroppedBitmapFromUri();
            if (mCropMode == CropMode.CIRCLE) {
                Bitmap circle = getCircularBitmap(cropped);
                if (cropped != getBitmap()) {
                    cropped.recycle();
                }
                cropped = circle;
            }
        }

        cropped = scaleBitmapIfNeeded(cropped);

        mOutputImageWidth = cropped.getWidth();
        mOutputImageHeight = cropped.getHeight();

        return cropped;
    }

    public RectF getActualCropRect() {
        if (mImageRect == null) return null;
        float offsetX = (mImageRect.left / mScale);
        float offsetY = (mImageRect.top / mScale);
        float l = (mFrameRect.left / mScale) - offsetX;
        float t = (mFrameRect.top / mScale) - offsetY;
        float r = (mFrameRect.right / mScale) - offsetX;
        float b = (mFrameRect.bottom / mScale) - offsetY;
        l = Math.max(0, l);
        t = Math.max(0, t);
        r = Math.min(mImageRect.right / mScale, r);
        b = Math.min(mImageRect.bottom / mScale, b);
        return new RectF(l, t, r, b);
    }

    private RectF applyInitialFrameRect(RectF initialFrameRect) {
        RectF frameRect = new RectF();
        frameRect.set(initialFrameRect.left * mScale, initialFrameRect.top * mScale,
                initialFrameRect.right * mScale, initialFrameRect.bottom * mScale);
        frameRect.offset(mImageRect.left, mImageRect.top);
        float l = Math.max(mImageRect.left, frameRect.left);
        float t = Math.max(mImageRect.top, frameRect.top);
        float r = Math.min(mImageRect.right, frameRect.right);
        float b = Math.min(mImageRect.bottom, frameRect.bottom);
        frameRect.set(l, t, r, b);
        return frameRect;
    }

    public void setCropMode(CropMode mode, int durationMillis) {
        if (mode == CropMode.CUSTOM) {
            setCustomRatio(1, 1);
        } else {
            mCropMode = mode;
            recalculateFrameRect(durationMillis);
        }
    }

    public void setCropMode(CropMode mode) {
        setCropMode(mode, mAnimationDurationMillis);
    }

    public void setCustomRatio(int ratioX, int ratioY, int durationMillis) {
        if (ratioX == 0 || ratioY == 0) return;
        mCropMode = CropMode.CUSTOM;
        mCustomRatio = new PointF(ratioX, ratioY);
        recalculateFrameRect(durationMillis);
    }

    public void setCustomRatio(int ratioX, int ratioY) {
        setCustomRatio(ratioX, ratioY, mAnimationDurationMillis);
    }

    public void setOverlayColor(int overlayColor) {
        this.mOverlayColor = overlayColor;
        invalidate();
    }

    public void setFrameColor(int frameColor) {
        this.mFrameColor = frameColor;
        invalidate();
    }

    public void setHandleColor(int handleColor) {
        this.mHandleColor = handleColor;
        invalidate();
    }

    public void setGuideColor(int guideColor) {
        this.mGuideColor = guideColor;
        invalidate();
    }

    public void setBackgroundColor(int bgColor) {
        this.mBackgroundColor = bgColor;
        invalidate();
    }

    public void setMinFrameSizeInDp(int minDp) {
        mMinFrameSize = minDp * getDensity();
    }

    public void setMinFrameSizeInPx(int minPx) {
        mMinFrameSize = minPx;
    }

    public void setHandleSizeInDp(int handleDp) {
        mHandleSize = (int) (handleDp * getDensity());
    }

    public void setTouchPaddingInDp(int paddingDp) {
        mTouchPadding = (int) (paddingDp * getDensity());
    }

    public void setGuideShowMode(ShowMode mode) {
        mGuideShowMode = mode;
        switch (mode) {
            case SHOW_ALWAYS:
                mShowGuide = true;
                break;
            case NOT_SHOW:
            case SHOW_ON_TOUCH:
                mShowGuide = false;
                break;
        }
        invalidate();
    }

    public void setHandleShowMode(ShowMode mode) {
        mHandleShowMode = mode;
        switch (mode) {
            case SHOW_ALWAYS:
                mShowHandle = true;
                break;
            case NOT_SHOW:
            case SHOW_ON_TOUCH:
                mShowHandle = false;
                break;
        }
        invalidate();
    }

    public void setFrameStrokeWeightInDp(int weightDp) {
        mFrameStrokeWeight = weightDp * getDensity();
        invalidate();
    }

    public void setGuideStrokeWeightInDp(int weightDp) {
        mGuideStrokeWeight = weightDp * getDensity();
        invalidate();
    }

    public void setCropEnabled(boolean enabled) {
        mIsCropEnabled = enabled;
        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mIsEnabled = enabled;
    }

    public void setInitialFrameScale(float initialScale) {
        mInitialFrameScale = constrain(initialScale, 0.01f, 1.0f, DEFAULT_INITIAL_FRAME_SCALE);
    }

    public void setAnimationEnabled(boolean enabled) {
        mIsAnimationEnabled = enabled;
    }

    public void setAnimationDuration(int durationMillis) {
        mAnimationDurationMillis = durationMillis;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        mAnimator = null;
        setupAnimatorIfNeeded();
    }

    public void setDebug(boolean debug) {
        mIsDebug = debug;
        invalidate();
    }

    public void setLoggingEnabled(boolean enabled) {
    }

    public void setOutputWidth(int outputWidth) {
        mOutputWidth = outputWidth;
        mOutputHeight = 0;
    }

    public void setOutputHeight(int outputHeight) {
        mOutputHeight = outputHeight;
        mOutputWidth = 0;
    }

    public void setOutputMaxSize(int maxWidth, int maxHeight) {
        mOutputMaxWidth = maxWidth;
        mOutputMaxHeight = maxHeight;
    }

    public void setCompressFormat(Bitmap.CompressFormat format) {
        mCompressFormat = format;
    }

    public void setCompressQuality(int quality) {
        mCompressQuality = quality;
    }

    public void setHandleShadowEnabled(boolean handleShadowEnabled) {
        mIsHandleShadowEnabled = handleShadowEnabled;
    }

    public boolean isCropping() {
        return mIsCropping.get();
    }

    public Uri getSourceUri() {
        return mSourceUri;
    }

    public Uri getSaveUri() {
        return mSaveUri;
    }

    public boolean isSaving() {
        return mIsSaving.get();
    }

    private void setScale(float mScale) {
        this.mScale = mScale;
    }

    private void setCenter(PointF mCenter) {
        this.mCenter = mCenter;
    }

    private float getFrameW() {
        return (mFrameRect.right - mFrameRect.left);
    }

    private float getFrameH() {
        return (mFrameRect.bottom - mFrameRect.top);
    }


    private enum TouchArea {
        OUT_OF_BOUNDS, CENTER, LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM, CENTER_TOP, CENTER_BOTTOM, CENTER_LEFT, CENTER_RIGHT
    }

    public enum CropMode {
        FIT_IMAGE(0), RATIO_4_3(1), RATIO_3_4(2), SQUARE(3), RATIO_16_9(4), RATIO_9_16(5), FREE(
                6), CUSTOM(7), CIRCLE(8), CIRCLE_SQUARE(9);
        private final int ID;

        CropMode(final int id) {
            this.ID = id;
        }

        public int getId() {
            return ID;
        }
    }

    public enum ShowMode {
        SHOW_ALWAYS(1), SHOW_ON_TOUCH(2), NOT_SHOW(3);
        private final int ID;

        ShowMode(final int id) {
            this.ID = id;
        }

        public int getId() {
            return ID;
        }
    }

    public enum RotateDegrees {
        ROTATE_90D(90), ROTATE_180D(180), ROTATE_270D(270), ROTATE_M90D(-90), ROTATE_M180D(
                -180), ROTATE_M270D(-270);

        private final int VALUE;

        RotateDegrees(final int value) {
            this.VALUE = value;
        }

        public int getValue() {
            return VALUE;
        }
    }


    public static class SavedState extends BaseSavedState {
        CropMode mode;
        int backgroundColor;
        int overlayColor;
        int frameColor;
        ShowMode guideShowMode;
        ShowMode handleShowMode;
        boolean showGuide;
        boolean showHandle;
        int handleSize;
        int touchPadding;
        float minFrameSize;
        float customRatioX;
        float customRatioY;
        float frameStrokeWeight;
        float guideStrokeWeight;
        boolean isCropEnabled;
        int handleColor;
        int guideColor;
        float initialFrameScale;
        float angle;
        boolean isAnimationEnabled;
        int animationDuration;
        int exifRotation;
        Uri sourceUri;
        Uri saveUri;
        Bitmap.CompressFormat compressFormat;
        int compressQuality;
        boolean isDebug;
        int outputMaxWidth;
        int outputMaxHeight;
        int outputWidth;
        int outputHeight;
        boolean isHandleShadowEnabled;
        int inputImageWidth;
        int inputImageHeight;
        int outputImageWidth;
        int outputImageHeight;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mode = (CropMode) in.readSerializable();
            backgroundColor = in.readInt();
            overlayColor = in.readInt();
            frameColor = in.readInt();
            guideShowMode = (ShowMode) in.readSerializable();
            handleShowMode = (ShowMode) in.readSerializable();
            showGuide = (in.readInt() != 0);
            showHandle = (in.readInt() != 0);
            handleSize = in.readInt();
            touchPadding = in.readInt();
            minFrameSize = in.readFloat();
            customRatioX = in.readFloat();
            customRatioY = in.readFloat();
            frameStrokeWeight = in.readFloat();
            guideStrokeWeight = in.readFloat();
            isCropEnabled = (in.readInt() != 0);
            handleColor = in.readInt();
            guideColor = in.readInt();
            initialFrameScale = in.readFloat();
            angle = in.readFloat();
            isAnimationEnabled = (in.readInt() != 0);
            animationDuration = in.readInt();
            exifRotation = in.readInt();
            sourceUri = in.readParcelable(Uri.class.getClassLoader());
            saveUri = in.readParcelable(Uri.class.getClassLoader());
            compressFormat = (Bitmap.CompressFormat) in.readSerializable();
            compressQuality = in.readInt();
            isDebug = (in.readInt() != 0);
            outputMaxWidth = in.readInt();
            outputMaxHeight = in.readInt();
            outputWidth = in.readInt();
            outputHeight = in.readInt();
            isHandleShadowEnabled = (in.readInt() != 0);
            inputImageWidth = in.readInt();
            inputImageHeight = in.readInt();
            outputImageWidth = in.readInt();
            outputImageHeight = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flag) {
            super.writeToParcel(out, flag);
            out.writeSerializable(mode);
            out.writeInt(backgroundColor);
            out.writeInt(overlayColor);
            out.writeInt(frameColor);
            out.writeSerializable(guideShowMode);
            out.writeSerializable(handleShowMode);
            out.writeInt(showGuide ? 1 : 0);
            out.writeInt(showHandle ? 1 : 0);
            out.writeInt(handleSize);
            out.writeInt(touchPadding);
            out.writeFloat(minFrameSize);
            out.writeFloat(customRatioX);
            out.writeFloat(customRatioY);
            out.writeFloat(frameStrokeWeight);
            out.writeFloat(guideStrokeWeight);
            out.writeInt(isCropEnabled ? 1 : 0);
            out.writeInt(handleColor);
            out.writeInt(guideColor);
            out.writeFloat(initialFrameScale);
            out.writeFloat(angle);
            out.writeInt(isAnimationEnabled ? 1 : 0);
            out.writeInt(animationDuration);
            out.writeInt(exifRotation);
            out.writeParcelable(sourceUri, flag);
            out.writeParcelable(saveUri, flag);
            out.writeSerializable(compressFormat);
            out.writeInt(compressQuality);
            out.writeInt(isDebug ? 1 : 0);
            out.writeInt(outputMaxWidth);
            out.writeInt(outputMaxHeight);
            out.writeInt(outputWidth);
            out.writeInt(outputHeight);
            out.writeInt(isHandleShadowEnabled ? 1 : 0);
            out.writeInt(inputImageWidth);
            out.writeInt(inputImageHeight);
            out.writeInt(outputImageWidth);
            out.writeInt(outputImageHeight);
        }

        public static final Creator CREATOR = new Creator() {
            public SavedState createFromParcel(final Parcel inParcel) {
                return new SavedState(inParcel);
            }

            public SavedState[] newArray(final int inSize) {
                return new SavedState[inSize];
            }
        };
    }
}
