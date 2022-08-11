package com.example.allviddownloader.collage_maker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import com.example.allviddownloader.collage_maker.features.BrushDrawView;
import com.example.allviddownloader.collage_maker.features.DrawBitmapModel;
import com.example.allviddownloader.collage_maker.features.PhotoEditorView;
import com.example.allviddownloader.collage_maker.ui.interfaces.BrushViewChangeListener;
import com.example.allviddownloader.collage_maker.ui.interfaces.OnPhotoEditorListener;
import com.example.allviddownloader.collage_maker.ui.interfaces.OnSaveBitmap;
import com.example.allviddownloader.collage_maker.utils.BitmapUtil;
import com.example.allviddownloader.collage_maker.utils.SaveSettings;

import org.wysaid.view.ImageGLSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoEditor implements BrushViewChangeListener {
    private static final String TAG = "PhotoEditor";
    private List<View> addedViews;
    private BrushDrawView brushDrawView;
    private ImageGLSurfaceView glSurfaceView;
    private OnPhotoEditorListener mOnPhotoEditorListener;

    public PhotoEditorView parentView;
    private List<View> redoViews;

    public interface OnSaveListener {
        void onFailure(@NonNull Exception exc);

        void onSuccess(@NonNull String str);
    }

    private PhotoEditor(Builder builder) {
        this.parentView = builder.parentView;
        this.brushDrawView = builder.brushDrawView;
        this.glSurfaceView = builder.glSurfaceView;
        this.brushDrawView.setBrushViewChangeListener(this);
        this.addedViews = new ArrayList();
        this.redoViews = new ArrayList();
    }

    public BrushDrawView getBrushDrawView() {
        return this.brushDrawView;
    }


    public void setBrushDrawingMode(boolean z) {
        if (this.brushDrawView != null) {
            this.brushDrawView.setBrushDrawingMode(z);
        }
    }

    public void setAdjustFilter(String str) {
        this.glSurfaceView.setFilterWithConfig(str);
    }

    public void setFilterIntensityForIndex(float f, int i, boolean z) {
        this.glSurfaceView.setFilterIntensityForIndex(f, i, z);
    }

    public void setBrushMode(int i) {
        this.brushDrawView.setDrawMode(i);
    }

    public void setBrushMagic(DrawBitmapModel drawBitmapModel) {
        this.brushDrawView.setCurrentMagicBrush(drawBitmapModel);
    }


    public void setBrushSize(float f) {
        if (this.brushDrawView != null) {
            this.brushDrawView.setBrushSize(f);
        }
    }


    public void setBrushColor(@ColorInt int i) {
        if (this.brushDrawView != null) {
            this.brushDrawView.setBrushColor(i);
        }
    }

    public void setBrushEraserSize(float f) {
        if (this.brushDrawView != null) {
            this.brushDrawView.setBrushEraserSize(f);
        }
    }


    public void brushEraser() {
        if (this.brushDrawView != null) {
            this.brushDrawView.brushEraser();
        }
    }

    public void redoBrush() {
        if (this.brushDrawView != null) {
            this.brushDrawView.redo();
        }
    }

    public void undoBrush() {
        if (this.brushDrawView != null) {
            this.brushDrawView.undo();
        }
    }

    public void clearBrushAllViews() {
        if (this.brushDrawView != null) {
            this.brushDrawView.clearAll();
        }
    }

    public void clearAllViews() {
        for (int i = 0; i < this.addedViews.size(); i++) {
            this.parentView.removeView(this.addedViews.get(i));
        }
        if (this.addedViews.contains(this.brushDrawView)) {
            this.parentView.addView(this.brushDrawView);
        }
        this.addedViews.clear();
        this.redoViews.clear();
        clearBrushAllViews();
    }

    public void setFilterEffect(String str) {
        this.parentView.setFilterEffect(str);
    }

    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @SuppressLint({"StaticFieldLeak"})
    @Deprecated
    public void saveImage(@NonNull String str, @NonNull OnSaveListener onSaveListener) {
        saveAsFile(str, onSaveListener);
    }

    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull String str, @NonNull OnSaveListener onSaveListener) {
        saveAsFile(str, new SaveSettings.Builder().build(), onSaveListener);
    }

    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    @SuppressLint({"StaticFieldLeak"})
    public void saveAsFile(@NonNull final String str, @NonNull final SaveSettings saveSettings, @NonNull final OnSaveListener onSaveListener) {
        this.parentView.saveGLSurfaceViewAsBitmap(new OnSaveBitmap() {
            public void onBitmapReady(Bitmap bitmap) {
                new AsyncTask<String, String, Exception>() {

                    public void onPreExecute() {
                        super.onPreExecute();
                    }


                    @SuppressLint({"MissingPermission"})
                    public Exception doInBackground(String... strArr) {
                        Bitmap bitmap;
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(str), false);
                            if (PhotoEditor.this.parentView != null) {
                                if (saveSettings.isTransparencyEnabled()) {
                                    bitmap = BitmapUtil.removeTransparency(getBitmapFromView(PhotoEditor.this.parentView));
                                } else {
                                    bitmap = getBitmapFromView(PhotoEditor.this.parentView);
                                }
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            }
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return e;
                        }
                    }


                    public void onPostExecute(Exception exc) {
                        super.onPostExecute(exc);
                        if (exc == null) {
                            if (saveSettings.isClearViewsEnabled()) {
                                PhotoEditor.this.clearAllViews();
                            }
                            onSaveListener.onSuccess(str);
                            return;
                        }
                        onSaveListener.onFailure(exc);
                    }
                }.execute(new String[0]);
            }

        });
    }

    @SuppressLint({"StaticFieldLeak"})
    public void saveStickerAsBitmap(@NonNull OnSaveBitmap onSaveBitmap) {
        saveStickerAsBitmap(new SaveSettings.Builder().build(), onSaveBitmap);
    }

    @SuppressLint({"StaticFieldLeak"})
    public void saveStickerAsBitmap(@NonNull SaveSettings saveSettings, @NonNull OnSaveBitmap onSaveBitmap) {
        Bitmap bitmap;

        if (saveSettings.isTransparencyEnabled()) {
            bitmap = BitmapUtil.removeTransparency(getBitmapFromView(this.parentView));
        } else {
            bitmap = getBitmapFromView(this.parentView);
        }
        onSaveBitmap.onBitmapReady(bitmap);
    }

    public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
    }

    public void onViewAdd(BrushDrawView brushDrawView2) {
        if (this.redoViews.size() > 0) {
            this.redoViews.remove(this.redoViews.size() - 1);
        }
        this.addedViews.add(brushDrawView2);
        if (this.mOnPhotoEditorListener != null) {
            this.mOnPhotoEditorListener.onAddViewListener();
        }
    }

    public void onViewRemoved() {
        if (this.addedViews.size() > 0) {
            View remove = this.addedViews.remove(this.addedViews.size() - 1);
            if (!(remove instanceof BrushDrawView)) {
                this.parentView.removeView(remove);
            }
            this.redoViews.add(remove);
        }
        if (this.mOnPhotoEditorListener != null) {
            this.mOnPhotoEditorListener.onRemoveViewListener();
            this.mOnPhotoEditorListener.onRemoveViewListener();
        }
    }

    public void onStartDrawing() {
        if (this.mOnPhotoEditorListener != null) {
            this.mOnPhotoEditorListener.onStartViewChangeListener();
        }
    }

    public void onStopDrawing() {
        if (this.mOnPhotoEditorListener != null) {
            this.mOnPhotoEditorListener.onStopViewChangeListener();
        }
    }

    public static class Builder {

        public BrushDrawView brushDrawView;

        public ImageGLSurfaceView glSurfaceView;

        public PhotoEditorView parentView;

        public Builder(PhotoEditorView photoEditorView) {
            this.parentView = photoEditorView;
            this.brushDrawView = photoEditorView.getBrushDrawingView();
            this.glSurfaceView = photoEditorView.getGLSurfaceView();
        }

        public Builder setPinchTextScalable(boolean z) {
            return this;
        }

        public PhotoEditor build() {
            return new PhotoEditor(this);
        }
    }

    public Bitmap getBitmapFromView(View view)
    {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
