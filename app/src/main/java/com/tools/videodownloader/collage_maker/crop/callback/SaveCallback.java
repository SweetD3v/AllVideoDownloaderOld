package com.tools.videodownloader.collage_maker.crop.callback;

import android.net.Uri;


public interface SaveCallback extends Callback {
  void onSuccess(Uri uri);
}
