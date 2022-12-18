package com.video.tools.videodownloader.collage_maker.features.college.slant;

import android.graphics.PointF;

import com.video.tools.videodownloader.collage_maker.features.college.Line;


class SlantLine implements Line {
    SlantLine attachLineEnd;
    SlantLine attachLineStart;
    public final Line.Direction direction;
    CrossoverPointF end;
    private float endRatio;
    Line lowerLine;
    private PointF previousEnd = new PointF();
    private PointF previousStart = new PointF();
    CrossoverPointF start;
    private float startRatio;
    Line upperLine;

    SlantLine(Line.Direction direction2) {
        this.direction = direction2;
    }

    SlantLine(CrossoverPointF crossoverPointF, CrossoverPointF crossoverPointF2, Line.Direction direction2) {
        this.start = crossoverPointF;
        this.end = crossoverPointF2;
        this.direction = direction2;
    }

    public void setStartRatio(float f) {
        this.startRatio = f;
    }

    public float getStartRatio() {
        return this.startRatio;
    }

    public void setEndRatio(float f) {
        this.endRatio = f;
    }

    public float getEndRatio() {
        return this.endRatio;
    }

    public float length() {
        return (float) Math.sqrt(Math.pow(this.end.x - this.start.x, 2.0d) + Math.pow(this.end.y - this.start.y, 2.0d));
    }

    public PointF startPoint() {
        return this.start;
    }

    public PointF endPoint() {
        return this.end;
    }

    public Line lowerLine() {
        return this.lowerLine;
    }

    public Line upperLine() {
        return this.upperLine;
    }

    public Line attachStartLine() {
        return this.attachLineStart;
    }

    public Line attachEndLine() {
        return this.attachLineEnd;
    }

    public void setLowerLine(Line line) {
        this.lowerLine = line;
    }

    public void setUpperLine(Line line) {
        this.upperLine = line;
    }

    public Line.Direction direction() {
        return this.direction;
    }

    public boolean contains(float f, float f2, float f3) {
        return SlantUtils.contains(this, f, f2, f3);
    }

    public boolean move(float f, float f2) {
        if (this.direction == Line.Direction.HORIZONTAL) {
            if (this.previousStart.y + f < this.lowerLine.maxY() + f2 || this.previousStart.y + f > this.upperLine.minY() - f2 || this.previousEnd.y + f < this.lowerLine.maxY() + f2 || this.previousEnd.y + f > this.upperLine.minY() - f2) {
                return false;
            }
            this.start.y = this.previousStart.y + f;
            this.end.y = this.previousEnd.y + f;
            return true;
        } else if (this.previousStart.x + f < this.lowerLine.maxX() + f2 || this.previousStart.x + f > this.upperLine.minX() - f2 || this.previousEnd.x + f < this.lowerLine.maxX() + f2 || this.previousEnd.x + f > this.upperLine.minX() - f2) {
            return false;
        } else {
            this.start.x = this.previousStart.x + f;
            this.end.x = this.previousEnd.x + f;
            return true;
        }
    }

    public void prepareMove() {
        this.previousStart.set(this.start);
        this.previousEnd.set(this.end);
    }

    public void update(float f, float f2) {
        SlantUtils.intersectionOfLines(this.start, this, this.attachLineStart);
        SlantUtils.intersectionOfLines(this.end, this, this.attachLineEnd);
    }

    public float minX() {
        return Math.min(this.start.x, this.end.x);
    }

    public float maxX() {
        return Math.max(this.start.x, this.end.x);
    }

    public float minY() {
        return Math.min(this.start.y, this.end.y);
    }

    public float maxY() {
        return Math.max(this.start.y, this.end.y);
    }

    public String toString() {
        return "start --> " + this.start.toString() + ",end --> " + this.end.toString();
    }
}
