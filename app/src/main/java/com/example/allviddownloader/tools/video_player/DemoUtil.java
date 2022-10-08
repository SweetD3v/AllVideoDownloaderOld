package com.example.allviddownloader.tools.video_player;

import android.text.TextUtils;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.MimeTypes;

import java.util.Locale;

public final class DemoUtil {
    public static final String VID_ORIENTATION = "vid_orientation";
    public static String buildTrackName(Format format) {
        String trackName;
        if (MimeTypes.isVideo(format.sampleMimeType)) {
            trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(buildResolutionString(format), buildBitrateString(format)), buildTrackIdString(format)), buildSampleMimeTypeString(format));
        } else if (MimeTypes.isAudio(format.sampleMimeType)) {
            trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(joinWithSeparator(buildLanguageString(format), buildAudioPropertyString(format)), buildBitrateString(format)), buildTrackIdString(format)), buildSampleMimeTypeString(format));
        } else {
            trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(buildLanguageString(format), buildBitrateString(format)), buildTrackIdString(format)), buildSampleMimeTypeString(format));
        }
        return trackName.length() == 0 ? "unknown" : trackName;
    }

    private static String buildResolutionString(Format format) {
        return (format.width == -1 || format.height == -1) ? "" : format.width + "x" + format.height;
    }

    private static String buildAudioPropertyString(Format format) {
        return (format.channelCount == -1 || format.sampleRate == -1) ? "" : format.channelCount + "ch, " + format.sampleRate + "Hz";
    }

    private static String buildLanguageString(Format format) {
        return (TextUtils.isEmpty(format.language) || "und".equals(format.language)) ? "" : format.language;
    }

    private static String buildBitrateString(Format format) {
        if (format.bitrate == -1) {
            return "";
        }
        return String.format(Locale.US, "%.2fMbit", Float.valueOf(((float) format.bitrate) / 1000000.0f));
    }

    private static String joinWithSeparator(String first, String second) {
        if (first.length() == 0) {
            return second;
        }
        return second.length() == 0 ? first : first + ", " + second;
    }

    private static String buildTrackIdString(Format format) {
        return format.id == null ? "" : "id:" + format.id;
    }

    private static String buildSampleMimeTypeString(Format format) {
        return format.sampleMimeType == null ? "" : format.sampleMimeType;
    }

    private DemoUtil() {
    }
}