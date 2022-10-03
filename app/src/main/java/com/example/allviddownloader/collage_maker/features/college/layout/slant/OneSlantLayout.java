package com.example.allviddownloader.collage_maker.features.college.layout.slant;


import com.example.allviddownloader.collage_maker.features.college.CollageLayout;
import com.example.allviddownloader.collage_maker.features.college.Line;
import com.example.allviddownloader.collage_maker.features.college.slant.SlantCollageLayout;

public class OneSlantLayout extends NumberSlantLayout {
    public int getThemeCount() {
        return 4;
    }

    public OneSlantLayout(SlantCollageLayout slantPuzzleLayout, boolean z) {
        super(slantPuzzleLayout, z);
    }

    public OneSlantLayout(int i) {
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
            case 2:
                addCross(0, 0.56f, 0.44f, 0.56f, 0.44f);
                return;
            case 3:
                cutArea(0, 1, 2);
                return;
            default:
        }
    }

    public CollageLayout clone(CollageLayout collegeLayout) {
        return new OneSlantLayout((SlantCollageLayout) collegeLayout, true);
    }
}