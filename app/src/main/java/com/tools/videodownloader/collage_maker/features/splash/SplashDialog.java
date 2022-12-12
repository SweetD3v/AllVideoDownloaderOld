package com.tools.videodownloader.collage_maker.features.splash;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.tools.videodownloader.R;
import com.tools.videodownloader.collage_maker.utils.UtilsAsset;
import com.tools.videodownloader.collage_maker.utils.UtilsFilter;


public class SplashDialog extends DialogFragment implements AdapterSplash.SplashChangeListener {
    private static final String TAG = "SplashDialog";
    private ImageView backgroundView;

    public Bitmap bitmap;
    private Bitmap blackAndWhiteBitmap;
    private Bitmap blurBitmap;
    private ElegantNumberButton blurNumber;
    private SplashSticker blurSticker;
    private SeekBar brushIntensity;

    public TextView draw;

    public LinearLayout drawLayout;

    public boolean isSplashView;
    private RelativeLayout loadingView;
    private ImageView redo;

    public RecyclerView rvSplashView;

    public TextView shape;

    public SplashDialogListener splashDialogListener;
    private SplashSticker splashSticker;

    public ResizableSplashView splashView;
    private ImageView undo;
    private ViewGroup viewGroup;

    public interface SplashDialogListener {
        void onSaveSplash(Bitmap bitmap);
    }

