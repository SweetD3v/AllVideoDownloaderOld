package com.example.allviddownloader.collage_maker.features.college.layout.slant;


import com.example.allviddownloader.collage_maker.features.college.CollageLayout;
import com.example.allviddownloader.collage_maker.features.college.Line;
import com.example.allviddownloader.collage_maker.features.college.slant.SlantCollageLayout;

public class FourSlantLayout extends NumberSlantLayout {
    public int getThemeCount() {
        return 6;
    }

    public FourSlantLayout(int i) {
        super(i);
    }

    public FourSlantLayout(CollageLayout collegeLayout, boolean z) {
        super((SlantCollageLayout) collegeLayout, z);
    }

    public void layout() {
        switch (this.theme) {
            case 0:
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                addLine(0, Line.Direction.VERTICAL, 0.3f, 0.3f);
                addLine(2, Line.Direction.VERTICAL, 0.7f, 0.7f);
                return;
            case 1:
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                addLine(0, Line.Direction.HORIZONTAL, 0.3f, 0.3f);
                addLine(1, Line.Direction.HORIZONTAL, 0.7f, 0.7f);
                return;
            case 2:
                addLine(0, Line.Direction.HORIZONTAL, 0.6666667f);
                addLine(0, Line.Direction.VERTICAL, 0.6666667f, 0.6666667f);
                addLine(2, Line.Direction.VERTICAL, 0.6666667f, 0.6666667f);
                return;
            case 3:
                addLine(0, Line.Direction.HORIZONTAL, 0.33333334f);
                addLine(0, Line.Direction.VERTICAL, 0.33333334f, 0.33333334f);
                addLine(2, Line.Direction.VERTICAL, 0.33333334f, 0.33333334f);
                return;
            case 4:
                addLine(0, Line.Direction.HORIZONTAL, 0.33333334f);
                addLine(1, Line.Direction.HORIZONTAL, 0.5f);
                addLine(1, Line.Direction.VERTICAL, 0.5f, 0.5f);
                return;
            case 5:
                addLine(0, Line.Direction.VERTICAL, 0.33333334f);
                addLine(1, Line.Direction.VERTICAL, 0.5f);
                addLine(1, Line.Direction.HORIZONTAL, 0.5f, 0.5f);
                return;
            case 6:
                addLine(0, Line.Direction.HORIZONTAL, 0.7f, 0.3f);
                addLine(0, Line.Direction.VERTICAL, 0.3f, 0.5f);
                addLine(2, Line.Direction.VERTICAL, 0.5f, 0.7f);
                return;
            default:
        }
    }

    public CollageLayout clone(CollageLayout collegeLayout) {
        return new FourSlantLayout(collegeLayout, true);
    }
}