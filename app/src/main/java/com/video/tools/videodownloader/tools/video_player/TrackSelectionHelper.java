package com.video.tools.videodownloader.tools.video_player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.video.tools.videodownloader.R;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;

public final class TrackSelectionHelper implements View.OnClickListener, DialogInterface.OnClickListener {
    private static final TrackSelection.Factory FIXED_FACTORY = new FixedTrackSelection.Factory();
    private static final TrackSelection.Factory RANDOM_FACTORY = new RandomTrackSelection.Factory();
    private final TrackSelection.Factory adaptiveTrackSelectionFactory;
    private CheckedTextView defaultView;
    private CheckedTextView disableView;
    private CheckedTextView enableRandomAdaptationView;
    private boolean isDisabled;
//    private MappingTrackSelector.SelectionOverride override;
    private int rendererIndex;
    private final MappingTrackSelector selector;
    private TrackGroupArray trackGroups;
    private boolean[] trackGroupsAdaptive;
    private MappedTrackInfo trackInfo;
    private CheckedTextView[][] trackViews;

    public TrackSelectionHelper(MappingTrackSelector selector, TrackSelection.Factory adaptiveTrackSelectionFactory) {
        this.selector = selector;
        this.adaptiveTrackSelectionFactory = adaptiveTrackSelectionFactory;
    }

    public void showSelectionDialog(Activity activity, CharSequence title, MappedTrackInfo trackInfo, int rendererIndex) {
        this.trackInfo = trackInfo;
        this.rendererIndex = rendererIndex;
        this.trackGroups = trackInfo.getTrackGroups(rendererIndex);
        this.trackGroupsAdaptive = new boolean[this.trackGroups.length];
        int i = 0;
        while (i < this.trackGroups.length) {
            boolean z;
            boolean[] zArr = this.trackGroupsAdaptive;
            z = this.adaptiveTrackSelectionFactory != null && trackInfo.getAdaptiveSupport(rendererIndex, i, false) != 0 && this.trackGroups.get(i).length > 1;
            zArr[i] = z;
            i++;
        }
//        this.isDisabled = this.selector.getRendererDisabled(rendererIndex);
//        this.override = this.selector.getSelectionOverride(rendererIndex, this.trackGroups);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setView(buildView(builder.getContext()))
                .setPositiveButton("Ok", this)
                .setNegativeButton("Cancel", null).create().show();
    }

    @SuppressLint("InflateParams")
    private View buildView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.track_selection_dialog, null);
        ViewGroup root = view.findViewById(R.id.root);

        TypedArray attributeArray = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.selectableItemBackground});
        int selectableItemBackgroundResourceId = attributeArray.getResourceId(0, 0);
        attributeArray.recycle();

        disableView = (CheckedTextView) inflater.inflate(
                android.R.layout.simple_list_item_single_choice, root, false);
        disableView.setBackgroundResource(selectableItemBackgroundResourceId);
        disableView.setText("Disabled");
        disableView.setFocusable(true);
        disableView.setOnClickListener(this);
        root.addView(disableView);

        defaultView = (CheckedTextView) inflater.inflate(
                android.R.layout.simple_list_item_single_choice, root, false);
        defaultView.setBackgroundResource(selectableItemBackgroundResourceId);
        defaultView.setText("Default");
        defaultView.setFocusable(true);
        defaultView.setOnClickListener(this);
        root.addView(defaultView);

        boolean haveSupportedTracks = false;
        boolean haveAdaptiveTracks = false;
        trackViews = new CheckedTextView[trackGroups.length][];
        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
            TrackGroup group = trackGroups.get(groupIndex);
            boolean groupIsAdaptive = trackGroupsAdaptive[groupIndex];
            haveAdaptiveTracks |= groupIsAdaptive;
            trackViews[groupIndex] = new CheckedTextView[group.length];
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                int trackViewLayoutId = groupIsAdaptive ? android.R.layout.simple_list_item_multiple_choice
                        : android.R.layout.simple_list_item_single_choice;
                CheckedTextView trackView = (CheckedTextView) inflater.inflate(
                        trackViewLayoutId, root, false);
                trackView.setBackgroundResource(selectableItemBackgroundResourceId);
                trackView.setText(DemoUtil.buildTrackName(group.getFormat(trackIndex)));
                if (trackInfo.getTrackFormatSupport(rendererIndex, groupIndex, trackIndex) == RendererCapabilities.FORMAT_HANDLED) {
                    trackView.setFocusable(true);
                    trackView.setTag(Pair.create(groupIndex, trackIndex));
                    trackView.setOnClickListener(this);
                    haveSupportedTracks = true;
                } else {
                    trackView.setFocusable(false);
                    trackView.setEnabled(false);
                }
                trackViews[groupIndex][trackIndex] = trackView;
                root.addView(trackView);
            }
        }

        if (!haveSupportedTracks) {
            defaultView.setText("Default (none)");
        } else if (haveAdaptiveTracks) {
            enableRandomAdaptationView = (CheckedTextView) inflater.inflate(
                    android.R.layout.simple_list_item_multiple_choice, root, false);
            enableRandomAdaptationView.setBackgroundResource(selectableItemBackgroundResourceId);
            enableRandomAdaptationView.setText("Enable random adaption");
            enableRandomAdaptationView.setOnClickListener(this);
            root.addView(enableRandomAdaptationView);
        }

