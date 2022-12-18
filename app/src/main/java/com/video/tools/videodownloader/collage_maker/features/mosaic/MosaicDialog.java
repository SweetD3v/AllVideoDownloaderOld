package com.video.tools.videodownloader.collage_maker.features.mosaic;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.video.tools.videodownloader.R;
import com.video.tools.videodownloader.collage_maker.utils.UtilsFilter;

public class MosaicDialog extends DialogFragment implements AdapterMosaic.MosaicChangeListener {
    private static final String TAG = "SplashDialog";

    public Bitmap adjustBitmap;
    private ImageView backgroundView;

    public Bitmap bitmap;
    private SeekBar eraseSize;
    private RelativeLayout loadingView;

    public MosaicDialogListener mosaicDialogListener;
    private SeekBar mosaicSize;

    public MosaicView mosaicView;
    private RecyclerView rvMosaic;

    public interface MosaicDialogListener {
        void onSaveMosaic(Bitmap bitmap);
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public static MosaicDialog show(@NonNull AppCompatActivity appCompatActivity, Bitmap bitmap2, Bitmap bitmap3, MosaicDialogListener mosaicDialogListener2) {
        MosaicDialog mosaicDialog = new MosaicDialog();
        mosaicDialog.setBitmap(bitmap2);
        mosaicDialog.setAdjustBitmap(bitmap3);
        mosaicDialog.setMosaicListener(mosaicDialogListener2);
        mosaicDialog.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return mosaicDialog;
    }

    public void setMosaicListener(MosaicDialogListener mosaicDialogListener2) {
        this.mosaicDialogListener = mosaicDialogListener2;
    }

    public void setAdjustBitmap(Bitmap bitmap2) {
        this.adjustBitmap = bitmap2;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.mosaic_layout, viewGroup, false);
        this.mosaicView = inflate.findViewById(R.id.mosaicView);
        this.mosaicView.setImageBitmap(this.bitmap);
        this.mosaicView.setMosaicItem(new AdapterMosaic.MosaicItem(R.drawable.blue_mosoic, 0, AdapterMosaic.Mode.BLUR));
        this.loadingView = inflate.findViewById(R.id.loadingView);
        this.loadingView.setVisibility(View.GONE);
        this.backgroundView = inflate.findViewById(R.id.backgroundView);
        this.adjustBitmap = UtilsFilter.getBlurImageFromBitmap(this.bitmap);
        this.backgroundView.setImageBitmap(this.adjustBitmap);
        this.eraseSize = inflate.findViewById(R.id.eraseSize);
        this.eraseSize.setVisibility(View.GONE);
        this.mosaicSize = inflate.findViewById(R.id.mosaicSize);
        this.rvMosaic = inflate.findViewById(R.id.rvMosaic);
        this.rvMosaic.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        this.rvMosaic.setHasFixedSize(true);
        this.rvMosaic.setAdapter(new AdapterMosaic(getContext(), this));
        inflate.findViewById(R.id.imgSave).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new SaveMosaicView().execute(new Void[0]);
            }
        });
        inflate.findViewById(R.id.imgClose).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MosaicDialog.this.dismiss();
            }
        });
        this.mosaicSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                MosaicDialog.this.mosaicView.setBrushBitmapSize(i + 25);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                MosaicDialog.this.mosaicView.updateBrush();
            }
        });
        inflate.findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MosaicDialog.this.mosaicView.undo();
            }
        });
        inflate.findViewById(R.id.redo).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MosaicDialog.this.mosaicView.redo();
            }
        });
        return inflate;
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
        this.bitmap.recycle();
        this.bitmap = null;
        this.adjustBitmap.recycle();
        this.adjustBitmap = null;
    }

    public void onStop() {
        super.onStop();
    }

    public void onSelected(AdapterMosaic.MosaicItem mosaicItem) {
        if (mosaicItem.mode == AdapterMosaic.Mode.BLUR) {
            this.adjustBitmap = UtilsFilter.getBlurImageFromBitmap(this.bitmap);
            this.backgroundView.setImageBitmap(this.adjustBitmap);
        } else if (mosaicItem.mode == AdapterMosaic.Mode.MOSAIC) {
            this.adjustBitmap = UtilsMosaic.getMosaic(this.bitmap);
            this.backgroundView.setImageBitmap(this.adjustBitmap);
        }
        this.mosaicView.setMosaicItem(mosaicItem);
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

    class SaveMosaicView extends AsyncTask<Void, Bitmap, Bitmap> {
        SaveMosaicView() {
        }


        public void onPreExecute() {
            MosaicDialog.this.showLoading(true);
        }


        public Bitmap doInBackground(Void... voidArr) {
            return MosaicDialog.this.mosaicView.getBitmap(MosaicDialog.this.bitmap, MosaicDialog.this.adjustBitmap);
        }


        public void onPostExecute(Bitmap bitmap) {
            MosaicDialog.this.showLoading(false);
            MosaicDialog.this.mosaicDialogListener.onSaveMosaic(bitmap);
            MosaicDialog.this.dismiss();
        }
    }
}
