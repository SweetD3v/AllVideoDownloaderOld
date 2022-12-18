package com.video.tools.videodownloader.tools.cartoonify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;


public class Blur {
    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 4.5f;

    public static Bitmap blur(View v) {
        return blur(v.getContext(), getScreenshot(v));
    }

    public static Bitmap blur(Context ctx, Bitmap image) {
        Bitmap photo = image.copy(Bitmap.Config.ARGB_8888, true);

        try {
            final RenderScript rs = RenderScript.create( ctx );
            final Allocation input = Allocation.createFromBitmap(rs, photo, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius( BLUR_RADIUS ); /* e.g. 3.f */
            script.setInput( input );
            script.forEach( output );
            output.copyTo( photo );
        }catch (Exception e){
            e.printStackTrace();
        }
        return photo;
    }

    private static Bitmap getScreenshot(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }
}