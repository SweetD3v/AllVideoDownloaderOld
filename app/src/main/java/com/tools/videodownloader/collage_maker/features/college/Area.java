package com.tools.videodownloader.collage_maker.features.college;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.List;

public interface Area {
    float bottom();

    float centerX();

    float centerY();

    boolean contains(float f, float f2);

    boolean contains(PointF pointF);

    boolean contains(Line line);

    Path getAreaPath();

    RectF getAreaRect();

    PointF getCenterPoint();

    PointF[] getHandleBarPoints(Line line);

    List<Line> getLines();

    float getPaddingBottom();

    float getPaddingLeft();

    float getPaddingRight();

    float getPaddingTop();

    float height();

    float left();

    float right();

    void setPadding(float f);

    void setPadding(float f, float f2, float f3, float f4);

    void setRadian(float f);

    float top();

    float width();
}
