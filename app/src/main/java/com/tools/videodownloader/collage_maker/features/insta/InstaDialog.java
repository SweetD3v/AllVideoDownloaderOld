package com.tools.videodownloader.collage_maker.features.insta;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tools.videodownloader.R;
import com.tools.videodownloader.collage_maker.features.crop.adapter.AspectRatioPreviewAdapter;
import com.tools.videodownloader.collage_maker.features.draw.AdapterColor;
import com.tools.videodownloader.collage_maker.features.draw.BrushColorListener;
import com.tools.videodownloader.collage_maker.utils.SystemUtils;
import com.tools.videodownloader.collage_maker.utils.UtilsFilter;
import com.steelkiwi.cropiwa.AspectRatio;


public class InstaDialog extends DialogFragment implements AspectRatioPreviewAdapter.OnNewSelectedListener, AdapterInsta.BackgroundInstaListener, BrushColorListener {
    private static final String TAG = "InstaDialog";

    public TextView background;
    private Bitmap bitmap;
    private Bitmap blurBitmap;
    private ImageView blurView;

    public TextView border;
    public RecyclerView fixedRatioList;
    public InstaSaveListener instaSaveListener;
    public RelativeLayout instagramPadding;
    public ImageView instagramPhoto;
    private RelativeLayout loadingView;
    public TextView ratio;
    private ConstraintLayout ratioLayout;

    public RecyclerView rvBackground;

    public FrameLayout wrapInstagram;

    public interface InstaSaveListener {
        void instaSavedBitmap(Bitmap bitmap);
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public static InstaDialog show(@NonNull AppCompatActivity appCompatActivity, InstaSaveListener instaSaveListener2, Bitmap bitmap2, Bitmap bitmap3) {
        InstaDialog instaDialog = new InstaDialog();
        instaDialog.setBitmap(bitmap2);
        instaDialog.setBlurBitmap(bitmap3);
        instaDialog.setInstaSaveListener(instaSaveListener2);
        instaDialog.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return instaDialog;
    }