    public void setSplashView(boolean z) {
        this.isSplashView = z;
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public static SplashDialog show(@NonNull AppCompatActivity appCompatActivity, Bitmap bitmap2, Bitmap bitmap3, Bitmap bitmap4, SplashDialogListener splashDialogListener2, boolean z) {
        SplashDialog splashDialog = new SplashDialog();
        splashDialog.setBlurBitmap(bitmap3);
        splashDialog.setBitmap(bitmap2);
        splashDialog.setBlackAndWhiteBitmap(bitmap4);
        splashDialog.setSplashDialogListener(splashDialogListener2);
        splashDialog.setSplashView(z);
        splashDialog.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return splashDialog;
    }

    public void setBlackAndWhiteBitmap(Bitmap bitmap2) {
        this.blackAndWhiteBitmap = bitmap2;
    }

    public void setBlurBitmap(Bitmap bitmap2) {
        this.blurBitmap = bitmap2;
    }

    public void setSplashDialogListener(SplashDialogListener splashDialogListener2) {
        this.splashDialogListener = splashDialogListener2;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup2, @Nullable Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.splash_layout, viewGroup2, false);
        this.viewGroup = viewGroup2;
        this.backgroundView = inflate.findViewById(R.id.backgroundView);
        this.splashView = inflate.findViewById(R.id.splashView);
        this.drawLayout = inflate.findViewById(R.id.drawLayout);
        this.drawLayout.setVisibility(View.GONE);
        this.undo = inflate.findViewById(R.id.undo);
        this.undo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SplashDialog.this.splashView.undo();
            }
        });
        this.redo = inflate.findViewById(R.id.redo);
        this.redo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SplashDialog.this.splashView.redo();
            }
        });
        this.brushIntensity = inflate.findViewById(R.id.brushIntensity);
        this.brushIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                SplashDialog.this.splashView.setBrushBitmapSize(i + 25);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                SplashDialog.this.splashView.updateBrush();
            }
        });
        this.loadingView = inflate.findViewById(R.id.loadingView);
        this.loadingView.setVisibility(View.GONE);
        this.blurNumber = inflate.findViewById(R.id.blurNumber);
        this.backgroundView.setImageBitmap(this.bitmap);
        this.shape = inflate.findViewById(R.id.shape);
        this.draw = inflate.findViewById(R.id.draw);
        if (this.isSplashView) {
            this.splashView.setImageBitmap(this.blackAndWhiteBitmap);
            this.blurNumber.setVisibility(View.GONE);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) this.shape.getLayoutParams();
            layoutParams.horizontalBias = 0.3f;
            this.shape.setLayoutParams(layoutParams);
            ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) this.draw.getLayoutParams();
            layoutParams2.horizontalBias = 0.7f;
            this.draw.setLayoutParams(layoutParams2);
        } else {
            this.splashView.setImageBitmap(this.blurBitmap);
            this.blurNumber.setRange(0, 10);
        }
        this.blurNumber.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            public void onValueChange(ElegantNumberButton elegantNumberButton, int i, int i2) {
                new LoadBlurBitmap((float) i2).execute(new Void[0]);
            }
        });
        this.rvSplashView = inflate.findViewById(R.id.rvSplashView);
        this.rvSplashView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        this.rvSplashView.setHasFixedSize(true);
        this.rvSplashView.setAdapter(new AdapterSplash(getContext(), this, this.isSplashView));
        if (this.isSplashView) {
            this.splashSticker = new SplashSticker(UtilsAsset.loadBitmapFromAssets(getContext(), "splash/icons/mask1.webp"), UtilsAsset.loadBitmapFromAssets(getContext(), "splash/icons/frame1.webp"));
            this.splashView.addSticker(this.splashSticker);
        } else {
            this.blurSticker = new SplashSticker(UtilsAsset.loadBitmapFromAssets(getContext(), "blur/icons/blur_1_mask.webp"), UtilsAsset.loadBitmapFromAssets(getContext(), "blur/icons/blur_1_shadow.webp"));
            this.splashView.addSticker(this.blurSticker);
        }
        this.splashView.refreshDrawableState();
        this.splashView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.shape.setOnClickListener(view -> {
            SplashDialog.this.draw.setBackgroundResource(0);
            SplashDialog.this.draw.setTextColor(ContextCompat.getColor(getContext(), R.color.unselected_color));
            SplashDialog.this.shape.setBackgroundResource(R.drawable.border_bottom);
            SplashDialog.this.shape.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorPrimary));
            SplashDialog.this.splashView.setCurrentSplashMode(0);
            SplashDialog.this.drawLayout.setVisibility(View.GONE);
            SplashDialog.this.rvSplashView.setVisibility(View.VISIBLE);
            SplashDialog.this.splashView.refreshDrawableState();
            SplashDialog.this.splashView.invalidate();
        });
        this.draw.setOnClickListener(view -> {
            SplashDialog.this.splashView.refreshDrawableState();
            SplashDialog.this.splashView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            SplashDialog.this.shape.setBackgroundResource(0);
            SplashDialog.this.shape.setTextColor(ContextCompat.getColor(getContext(), R.color.unselected_color));
            SplashDialog.this.draw.setBackgroundResource(R.drawable.border_bottom);
            SplashDialog.this.draw.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorPrimary));
            SplashDialog.this.splashView.setCurrentSplashMode(1);
            SplashDialog.this.drawLayout.setVisibility(View.VISIBLE);
            SplashDialog.this.rvSplashView.setVisibility(View.GONE);
            SplashDialog.this.splashView.invalidate();
        });
        inflate.findViewById(R.id.imgSave).setOnClickListener(view -> {
            SplashDialog.this.splashDialogListener.onSaveSplash(SplashDialog.this.splashView.getBitmap(SplashDialog.this.bitmap));
            SplashDialog.this.dismiss();
        });
        inflate.findViewById(R.id.imgClose).setOnClickListener(view -> SplashDialog.this.dismiss());
        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(-16777216));
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.splashView.getSticker().release();
        if (this.blurBitmap != null) {
            this.blurBitmap.recycle();
        }
        this.blurBitmap = null;
        if (this.blackAndWhiteBitmap != null) {
            this.blackAndWhiteBitmap.recycle();
        }
        this.blackAndWhiteBitmap = null;
        this.bitmap = null;
    }

    public void onSelected(SplashSticker splashSticker2) {
        this.splashView.addSticker(splashSticker2);
    }

    class LoadBlurBitmap extends AsyncTask<Void, Bitmap, Bitmap> {
        private float intensity;

        public LoadBlurBitmap(float f) {
            this.intensity = f;
        }


        public void onPreExecute() {
            SplashDialog.this.showLoading(true);
        }


        public Bitmap doInBackground(Void... voidArr) {
            return UtilsFilter.getBlurImageFromBitmap(SplashDialog.this.bitmap, this.intensity);
        }


        public void onPostExecute(Bitmap bitmap) {
            SplashDialog.this.showLoading(false);
            SplashDialog.this.splashView.setImageBitmap(bitmap);
        }
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
}
