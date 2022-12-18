package com.video.tools.videodownloader.collage_maker.features.college.layout.straight;

import com.video.tools.videodownloader.collage_maker.features.college.straight.StraightPuzzleLayout;

public abstract class NumberStraightLayout extends StraightPuzzleLayout {
    protected int theme;

    public abstract int getThemeCount();

    public NumberStraightLayout() {
    }

    public NumberStraightLayout(StraightPuzzleLayout straightPuzzleLayout, boolean z) {
        super(straightPuzzleLayout, z);
    }

    public NumberStraightLayout(int i) {
        if (i >= getThemeCount()) {
            StringBuilder sb = new StringBuilder();
            sb.append("NumberStraightLayout: the most theme count is ");
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
