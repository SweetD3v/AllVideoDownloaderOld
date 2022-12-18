# Add *one* of the following rules to your Proguard configuration file.
# Alternatively, you can annotate classes and class members with @androidx.annotation.Keep

# keep everything in this package from being removed or renamed
-keep class com.video.tools.videodownloader.models.** { *; }

# keep everything in this package from being renamed only
-keepnames class com.video.tools.videodownloader.models.** { *; }