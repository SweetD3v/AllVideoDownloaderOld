package com.tools.videodownloader.collage_maker.features;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class FilterImageView extends AppCompatImageView {

    public FilterImageView(Context context) {
        super(context);
    }

    public FilterImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public FilterImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }


    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);
    }

    public void setImageIcon(@Nullable Icon icon) {
        super.setImageIcon(icon);
    }

    public void setImageMatrix(Matrix matrix) {
        super.setImageMatrix(matrix);
    }

    public void setImageState(int[] iArr, boolean z) {
        super.setImageState(iArr, z);
    }

    public void setImageTintList(@Nullable ColorStateList colorStateList) {
        super.setImageTintList(colorStateList);
    }

    public void setImageTintMode(@Nullable PorterDuff.Mode mode) {
        super.setImageTintMode(mode);
    }

    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    public void setImageResource(int i) {
        super.setImageResource(i);
    }

    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
    }

    public void setImageLevel(int i) {
        super.setImageLevel(i);
    }

}
