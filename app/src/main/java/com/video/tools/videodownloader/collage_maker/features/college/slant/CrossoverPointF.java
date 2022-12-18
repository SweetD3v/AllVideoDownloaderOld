package com.video.tools.videodownloader.collage_maker.features.college.slant;

import android.graphics.PointF;

class CrossoverPointF extends PointF {
    SlantLine horizontal;
    SlantLine vertical;

    CrossoverPointF() {
    }

    CrossoverPointF(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    CrossoverPointF(SlantLine slantLine, SlantLine slantLine2) {
        this.horizontal = slantLine;
        this.vertical = slantLine2;
    }


}
