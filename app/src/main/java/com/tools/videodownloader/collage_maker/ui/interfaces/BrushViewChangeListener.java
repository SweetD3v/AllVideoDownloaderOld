package com.tools.videodownloader.collage_maker.ui.interfaces;

import com.tools.videodownloader.tools.photoeditor.BrushDrawingView;

public interface BrushViewChangeListener {
    void onStartDrawing();

    void onStopDrawing();

    void onViewAdd(BrushDrawingView brushDrawingView);

    void onViewRemoved(BrushDrawingView brushDrawingView);
}
