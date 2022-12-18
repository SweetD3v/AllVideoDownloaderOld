package com.video.tools.videodownloader.collage_maker.features;

import android.view.MotionEvent;

public interface EventStickerIcon {
    void onActionDown(StickerView paramStickerView, MotionEvent paramMotionEvent);

    void onActionMove(StickerView paramStickerView, MotionEvent paramMotionEvent);

    void onActionUp(StickerView paramStickerView, MotionEvent paramMotionEvent);
}
