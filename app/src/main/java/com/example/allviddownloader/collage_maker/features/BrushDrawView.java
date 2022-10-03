package com.example.allviddownloader.collage_maker.features;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.example.allviddownloader.collage_maker.features.draw.AdapterMagicBrush;
import com.example.allviddownloader.collage_maker.ui.interfaces.BrushViewChangeListener;
import com.example.allviddownloader.collage_maker.utils.SystemUtils;
import com.example.allviddownloader.collage_maker.utils.UtilsColor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class BrushDrawView extends View {
    private static final float TOUCH_TOLERANCE = 4.0f;
    private Paint bitmapPaint;
    private int brushBitmapSize;
    private List<Point> currentBitmapPoint;
    private DrawBitmapModel currentMagicBrush;
    private int distance;
    private int drawMode;
    private Stack<List<Point>> lstPoints;
    private boolean mBrushDrawMode;
    private float mBrushEraserSize;
    private float mBrushSize;
    private BrushViewChangeListener mBrushViewChangeListener;
    private Canvas mDrawCanvas;
    private Paint mDrawPaint;
    private Paint mDrawPaintBlur;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mOpacity;
    private Path mPath;
    private Stack<Point> mPoints;
    private Stack<List<Point>> mRedoPaths;
    private float mTouchX;
    private float mTouchY;
    private Rect tempRect;

    public BrushDrawView(Context context) {
        this(context, null);
    }

    public BrushDrawView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mBrushSize = 25.0f;
        this.mBrushEraserSize = 50.0f;
        this.mOpacity = 255;
        this.mPoints = new Stack<>();
        this.lstPoints = new Stack<>();
        this.mRedoPaths = new Stack<>();
        this.brushBitmapSize = SystemUtils.dpToPx(getContext(), 25);
        this.distance = SystemUtils.dpToPx(getContext(), 3);
        this.currentBitmapPoint = new ArrayList();
        this.tempRect = new Rect();
        setupBrushDrawing();
    }

    public BrushDrawView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBrushSize = 25.0f;
        this.mBrushEraserSize = 50.0f;
        this.mOpacity = 255;
        this.mPoints = new Stack<>();
        this.lstPoints = new Stack<>();
        this.mRedoPaths = new Stack<>();
        this.brushBitmapSize = SystemUtils.dpToPx(getContext(), 25);
        this.distance = SystemUtils.dpToPx(getContext(), 3);
        this.currentBitmapPoint = new ArrayList();
        this.tempRect = new Rect();
        setupBrushDrawing();
    }

    public void setCurrentMagicBrush(DrawBitmapModel drawBitmapModel) {
        this.currentMagicBrush = drawBitmapModel;
    }

    public void setDrawMode(int i) {
        this.drawMode = i;
        if (this.drawMode == 2) {
            this.mDrawPaint.setColor(-1);
            this.mDrawPaintBlur.setColor(Color.parseColor(UtilsColor.lstColorForBrush().get(0)));
            refreshBrushDrawing();
            return;
        }
        this.mDrawPaint.setColor(Color.parseColor(UtilsColor.lstColorForBrush().get(0)));
        refreshBrushDrawing();
    }

    private void setupBrushDrawing() {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        this.mDrawPaint = new Paint();
        this.mPath = new Path();
        this.mDrawPaint.setAntiAlias(true);
        this.mDrawPaint.setDither(true);
        this.mDrawPaint.setColor(Color.parseColor(UtilsColor.lstColorForBrush().get(0)));
        this.mDrawPaint.setStyle(Paint.Style.FILL);
        this.mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mDrawPaint.setStrokeWidth(this.mBrushSize);
        this.mDrawPaint.setAlpha(this.mOpacity);
        this.mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        this.mDrawPaintBlur = new Paint();
        this.mDrawPaintBlur.setAntiAlias(true);
        this.mDrawPaintBlur.setDither(true);
        this.mDrawPaintBlur.setStyle(Paint.Style.STROKE);
        this.mDrawPaintBlur.setStrokeJoin(Paint.Join.ROUND);
        this.mDrawPaintBlur.setMaskFilter(new BlurMaskFilter(25.0f, BlurMaskFilter.Blur.OUTER));
        this.mDrawPaintBlur.setStrokeCap(Paint.Cap.ROUND);
        this.mDrawPaintBlur.setStrokeWidth(this.mBrushSize * 1.1f);
        this.mDrawPaintBlur.setColor(Color.parseColor(UtilsColor.lstColorForBrush().get(0)));
        this.mDrawPaintBlur.setAlpha(this.mOpacity);
        this.mDrawPaintBlur.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        this.bitmapPaint = new Paint();
        this.bitmapPaint.setStyle(Paint.Style.FILL);
        this.bitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        this.bitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        this.bitmapPaint.setStrokeWidth(this.mBrushSize);
        this.bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        setVisibility(View.GONE);
    }

    private void refreshBrushDrawing() {
        this.mBrushDrawMode = true;
        this.mPath = new Path();
        this.mDrawPaint.setAntiAlias(true);
        this.mDrawPaint.setDither(true);
        this.mDrawPaint.setStyle(Paint.Style.STROKE);
        this.mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mDrawPaint.setStrokeWidth(this.mBrushSize);
        this.mDrawPaint.setAlpha(this.mOpacity);
        this.mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        this.mDrawPaintBlur.setAntiAlias(true);
        this.mDrawPaintBlur.setDither(true);
        this.mDrawPaintBlur.setStyle(Paint.Style.STROKE);
        this.mDrawPaintBlur.setStrokeJoin(Paint.Join.ROUND);
        this.mDrawPaintBlur.setMaskFilter(new BlurMaskFilter(30.0f, BlurMaskFilter.Blur.OUTER));
        this.mDrawPaintBlur.setStrokeCap(Paint.Cap.ROUND);
        this.mDrawPaintBlur.setStrokeWidth(this.mBrushSize * 1.1f);
        this.mDrawPaintBlur.setAlpha(this.mOpacity);
        this.mDrawPaintBlur.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        this.bitmapPaint.setStyle(Paint.Style.FILL);
        this.bitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        this.bitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        this.bitmapPaint.setStrokeWidth(this.mBrushSize);
        this.bitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
    }

    public void brushEraser() {
        this.mBrushDrawMode = true;
        this.drawMode = 4;
        this.mDrawPaint.setStrokeWidth(this.mBrushEraserSize);
        this.mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setBrushDrawingMode(boolean z) {
        this.mBrushDrawMode = z;
        if (z) {
            setVisibility(View.VISIBLE);
            refreshBrushDrawing();
        }
    }

    public void setBrushSize(float f) {
        if (this.drawMode == 3) {
            this.brushBitmapSize = SystemUtils.dpToPx(getContext(), (int) f);
            return;
        }
        this.mBrushSize = f;
        setBrushDrawingMode(true);
    }


    public void setBrushColor(@ColorInt int i) {
        if (this.drawMode == 1) {
            this.mDrawPaint.setColor(i);
        } else if (this.drawMode == 2) {
            this.mDrawPaintBlur.setColor(i);
        }
        setBrushDrawingMode(true);
    }

    public void setBrushEraserSize(float f) {
        this.mBrushEraserSize = f;
        setBrushDrawingMode(true);
    }

    public void clearAll() {
        this.mRedoPaths.clear();
        this.mPoints.clear();
        this.lstPoints.clear();
        for (DrawBitmapModel next : AdapterMagicBrush.drawBitmapModels) {
            if (next.isLoadBitmap()) {
                next.clearBitmap();
                next.setLoadBitmap(false);
            }
        }
        if (this.mDrawCanvas != null) {
            this.mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        invalidate();
    }

    public void setBrushViewChangeListener(BrushViewChangeListener brushViewChangeListener) {
        this.mBrushViewChangeListener = brushViewChangeListener;
    }

    @Override
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i > 0 && i2 > 0) {
            this.mDrawCanvas = new Canvas(Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888));
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        Iterator<Point> it = this.mPoints.iterator();
        while (it.hasNext()) {
            Point point = it.next();
            if (point.vector2 != null) {
                this.tempRect.set(point.vector2.x, point.vector2.y, point.vector2.x1, point.vector2.y1);
                canvas.drawBitmap(point.vector2.bitmap, null, this.tempRect, this.bitmapPaint);
            } else if (point.linePath != null) {
                canvas.drawPath(point.linePath.getDrawPath(), point.linePath.getDrawPaint());
            }
        }
        if (this.drawMode == 2) {
            canvas.drawPath(this.mPath, this.mDrawPaintBlur);
        }
        canvas.drawPath(this.mPath, this.mDrawPaint);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(@NonNull MotionEvent motionEvent) {
        if (!this.mBrushDrawMode) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        switch (motionEvent.getAction()) {
            case 0:
                touchStart((float) x, (float) y);
                break;
            case 1:
                touchUp();
                break;
            case 2:
                touchMove(x, y);
                break;
        }
        invalidate();
        return true;
    }

    public static class LinePath {
        private Paint mDrawPaint;
        private Path mDrawPath;

        public LinePath(Path path, Paint paint) {
            this.mDrawPaint = new Paint(paint);
            this.mDrawPath = new Path(path);
        }

        public Paint getDrawPaint() {
            return this.mDrawPaint;
        }

        public Path getDrawPath() {
            return this.mDrawPath;
        }
    }


    public boolean undo() {
        if (!this.lstPoints.empty()) {
            List pop = this.lstPoints.pop();
            this.mRedoPaths.push(pop);
            this.mPoints.removeAll(pop);
            invalidate();
        }
        if (this.mBrushViewChangeListener != null) {
            this.mBrushViewChangeListener.onViewRemoved();
        }
        return !this.lstPoints.empty();
    }

    public boolean redo() {
        if (!this.mRedoPaths.empty()) {
            List<Point> pop = this.mRedoPaths.pop();
            for (Point push : pop) {
                this.mPoints.push(push);
            }
            this.lstPoints.push(pop);
            invalidate();
        }
        if (this.mBrushViewChangeListener != null) {
            this.mBrushViewChangeListener.onViewAdd(this);
        }
        return !this.mRedoPaths.empty();
    }

    private void touchStart(float f, float f2) {
        this.mRedoPaths.clear();
        this.mPath.reset();
        this.mPath.moveTo(f, f2);
        this.mTouchX = f;
        this.mTouchY = f2;
        if (this.mBrushViewChangeListener != null) {
            this.mBrushViewChangeListener.onStartDrawing();
        }
        if (this.drawMode == 3) {
            this.currentBitmapPoint.clear();
        }
    }

    private void touchMove(int i, int i2) {
        int nextInt;
        float f = (float) i;
        float abs = Math.abs(f - this.mTouchX);
        float f2 = (float) i2;
        float abs2 = Math.abs(f2 - this.mTouchY);
        if (abs < TOUCH_TOLERANCE && abs2 < TOUCH_TOLERANCE) {
            return;
        }
        if (this.drawMode != 3) {
            this.mPath.quadTo(this.mTouchX, this.mTouchY, (this.mTouchX + f) / 2.0f, (this.mTouchY + f2) / 2.0f);
            this.mTouchX = f;
            this.mTouchY = f2;
        } else if (Math.abs(f - this.mLastTouchX) > ((float) (this.brushBitmapSize + this.distance)) || Math.abs(f2 - this.mLastTouchY) > ((float) (this.brushBitmapSize + this.distance))) {
            Random random = new Random();
            int i3 = -1;
            List<Vector2> list = this.currentMagicBrush.getmPositions();
            if (list.size() > 0) {
                i3 = list.get(list.size() - 1).drawableIndex;
            }
            do {
                nextInt = random.nextInt(this.currentMagicBrush.getLstIconWhenDrawing().size());
            } while (nextInt == i3);
            Vector2 vector2 = new Vector2(i, i2, i + this.brushBitmapSize, i2 + this.brushBitmapSize, nextInt, this.currentMagicBrush.getBitmapByIndex(nextInt));
            list.add(vector2);
            Point point = new Point(vector2);
            this.mPoints.push(point);
            this.currentBitmapPoint.add(point);
            this.mLastTouchX = f;
            this.mLastTouchY = f2;
        }
    }

    private void touchUp() {
        if (this.drawMode != 3) {
            ArrayList<Point> arrayList = new ArrayList<>();
            Point point = new Point(new LinePath(this.mPath, this.mDrawPaint));
            this.mPoints.push(point);
            arrayList.add(point);
            if (this.drawMode == 2) {
                Point point2 = new Point(new LinePath(this.mPath, this.mDrawPaintBlur));
                this.mPoints.push(point2);
                arrayList.add(point2);
            }
            this.lstPoints.push(arrayList);
        } else {
            this.lstPoints.push(new ArrayList<>(this.currentBitmapPoint));
            this.currentBitmapPoint.clear();
        }
        this.mPath = new Path();
        if (this.mBrushViewChangeListener != null) {
            this.mBrushViewChangeListener.onStopDrawing();
            this.mBrushViewChangeListener.onViewAdd(this);
        }
        this.mLastTouchX = 0.0f;
        this.mLastTouchY = 0.0f;
    }

    public static final class Vector2 {
        public Bitmap bitmap;
        int drawableIndex;

        public int x;

        int x1;

        public int y;

        int y1;

        Vector2(int i, int i2, int i3, int i4, int i5, Bitmap bitmap2) {
            this.x = i;
            this.y = i2;
            this.x1 = i3;
            this.y1 = i4;
            this.bitmap = bitmap2;
            this.drawableIndex = i5;
        }
    }

    class Point {
        LinePath linePath;
        Vector2 vector2;

        Point(LinePath linePath2) {
            this.linePath = linePath2;
        }

        Point(Vector2 vector22) {
            this.vector2 = vector22;
        }
    }

    public Bitmap getDrawBitmap(Bitmap bitmap) {
        int width = getWidth();
        int height = getHeight();
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawBitmap(bitmap, null, new RectF(0.0f, 0.0f, (float) width, (float) height), null);
        Iterator it = this.mPoints.iterator();
        while (it.hasNext()) {
            Point point = (Point) it.next();
            if (point.vector2 != null) {
                this.tempRect.set(point.vector2.x, point.vector2.y, point.vector2.x1, point.vector2.y1);
                canvas.drawBitmap(point.vector2.bitmap, null, this.tempRect, this.bitmapPaint);
            } else if (point.linePath != null) {
                canvas.drawPath(point.linePath.getDrawPath(), point.linePath.getDrawPaint());
            }
        }
        return createBitmap;
    }
}
