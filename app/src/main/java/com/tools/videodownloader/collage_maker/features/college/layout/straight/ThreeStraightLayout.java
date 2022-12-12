package com.tools.videodownloader.collage_maker.features.college.layout.straight;

import com.tools.videodownloader.collage_maker.features.college.PuzzleLayout;
import com.tools.videodownloader.collage_maker.features.college.Line;
import com.tools.videodownloader.collage_maker.features.college.straight.StraightPuzzleLayout;

public class ThreeStraightLayout extends NumberStraightLayout {
    public int getThemeCount() {
        return 6;
    }

    public ThreeStraightLayout(StraightPuzzleLayout straightPuzzleLayout, boolean z) {
        super(straightPuzzleLayout, z);
    }

    public ThreeStraightLayout(int i) {
        super(i);
    }

    public void layout() {
        switch (this.theme) {
            case 0:
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                return;
            case 1:
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                addLine(1, Line.Direction.VERTICAL, 0.5f);
                return;
            case 2:
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 3:
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                addLine(1, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 4:
                cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
                return;
            case 5:
                cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
                return;
            default:
                cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
        }
    }

    public PuzzleLayout clone(PuzzleLayout collegeLayout) {
        return new ThreeStraightLayout((StraightPuzzleLayout) collegeLayout, true);
    }
}
