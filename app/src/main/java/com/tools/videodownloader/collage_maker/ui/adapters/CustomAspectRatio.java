package com.tools.videodownloader.collage_maker.ui.adapters;

import com.steelkiwi.cropiwa.AspectRatio;

class CustomAspectRatio extends AspectRatio {
    private int selectedIem;
    private int unselectItem;

    CustomAspectRatio(int from, int to, int unselectItem, int selectedIem) {
        super(from, to);
        this.selectedIem = selectedIem;
        this.unselectItem = unselectItem;
    }

    public int getSelectedIem() {
        return this.selectedIem;
    }

    public int getUnselectItem() {
        return this.unselectItem;
    }
}
