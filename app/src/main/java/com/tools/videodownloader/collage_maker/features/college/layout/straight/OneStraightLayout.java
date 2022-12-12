package com.tools.videodownloader.collage_maker.features.college.layout.straight;

import com.tools.videodownloader.collage_maker.features.college.PuzzleLayout;
import com.tools.videodownloader.collage_maker.features.college.Line;
import com.tools.videodownloader.collage_maker.features.college.straight.StraightPuzzleLayout;

public class OneStraightLayout extends NumberStraightLayout {
    public int getThemeCount() {
        return 6;
    }

    public OneStraightLayout(StraightPuzzleLayout straightPuzzleLayout, boolean z) {
        super(straightPuzzleLayout, z);
    }

    public OneStraightLayout(int i) {
        super(i);
    }

    public void layout() {
        switch (this.theme) {
            case 0:
                addLine(0,
                        Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 1:
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                return;
            case 2:
                addCross(0, 0.5f);
                return;
            case 3:
                cutAreaEqualPart(0, 2, 1);
                return;
            case 4:
                cutAreaEqualPart(0, 1, 2);
                return;
            case 5:
                cutAreaEqualPart(0, 2, 2);
                return;
            default:
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
        }
    }

    public PuzzleLayout clone(PuzzleLayout PuzzleLayout) {
        return new OneStraightLayout((StraightPuzzleLayout) PuzzleLayout, true);
    }
}
