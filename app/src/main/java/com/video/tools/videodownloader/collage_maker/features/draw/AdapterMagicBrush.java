package com.video.tools.videodownloader.collage_maker.features.draw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.video.tools.videodownloader.R;
import com.video.tools.videodownloader.collage_maker.features.DrawBitmapModel;
import com.video.tools.videodownloader.collage_maker.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMagicBrush extends RecyclerView.Adapter<AdapterMagicBrush.ViewHolder> {
    public static List<DrawBitmapModel> drawBitmapModels = new ArrayList();
    private int borderSize = 0;

    public BrushMagicListener brushMagicListener;
    private Context context;

    public int selectedColorIndex;

    public AdapterMagicBrush(Context context2, BrushMagicListener brushMagicListener2) {
        this.context = context2;
        this.borderSize = SystemUtils.dpToPx(context2, 2);
        this.brushMagicListener = brushMagicListener2;
        drawBitmapModels = lstDrawBitmapModel(context2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_magic_brush, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.magicBrush.setImageResource(drawBitmapModels.get(i).getMainIcon());
        if (this.selectedColorIndex == i) {
            viewHolder.magicBrush.setBorderWidth(this.borderSize);
        } else {
            viewHolder.magicBrush.setBorderWidth(0);
        }
    }

    public void setSelectedColorIndex(int i) {
        this.selectedColorIndex = i;
    }

    @Override
    public int getItemCount() {
        return drawBitmapModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView magicBrush;

        ViewHolder(View view) {
            super(view);
            this.magicBrush = view.findViewById(R.id.magicBrush);
            this.magicBrush.setOnClickListener(view1 -> {
                selectedColorIndex = getLayoutPosition();
                brushMagicListener.onMagicChanged(AdapterMagicBrush.drawBitmapModels.get(selectedColorIndex));
                notifyDataSetChanged();
            });
        }
    }

    public static List<DrawBitmapModel> lstDrawBitmapModel(Context context2) {
        if (drawBitmapModels != null && !drawBitmapModels.isEmpty()) {
            return drawBitmapModels;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(Integer.valueOf(R.drawable.b4));
        arrayList.add(Integer.valueOf(R.drawable.b5));
        arrayList.add(Integer.valueOf(R.drawable.b6));
        arrayList.add(Integer.valueOf(R.drawable.b7));
        arrayList.add(Integer.valueOf(R.drawable.b8));
        arrayList.add(Integer.valueOf(R.drawable.b9));
        arrayList.add(Integer.valueOf(R.drawable.b10));
        arrayList.add(Integer.valueOf(R.drawable.b11));
        arrayList.add(Integer.valueOf(R.drawable.b12));
        arrayList.add(Integer.valueOf(R.drawable.b13));
        arrayList.add(Integer.valueOf(R.drawable.b14));
        arrayList.add(Integer.valueOf(R.drawable.b15));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.butterfly, arrayList, context2));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(Integer.valueOf(R.drawable.magic21));
        arrayList2.add(Integer.valueOf(R.drawable.magic22));
        arrayList2.add(Integer.valueOf(R.drawable.magic23));
        arrayList2.add(Integer.valueOf(R.drawable.magic24));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.heart_1, arrayList2, context2));
        ArrayList arrayList3 = new ArrayList();
        arrayList3.add(Integer.valueOf(R.drawable.f1));
        arrayList3.add(Integer.valueOf(R.drawable.f1));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.f1_icon, arrayList3, context2));
        ArrayList arrayList4 = new ArrayList();
        arrayList4.add(Integer.valueOf(R.drawable.s1));
        arrayList4.add(Integer.valueOf(R.drawable.s1));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.s1_icon, arrayList4, context2));
        ArrayList arrayList5 = new ArrayList();
        arrayList5.add(Integer.valueOf(R.drawable.b1));
        arrayList5.add(Integer.valueOf(R.drawable.b2));
        arrayList5.add(Integer.valueOf(R.drawable.b3));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.b1_icon, arrayList5, context2));
        ArrayList arrayList6 = new ArrayList();
        arrayList6.add(Integer.valueOf(R.drawable.f3));
        arrayList6.add(Integer.valueOf(R.drawable.f7));
        arrayList6.add(Integer.valueOf(R.drawable.f5));
        arrayList6.add(Integer.valueOf(R.drawable.f6));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.f2_icon, arrayList6, context2));
        ArrayList arrayList7 = new ArrayList();
        arrayList7.add(Integer.valueOf(R.drawable.m1));
        arrayList7.add(Integer.valueOf(R.drawable.m2));
        arrayList7.add(Integer.valueOf(R.drawable.m3));
        arrayList7.add(Integer.valueOf(R.drawable.m4));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.bb2_icon, arrayList7, context2));
        ArrayList arrayList8 = new ArrayList();
        arrayList8.add(Integer.valueOf(R.drawable.ss1));
        arrayList8.add(Integer.valueOf(R.drawable.ss2));
        arrayList8.add(Integer.valueOf(R.drawable.ss3));
        arrayList8.add(Integer.valueOf(R.drawable.ss5));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.f3_icon, arrayList8, context2));
        ArrayList arrayList9 = new ArrayList();
        arrayList9.add(Integer.valueOf(R.drawable.s17));
        arrayList9.add(Integer.valueOf(R.drawable.s17));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.smile_icon1, arrayList9, context2));
        ArrayList arrayList10 = new ArrayList();
        arrayList10.add(Integer.valueOf(R.drawable.s21));
        arrayList10.add(Integer.valueOf(R.drawable.s21));
        drawBitmapModels.add(new DrawBitmapModel(R.drawable.smile_icon2, arrayList10, context2));
        return drawBitmapModels;
    }
}
