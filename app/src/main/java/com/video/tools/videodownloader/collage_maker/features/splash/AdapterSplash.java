package com.video.tools.videodownloader.collage_maker.features.splash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.video.tools.videodownloader.R;
import com.video.tools.videodownloader.collage_maker.utils.AppConstants;
import com.video.tools.videodownloader.collage_maker.utils.SystemUtils;
import com.video.tools.videodownloader.collage_maker.utils.UtilsAsset;
import com.github.siyamed.shapeimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class AdapterSplash extends RecyclerView.Adapter<AdapterSplash.ViewHolder> {
    private int borderWidth;
    private Context context;

    public int selectedSquareIndex;

    public SplashChangeListener splashChangeListener;

    public List<SplashItem> splashList = new ArrayList();

    interface SplashChangeListener {
        void onSelected(SplashSticker splashSticker);
    }

    AdapterSplash(Context context2, SplashChangeListener splashChangeListener2, boolean z) {
        this.context = context2;
        this.splashChangeListener = splashChangeListener2;
        this.borderWidth = SystemUtils.dpToPx(context2, AppConstants.BORDER_WIDTH_DP);
        if (z) {
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask2.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame2.webp")), R.drawable.splash02));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask3.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame3.webp")), R.drawable.splash03));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask4.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame4.webp")), R.drawable.splash04));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask5.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame5.webp")), R.drawable.splash05));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask6.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame6.webp")), R.drawable.splash06));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask7.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame7.webp")), R.drawable.splash07));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask8.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame8.webp")), R.drawable.splash08));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask9.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame9.webp")), R.drawable.splash09));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask11.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame11.webp")), R.drawable.splash11));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask12.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame12.webp")), R.drawable.splash12));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask14.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame14.webp")), R.drawable.splash14));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask17.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame17.webp")), R.drawable.splash17));
            this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/mask18.webp"), UtilsAsset.loadBitmapFromAssets(context2, "splash/icons/frame18.webp")), R.drawable.splash18));
            return;
        }
        this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_1_mask.webp"), UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_1_shadow.webp")), R.drawable.blur_1));
        this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_2_mask.webp"), UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_2_shadow.webp")), R.drawable.blur_2));
        this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_3_mask.webp"), UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_3_shadow.webp")), R.drawable.blur_3));
        this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_4_mask.webp"), UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_4_shadow.webp")), R.drawable.blur_4));
        this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_5_mask.webp"), UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_5_shadow.webp")), R.drawable.blur_5));
        this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_7_mask.webp"), UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_7_shadow.webp")), R.drawable.blur_7));
        this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_8_mask.webp"), UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_8_shadow.webp")), R.drawable.blur_8));
        this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_9_mask.webp"), UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_9_shadow.webp")), R.drawable.blur_9));
        this.splashList.add(new SplashItem(new SplashSticker(UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_10_mask.webp"), UtilsAsset.loadBitmapFromAssets(context2, "blur/icons/blur_10_shadow.webp")), R.drawable.blur_10));
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.splash_view, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.splash.setImageResource(this.splashList.get(i).drawableId);
        if (this.selectedSquareIndex == i) {
            viewHolder.splash.setBorderColor(ContextCompat.getColor(context,R.color.colorAccent));
            viewHolder.splash.setBorderWidth(this.borderWidth);
            return;
        }
        viewHolder.splash.setBorderColor(0);
        viewHolder.splash.setBorderWidth(this.borderWidth);
    }

    public int getItemCount() {
        return this.splashList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RoundedImageView splash;

        public ViewHolder(View view) {
            super(view);
            this.splash = view.findViewById(R.id.splash);
            view.setOnClickListener(this);
        }

        public void onClick(View view) {
            AdapterSplash.this.selectedSquareIndex = getAdapterPosition();
            if (AdapterSplash.this.selectedSquareIndex < 0) {
                AdapterSplash.this.selectedSquareIndex = 0;
            }
            if (AdapterSplash.this.selectedSquareIndex >= AdapterSplash.this.splashList.size()) {
                AdapterSplash.this.selectedSquareIndex = AdapterSplash.this.splashList.size() - 1;
            }
            AdapterSplash.this.splashChangeListener.onSelected((AdapterSplash.this.splashList.get(AdapterSplash.this.selectedSquareIndex)).splashSticker);
            AdapterSplash.this.notifyDataSetChanged();
        }
    }

    class SplashItem {
        int drawableId;
        SplashSticker splashSticker;

        SplashItem(SplashSticker splashSticker2, int i) {
            this.splashSticker = splashSticker2;
            this.drawableId = i;
        }
    }
}
