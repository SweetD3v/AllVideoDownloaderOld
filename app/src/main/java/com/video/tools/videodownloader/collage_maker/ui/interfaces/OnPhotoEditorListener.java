package com.video.tools.videodownloader.collage_maker.ui.interfaces;

import com.video.tools.videodownloader.tools.photoeditor.BrushViewType;

public interface OnPhotoEditorListener {
    void onAddViewListener(BrushViewType brushViewType, int i);


    void onRemoveViewListener(int i);

    void onRemoveViewListener(BrushViewType brushViewType, int i);

    void onStartViewChangeListener(BrushViewType brushViewType);

    void onStopViewChangeListener(BrushViewType brushViewType);
}
