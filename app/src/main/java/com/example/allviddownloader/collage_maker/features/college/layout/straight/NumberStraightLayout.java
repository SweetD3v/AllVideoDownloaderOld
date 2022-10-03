package com.example.allviddownloader.collage_maker.features.college.layout.straight;

import com.example.allviddownloader.collage_maker.features.college.straight.StraightCollageLayout;

public abstract class NumberStraightLayout extends StraightCollageLayout {
    protected int theme;

    public abstract int getThemeCount();

    public NumberStraightLayout() {
    }

    public NumberStraightLayout(StraightCollageLayout straightPuzzleLayout, boolean z) {
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
