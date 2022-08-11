package com.example.allviddownloader.collage_maker.ui.interfaces;

import com.example.allviddownloader.collage_maker.features.BrushDrawView;

public interface BrushViewChangeListener {
    void onStartDrawing();

    void onStopDrawing();

    void onViewAdd(BrushDrawView brushDrawView);

    void onViewRemoved();
}
