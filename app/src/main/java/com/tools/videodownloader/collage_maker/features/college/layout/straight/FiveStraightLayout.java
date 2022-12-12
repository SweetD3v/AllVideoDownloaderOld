package com.tools.videodownloader.collage_maker.features.college.layout.straight;

import com.tools.videodownloader.collage_maker.features.college.PuzzleLayout;
import com.tools.videodownloader.collage_maker.features.college.Line;
import com.tools.videodownloader.collage_maker.features.college.straight.StraightPuzzleLayout;

public class FiveStraightLayout extends NumberStraightLayout {
    public int getThemeCount() {
        return 17;
    }

    public FiveStraightLayout(StraightPuzzleLayout straightPuzzleLayout, boolean z) {
        super(straightPuzzleLayout, z);
    }

    public FiveStraightLayout(int i) {
        super(i);
    }

    public void layout() {
        switch (this.theme) {
            case 0:
                addLine(0, Line.Direction.VERTICAL, 0.25f);
                addLine(1, Line.Direction.VERTICAL, 0.6666667f);
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                addLine(2, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 1:
                addLine(0, Line.Direction.HORIZONTAL, 0.6f);
                cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
                addLine(3, Line.Direction.VERTICAL, 0.5f);
                return;
            case 2:
                addLine(0, Line.Direction.VERTICAL, 0.4f);
                cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
                addLine(1, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 3:
                addLine(0, Line.Direction.VERTICAL, 0.4f);
                cutAreaEqualPart(1, 3, Line.Direction.HORIZONTAL);
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 4:
                addLine(0, Line.Direction.HORIZONTAL, 0.75f);
                cutAreaEqualPart(1, 4, Line.Direction.VERTICAL);
                return;
            case 5:
                addLine(0, Line.Direction.HORIZONTAL, 0.25f);
                cutAreaEqualPart(0, 4, Line.Direction.VERTICAL);
                return;
            case 6:
                addLine(0, Line.Direction.VERTICAL, 0.75f);
                cutAreaEqualPart(1, 4, Line.Direction.HORIZONTAL);
                return;
            case 7:
                addLine(0, Line.Direction.VERTICAL, 0.25f);
                cutAreaEqualPart(0, 4, Line.Direction.HORIZONTAL);
                return;
            case 8:
                addLine(0, Line.Direction.HORIZONTAL, 0.25f);
                addLine(1, Line.Direction.HORIZONTAL, 0.6666667f);
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                addLine(3, Line.Direction.VERTICAL, 0.5f);
                return;
            case 9:
                addLine(0, Line.Direction.HORIZONTAL, 0.4f);
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                cutAreaEqualPart(2, 3, Line.Direction.VERTICAL);
                return;
            case 10:
                addCross(0, 0.33333334f);
                addLine(2, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 11:
                addCross(0, 0.6666667f);
                addLine(1, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 12:
                addCross(0, 0.33333334f, 0.6666667f);
                addLine(3, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 13:
                addCross(0, 0.6666667f, 0.33333334f);
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 14:
                cutSpiral(0);
                return;
            case 15:
                cutAreaEqualPart(0, 5, Line.Direction.HORIZONTAL);
                return;
            case 16:
                cutAreaEqualPart(0, 5, Line.Direction.VERTICAL);
                return;
            default:
                cutAreaEqualPart(0, 5, Line.Direction.HORIZONTAL);
        }
    }

    public PuzzleLayout clone(PuzzleLayout PuzzleLayout) {
        return new FiveStraightLayout((StraightPuzzleLayout) PuzzleLayout, true);
    }
}
