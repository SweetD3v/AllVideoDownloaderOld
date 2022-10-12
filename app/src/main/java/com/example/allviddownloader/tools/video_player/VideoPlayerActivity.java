package com.example.allviddownloader.tools.video_player;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.allviddownloader.AllVidApp;
import com.example.allviddownloader.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.util.UUID;

import showcase.view.video.lib.GuideView;
import showcase.view.video.lib.config.DismissType;
import showcase.view.video.lib.config.Gravity;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener, ExoPlayer.EventListener, PlaybackControlView.VisibilityListener {

    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    String videoURL;
    private long newPosition = -1;
    private Query query;
    private float brightness = -1.0f;
    private AudioManager audioManager;
    private Handler mainHandler;
    private int volume = -1;
    private int mMaxVolume;
    private int screenWidthPixels;
    private TrackGroupArray lastSeenTrackGroupArray;
    private int resizeMode = 0;
    AppCompatImageView btnAudio;
    AppCompatImageView btnVideo;
    ProgressBar progress_player;
    private TrackSelectionHelper trackSelectionHelper;
    private DefaultTrackSelector trackSelector;
    int i = 0;
    private LinearLayout controls_root;
    private LinearLayout debugRootView;
    AppCompatImageView exo_lock;
    AppCompatImageView btnEquilize;
    AppCompatImageView exo_aspect;
    AppCompatImageView btnRotate;
    boolean fullScreenMode;
    ViewGroup rootView;
    private VideoPlayerEventLogger videoPlayerEventLogger;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    public static final String DRM_KEY_REQUEST_PROPERTIES = "drm_key_request_properties";
    public static final String DRM_LICENSE_URL = "drm_license_url";
    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";
    ProgressBar buffering;
    SharedPreferences guidePrefs;
    ConstraintLayout footer_view;

    private int currentWindow;
    private long playbackPosition;
    private boolean autoPlay = true;
    public static final String AUTOPLAY = "autoplay";
    public static final String CURRENT_WINDOW_INDEX = "current_window_index";
    public static final String PLAYBACK_POSITION = "playback_position";

    private GuideView mGuideView;
    private GuideView.Builder builder;
    public static final String guide_key = "guide_key";

    private void showGuide() {
        builder = new GuideView.Builder(this)
                .setTitle("Screen orientation")
                .setContentText("Rotate the display")
                .setGravity(Gravity.center)
                .setDismissType(DismissType.anywhere)
                .setTargetView(btnRotate)
                .setGuideListener(view -> {
                    switch (view.getId()) {
                        case R.id.btn_rotate:
                            builder.setTitle("Subtitles")
                                    .setContentText("Add subtitles")
                                    .setGravity(Gravity.center)
                                    .setDismissType(DismissType.anywhere).setTargetView(exo_lock).build();
                            break;
                        case R.id.btnEquilizer:
                            builder.setTitle("Lock")
                                    .setContentText("Lock controls")
                                    .setGravity(Gravity.center)
                                    .setDismissType(DismissType.anywhere).setTargetView(exo_lock).build();
                            break;
                        case R.id.exo_lock:
                            builder.setTitle("Scale")
                                    .setContentText("Change video scale type")
                                    .setGravity(Gravity.center)
                                    .setDismissType(DismissType.anywhere).setTargetView(exo_aspect).build();
                            break;
                        case R.id.exo_aspect:
                            guidePrefs.edit().putBoolean(guide_key, false).apply();
                            debugRootView.setVisibility(GONE);
                            exo_lock.setVisibility(GONE);
                            exo_aspect.setVisibility(GONE);
                            new Handler().post(() -> getWindow().getDecorView()
                                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY));
                            return;
                    }
                    mGuideView = builder.build();
                    mGuideView.show();
                });

        mGuideView = builder.build();
        mGuideView.show();
        updatingForDynamicLocationViews();
    }

    private void updatingForDynamicLocationViews() {
        exo_lock.setOnFocusChangeListener((view, b) -> mGuideView.updateGuideViewLocation());
    }

    private void showSystemUI(final boolean show) {
        new Handler().post(() -> getWindow().getDecorView().setSystemUiVisibility(show ?
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN :
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY));
        showUI(show);
    }

    private void showUI(boolean show) {
        float toolbar_translationY = show ? 0 : -(controls_root.getHeight());
        controls_root.animate()
                .translationY(toolbar_translationY)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
        fullScreenMode = show;
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    if (newPosition >= 0) {
                        exoPlayerView.getPlayer().seekTo((int) newPosition);
                        newPosition = -1;
                        return;
                    }
                    return;
                case 4:
                    query.id(R.id.app_video_volume_box).gone();
                    query.id(R.id.app_video_brightness_box).gone();
                    query.id(R.id.app_video_fastForward_box).gone();
                    query.id(R.id.app_video_ratio_box).gone();
                    return;
                default:
                    return;
            }
        }
    };

    private void updateButtonVisibilities() {
        if (this.exoPlayer != null) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = this.trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                for (int i = 0; i < mappedTrackInfo.length; i++) {
                    if (mappedTrackInfo.getTrackGroups(i).length != 0) {
                        switch (this.exoPlayer.getRendererType(i)) {
                            case 1:
                                this.btnAudio.setTag(Integer.valueOf(i));
                                this.btnAudio.setVisibility(View.VISIBLE);
                                this.btnAudio.setOnClickListener(this);
                                break;
                            case 2:
                                this.btnVideo.setTag(Integer.valueOf(i));
                                this.btnVideo.setOnClickListener(this);
                                this.btnVideo.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAudio) {
            trackSelectionHelper.showSelectionDialog(this, "Select Audio track", trackSelector.getCurrentMappedTrackInfo(), (Integer) view.getTag());
        } else if (view.getId() == R.id.btnVideo) {
            trackSelectionHelper.showSelectionDialog(this, "Select Video track", trackSelector.getCurrentMappedTrackInfo(), (Integer) view.getTag());
        }
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        showUI(true);
        updateButtonVisibilities();
        if (trackGroups != this.lastSeenTrackGroupArray) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = this.trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                if (mappedTrackInfo.getTrackTypeRendererSupport(2) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    Toast.makeText(this, "" + R.string.error_unsupported_video, Toast.LENGTH_SHORT).show();
                }
                if (mappedTrackInfo.getTrackTypeRendererSupport(1) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    Toast.makeText(this, "" + R.string.error_unsupported_audio, Toast.LENGTH_SHORT).show();
                }
            }
            this.lastSeenTrackGroupArray = trackGroups;
        }
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            buffering.setVisibility(View.VISIBLE);
        } else {
            buffering.setVisibility(View.INVISIBLE);
        }
        showUI(true);
        updateButtonVisibilities();
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        String errorString = null;
        if (error.type == 1) {
            Exception cause = error.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException = (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName != null) {
                    errorString = getString(R.string.error_instantiating_decoder, decoderInitializationException.decoderName);
                } else if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                    errorString = getString(R.string.error_querying_decoders);
                } else if (decoderInitializationException.secureDecoderRequired) {
                    errorString = getString(R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                } else {
                    errorString = getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                }
            }
        }

        if (errorString != null) {
            Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
        }

        if (isBehindLiveWindow(error)) {
            Intent intent = getIntent();
            videoURL = intent.getStringExtra("selectedvideo");
            initializePlayer();
        }
        showSystemUI(true);
        updateButtonVisibilities();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onVisibilityChange(int visibility) {
        if (!guidePrefs.getBoolean(guide_key, true)) {
            debugRootView.setVisibility(visibility);
            exo_lock.setVisibility(visibility);
            exo_aspect.setVisibility(visibility);
        }
        if (visibility == GONE)
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            });
        else
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            });
    }

    class Query {
        private final Activity activity;
        private View view;

        public Query(Activity activity) {
            this.activity = activity;
        }

        public Query id(int id) {
            view = findViewById(id);
            return this;
        }

        public Query image(int resId) {
            if (view instanceof AppCompatImageView) {
                ((AppCompatImageView) view).setImageResource(resId);
            }
            return this;
        }

        public Query visible() {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public Query gone() {
            if (view != null) {
                view.setVisibility(GONE);
            }
            return this;
        }

        public Query invisible() {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
            return this;
        }


        public Query text(CharSequence text) {
            if (view != null && (view instanceof TextView)) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public Query visibility(int visible) {
            if (view != null) {
                view.setVisibility(visible);
            }
            return this;
        }

        private void size(boolean width, int n, boolean dip) {
            if (view != null) {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (n > 0 && dip) {
                    n = dip2pixel(activity, (float) n);
                }
                if (width) {
                    lp.width = n;
                } else {
                    lp.height = n;
                }
                view.setLayoutParams(lp);
            }
        }

        public void height(int height, boolean dip) {
            size(false, height, dip);
        }

        public int dip2pixel(Context context, float n) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
        }

    }


    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean toSeek;
        private boolean volumeControl;

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            if (exoPlayerView.isEnabled()) {
                exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            firstTouch = true;
            return super.onDown(motionEvent);
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent1, MotionEvent motionEvent2, float distanceX, float distanceY) {
            if (exoPlayerView.isEnabled()) {
                boolean z = true;
                float mOldX = motionEvent1.getX();
                float deltaY = motionEvent1.getY() - motionEvent2.getY();
                float deltaX = mOldX - motionEvent2.getX();
                if (firstTouch) {
                    toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                    if (mOldX <= ((float) screenWidthPixels) * 0.5f) {
                        z = false;
                    }
                    volumeControl = z;
                    firstTouch = false;
                }
                if (toSeek) {
                    onProgressSlide((-deltaX) / ((float) exoPlayerView.getWidth()));
                } else {
                    float percent = deltaY / ((float) exoPlayerView.getHeight());
                    if (volumeControl) {
                        onVolumeSlide(percent);
                    } else {
                        onBrightnessSlide(percent);
                    }
                }
                return super.onScroll(motionEvent1, motionEvent2, distanceX, distanceY);
            } else {
                return false;
            }
        }

    }

    class lockControls implements View.OnClickListener {
        lockControls() {
        }

        public void onClick(View v) {
            if (i == 0) {
                exoPlayerView.setEnabled(false);
                exo_lock.setImageResource(R.drawable.unlock);
                exoPlayerView.findViewById(R.id.exo_play).setEnabled(false);
                exoPlayerView.findViewById(R.id.exo_pause).setEnabled(false);
                exoPlayerView.findViewById(R.id.exo_rew).setEnabled(false);
                exoPlayerView.findViewById(R.id.exo_ffwd).setEnabled(false);
                exoPlayerView.findViewById(R.id.exo_progress).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                findViewById(R.id.exo_aspect).setVisibility(GONE);
                i = 1;
            } else {
                exoPlayerView.setEnabled(true);
                exoPlayerView.setUseController(true);
                exoPlayerView.findViewById(R.id.exo_play).setEnabled(true);
                exoPlayerView.findViewById(R.id.exo_pause).setEnabled(true);
                exoPlayerView.findViewById(R.id.exo_rew).setEnabled(true);
                exoPlayerView.findViewById(R.id.exo_ffwd).setEnabled(true);
                exoPlayerView.findViewById(R.id.exo_progress).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                findViewById(R.id.exo_aspect).setVisibility(View.VISIBLE);
                exoPlayerView.setControllerVisibilityListener(VideoPlayerActivity.this);
                exo_lock.setImageResource(R.drawable.lock);
                i = 0;
            }
        }
    }

    public boolean isFullScreen() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final ViewGroup rootView = findViewById(R.id.root_view);
        rootView.setOnApplyWindowInsetsListener((view, insets) -> {

            controls_root.setPadding(insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), 0);

            footer_view.setPadding(insets.getSystemWindowInsetLeft(),
                    0, insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());

            return insets.consumeSystemWindowInsets();
        });
        showSystemUI(fullScreenMode);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        rootView = findViewById(R.id.root_view);
        rootView.setOnApplyWindowInsetsListener((view, insets) -> {

            controls_root.setPadding(insets.getSystemWindowInsetLeft(),
                    insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), 0);

            footer_view.setPadding(insets.getSystemWindowInsetLeft(),
                    0, insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());

            return insets.consumeSystemWindowInsets();
        });

        guidePrefs = getApplicationContext().getSharedPreferences("GUIDE_PREF", MODE_PRIVATE);

        if (getIntent().getIntExtra(DemoUtil.VID_ORIENTATION, 0) == 0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        mainHandler = new Handler();
        exoPlayerView = findViewById(R.id.exo_player_view);
        btnEquilize = findViewById(R.id.btnEquilizer);
        screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
        audioManager = (AudioManager) getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        assert audioManager != null;
        mMaxVolume = audioManager.getStreamMaxVolume(3);
        screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
        query = new Query(this);
        exoPlayerView.setClickable(true);
        btnAudio = findViewById(R.id.btnAudio);
        progress_player = findViewById(R.id.progress_player);

        controls_root = findViewById(R.id.header_set);
        btnVideo = findViewById(R.id.btnVideo);
        exo_lock = findViewById(R.id.exo_lock);
        exoPlayerView.setControllerVisibilityListener(this);
        exo_lock.setOnClickListener(new lockControls());
        debugRootView = findViewById(R.id.header_set);
        exo_aspect = findViewById(R.id.exo_aspect);
        btnRotate = findViewById(R.id.btn_rotate);
        footer_view = findViewById(R.id.footer_view);
        btnAudio.setOnClickListener(this);
        btnVideo.setOnClickListener(this);

        if (guidePrefs.getBoolean(guide_key, true)) {
            showGuide();
        }
        showSystemUI(true);
        exo_aspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resizeMode++;
                if (resizeMode > 3) {
                    resizeMode = 0;
                }

                if (resizeMode == 0) {
                    Toast.makeText(getApplicationContext(), "DEFAULT", Toast.LENGTH_SHORT).show();
                    exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                } else if (resizeMode == 1) {
                    Toast.makeText(getApplicationContext(), "FIT TO CENTER", Toast.LENGTH_SHORT).show();
                    exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                } else if (resizeMode == 2) {
                    Toast.makeText(getApplicationContext(), "FILL", Toast.LENGTH_SHORT).show();
                    exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                } else if (resizeMode == 3) {
                    Toast.makeText(getApplicationContext(), "CENTRE CROP", Toast.LENGTH_SHORT).show();
                    exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                }
            }
        });

        exoPlayerView.requestFocus();
        query = new Query(this);
        buffering = findViewById(R.id.buffer);

        final GestureDetector gestureDetector = new GestureDetector(this, new PlayerGestureListener());
        btnRotate.setOnClickListener(view -> {

            Display display = null;
            if (getSystemService(WINDOW_SERVICE) != null) {
                display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
            }
            switch (display.getOrientation()) {
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
            }
            if (isFullScreen()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                return;
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        });

        exoPlayerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent)) {
                    return true;
                }
                switch (motionEvent.getAction() & 255) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });

        btnEquilize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AudioEffect
                        .ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);

                if ((intent.resolveActivity(getPackageManager()) != null)) {
                    startActivityForResult(intent, 100);
                } else {
                    Toast.makeText(getApplicationContext(), "No Equilizer found on this Device", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = getIntent();
        videoURL = intent.getStringExtra("selectedvideo");

        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION, 0);
            currentWindow = savedInstanceState.getInt(CURRENT_WINDOW_INDEX, 0);
            autoPlay = savedInstanceState.getBoolean(AUTOPLAY, false);
        }

    }

    private void initializePlayer() {
        try {
            Intent intent = getIntent();
            Intent intent1 = new Intent("android.intent.action.VIEW");
            videoURL = intent.getStringExtra("selectedvideo");

            if (videoURL.endsWith(".avi")) {
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
                errorDialog.setMessage("Video Format is not Supported..!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                AlertDialog alert = errorDialog.create();
                alert.show();
            }


            TrackSelection.Factory adaptiveTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            this.trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            this.trackSelectionHelper = new TrackSelectionHelper(this.trackSelector, adaptiveTrackSelectionFactory);
            this.lastSeenTrackGroupArray = null;
            this.videoPlayerEventLogger = new VideoPlayerEventLogger(this.trackSelector);
            UUID drmSchemeUuid = intent1.hasExtra(DRM_SCHEME_UUID_EXTRA) ? UUID.fromString(intent.getStringExtra(DRM_SCHEME_UUID_EXTRA)) : null;
            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
            if (drmSchemeUuid != null) {
                try {
                    drmSessionManager = buildDrmSessionManager(drmSchemeUuid, intent.getStringExtra(DRM_LICENSE_URL), intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES));
                } catch (UnsupportedDrmException e) {
                    String errorStringId = Util.SDK_INT < 18 ? "Protected content not supported on API levels below 18" : e.reason == 1 ? "This device does not support the required DRM scheme" : "An unknown DRM error occured";
                    Toast.makeText(this, errorStringId, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            Uri videoURI = Uri.parse(videoURL);

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "VideoMusicApp"));
            MediaSource videoSource = new ExtractorMediaSource(videoURI,
                    dataSourceFactory, new DefaultExtractorsFactory(), null, null);

            this.exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this, drmSessionManager, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER), this.trackSelector);
            exoPlayerView.setPlayer(exoPlayer);
            this.exoPlayer.addListener(this);
            exoPlayer.addListener(videoPlayerEventLogger);
            exoPlayer.prepare(videoSource);
            this.exoPlayer.setAudioDebugListener(this.videoPlayerEventLogger);
            this.exoPlayer.setVideoDebugListener(this.videoPlayerEventLogger);
            this.exoPlayer.setMetadataOutput(this.videoPlayerEventLogger);
            exoPlayer.seekTo(currentWindow, playbackPosition);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManager(UUID uuid, String licenseUrl, String[] keyRequestPropertiesArray) throws UnsupportedDrmException {
        if (Util.SDK_INT < 18) {
            return null;
        }
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl, buildHttpDataSourceFactory(false));
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i], keyRequestPropertiesArray[i + 1]);
            }
        }
        return new DefaultDrmSessionManager(uuid, FrameworkMediaDrm.newInstance(uuid), drmCallback, null, true);
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return ((AllVidApp) getApplication()).buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event) || this.exoPlayerView.dispatchMediaKeyEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onPause() {
        releasePlayer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        exoPlayer.setPlayWhenReady(autoPlay);
        super.onResume();
    }

    @Override
    protected void onStop() {
        releasePlayer();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        releasePlayer();
        finish();
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayerView.setPlayer(null);
            exoPlayer.stop();
            exoPlayer.release();
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            autoPlay = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (exoPlayer == null) {
            outState.putLong(PLAYBACK_POSITION, playbackPosition);
            outState.putInt(CURRENT_WINDOW_INDEX, currentWindow);
            outState.putBoolean(AUTOPLAY, autoPlay);
        }
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != 0) {
            return false;
        }
        for (Throwable cause = e.getSourceException(); cause != null; cause = cause.getCause()) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
        }
        return false;
    }

    private void onBrightnessSlide(float percent) {
        if (brightness < 0.0f) {
            brightness = getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.0f) {
                brightness = 0.5f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }

        query.id(R.id.app_video_brightness_box).visible();
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }
        query.id(R.id.app_video_brightness).text(((int) (lpa.screenBrightness * 100.0f)) + "%");
        getWindow().setAttributes(lpa);
    }

    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(3);
            if (volume < 0) {
                volume = 0;
            }
        }
        int index = ((int) (((float) mMaxVolume) * percent)) + volume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }
        audioManager.setStreamVolume(3, index, 0);
        int i = (int) (((((double) index) * 1.0d) / ((double) mMaxVolume)) * 100.0d);
        String s = i + "%";
        if (i == 0) {
            s = "off";
        }
        query.id(R.id.app_video_volume_icon).image(i == 0 ? R.drawable.sound_off : R.drawable.sound_on);
        query.id(R.id.app_video_brightness_box).gone();
        query.id(R.id.app_video_ratio_box).gone();
        query.id(R.id.app_video_volume_box).visible();
        query.id(R.id.app_video_volume).text(s).visible();
    }

    private void onProgressSlide(float percent) {
        long position = exoPlayerView.getPlayer().getCurrentPosition();
        long duration = exoPlayerView.getPlayer().getDuration();
        long delta = (long) (((float) Math.min(100000, duration - position)) * percent);
        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = ((int) delta) / 1000;
        if (showDelta != 0) {
            query.id(R.id.app_video_fastForward_box).visible();
            query.id(R.id.app_video_fastForward).text((showDelta > 0 ? "+" + showDelta : "" + showDelta) + "s");
            query.id(R.id.app_video_fastForward_target).text(generateTime(newPosition));
            progress_player.setMax(generateTimeInt(duration));
            progress_player.setProgress(generateTimeInt(newPosition));
        }
    }

    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        if (totalSeconds / 3600 > 0) {
            return String.format("%02d:%02d:%02d", Integer.valueOf(totalSeconds / 3600), Integer.valueOf(minutes), Integer.valueOf(seconds));
        }
        return String.format("%02d:%02d", Integer.valueOf(minutes), Integer.valueOf(seconds));
    }

    private int generateTimeInt(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0) {
            return (totalSeconds) + minutes * 60 + seconds;
        } else {
            return minutes * 60 + seconds;
        }
    }

    private void endGesture() {
        volume = -1;
        brightness = -1.0f;
        if (newPosition >= 0) {
            handler.removeMessages(3);
            handler.sendEmptyMessage(3);
        }
        handler.removeMessages(4);
        handler.sendEmptyMessageDelayed(4, 500);
    }
}
