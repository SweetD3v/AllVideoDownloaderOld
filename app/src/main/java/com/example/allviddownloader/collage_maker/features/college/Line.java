package com.example.allviddownloader.collage_maker.features.college;

import android.graphics.PointF;

public interface Line {

    enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    Line attachEndLine();

    Line attachStartLine();

    boolean contains(float f, float f2, float f3);

    Direction direction();

    PointF endPoint();

    float getEndRatio();

    float getStartRatio();

    float length();

    Line lowerLine();

    float maxX();

    float maxY();

    float minX();

    float minY();

    boolean move(float f, float f2);

    void prepareMove();

    void setEndRatio(float f);

    void setLowerLine(Line line);

    void setStartRatio(float f);

    void setUpperLine(Line line);

    PointF startPoint();

    void update(float f, float f2);

    Line upperLine();
}
