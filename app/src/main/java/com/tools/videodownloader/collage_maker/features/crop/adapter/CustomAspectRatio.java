package com.tools.videodownloader.collage_maker.features.crop.adapter;

import com.steelkiwi.cropiwa.AspectRatio;

class CustomAspectRatio extends AspectRatio {
    private int selectedIem;
    private int unselectItem;

    CustomAspectRatio(int from, int to, int drawable1, int drawable2) {
        super(from, to);
        this.selectedIem = drawable2;
        this.unselectItem = drawable1;
    }

    public int getSelectedIem() {
        return this.selectedIem;
    }

    public int getUnselectItem() {
        return this.unselectItem;
    }
}
