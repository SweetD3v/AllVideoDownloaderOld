package com.tools.videodownloader.collage_maker.features.college.layout.slant;


import com.tools.videodownloader.collage_maker.features.college.slant.SlantPuzzleLayout;

public abstract class NumberSlantLayout extends SlantPuzzleLayout {
    protected int theme;

    public abstract int getThemeCount();

    public NumberSlantLayout() {
    }

    public NumberSlantLayout(SlantPuzzleLayout slantPuzzleLayout, boolean z) {
        super(slantPuzzleLayout, z);
    }

    public NumberSlantLayout(int i) {
        if (i >= getThemeCount()) {
            StringBuilder sb = new StringBuilder();
            sb.append("NumberSlantLayout: the most theme count is ");
            sb.append(getThemeCount());
            sb.append(" ,you should let theme from 0 to ");
            sb.append(getThemeCount() - 1);
            sb.append(" .");
        }
        this.theme = i;
    }

    public int getTheme() {
        return this.theme;
    }
}
