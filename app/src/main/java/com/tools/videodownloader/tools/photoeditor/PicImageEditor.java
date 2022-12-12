package com.tools.videodownloader.tools.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.annotation.UiThread;

import com.tools.videodownloader.collage_maker.features.DrawBitmapModel;
import com.tools.videodownloader.collage_maker.ui.interfaces.BrushViewChangeListener;
import com.tools.videodownloader.collage_maker.ui.interfaces.OnPhotoEditorListener;
import com.tools.videodownloader.collage_maker.ui.interfaces.OnSaveBitmap;
import com.tools.videodownloader.collage_maker.utils.BitmapUtil;

import org.wysaid.view.ImageGLSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class PicImageEditor implements BrushViewChangeListener {
    private List<View> addedViews;
    private BrushDrawingView brushDrawingView;
    private ImageGLSurfaceView glSurfaceView;
    private OnPhotoEditorListener mOnPhotoEditorListener;

    public ImagePicEditorView parentView;
    private List<View> redoViews;

    public interface OnSaveListener {
        void onFailure(@NonNull Exception exc);

        void onSuccess(@NonNull String str);
    }

    private PicImageEditor(Builder builder) {
        this.parentView = builder.parentView;
        this.brushDrawingView = builder.brushDrawingView;
        this.glSurfaceView = builder.glSurfaceView;
        this.brushDrawingView.setBrushViewChangeListener(this);
        this.addedViews = new ArrayList();
        this.redoViews = new ArrayList();
    }

    public BrushDrawingView getBrushDrawingView() {
        return this.brushDrawingView;
    }

    public void setBrushDrawingMode(boolean z) {
        if (this.brushDrawingView != null) {
            this.brushDrawingView.setBrushDrawingMode(z);
        }
    }

    public void setAdjustFilter(String str) {
        this.glSurfaceView.setFilterWithConfig(str);
    }

    public void setFilterIntensityForIndex(float f, int i, boolean z) {
        this.glSurfaceView.setFilterIntensityForIndex(f, i, z);
    }

    public void setBrushMode(int i) {
        this.brushDrawingView.setDrawMode(i);
    }

    public void setBrushMagic(DrawBitmapModel drawBitmapModel) {
        this.brushDrawingView.setCurrentMagicBrush(drawBitmapModel);
    }


    public void setBrushSize(float f) {
        if (this.brushDrawingView != null) {
            this.brushDrawingView.setBrushSize(f);
        }
    }


    public void setBrushColor(@ColorInt int i) {
        if (this.brushDrawingView != null) {
            this.brushDrawingView.setBrushColor(i);
        }
    }

    public void setBrushEraserSize(float f) {
        if (this.brushDrawingView != null) {
            this.brushDrawingView.setBrushEraserSize(f);
        }
    }


    public void brushEraser() {
        if (this.brushDrawingView != null) {
            this.brushDrawingView.brushEraser();
        }
    }


    public boolean undo() {
        if (this.addedViews.size() > 0) {
            View view = this.addedViews.get(this.addedViews.size() - 1);
            if (!(view instanceof BrushDrawingView)) {
                this.addedViews.remove(this.addedViews.size() - 1);
                this.parentView.removeView(view);
                this.redoViews.add(view);
                if (this.mOnPhotoEditorListener != null) {
                    this.mOnPhotoEditorListener.onRemoveViewListener(this.addedViews.size());
                    Object tag = view.getTag();
                    if (tag != null && (tag instanceof BrushViewType)) {
                        this.mOnPhotoEditorListener.onRemoveViewListener((BrushViewType) tag, this.addedViews.size());
                    }
                }
            } else if (this.brushDrawingView == null || !this.brushDrawingView.undo()) {
                return false;
            } else {
                return true;
            }
        }
        if (this.addedViews.size() != 0) {
            return true;
        }
        return false;
    }

    public boolean redo() {
        if (this.redoViews.size() > 0) {
            View view = this.redoViews.get(this.redoViews.size() - 1);
            if (!(view instanceof BrushDrawingView)) {
                this.redoViews.remove(this.redoViews.size() - 1);
                this.parentView.addView(view);
                this.addedViews.add(view);
                Object tag = view.getTag();
                if (!(this.mOnPhotoEditorListener == null || tag == null || !(tag instanceof BrushViewType))) {
                    this.mOnPhotoEditorListener.onAddViewListener((BrushViewType) tag, this.addedViews.size());
                }
            } else if (this.brushDrawingView == null || !this.brushDrawingView.redo()) {
                return false;
            } else {
                return true;
            }
        }
        if (this.redoViews.size() != 0) {
            return true;
        }
        return false;
    }

    public void redoBrush() {
        if (this.brushDrawingView != null) {
            this.brushDrawingView.redo();
        }
    }

    public void undoBrush() {
        if (this.brushDrawingView != null) {
            this.brushDrawingView.undo();
        }
    }

    public void clearBrushAllViews() {
        if (this.brushDrawingView != null) {
            this.brushDrawingView.clearAll();
        }
    }

    public void clearAllViews() {
        for (int i = 0; i < this.addedViews.size(); i++) {
            this.parentView.removeView(this.addedViews.get(i));
        }
        if (this.addedViews.contains(this.brushDrawingView)) {
            this.parentView.addView(this.brushDrawingView);
        }
        this.addedViews.clear();
        this.redoViews.clear();
        clearBrushAllViews();
    }

    @UiThread
    public void clearHelperBox() {
    }

    public void setFilterEffect(String str) {
        this.parentView.setFilterEffect(str);
    }

    @RequiresPermission(allOf = {"android.permission.WRITE_EXTERNAL_STORAGE"})
    @SuppressLint({"StaticFieldLeak"})
    @Deprecated
    public void saveImage(@NonNull String str, @NonNull OnSaveListener onSaveListener) {
        saveAsFile(str, onSaveListener);
    }

    @RequiresPermission(allOf = {"android.permission.WRITE_EXTERNAL_STORAGE"})
    public void saveAsFile(@NonNull String str, @NonNull OnSaveListener onSaveListener) {
        saveAsFile(str, new SaveImageSettings.Builder().build(), onSaveListener);
    }

    @RequiresPermission(allOf = {"android.permission.WRITE_EXTERNAL_STORAGE"})
    @SuppressLint({"StaticFieldLeak"})
    public void saveAsFile(@NonNull final String str, @NonNull final SaveImageSettings saveImageSettings, @NonNull final OnSaveListener onSaveListener) {
        this.parentView.saveGLSurfaceViewAsBitmap(new OnSaveBitmap() {
            public void onBitmapReady(Bitmap bitmap) {
                new AsyncTask<String, String, Exception>() {

                    public void onPreExecute() {
                        super.onPreExecute();
                        PicImageEditor.this.clearHelperBox();
                    }


                    @SuppressLint({"MissingPermission"})
                    public Exception doInBackground(String... strArr) {
                        Bitmap bitmap;
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(str), false);
                            if (PicImageEditor.this.parentView != null) {
                                if (saveImageSettings.isTransparencyEnabled()) {
                                    bitmap = BitmapUtil.removeTransparency(getBitmapFromView(PicImageEditor.this.parentView));
                                } else {
                                    bitmap = getBitmapFromView(PicImageEditor.this.parentView);
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
                            if (saveImageSettings.isClearViewsEnabled()) {
                                PicImageEditor.this.clearAllViews();
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
        saveStickerAsBitmap(new SaveImageSettings.Builder().build(), onSaveBitmap);
    }

    @SuppressLint({"StaticFieldLeak"})
    public void saveStickerAsBitmap(@NonNull SaveImageSettings saveImageSettings, @NonNull OnSaveBitmap onSaveBitmap) {
        Bitmap bitmap;

        if (saveImageSettings.isTransparencyEnabled()) {
            bitmap = BitmapUtil.removeTransparency(getBitmapFromView(this.parentView));
        } else {
            bitmap = getBitmapFromView(this.parentView);
        }
        onSaveBitmap.onBitmapReady(bitmap);
    }

    public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
    }

    public void onViewAdd(BrushDrawingView brushDrawingView2) {
        if (this.redoViews.size() > 0) {
            this.redoViews.remove(this.redoViews.size() - 1);
        }
        this.addedViews.add(brushDrawingView2);
        if (this.mOnPhotoEditorListener != null) {
            this.mOnPhotoEditorListener.onAddViewListener(BrushViewType.BRUSH_DRAWING, this.addedViews.size());
        }
    }

    public void onViewRemoved(BrushDrawingView brushDrawingView2) {
        if (this.addedViews.size() > 0) {
            View remove = this.addedViews.remove(this.addedViews.size() - 1);
            if (!(remove instanceof BrushDrawingView)) {
                this.parentView.removeView(remove);
            }
            this.redoViews.add(remove);
        }
        if (this.mOnPhotoEditorListener != null) {
            this.mOnPhotoEditorListener.onRemoveViewListener(this.addedViews.size());
            this.mOnPhotoEditorListener.onRemoveViewListener(BrushViewType.BRUSH_DRAWING, this.addedViews.size());
        }
    }

    public void onStartDrawing() {
        if (this.mOnPhotoEditorListener != null) {
            this.mOnPhotoEditorListener.onStartViewChangeListener(BrushViewType.BRUSH_DRAWING);
        }
    }

    public void onStopDrawing() {
        if (this.mOnPhotoEditorListener != null) {
            this.mOnPhotoEditorListener.onStopViewChangeListener(BrushViewType.BRUSH_DRAWING);
        }
    }

    public static class Builder {

        public BrushDrawingView brushDrawingView;
        public Context context;
        public ImageGLSurfaceView glSurfaceView;
        public boolean isTextPinchZoomable = true;
        public ImagePicEditorView parentView;

        public Builder(Context context2, ImagePicEditorView imagePicEditorView) {
            this.context = context2;
            this.parentView = imagePicEditorView;
            this.brushDrawingView = imagePicEditorView.getBrushDrawingView();
            this.glSurfaceView = imagePicEditorView.getGLSurfaceView();
        }

        public Builder setPinchTextScalable(boolean z) {
            this.isTextPinchZoomable = z;
            return this;
        }

        public PicImageEditor build() {
            return new PicImageEditor(this);
        }
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
