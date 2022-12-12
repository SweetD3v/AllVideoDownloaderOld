package com.tools.videodownloader.collage_maker.features.college;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tools.videodownloader.R;
import com.steelkiwi.cropiwa.AspectRatio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuzzleView extends com.tools.videodownloader.collage_maker.features.StickerView {
    private Map<Area, PuzzlePiece> areaPieceMap;
    private AspectRatio aspectRatio;
    private int backgroundResource;
    private RectF bounds;
    private boolean canDrag;
    private boolean canMoveLine;

    public boolean canSwap;
    private boolean canZoom;

    public ActionMode currentMode;
    private float downX;
    private float downY;
    private int duration;
    private int handleBarColor;
    private Paint handleBarPaint;
    private Line handlingLine;

    public PuzzlePiece handlingPiece;
    private PuzzleLayout.Info initialInfo;
    private int lineColor;
    private Paint linePaint;
    private int lineSize;
    private PointF midPoint;
    private List<PuzzlePiece> needChangePieces;
    private boolean needDrawLine;
    private boolean needDrawOuterLine;
    private boolean needResetPieceMatrix;

    public OnPieceSelectedListener onPieceSelectedListener;
    private OnPieceUnSelectedListener onPieceUnSelectedListener;
    private float piecePadding;
    private float pieceRadian;
    private float previousDistance;

    public PuzzlePiece previousHandlingPiece;
    private PuzzleLayout puzzleLayout;

    public List<PuzzlePiece> puzzlePieces;
    private boolean quickMode;
    private PuzzlePiece replacePiece;
    private Paint selectedAreaPaint;
    private int selectedLineColor;
    private Runnable switchToSwapAction;
    private boolean touchEnable;

    private enum ActionMode {
        NONE,
        DRAG,
        ZOOM,
        MOVE,
        SWAP
    }

    public interface OnPieceSelectedListener {
        void onPieceSelected(PuzzlePiece puzzlePiece, int i);
    }

    public interface OnPieceUnSelectedListener {
        void onPieceUnSelected();
    }

    public PuzzleView(Context context) {
        this(context, null);
    }

    public PuzzleView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PuzzleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.currentMode = ActionMode.NONE;
        this.puzzlePieces = new ArrayList();
        this.needChangePieces = new ArrayList();
        this.areaPieceMap = new HashMap();
        this.touchEnable = true;
        this.needResetPieceMatrix = true;
        this.quickMode = false;
        this.canDrag = true;
        this.canMoveLine = true;
        this.canZoom = true;
        this.canSwap = true;
        this.switchToSwapAction = new Runnable() {
            public void run() {
                if (PuzzleView.this.canSwap) {
                    PuzzleView.this.currentMode = ActionMode.SWAP;
                    PuzzleView.this.invalidate();
                }
            }
        };
        init(context, attributeSet);
    }

    @SuppressLint("ResourceType")
    private void init(Context context, AttributeSet attributeSet) {
        int[] PuzzleView = {R.attr.animation_duration, R.attr.handle_bar_color, R.attr.line_color, R.attr.line_size, R.attr.need_draw_line, R.attr.need_draw_outer_line, R.attr.piece_padding, R.attr.radian, R.attr.selected_line_color};
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, PuzzleView);
        this.lineSize = obtainStyledAttributes.getInt(3, 4);
        this.lineColor = obtainStyledAttributes.getColor(2, -1);
        this.selectedLineColor = obtainStyledAttributes.getColor(8, Color.parseColor("#99BBFB"));
        this.handleBarColor = obtainStyledAttributes.getColor(1, Color.parseColor("#99BBFB"));
        this.piecePadding = (float) obtainStyledAttributes.getDimensionPixelSize(6, 0);
        this.needDrawLine = obtainStyledAttributes.getBoolean(4, true);
        this.needDrawOuterLine = obtainStyledAttributes.getBoolean(5, true);
        this.duration = obtainStyledAttributes.getInt(0, 300);
        this.pieceRadian = obtainStyledAttributes.getFloat(7, 0.0f);
        obtainStyledAttributes.recycle();
        this.bounds = new RectF();
        this.linePaint = new Paint();
        this.linePaint.setAntiAlias(true);
        this.linePaint.setColor(this.lineColor);
        this.linePaint.setStrokeWidth((float) this.lineSize);
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setStrokeJoin(Paint.Join.ROUND);
        this.linePaint.setStrokeCap(Paint.Cap.SQUARE);
        this.selectedAreaPaint = new Paint();
        this.selectedAreaPaint.setAntiAlias(true);
        this.selectedAreaPaint.setStyle(Paint.Style.STROKE);
        this.selectedAreaPaint.setStrokeJoin(Paint.Join.ROUND);
        this.selectedAreaPaint.setStrokeCap(Paint.Cap.ROUND);
        this.selectedAreaPaint.setColor(this.selectedLineColor);
        this.selectedAreaPaint.setStrokeWidth((float) this.lineSize);
        this.handleBarPaint = new Paint();
        this.handleBarPaint.setAntiAlias(true);
        this.handleBarPaint.setStyle(Paint.Style.FILL);
        this.handleBarPaint.setColor(this.handleBarColor);
        this.handleBarPaint.setStrokeWidth((float) (this.lineSize * 3));
        this.midPoint = new PointF();
    }


    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        resetPuzzleBounds();
        this.areaPieceMap.clear();
        if (this.puzzlePieces.size() != 0) {
            for (int i5 = 0; i5 < this.puzzlePieces.size(); i5++) {
                PuzzlePiece puzzlePiece = this.puzzlePieces.get(i5);
                Area area = this.puzzleLayout.getArea(i5);
                puzzlePiece.setArea(area);
                this.areaPieceMap.put(area, puzzlePiece);
                if (this.needResetPieceMatrix) {
                    puzzlePiece.set(MatrixUtils.generateMatrix(puzzlePiece, 0.0f));
                } else {
                    puzzlePiece.fillArea(this, true);
                }
            }
        }
        invalidate();
    }

    public AspectRatio getAspectRatio() {
        return this.aspectRatio;
    }

    public void setAspectRatio(AspectRatio aspectRatio2) {
        this.aspectRatio = aspectRatio2;
    }

    private void resetPuzzleBounds() {
        this.bounds.left = (float) getPaddingLeft();
        this.bounds.top = (float) getPaddingTop();
        this.bounds.right = (float) (getWidth() - getPaddingRight());
        this.bounds.bottom = (float) (getHeight() - getPaddingBottom());
        if (this.puzzleLayout != null) {
            this.puzzleLayout.reset();
            this.puzzleLayout.setOuterBounds(this.bounds);
            this.puzzleLayout.layout();
            this.puzzleLayout.setPadding(this.piecePadding);
            this.puzzleLayout.setRadian(this.pieceRadian);
            if (this.initialInfo != null) {
                int size = this.initialInfo.lineInfos.size();
                for (int i = 0; i < size; i++) {
                    PuzzleLayout.LineInfo lineInfo = this.initialInfo.lineInfos.get(i);
                    Line line = this.puzzleLayout.getLines().get(i);
                    line.startPoint().x = lineInfo.startX;
                    line.startPoint().y = lineInfo.startY;
                    line.endPoint().x = lineInfo.endX;
                    line.endPoint().y = lineInfo.endY;
                }
            }
            this.puzzleLayout.sortAreas();
            this.puzzleLayout.update();
        }
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.puzzleLayout != null) {
            this.linePaint.setStrokeWidth((float) this.lineSize);
            this.selectedAreaPaint.setStrokeWidth((float) this.lineSize);
            this.handleBarPaint.setStrokeWidth((float) (this.lineSize * 3));
            int i = 0;
            while (i < this.puzzleLayout.getAreaCount() && i < this.puzzlePieces.size()) {
                PuzzlePiece puzzlePiece = this.puzzlePieces.get(i);
                if (!(puzzlePiece == this.handlingPiece && this.currentMode == ActionMode.SWAP) && this.puzzlePieces.size() > i) {
                    puzzlePiece.draw(canvas, this.quickMode);
                }
                i++;
            }
            if (this.needDrawOuterLine) {
                for (Line drawLine : this.puzzleLayout.getOuterLines()) {
                    drawLine(canvas, drawLine);
                }
            }
            if (this.needDrawLine) {
                for (Line drawLine2 : this.puzzleLayout.getLines()) {
                    drawLine(canvas, drawLine2);
                }
            }
            if (!(this.handlingPiece == null || this.currentMode == ActionMode.SWAP)) {
                drawSelectedArea(canvas, this.handlingPiece);
            }
            if (this.handlingPiece != null && this.currentMode == ActionMode.SWAP) {
                this.handlingPiece.draw(canvas, 128, this.quickMode);
                if (this.replacePiece != null) {
                    drawSelectedArea(canvas, this.replacePiece);
                }
            }
        }
    }

    private void drawSelectedArea(Canvas canvas, PuzzlePiece puzzlePiece) {
        Area area = puzzlePiece.getArea();
        canvas.drawPath(area.getAreaPath(), this.selectedAreaPaint);
        for (Line next : area.getLines()) {
            if (this.puzzleLayout.getLines().contains(next)) {
                PointF[] handleBarPoints = area.getHandleBarPoints(next);
                canvas.drawLine(handleBarPoints[0].x, handleBarPoints[0].y, handleBarPoints[1].x, handleBarPoints[1].y, this.handleBarPaint);
                canvas.drawCircle(handleBarPoints[0].x, handleBarPoints[0].y, (float) ((this.lineSize * 3) / 2), this.handleBarPaint);
                canvas.drawCircle(handleBarPoints[1].x, handleBarPoints[1].y, (float) ((this.lineSize * 3) / 2), this.handleBarPaint);
            }
        }
    }

    private void drawLine(Canvas canvas, Line line) {
        canvas.drawLine(line.startPoint().x, line.startPoint().y, line.endPoint().x, line.endPoint().y, this.linePaint);
    }

    public void updateLayout(PuzzleLayout puzzleLayout2) {
        ArrayList<PuzzlePiece> arrayList = new ArrayList<>(this.puzzlePieces);
        setPuzzleLayout(puzzleLayout2);
        for (PuzzlePiece drawable : arrayList) {
            addPiece(drawable.getDrawable());
        }
        invalidate();
    }

    public void setPuzzleLayout(PuzzleLayout puzzleLayout2) {
        clearPieces();
        this.puzzleLayout = puzzleLayout2;
        puzzleLayout2.setOuterBounds(this.bounds);
        puzzleLayout2.layout();
        invalidate();
    }

    public void setPuzzleLayout(PuzzleLayout.Info info) {
        this.initialInfo = info;
        clearPieces();
        this.puzzleLayout = PuzzleLayoutParser.parse(info);
        this.piecePadding = info.padding;
        this.pieceRadian = info.radian;
        setBackgroundColor(info.color);
        invalidate();
    }

    public PuzzleLayout getPuzzleLayout() {
        return this.puzzleLayout;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.touchEnable) {
            return super.onTouchEvent(motionEvent);
        }
        int action = motionEvent.getAction() & 255;
        if (action != 5) {
            switch (action) {
                case 0:
                    this.downX = motionEvent.getX();
                    this.downY = motionEvent.getY();
                    decideActionMode(motionEvent);
                    prepareAction(motionEvent);
                    break;
                case 1:
                case 3:
                    finishAction(motionEvent);
                    this.currentMode = ActionMode.NONE;
                    removeCallbacks(this.switchToSwapAction);
                    break;
                case 2:
                    performAction(motionEvent);
                    if ((Math.abs(motionEvent.getX() - this.downX) > 10.0f || Math.abs(motionEvent.getY() - this.downY) > 10.0f) && this.currentMode != ActionMode.SWAP) {
                        removeCallbacks(this.switchToSwapAction);
                        break;
                    }
            }
        } else {
            this.previousDistance = calculateDistance(motionEvent);
            calculateMidPoint(motionEvent, this.midPoint);
            decideActionMode(motionEvent);
        }
        invalidate();
        return true;
    }

    private void decideActionMode(MotionEvent motionEvent) {
        for (PuzzlePiece isAnimateRunning : this.puzzlePieces) {
            if (isAnimateRunning.isAnimateRunning()) {
                this.currentMode = ActionMode.NONE;
                return;
            }
        }
        if (motionEvent.getPointerCount() == 1) {
            this.handlingLine = findHandlingLine();
            if (this.handlingLine == null || !this.canMoveLine) {
                this.handlingPiece = findHandlingPiece();
                if (this.handlingPiece != null && this.canDrag) {
                    this.currentMode = ActionMode.DRAG;
                    postDelayed(this.switchToSwapAction, 500);
                    return;
                }
                return;
            }
            this.currentMode = ActionMode.MOVE;
        } else if (motionEvent.getPointerCount() > 1 && this.handlingPiece != null && this.handlingPiece.contains(motionEvent.getX(1), motionEvent.getY(1)) && this.currentMode == ActionMode.DRAG && this.canZoom) {
            this.currentMode = ActionMode.ZOOM;
        }
    }

    private void prepareAction(MotionEvent motionEvent) {
        switch (this.currentMode) {
            case DRAG:
                this.handlingPiece.record();
                return;
            case ZOOM:
                this.handlingPiece.record();
                return;
            case MOVE:
                this.handlingLine.prepareMove();
                this.needChangePieces.clear();
                this.needChangePieces.addAll(findNeedChangedPieces());
                for (PuzzlePiece next : this.needChangePieces) {
                    next.record();
                    next.setPreviousMoveX(this.downX);
                    next.setPreviousMoveY(this.downY);
                }
                return;
            default:
        }
    }

    private void performAction(MotionEvent motionEvent) {
        switch (this.currentMode) {
            case DRAG:
                dragPiece(this.handlingPiece, motionEvent);
                return;
            case ZOOM:
                zoomPiece(this.handlingPiece, motionEvent);
                return;
            case MOVE:
                moveLine(this.handlingLine, motionEvent);
                return;
            case SWAP:
                dragPiece(this.handlingPiece, motionEvent);
                this.replacePiece = findReplacePiece(motionEvent);
                return;
            default:
        }
    }

    private void finishAction(MotionEvent motionEvent) {
        switch (this.currentMode) {
            case DRAG:
                if (this.handlingPiece != null && !this.handlingPiece.isFilledArea()) {
                    this.handlingPiece.moveToFillArea(this);
                }
                if (this.previousHandlingPiece == this.handlingPiece && Math.abs(this.downX - motionEvent.getX()) < 3.0f && Math.abs(this.downY - motionEvent.getY()) < 3.0f) {
                    this.handlingPiece = null;
                }
                this.previousHandlingPiece = this.handlingPiece;
                break;
            case ZOOM:
                if (this.handlingPiece != null && !this.handlingPiece.isFilledArea()) {
                    if (this.handlingPiece.canFilledArea()) {
                        this.handlingPiece.moveToFillArea(this);
                    } else {
                        this.handlingPiece.fillArea(this, false);
                    }
                }
                this.previousHandlingPiece = this.handlingPiece;
                break;
            case SWAP:
                if (!(this.handlingPiece == null || this.replacePiece == null)) {
                    swapPiece();
                    this.handlingPiece = null;
                    this.replacePiece = null;
                    this.previousHandlingPiece = null;
                    break;
                }
        }
        if (this.handlingPiece != null && this.onPieceSelectedListener != null) {
            this.onPieceSelectedListener.onPieceSelected(this.handlingPiece, this.puzzlePieces.indexOf(this.handlingPiece));
        } else if (this.handlingPiece == null && this.onPieceUnSelectedListener != null) {
            this.onPieceUnSelectedListener.onPieceUnSelected();
        }
        this.handlingLine = null;
        this.needChangePieces.clear();
    }

    public void setPreviousHandlingPiece(PuzzlePiece puzzlePiece) {
        this.previousHandlingPiece = puzzlePiece;
    }

    private void swapPiece() {
        Drawable drawable = this.handlingPiece.getDrawable();
        String path = this.handlingPiece.getPath();
        this.handlingPiece.setDrawable(this.replacePiece.getDrawable());
        this.handlingPiece.setPath(this.replacePiece.getPath());
        this.replacePiece.setDrawable(drawable);
        this.replacePiece.setPath(path);
        this.handlingPiece.fillArea(this, true);
        this.replacePiece.fillArea(this, true);
    }

    private void moveLine(Line line, MotionEvent motionEvent) {
        boolean z;
        if (line != null && motionEvent != null) {
            if (line.direction() == Line.Direction.HORIZONTAL) {
                z = line.move(motionEvent.getY() - this.downY, 80.0f);
            } else {
                z = line.move(motionEvent.getX() - this.downX, 80.0f);
            }
            if (z) {
                this.puzzleLayout.update();
                this.puzzleLayout.sortAreas();
                updatePiecesInArea(line, motionEvent);
            }
        }
    }

    private void updatePiecesInArea(Line line, MotionEvent motionEvent) {
        for (int i = 0; i < this.needChangePieces.size(); i++) {
            this.needChangePieces.get(i).updateWith(motionEvent, line);
        }
    }

    private void zoomPiece(PuzzlePiece puzzlePiece, MotionEvent motionEvent) {
        if (puzzlePiece != null && motionEvent != null && motionEvent.getPointerCount() >= 2) {
            float calculateDistance = calculateDistance(motionEvent) / this.previousDistance;
            puzzlePiece.zoomAndTranslate(calculateDistance, calculateDistance, this.midPoint, motionEvent.getX() - this.downX, motionEvent.getY() - this.downY);
        }
    }

    private void dragPiece(PuzzlePiece puzzlePiece, MotionEvent motionEvent) {
        if (puzzlePiece != null && motionEvent != null) {
            puzzlePiece.translate(motionEvent.getX() - this.downX, motionEvent.getY() - this.downY);
        }
    }

    public void replace(Bitmap bitmap, String str) {
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        bitmapDrawable.setAntiAlias(true);
        bitmapDrawable.setFilterBitmap(true);
        replace(bitmapDrawable, str);
    }

    public void replace(Drawable drawable, String str) {
        if (this.handlingPiece != null) {
            this.handlingPiece.setPath(str);
            this.handlingPiece.setDrawable(drawable);
            this.handlingPiece.set(MatrixUtils.generateMatrix(this.handlingPiece, 0.0f));
            invalidate();
        }
    }

    public void setHandlingPiece(PuzzlePiece puzzlePiece) {
        this.handlingPiece = puzzlePiece;
    }

    public void flipVertically() {
        if (this.handlingPiece != null) {
            this.handlingPiece.postFlipVertically();
            this.handlingPiece.record();
            invalidate();
        }
    }

    public void flipHorizontally() {
        if (this.handlingPiece != null) {
            this.handlingPiece.postFlipHorizontally();
            this.handlingPiece.record();
            invalidate();
        }
    }

    public void rotate(float f) {
        if (this.handlingPiece != null) {
            this.handlingPiece.postRotate(f);
            this.handlingPiece.record();
            invalidate();
        }
    }

    private PuzzlePiece findHandlingPiece() {
        for (PuzzlePiece next : this.puzzlePieces) {
            if (next.contains(this.downX, this.downY)) {
                return next;
            }
        }
        return null;
    }

    private Line findHandlingLine() {
        for (Line next : this.puzzleLayout.getLines()) {
            if (next.contains(this.downX, this.downY, 40.0f)) {
                return next;
            }
        }
        return null;
    }

    private PuzzlePiece findReplacePiece(MotionEvent motionEvent) {
        for (PuzzlePiece next : this.puzzlePieces) {
            if (next.contains(motionEvent.getX(), motionEvent.getY())) {
                return next;
            }
        }
        return null;
    }

    private List<PuzzlePiece> findNeedChangedPieces() {
        if (this.handlingLine == null) {
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList();
        for (PuzzlePiece next : this.puzzlePieces) {
            if (next.contains(this.handlingLine)) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }


    public float calculateDistance(MotionEvent motionEvent) {
        float x = motionEvent.getX(0) - motionEvent.getX(1);
        float y = motionEvent.getY(0) - motionEvent.getY(1);
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    private void calculateMidPoint(MotionEvent motionEvent, PointF pointF) {
        pointF.x = (motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f;
        pointF.y = (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f;
    }

    public void reset() {
        clearPieces();
        if (this.puzzleLayout != null) {
            this.puzzleLayout.reset();
        }
    }

    public void clearPieces() {
        clearHandlingPieces();
        this.puzzlePieces.clear();
        invalidate();
    }

    public void clearHandlingPieces() {
        this.handlingLine = null;
        this.handlingPiece = null;
        this.replacePiece = null;
        this.needChangePieces.clear();
        invalidate();
    }

    public void addPieces(List<Bitmap> list) {
        for (Bitmap addPiece : list) {
            addPiece(addPiece);
        }
        postInvalidate();
    }

    public void addPiece(Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        bitmapDrawable.setAntiAlias(true);
        bitmapDrawable.setFilterBitmap(true);
        addPiece(bitmapDrawable, (Matrix) null);
    }

    public void addPiece(Bitmap bitmap, Matrix matrix, String str) {
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        bitmapDrawable.setAntiAlias(true);
        bitmapDrawable.setFilterBitmap(true);
        addPiece(bitmapDrawable, matrix, str);
    }

    public void addPiece(Drawable drawable) {
        addPiece(drawable, (Matrix) null);
    }

    public void addPiece(Drawable drawable, Matrix matrix) {
        addPiece(drawable, matrix, "");
    }

    public void addPiece(Drawable drawable, Matrix matrix, String str) {
        Matrix matrix2;
        int size = this.puzzlePieces.size();
        if (size >= this.puzzleLayout.getAreaCount()) {
            return;
        }
        Area area = this.puzzleLayout.getArea(size);
        area.setPadding(this.piecePadding);
        PuzzlePiece puzzlePiece = new PuzzlePiece(drawable, area, new Matrix());
        if (matrix != null) {
            matrix2 = new Matrix(matrix);
        } else {
            matrix2 = MatrixUtils.generateMatrix(area, drawable, 0.0f);
        }
        puzzlePiece.set(matrix2);
        puzzlePiece.setAnimateDuration(this.duration);
        puzzlePiece.setPath(str);
        this.puzzlePieces.add(puzzlePiece);
        this.areaPieceMap.put(area, puzzlePiece);
        setPiecePadding(this.piecePadding);
        setPieceRadian(this.pieceRadian);
        invalidate();
    }


    public PuzzlePiece getHandlingPiece() {
        return this.handlingPiece;
    }


    public void setAnimateDuration(int i) {
        this.duration = i;
        for (PuzzlePiece animateDuration : this.puzzlePieces) {
            animateDuration.setAnimateDuration(i);
        }
    }


    public void setNeedDrawLine(boolean z) {
        this.needDrawLine = z;
        this.handlingPiece = null;
        this.previousHandlingPiece = null;
        invalidate();
    }


    public void setNeedDrawOuterLine(boolean z) {
        this.needDrawOuterLine = z;
        invalidate();
    }


    public void setLineColor(int i) {
        this.lineColor = i;
        this.linePaint.setColor(i);
        invalidate();
    }


    public void setLineSize(int i) {
        this.lineSize = i;
        invalidate();
    }


    public void setSelectedLineColor(int i) {
        this.selectedLineColor = i;
        this.selectedAreaPaint.setColor(i);
        invalidate();
    }


    public void setHandleBarColor(int i) {
        this.handleBarColor = i;
        this.handleBarPaint.setColor(i);
        invalidate();
    }


    public void setTouchEnable(boolean z) {
        this.touchEnable = z;
    }

    public void clearHandling() {
        this.handlingPiece = null;
        this.handlingLine = null;
        this.replacePiece = null;
        this.previousHandlingPiece = null;
        this.needChangePieces.clear();
    }

    public void setPiecePadding(float f) {
        this.piecePadding = f;
        if (this.puzzleLayout != null) {
            this.puzzleLayout.setPadding(f);
            int size = this.puzzlePieces.size();
            for (int i = 0; i < size; i++) {
                PuzzlePiece puzzlePiece = this.puzzlePieces.get(i);
                if (puzzlePiece.canFilledArea()) {
                    puzzlePiece.moveToFillArea((View) null);
                } else {
                    puzzlePiece.fillArea(this, true);
                }
            }
        }
        invalidate();
    }

    public void setPieceRadian(float f) {
        this.pieceRadian = f;
        if (this.puzzleLayout != null) {
            this.puzzleLayout.setRadian(f);
        }
        invalidate();
    }

    public int getBackgroundResourceMode() {
        return this.backgroundResource;
    }

    public void setBackgroundResourceMode(int i) {
        this.backgroundResource = i;
    }


    public void setBackgroundColor(int i) {
        super.setBackgroundColor(i);
        if (this.puzzleLayout != null) {
            this.puzzleLayout.setColor(i);
        }
    }


    public float getPiecePadding() {
        return this.piecePadding;
    }

    public float getPieceRadian() {
        return this.pieceRadian;
    }

    public List<PuzzlePiece> getPuzzlePieces() {
        int size = this.puzzlePieces.size();
        ArrayList arrayList = new ArrayList(size);
        this.puzzleLayout.sortAreas();
        for (int i = 0; i < size; i++) {
            arrayList.add(this.areaPieceMap.get(this.puzzleLayout.getArea(i)));
        }
        return arrayList;
    }


    public void setOnPieceSelectedListener(OnPieceSelectedListener onPieceSelectedListener2) {
        this.onPieceSelectedListener = onPieceSelectedListener2;
    }

    public void setOnPieceUnSelectedListener(OnPieceUnSelectedListener onPieceUnSelectedListener2) {
        this.onPieceUnSelectedListener = onPieceUnSelectedListener2;
    }
}
