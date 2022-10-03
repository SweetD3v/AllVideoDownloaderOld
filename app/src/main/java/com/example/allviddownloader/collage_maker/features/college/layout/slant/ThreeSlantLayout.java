package com.example.allviddownloader.collage_maker.features.college.layout.slant;


import com.example.allviddownloader.collage_maker.features.college.CollageLayout;
import com.example.allviddownloader.collage_maker.features.college.Line;
import com.example.allviddownloader.collage_maker.features.college.slant.SlantCollageLayout;

public class ThreeSlantLayout extends NumberSlantLayout {
    public int getThemeCount() {
        return 6;
    }

    public ThreeSlantLayout(SlantCollageLayout slantPuzzleLayout, boolean z) {
        super(slantPuzzleLayout, z);
        this.theme = ((NumberSlantLayout) slantPuzzleLayout).getTheme();
    }

    public ThreeSlantLayout(int i) {
        super(i);
    }

    public void layout() {
        switch (this.theme) {
            case 0:
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                addLine(0, Line.Direction.VERTICAL, 0.56f, 0.44f);
                return;
            case 1:
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                addLine(1, Line.Direction.VERTICAL, 0.56f, 0.44f);
                return;
            case 2:
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                addLine(0, Line.Direction.HORIZONTAL, 0.56f, 0.44f);
                return;
            case 3:
                addLine(0, Line.Direction.VERTICAL, 0.5f);
                addLine(1, Line.Direction.HORIZONTAL, 0.56f, 0.44f);
                return;
            case 4:
                addLine(0, Line.Direction.HORIZONTAL, 0.44f, 0.56f);
                addLine(0, Line.Direction.VERTICAL, 0.56f, 0.44f);
                return;
            case 5:
                addLine(0, Line.Direction.VERTICAL, 0.56f, 0.44f);
                addLine(1, Line.Direction.HORIZONTAL, 0.44f, 0.56f);
                return;
            default:
        }
    }

    public CollageLayout clone(CollageLayout CollageLayout) {
        return new ThreeSlantLayout((SlantCollageLayout) CollageLayout, true);
    }
}
