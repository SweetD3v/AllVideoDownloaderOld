package com.example.allviddownloader.collage_maker.ui.interfaces;

import com.example.allviddownloader.tools.photoeditor.BrushDrawingView;

public interface BrushViewChangeListener {
    void onStartDrawing();

    void onStopDrawing();

    void onViewAdd(BrushDrawingView brushDrawingView);

    void onViewRemoved(BrushDrawingView brushDrawingView);
}