//        updateViews();
        return view;
    }


//    private void updateViews() {
//        boolean z;
//        boolean z2 = true;
//        this.disableView.setChecked(this.isDisabled);
//        CheckedTextView checkedTextView = this.defaultView;
//        z = !this.isDisabled && this.override == null;
//        checkedTextView.setChecked(z);
//        int i = 0;
//        while (i < this.trackViews.length) {
//            int j = 0;
//            while (j < this.trackViews[i].length) {
//                checkedTextView = this.trackViews[i][j];
//                z = this.override != null && this.override.groupIndex == i && this.override.containsTrack(j);
//                checkedTextView.setChecked(z);
//                j++;
//            }
//            i++;
//        }
//        if (this.enableRandomAdaptationView != null) {
//            boolean enableView = !this.isDisabled && this.override != null && this.override.length > 1;
//            this.enableRandomAdaptationView.setEnabled(enableView);
//            this.enableRandomAdaptationView.setFocusable(enableView);
//            if (enableView) {
//                CheckedTextView checkedTextView2 = this.enableRandomAdaptationView;
//                if (this.isDisabled || !(this.override.factory instanceof RandomTrackSelection.Factory)) {
//                    z2 = false;
//                }
//                checkedTextView2.setChecked(z2);
//            }
//        }
//    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
//        this.selector.setRendererDisabled(this.rendererIndex, this.isDisabled);
//        if (this.override != null) {
//            this.selector.setSelectionOverride(this.rendererIndex, this.trackGroups, this.override);
//        } else {
//            this.selector.clearSelectionOverrides(this.rendererIndex);
//        }
    }

    public void onClick(View view) {
        if (view == this.disableView) {
            this.isDisabled = true;
//            this.override = null;
        } else if (view == this.defaultView) {
            this.isDisabled = false;
//            this.override = null;
        } else if (view == this.enableRandomAdaptationView) {
//            setOverride(this.override.groupIndex, this.override.tracks, !this.enableRandomAdaptationView.isChecked());
        } else {
//            this.isDisabled = false;
//            Pair<Integer, Integer> tag = (Pair) view.getTag();
//            int groupIndex = tag.first.intValue();
//            int trackIndex = tag.second.intValue();
//            if (this.trackGroupsAdaptive[groupIndex] && this.override != null && this.override.groupIndex == groupIndex) {
//                boolean isEnabled = ((CheckedTextView) view).isChecked();
//                int overrideLength = this.override.length;
//                if (!isEnabled) {
//                    setOverride(groupIndex, getTracksAdding(this.override, trackIndex), this.enableRandomAdaptationView.isChecked());
//                } else if (overrideLength == 1) {
//                    this.override = null;
//                    this.isDisabled = true;
//                } else {
//                    setOverride(groupIndex, getTracksRemoving(this.override, trackIndex), this.enableRandomAdaptationView.isChecked());
//                }
//            } else {
//                this.override = new MappingTrackSelector.SelectionOverride(FIXED_FACTORY, groupIndex, trackIndex);
//            }
        }
//        updateViews();
    }

//    private void setOverride(int group, int[] tracks, boolean enableRandomAdaptation) {
//        TrackSelection.Factory factory = tracks.length == 1 ? FIXED_FACTORY : enableRandomAdaptation ? RANDOM_FACTORY : this.adaptiveTrackSelectionFactory;
//        this.override = new MappingTrackSelector.SelectionOverride(factory, group, tracks);
//    }

//    private static int[] getTracksAdding(MappingTrackSelector.SelectionOverride override, int addedTrack) {
//        int[] tracks = override.tracks;
//        tracks = Arrays.copyOf(tracks, tracks.length + 1);
//        tracks[tracks.length - 2] = addedTrack;
//        return tracks;
//    }
//
//    private static int[] getTracksRemoving(MappingTrackSelector.SelectionOverride override, int removedTrack) {
//        int[] tracks = new int[(override.length - 1)];
//        int trackCount = 0;
//        for (int i = 0; i < tracks.length + 1; i++) {
//            int track = override.tracks[i];
//            if (track != removedTrack) {
//                int trackCount2 = trackCount + 1;
//                tracks[trackCount] = track;
//                trackCount = trackCount2;
//            }
//        }
//        return tracks;
//    }
}
