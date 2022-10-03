package com.example.allviddownloader.collage_maker.features.college;

import android.graphics.RectF;

import com.example.allviddownloader.collage_maker.features.college.slant.SlantCollageLayout;
import com.example.allviddownloader.collage_maker.features.college.straight.StraightCollageLayout;


public class CollegeLayoutParser {
    private CollegeLayoutParser() {
    }

    public static CollageLayout parse(final CollageLayout.Info info) {
        CollageLayout collageLayout;
        if (info.type == 0) {
            collageLayout = new StraightCollageLayout() {
                public CollageLayout clone(CollageLayout collageLayout) {
                    return null;
                }

                public void layout() {
                    int size = info.steps.size();
                    int i = 0;
                    for (int i2 = 0; i2 < size; i2++) {
                        CollageLayout.Step step = info.steps.get(i2);
                        switch (step.type) {
                            case 0:
                                addLine(step.position, step.lineDirection(), info.lines.get(i).getStartRatio());
                                break;
                            case 1:
                                addCross(step.position, step.hRatio, step.vRatio);
                                break;
                            case 2:
                                cutAreaEqualPart(step.position, step.hSize, step.vSize);
                                break;
                            case 3:
                                cutAreaEqualPart(step.position, step.part, step.lineDirection());
                                break;
                            case 4:
                                cutSpiral(step.position);
                                break;
                        }
                        i += step.numOfLine;
                    }
                }
            };
        } else {
            collageLayout = new SlantCollageLayout() {
                public CollageLayout clone(CollageLayout collageLayout) {
                    return null;
                }

                public void layout() {
                    int size = info.steps.size();
                    for (int i = 0; i < size; i++) {
                        CollageLayout.Step step = info.steps.get(i);
                        switch (step.type) {
                            case 0:
                                addLine(step.position, step.lineDirection(), info.lines.get(i).getStartRatio(), info.lines.get(i).getEndRatio());
                                break;
                            case 1:
                                addCross(step.position, 0.5f, 0.5f, 0.5f, 0.5f);
                                break;
                            case 2:
                                cutArea(step.position, step.hSize, step.vSize);
                                break;
                        }
                    }
                }
            };
        }
        collageLayout.setOuterBounds(new RectF(info.left, info.top, info.right, info.bottom));
        collageLayout.layout();
        collageLayout.setColor(info.color);
        collageLayout.setRadian(info.radian);
        collageLayout.setPadding(info.padding);
        int size = info.lineInfos.size();
        for (int i = 0; i < size; i++) {
            CollageLayout.LineInfo lineInfo = info.lineInfos.get(i);
            Line line = collageLayout.getLines().get(i);
            line.startPoint().x = lineInfo.startX;
            line.startPoint().y = lineInfo.startY;
            line.endPoint().x = lineInfo.endX;
            line.endPoint().y = lineInfo.endY;
        }
        collageLayout.sortAreas();
        collageLayout.update();
        return collageLayout;
    }
}
