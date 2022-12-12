package com.tools.videodownloader.collage_maker.utils;

import android.graphics.Bitmap;
import android.util.Log;

import org.wysaid.common.SharedContext;
import org.wysaid.nativePort.CGEImageHandler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class UtilsFilter {
    public static final FilterBean[] EFFECT_CONFIGS = {new FilterBean("", "Original"), new FilterBean("@adjust lut filters/bright01.webp", "Fresh 01"), new FilterBean("@adjust lut filters/bright02.webp", "Fresh 02"), new FilterBean("@adjust lut filters/bright03.webp", "Fresh 03"), new FilterBean("@adjust lut filters/bright05.webp", "Fresh 04"), new FilterBean("@adjust lut filters/euro01.webp", "Euro 01"), new FilterBean("@adjust lut filters/euro02.webp", "Euro 02"), new FilterBean("@adjust lut filters/euro05.webp", "Euro 03"), new FilterBean("@adjust lut filters/euro04.webp", "Euro 04"), new FilterBean("@adjust lut filters/euro06.webp", "Euro 05"), new FilterBean("@adjust lut filters/euro07.webp", "Euro 06"), new FilterBean("@adjust lut filters/film01.webp", "Film 01"), new FilterBean("@adjust lut filters/film02.webp", "Film 02"), new FilterBean("@adjust lut filters/film03.webp", "Film 03"), new FilterBean("@adjust lut filters/film04.webp", "Film 04"), new FilterBean("@adjust lut filters/film05.webp", "Film 05"), new FilterBean("@adjust lut filters/lomo1.webp", "Lomo 01"), new FilterBean("@adjust lut filters/lomo2.webp", "Lomo 02"), new FilterBean("@adjust lut filters/lomo3.webp", "Lomo 03"), new FilterBean("@adjust lut filters/lomo4.webp", "Lomo 04"), new FilterBean("@adjust lut filters/lomo5.webp", "Lomo 05"), new FilterBean("@adjust lut filters/movie01.webp", "Movie 01"), new FilterBean("@adjust lut filters/movie02.webp", "Movie 02"), new FilterBean("@adjust lut filters/movie03.webp", "Movie 03"), new FilterBean("@adjust lut filters/movie04.webp", "Movie 04"), new FilterBean("@adjust lut filters/movie05.webp", "Movie 05")};
    public static final FilterBean[] OVERLAY_CONFIG = {new FilterBean("", ""), new FilterBean("#unpack @krblend sr overlay/01.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/2.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/02.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/3.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/4.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/5.webp 100", ""), new FilterBean("#unpack @krblend sr overlay/1.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/19.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/46.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/47.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/effect_00005.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/26.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/35.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/42.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/43.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/44.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/45.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/effect_00018.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/effect_00025.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/effect_00026.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/effect_00031.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/effect_00037.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/53.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/54.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/55.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/56.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/57.jpg 100", ""), new FilterBean("#unpack @krblend sr overlay/11.webp 100", ""), new FilterBean("#unpack @krblend sr overlay/12.webp 100", ""), new FilterBean("#unpack @krblend sr overlay/13.webp 100", "")};

    public static class FilterBean {
        private String config;
        private String name;

        FilterBean(String str, String str2) {
            this.config = str;
            this.name = str2;
        }

        public String getConfig() {
            return this.config;
        }

        public String getName() {
            return this.name;
        }
    }

    public static Bitmap getBlurImageFromBitmap(Bitmap bitmap) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig("@blur lerp 0.6");
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }

    public static Bitmap getBlurImageFromBitmap(Bitmap bitmap, float f) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig(MessageFormat.format("@blur lerp {0}", new Object[]{(f / 10.0f) + ""}));
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }

    public static Bitmap cloneBitmap(Bitmap bitmap) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig("");
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }

    public static Bitmap getBlackAndWhiteImageFromBitmap(Bitmap bitmap) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig("@adjust saturation 0");
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }

    public static List<Bitmap> getLstBitmapWithFilter(Bitmap bitmap) {
        ArrayList<Bitmap> arrayList = new ArrayList<>();
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        Log.e("TAG", "getLstBitmapWithFilter: " + EFFECT_CONFIGS.length);
        for (FilterBean config : EFFECT_CONFIGS) {
            cGEImageHandler.setFilterWithConfig(config.getConfig());
            cGEImageHandler.processFilters();
            arrayList.add(cGEImageHandler.getResultBitmap());
            Log.e("TAG", "getLstBitmapWithFilter: " + arrayList.size());
        }
        Log.e("TAG", "getLstBitmapWithFilter: " + arrayList.size());
        create.release();
        return arrayList;
    }

    public static Bitmap getBitmapWithFilter(Bitmap bitmap, String str) {
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        cGEImageHandler.setFilterWithConfig(str);
        cGEImageHandler.setFilterIntensity(0.8f);
        cGEImageHandler.processFilters();
        Bitmap resultBitmap = cGEImageHandler.getResultBitmap();
        create.release();
        return resultBitmap;
    }

    public static List<Bitmap> getLstBitmapWithOverlay(Bitmap bitmap) {
        ArrayList<Bitmap> arrayList = new ArrayList<Bitmap>();
        SharedContext create = SharedContext.create();
        create.makeCurrent();
        CGEImageHandler cGEImageHandler = new CGEImageHandler();
        cGEImageHandler.initWithBitmap(bitmap);
        for (FilterBean config : OVERLAY_CONFIG) {
            cGEImageHandler.setFilterWithConfig(config.getConfig());
            cGEImageHandler.processFilters();
            arrayList.add(cGEImageHandler.getResultBitmap());
        }
        create.release();
        return arrayList;
    }
}
