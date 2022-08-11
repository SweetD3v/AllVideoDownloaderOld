package com.example.allviddownloader.collage_maker.features;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.allviddownloader.R;
import com.example.allviddownloader.collage_maker.ui.interfaces.OnSaveBitmap;

import org.wysaid.view.ImageGLSurfaceView;

import java.util.ArrayList;

public class PhotoEditorView extends StickerView {

    private Bitmap currentBitmap;
    private BrushDrawView mBrushDrawView;
    ArrayList<Bitmap> allBitmaps;

    public ImageGLSurfaceView mGLSurfaceView;
    private FilterImageView mImgSource;
    public int undo_count = 0;
    public int redo_count = 0;
    Bitmap originalBitmap;

    public PhotoEditorView(Context context) {
        super(context);
        init(null);
    }

    public PhotoEditorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet);
    }

    public PhotoEditorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet);
    }

    public ArrayList<Bitmap> getAllBitmaps() {
        return allBitmaps;
    }

    public Bitmap getUndoBitmap() {
        if (allBitmaps.size() > 0) {
            redo_count++;
            undo_count--;
            return getAllBitmaps().get(undo_count);
        } else return null;
    }

    public Bitmap getRedoBitmap() {
        if (allBitmaps.size() > 0) {
            redo_count--;
            undo_count++;
            return getAllBitmaps().get(undo_count);
        } else return null;
    }

    public void clearAllUndo() {
        if (undo_count == 0) {
            allBitmaps.clear();
            allBitmaps = new ArrayList<>();
            addNewBitmap(getCurrentBitmap());
        }
    }

    public void addNewBitmap(Bitmap bitmap) {
        allBitmaps.add(bitmap);
        undo_count = allBitmaps.size() - 1;
    }

    @SuppressLint({"Recycle", "ResourceType"})
    private void init(@Nullable AttributeSet attributeSet) {
        allBitmaps = new ArrayList<>();
        this.mImgSource = new FilterImageView(getContext());
        this.mImgSource.setId(1);
        this.mImgSource.setAdjustViewBounds(true);
        this.mImgSource.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams.addRule(13, -1);
        this.mBrushDrawView = new BrushDrawView(getContext());
        this.mBrushDrawView.setVisibility(GONE);
        this.mBrushDrawView.setId(2);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams2.addRule(13, -1);
        layoutParams2.addRule(6, 1);
        layoutParams2.addRule(8, 1);
        this.mGLSurfaceView = new ImageGLSurfaceView(getContext(), attributeSet);
        this.mGLSurfaceView.setId(3);
        this.mGLSurfaceView.setVisibility(VISIBLE);
        this.mGLSurfaceView.setAlpha(1.0f);
        this.mGLSurfaceView.setDisplayMode(ImageGLSurfaceView.DisplayMode.DISPLAY_ASPECT_FIT);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams3.addRule(13, -1);
        layoutParams3.addRule(6, 1);
        layoutParams3.addRule(8, 1);
        addView(this.mImgSource, layoutParams);
        addView(this.mGLSurfaceView, layoutParams3);
        addView(this.mBrushDrawView, layoutParams2);
    }


    public void setImageSource(final Bitmap bitmap) {
        this.mImgSource.setImageBitmap(bitmap);
        if (this.mGLSurfaceView.getImageHandler() != null) {
            this.mGLSurfaceView.setImageBitmap(bitmap);
        } else {
            this.mGLSurfaceView.setSurfaceCreatedCallback(new ImageGLSurfaceView.OnSurfaceCreatedCallback() {
                public void surfaceCreated() {
                    PhotoEditorView.this.mGLSurfaceView.setImageBitmap(bitmap);
                }
            });
        }
        this.currentBitmap = bitmap;
    }

    public void setImageSource(Bitmap bitmap, ImageGLSurfaceView.OnSurfaceCreatedCallback onSurfaceCreatedCallback) {
        this.mImgSource.setImageBitmap(bitmap);
        if (this.mGLSurfaceView.getImageHandler() != null) {
            this.mGLSurfaceView.setImageBitmap(bitmap);
        } else {
            this.mGLSurfaceView.setSurfaceCreatedCallback(onSurfaceCreatedCallback);
        }
        this.currentBitmap = bitmap;
    }

    public Bitmap getCurrentBitmap() {
        return this.currentBitmap;
    }


    public BrushDrawView getBrushDrawingView() {
        return this.mBrushDrawView;
    }

    public ImageGLSurfaceView getGLSurfaceView() {
        return this.mGLSurfaceView;
    }

    public void saveGLSurfaceViewAsBitmap(@NonNull final OnSaveBitmap onSaveBitmap) {
        if (this.mGLSurfaceView.getVisibility() == VISIBLE) {
            this.mGLSurfaceView.getResultBitmap(new ImageGLSurfaceView.QueryResultBitmapCallback() {
                public void get(Bitmap bitmap) {
                    onSaveBitmap.onBitmapReady(bitmap);
                }
            });
        }
    }

    public void setFilterEffect(String str) {
        this.mGLSurfaceView.setFilterWithConfig(str);
    }

    public void setFilterIntensity(float f) {
        this.mGLSurfaceView.setFilterIntensity(f);
    }
}
