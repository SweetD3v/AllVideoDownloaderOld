package com.tools.videodownloader.collage_maker.features;

import android.view.MotionEvent;

public class EventZoomIconStickerIcon implements EventStickerIcon {
    public void onActionDown(StickerView paramStickerView, MotionEvent paramMotionEvent) {
    }

    public void onActionMove(StickerView paramStickerView, MotionEvent paramMotionEvent) {
        paramStickerView.zoomAndRotateCurrentSticker(paramMotionEvent);
    }

    public void onActionUp(StickerView paramStickerView, MotionEvent paramMotionEvent) {
        if (paramStickerView.getOnStickerOperationListener() != null)
            paramStickerView.getOnStickerOperationListener().onStickerZoomFinished();
    }
}
