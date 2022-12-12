package com.tools.videodownloader.collage_maker.features.college.layout.slant;

import com.tools.videodownloader.collage_maker.features.college.PuzzleLayout;
import com.tools.videodownloader.collage_maker.features.college.Line;
import com.tools.videodownloader.collage_maker.features.college.slant.SlantPuzzleLayout;

public class TwoSlantLayout extends NumberSlantLayout {
    public int getThemeCount() {
        return 2;
    }


    public TwoSlantLayout(SlantPuzzleLayout slantPuzzleLayout, boolean z) {
        super(slantPuzzleLayout, z);
    }

    public TwoSlantLayout(int i) {
        super(i);
    }

    public void layout() {
        switch (this.theme) {
            case 0:
                addLine(0, Line.Direction.HORIZONTAL, 0.56f, 0.44f);
                return;
            case 1:
                addLine(0, Line.Direction.VERTICAL, 0.56f, 0.44f);
                return;
            default:
        }
    }

    public PuzzleLayout clone(PuzzleLayout PuzzleLayout) {
        return new TwoSlantLayout((SlantPuzzleLayout) PuzzleLayout, true);
    }
}
