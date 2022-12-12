package com.tools.videodownloader.widgets;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.tools.videodownloader.R;
import com.tools.videodownloader.utils.ExtensionsKt;

/**
 * This is a custom view to draw dotted, vertical lines.
 * <p>
 * A regular shape drawable (like we use for horizontal lines) is usually not sufficient because
 * rotating such a (by default horizontal) line to be vertical does not recalculate the correct
 * with and height if they are set to match_parent or wrap_content.
 * <p>
 * Furthermore, this view draws actual round dots, not those fake tiny square ones like shape
 * drawables do.
 * <p>
 * A more elaborate version of this view would use custom attributes to set the color of the line
 * more dynamically, as well as the line shape, gap size, etc.
 */
public class DottedLine extends View {

    private Paint mPaint;

    public DottedLine(Context context) {
        super(context);
        init();
    }

    public DottedLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DottedLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public DottedLine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Resources res = getResources();
        mPaint = new Paint();

        mPaint.setColor(res.getColor(R.color.dash_line));
        int size = res.getDimensionPixelSize(getHeight());
        int gap = ExtensionsKt.dpToPx(getContext(), 1);
        mPaint.setStyle(Paint.Style.FILL);

        // To get actually round dots, we define a circle...
        Path path = new Path();
        path.addCircle(0, 0, size, Path.Direction.CW);
        // ...and use the path with the circle as our path effect
        mPaint.setPathEffect(new PathDashPathEffect(path, gap, 0, PathDashPathEffect.Style.ROTATE));

        // If we don't render in software mode, the dotted line becomes a solid line.
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), mPaint);
    }
}