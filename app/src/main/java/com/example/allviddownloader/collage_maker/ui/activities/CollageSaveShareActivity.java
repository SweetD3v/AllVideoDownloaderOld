package com.example.allviddownloader.collage_maker.ui.activities;

import static com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity.CREATION_TYPE;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.allviddownloader.R;
import com.example.allviddownloader.databinding.CollageSaveLayoutBinding;
import com.example.allviddownloader.ui.mycreation.MyCreationToolsActivity;
import com.example.allviddownloader.utils.AdsUtils;
import com.example.allviddownloader.utils.NetworkState;

import java.io.File;

public class CollageSaveShareActivity extends AppCompatActivity {
    CollageSaveLayoutBinding binding;

    ImageView preview;

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        binding = CollageSaveLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (NetworkState.Companion.isOnline()) {
//            AdsUtils.Companion.loadBanner(this, binding.bannerContainer,
//                    getString(R.string.banner_id_details));
            AdsUtils.Companion.loadNative(
                    this,
                    getString(R.string.admob_native_id),
                    binding.adFrame
            );
        }

        preview = findViewById(R.id.preview);

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.btnMyCreation.setOnClickListener(v -> {
            Intent intent = new Intent(CollageSaveShareActivity.this, MyCreationToolsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (getIntent().getIntExtra("type", 0) == 0) {
                intent.putExtra(CREATION_TYPE, "photo_editor");
            } else {
                intent.putExtra(CREATION_TYPE, "collage_maker");
            }
            startActivity(intent);
        });

        String string = getIntent().getExtras().getString("path");
        File file = new File(string);
        Glide.with(getApplicationContext()).load(file).into((ImageView) findViewById(R.id.preview));

        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setWindowHeight(800);
        imagePopup.setWindowWidth(800);
        imagePopup.setBackgroundColor(Color.TRANSPARENT);
        imagePopup.setFullScreen(true);
        imagePopup.setHideCloseIcon(true);
        imagePopup.setImageOnClickClose(true);
        imagePopup.initiatePopupWithGlide(string);

//        findViewById(R.id.shareLayout).setOnClickListener(view -> {
//            Intent intent = new Intent("android.intent.action.SEND");
//            intent.setType("image/*");
//            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file));
//            startActivity(Intent.createChooser(intent, "Share"));
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(CollageSaveShareActivity.this, CollageMakerHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        AdsUtils.Companion.destroyBanner();
        super.onDestroy();
    }
}
