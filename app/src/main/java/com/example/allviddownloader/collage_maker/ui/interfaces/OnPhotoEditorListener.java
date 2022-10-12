package com.example.allviddownloader.collage_maker.ui.interfaces;

import com.example.allviddownloader.tools.photoeditor.BrushViewType;

public interface OnPhotoEditorListener {
    void onAddViewListener(BrushViewType brushViewType, int i);


    void onRemoveViewListener(int i);

    void onRemoveViewListener(BrushViewType brushViewType, int i);

    void onStartViewChangeListener(BrushViewType brushViewType);

    void onStopViewChangeListener(BrushViewType brushViewType);
}
