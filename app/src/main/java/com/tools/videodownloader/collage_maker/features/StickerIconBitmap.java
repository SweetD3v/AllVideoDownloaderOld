package com.tools.videodownloader.collage_maker.features;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;


public class StickerIconBitmap extends DrawableSticker implements EventStickerIcon {
    public static final String EDIT = "EDIT";
    public static final String FLIP = "FLIP";
    public static final String REMOVE = "REMOVE";
    public static final String ROTATE = "ROTATE";
    public static final String ZOOM = "ZOOM";
    private EventStickerIcon iconEvent;
    private float iconRadius = 30.0F;
    private int position;
    private String tag;
    private float x;
    private float y;

    public StickerIconBitmap(Drawable paramDrawable, int paramInt, String paramString) {
        super(paramDrawable);
        this.position = paramInt;
        this.tag = paramString;
    }

    public void draw(Canvas paramCanvas, Paint paramPaint) {
        paramCanvas.drawCircle(this.x, this.y, this.iconRadius, paramPaint);
        draw(paramCanvas);
    }

    public float getIconRadius() {
        return this.iconRadius;
    }

    public int getPosition() {
        return this.position;
    }

    public String getTag() {
        return this.tag;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void onActionDown(StickerView paramStickerView, MotionEvent paramMotionEvent) {
        if (this.iconEvent != null)
            this.iconEvent.onActionDown(paramStickerView, paramMotionEvent);
    }

    public void onActionMove(StickerView paramStickerView, MotionEvent paramMotionEvent) {
        if (this.iconEvent != null)
            this.iconEvent.onActionMove(paramStickerView, paramMotionEvent);
    }

    public void onActionUp(StickerView paramStickerView, MotionEvent paramMotionEvent) {
        if (this.iconEvent != null)
            this.iconEvent.onActionUp(paramStickerView, paramMotionEvent);
    }

    public void setIconEvent(EventStickerIcon paramEventStickerIcon) {
        this.iconEvent = paramEventStickerIcon;
    }

    public void setX(float paramFloat) {
        this.x = paramFloat;
    }

    public void setY(float paramFloat) {
        this.y = paramFloat;
    }

}
