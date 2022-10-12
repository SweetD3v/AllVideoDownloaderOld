package com.example.allviddownloader.collage_maker.features.college;

import com.example.allviddownloader.collage_maker.features.college.layout.slant.SlantLayoutHelper;
import com.example.allviddownloader.collage_maker.features.college.layout.straight.StraightLayoutHelper;
import java.util.ArrayList;
import java.util.List;

public class CollegeUtils {

    private CollegeUtils() {
    }

    public static List<PuzzleLayout> getPuzzleLayouts(int i) {
        ArrayList<PuzzleLayout> arrayList = new ArrayList<>();
        arrayList.addAll(SlantLayoutHelper.getAllThemeLayout(i));
        arrayList.addAll(StraightLayoutHelper.getAllThemeLayout(i));
        return arrayList;
    }
}