package com.tools.videodownloader.collage_maker.features.college.layout.slant;


import com.tools.videodownloader.collage_maker.features.college.PuzzleLayout;
import com.tools.videodownloader.collage_maker.features.college.Line;
import com.tools.videodownloader.collage_maker.features.college.slant.SlantPuzzleLayout;

public class SevenSlantLayout extends NumberSlantLayout {
    public int getThemeCount() {
        return 2;
    }

    public SevenSlantLayout(SlantPuzzleLayout slantPuzzleLayout, boolean z) {
        super(slantPuzzleLayout, z);
    }

    public SevenSlantLayout(int i) {
        super(i);
    }

    public void layout() {
        if (this.theme == 0) {
            addLine(0, Line.Direction.VERTICAL, 0.33333334f);
            addLine(1, Line.Direction.VERTICAL, 0.5f);
            addLine(0, Line.Direction.HORIZONTAL, 0.5f, 0.5f);
            addLine(1, Line.Direction.HORIZONTAL, 0.33f, 0.33f);
            addLine(3, Line.Direction.HORIZONTAL, 0.5f, 0.5f);
            addLine(2, Line.Direction.HORIZONTAL, 0.5f, 0.5f);
        }
    }

    public PuzzleLayout clone(PuzzleLayout collegeLayout) {
        return new SevenSlantLayout((SlantPuzzleLayout) collegeLayout, true);
    }
}