    public void setBlurBitmap(Bitmap bitmap2) {
        this.blurBitmap = bitmap2;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public void setInstaSaveListener(InstaSaveListener instaSaveListener2) {
        this.instaSaveListener = instaSaveListener2;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.insta_layout, viewGroup, false);
        AspectRatioPreviewAdapter aspectRatioPreviewAdapter = new AspectRatioPreviewAdapter();
        aspectRatioPreviewAdapter.setListener(this);
        this.loadingView = inflate.findViewById(R.id.loadingView);
        this.loadingView.setVisibility(View.GONE);
        this.fixedRatioList = inflate.findViewById(R.id.fixed_ratio_list);
        this.fixedRatioList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        this.fixedRatioList.setAdapter(aspectRatioPreviewAdapter);
        this.rvBackground = inflate.findViewById(R.id.rv_background);
        this.rvBackground.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        this.rvBackground.setAdapter(new AdapterInsta(getContext(), this));
        this.rvBackground.setVisibility(View.GONE);
        this.ratio = inflate.findViewById(R.id.ratio);
        this.ratio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InstaDialog.this.fixedRatioList.setVisibility(View.VISIBLE);
                InstaDialog.this.rvBackground.setVisibility(View.GONE);
                InstaDialog.this.instagramPadding.setVisibility(View.GONE);
                InstaDialog.this.ratio.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorPrimary));
                InstaDialog.this.ratio.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_bottom));
                InstaDialog.this.background.setTextColor(ContextCompat.getColor(getContext(), R.color.unselected_color));
                InstaDialog.this.border.setTextColor(ContextCompat.getColor(getContext(), R.color.unselected_color));
                InstaDialog.this.background.setBackgroundResource(0);
                InstaDialog.this.border.setBackgroundResource(0);
            }
        });
        this.background = inflate.findViewById(R.id.background);
        this.background.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InstaDialog.this.fixedRatioList.setVisibility(View.GONE);
                InstaDialog.this.rvBackground.setVisibility(View.VISIBLE);
                InstaDialog.this.instagramPadding.setVisibility(View.GONE);
                InstaDialog.this.background.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorPrimary));
                InstaDialog.this.background.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_bottom));
                InstaDialog.this.ratio.setTextColor(ContextCompat.getColor(getContext(), R.color.unselected_color));
                InstaDialog.this.border.setTextColor(ContextCompat.getColor(getContext(), R.color.unselected_color));
                InstaDialog.this.ratio.setBackgroundResource(0);
                InstaDialog.this.border.setBackgroundResource(0);
            }
        });
        this.instagramPadding = inflate.findViewById(R.id.instagramPadding);
        this.instagramPadding.setVisibility(View.GONE);
        this.border = inflate.findViewById(R.id.border);
        this.border.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InstaDialog.this.instagramPadding.setVisibility(View.VISIBLE);
                InstaDialog.this.fixedRatioList.setVisibility(View.GONE);
                InstaDialog.this.rvBackground.setVisibility(View.GONE);
                InstaDialog.this.border.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorPrimary));
                InstaDialog.this.border.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_bottom));
                InstaDialog.this.background.setTextColor(ContextCompat.getColor(getContext(), R.color.unselected_color));
                InstaDialog.this.ratio.setTextColor(ContextCompat.getColor(getContext(), R.color.unselected_color));
                InstaDialog.this.background.setBackgroundResource(0);
                InstaDialog.this.ratio.setBackgroundResource(0);
            }
        });
        RecyclerView recyclerView = inflate.findViewById(R.id.rv_colors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new AdapterColor(getContext(), this));
        ((SeekBar) inflate.findViewById(R.id.paddingInsta)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                int dpToPx = SystemUtils.dpToPx(InstaDialog.this.getContext(), i);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) InstaDialog.this.instagramPhoto.getLayoutParams();
                layoutParams.setMargins(dpToPx, dpToPx, dpToPx, dpToPx);
                InstaDialog.this.instagramPhoto.setLayoutParams(layoutParams);
            }
        });
        this.instagramPhoto = inflate.findViewById(R.id.instagramPhoto);
        this.instagramPhoto.setImageBitmap(this.bitmap);
        this.instagramPhoto.setAdjustViewBounds(true);
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        this.ratioLayout = inflate.findViewById(R.id.ratioLayout);
        this.blurView = inflate.findViewById(R.id.blurView);
        this.blurView.setImageBitmap(this.blurBitmap);
        this.wrapInstagram = inflate.findViewById(R.id.wrapInstagram);
        this.wrapInstagram.setLayoutParams(new ConstraintLayout.LayoutParams(point.x, point.x));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.ratioLayout);
        ConstraintSet constraintSet2 = constraintSet;
        constraintSet2.connect(this.wrapInstagram.getId(), 3, this.ratioLayout.getId(), 3, 0);
        constraintSet2.connect(this.wrapInstagram.getId(), 1, this.ratioLayout.getId(), 1, 0);
        constraintSet2.connect(this.wrapInstagram.getId(), 4, this.ratioLayout.getId(), 4, 0);
        constraintSet2.connect(this.wrapInstagram.getId(), 2, this.ratioLayout.getId(), 2, 0);
        constraintSet.applyTo(this.ratioLayout);
        inflate.findViewById(R.id.imgClose).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InstaDialog.this.dismiss();
            }
        });
        inflate.findViewById(R.id.imgSave).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new SaveInstaView().execute(getBitmapFromView(InstaDialog.this.wrapInstagram));
            }
        });
        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(-16777216));
        }
    }

    public void onStop() {
        super.onStop();
    }

    private int[] calculateWidthAndHeight(AspectRatio aspectRatio, Point point) {
        int height = this.ratioLayout.getHeight();
        if (aspectRatio.getHeight() > aspectRatio.getWidth()) {
            int ratio2 = (int) (aspectRatio.getRatio() * ((float) height));
            if (ratio2 < point.x) {
                return new int[]{ratio2, height};
            }
            return new int[]{point.x, (int) (((float) point.x) / aspectRatio.getRatio())};
        }
        int ratio3 = (int) (((float) point.x) / aspectRatio.getRatio());
        if (ratio3 > height) {
            return new int[]{(int) (((float) height) * aspectRatio.getRatio()), height};
        }
        return new int[]{point.x, ratio3};
    }

    public void onNewAspectRatioSelected(AspectRatio aspectRatio) {
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int[] calculateWidthAndHeight = calculateWidthAndHeight(aspectRatio, point);
        this.wrapInstagram.setLayoutParams(new ConstraintLayout.LayoutParams(calculateWidthAndHeight[0], calculateWidthAndHeight[1]));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this.ratioLayout);
        ConstraintSet constraintSet2 = constraintSet;
        constraintSet2.connect(this.wrapInstagram.getId(), 3, this.ratioLayout.getId(), 3, 0);
        constraintSet2.connect(this.wrapInstagram.getId(), 1, this.ratioLayout.getId(), 1, 0);
        constraintSet2.connect(this.wrapInstagram.getId(), 4, this.ratioLayout.getId(), 4, 0);
        constraintSet2.connect(this.wrapInstagram.getId(), 2, this.ratioLayout.getId(), 2, 0);
        constraintSet.applyTo(this.ratioLayout);
    }

    public void showLoading(boolean z) {
        if (z) {
            getActivity().getWindow().setFlags(16, 16);
            this.loadingView.setVisibility(View.VISIBLE);
            return;
        }
        getActivity().getWindow().clearFlags(16);
        this.loadingView.setVisibility(View.GONE);
    }

    class SaveInstaView extends AsyncTask<Bitmap, Bitmap, Bitmap> {
        SaveInstaView() {
        }


        public void onPreExecute() {
            InstaDialog.this.showLoading(true);
        }


        public Bitmap doInBackground(Bitmap... bitmapArr) {
            Bitmap cloneBitmap = UtilsFilter.cloneBitmap(bitmapArr[0]);
            bitmapArr[0].recycle();
            bitmapArr[0] = null;
            return cloneBitmap;
        }

        public void onPostExecute(Bitmap bitmap) {
            InstaDialog.this.showLoading(false);
            InstaDialog.this.instaSaveListener.instaSavedBitmap(bitmap);
            InstaDialog.this.dismiss();
        }
    }

    public void onBackgroundSelected(AdapterInsta.SquareView squareView) {
        if (squareView.isColor) {
            this.wrapInstagram.setBackgroundColor(squareView.drawableId);
        } else if (squareView.text.equals("Blur")) {
            this.blurView.setVisibility(View.VISIBLE);
        } else {
            this.wrapInstagram.setBackgroundResource(squareView.drawableId);
            this.blurView.setVisibility(View.GONE);
        }
        this.wrapInstagram.invalidate();
    }

    public void onDestroyView() {
        super.onDestroyView();
        if (this.blurBitmap != null) {
            this.blurBitmap.recycle();
            this.blurBitmap = null;
        }
        this.bitmap = null;
    }

    public void onColorChanged(String str) {
        this.instagramPhoto.setBackgroundColor(Color.parseColor(str));
        if (!str.equals("#00000000")) {
            int dpToPx = SystemUtils.dpToPx(getContext(), 3);
            this.instagramPhoto.setPadding(dpToPx, dpToPx, dpToPx, dpToPx);
            return;
        }
        this.instagramPhoto.setPadding(0, 0, 0, 0);
    }

    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
