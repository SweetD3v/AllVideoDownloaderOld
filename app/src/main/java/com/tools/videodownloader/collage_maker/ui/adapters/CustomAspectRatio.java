package com.tools.videodownloader.collage_maker.ui.adapters;

import com.steelkiwi.cropiwa.AspectRatio;

class CustomAspectRatio extends AspectRatio {
    private int selectedIem;
    private int unselectItem;
    private String txtRatio;

    CustomAspectRatio(int from, int to, int unselectItem, int selectedIem, String txtRatio) {
        super(from, to);
        this.selectedIem = selectedIem;
        this.unselectItem = unselectItem;
        this.txtRatio = txtRatio;
    }

    public int getSelectedIem() {
        return this.selectedIem;
    }

    public int getUnselectItem() {
        return this.unselectItem;
    }

    public void setTxtRatio(String txtRatio) {
        this.txtRatio = txtRatio;
    }

    public String getTxtRatio() {
        return txtRatio;
    }
}
