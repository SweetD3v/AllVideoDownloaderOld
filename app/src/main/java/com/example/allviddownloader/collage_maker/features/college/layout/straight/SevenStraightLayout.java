package com.example.allviddownloader.collage_maker.features.college.layout.straight;


import com.example.allviddownloader.collage_maker.features.college.CollageLayout;
import com.example.allviddownloader.collage_maker.features.college.Line;
import com.example.allviddownloader.collage_maker.features.college.straight.StraightCollageLayout;

public class SevenStraightLayout extends NumberStraightLayout {
    public int getThemeCount() {
        return 9;
    }

    public SevenStraightLayout(StraightCollageLayout straightPuzzleLayout, boolean z) {
        super(straightPuzzleLayout, z);
    }

    public SevenStraightLayout(int i) {
        super(i);
    }

    public void layout() {
        switch (this.theme) {
            case 0:
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                cutAreaEqualPart(1, 4, Line.Direction.VERTICAL);
                cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
                return;
            case 1:
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                cutAreaEqualPart(1, 4, Line.Direction.HORIZONTAL);
                cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
                return;
            case 2:
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                cutAreaEqualPart(1, 1, 2);
                return;
            case 3:
                addLine(0, Line.Direction.HORIZONTAL, 0.6666667f);
                cutAreaEqualPart(1, 3, Line.Direction.VERTICAL);
                addCross(0, 0.5f);
                return;
            case 4:
                cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
                cutAreaEqualPart(2, 3, Line.Direction.HORIZONTAL);
                cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
                return;
            case 5:
                addLine(0, Line.Direction.HORIZONTAL, 0.6666667f);
                addLine(1, Line.Direction.VERTICAL, 0.75f);
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                addLine(1, Line.Direction.VERTICAL, 0.4f);
                cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
                return;
            case 6:
                addLine(0, Line.Direction.VERTICAL, 0.6666667f);
                addLine(1, Line.Direction.HORIZONTAL, 0.75f);
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                addLine(1, Line.Direction.HORIZONTAL, 0.4f);
                cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
                return;
            case 7:
                addLine(0, Line.Direction.VERTICAL, 0.25f);
                addLine(1, Line.Direction.VERTICAL, 0.6666667f);
                addLine(2, Line.Direction.HORIZONTAL, 0.5f);
                addLine(1, Line.Direction.HORIZONTAL, 0.75f);
                addLine(1, Line.Direction.HORIZONTAL, 0.33333334f);
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                return;
            case 8:
                addLine(0, Line.Direction.HORIZONTAL, 0.25f);
                addLine(1, Line.Direction.HORIZONTAL, 0.6666667f);
                cutAreaEqualPart(2, 3, Line.Direction.VERTICAL);
                cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
                return;
            default:
        }
    }

    public CollageLayout clone(CollageLayout CollageLayout) {
        return new SevenStraightLayout((StraightCollageLayout) CollageLayout, true);
    }
}
