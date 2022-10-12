package com.example.allviddownloader.collage_maker.features.college.layout.straight;

import com.example.allviddownloader.collage_maker.features.college.PuzzleLayout;
import com.example.allviddownloader.collage_maker.features.college.Line;
import com.example.allviddownloader.collage_maker.features.college.straight.StraightPuzzleLayout;

public class TwoStraightLayout extends NumberStraightLayout {
    private float mRadio = 0.5f;

    public int getThemeCount() {
        return 6;
    }

    public TwoStraightLayout(StraightPuzzleLayout straightPuzzleLayout, boolean z) {
        super(straightPuzzleLayout, z);
    }

    public TwoStraightLayout(int i) {
        super(i);
    }


    public void layout() {
        switch (this.theme) {
            case 0:
                addLine(0, Line.Direction.HORIZONTAL, this.mRadio);
                return;
            case 1:
                addLine(0, Line.Direction.VERTICAL, this.mRadio);
                return;
            case 2:
                addLine(0, Line.Direction.HORIZONTAL, 0.33333334f);
                return;
            case 3:
                addLine(0, Line.Direction.HORIZONTAL, 0.6666667f);
                return;
            case 4:
                addLine(0, Line.Direction.VERTICAL, 0.33333334f);
                return;
            case 5:
                addLine(0, Line.Direction.VERTICAL, 0.6666667f);
                return;
            default:
                addLine(0, Line.Direction.HORIZONTAL, this.mRadio);
        }
    }

    public PuzzleLayout clone(PuzzleLayout PuzzleLayout) {
        return new TwoStraightLayout((StraightPuzzleLayout) PuzzleLayout, true);
    }
}
