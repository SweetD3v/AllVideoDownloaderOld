package com.example.allviddownloader.collage_maker.ui.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allviddownloader.R;
import com.example.allviddownloader.collage_maker.ui.adapters.AdapterFilterView;
import com.example.allviddownloader.collage_maker.ui.interfaces.FilterListener;
import com.example.allviddownloader.collage_maker.utils.UtilsFilter;

import java.util.Arrays;
import java.util.List;


public class FilterDialogFragment extends DialogFragment implements FilterListener {
    private static final String TAG = "FilterDialogFragment";

    public Bitmap bitmap;
    private RelativeLayout loadingView;
    private List<Bitmap> lstFilterBitmap;

    public OnFilterSavePhoto onFilterSavePhoto;

    public ImageView preview;
    private RecyclerView rvFilterView;

    public interface OnFilterSavePhoto {
        void onSaveFilter(Bitmap bitmap);
    }

    public void setOnFilterSavePhoto(OnFilterSavePhoto onFilterSavePhoto2) {
        this.onFilterSavePhoto = onFilterSavePhoto2;
    }

    public void setLstFilterBitmap(List<Bitmap> list) {
        this.lstFilterBitmap = list;
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public static FilterDialogFragment show(@NonNull AppCompatActivity appCompatActivity, OnFilterSavePhoto onFilterSavePhoto2, Bitmap bitmap2, List<Bitmap> list) {
        FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
        filterDialogFragment.setBitmap(bitmap2);
        filterDialogFragment.setOnFilterSavePhoto(onFilterSavePhoto2);
        filterDialogFragment.setLstFilterBitmap(list);
        filterDialogFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return filterDialogFragment;
    }

    public void onDestroy() {
        super.onDestroy();
        this.lstFilterBitmap.clear();
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        getDialog().getWindow().requestFeature(1);
        getDialog().getWindow().setFlags(1024, 1024);
        View inflate = layoutInflater.inflate(R.layout.filter_layout, viewGroup, false);
        this.rvFilterView = inflate.findViewById(R.id.rvFilterView);
        this.rvFilterView.setAdapter(new AdapterFilterView(this.lstFilterBitmap, getContext(), this, Arrays.asList(UtilsFilter.EFFECT_CONFIGS)));
        this.preview = inflate.findViewById(R.id.preview);
        this.preview.setImageBitmap(this.bitmap);
        (inflate.findViewById(R.id.imgSave)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FilterDialogFragment.this.onFilterSavePhoto.onSaveFilter(((BitmapDrawable) FilterDialogFragment.this.preview.getDrawable()).getBitmap());
                FilterDialogFragment.this.dismiss();
            }
        });
        this.loadingView = inflate.findViewById(R.id.loadingView);
        this.loadingView.setVisibility(View.GONE);
        inflate.findViewById(R.id.imgClose).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FilterDialogFragment.this.dismiss();
            }
        });
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(-16777216));
        }
    }

    public void onFilterSelected(String str) {
        new LoadBitmapWithFilter().execute(new String[]{str});
    }

    class LoadBitmapWithFilter extends AsyncTask<String, Bitmap, Bitmap> {
        LoadBitmapWithFilter() {
        }


        public void onPreExecute() {
            FilterDialogFragment.this.showLoading(true);
        }


        public Bitmap doInBackground(String... strArr) {
            return UtilsFilter.getBitmapWithFilter(FilterDialogFragment.this.bitmap, strArr[0]);
        }


        public void onPostExecute(Bitmap bitmap) {
            FilterDialogFragment.this.preview.setImageBitmap(bitmap);
            FilterDialogFragment.this.showLoading(false);
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
