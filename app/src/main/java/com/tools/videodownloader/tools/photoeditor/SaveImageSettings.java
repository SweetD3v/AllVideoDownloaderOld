package com.tools.videodownloader.tools.photoeditor;

public class SaveImageSettings {
    private boolean isClearViewsEnabled;
    private boolean isTransparencyEnabled;


    public boolean isTransparencyEnabled() {
        return this.isTransparencyEnabled;
    }


    public boolean isClearViewsEnabled() {
        return this.isClearViewsEnabled;
    }

    private SaveImageSettings(Builder builder) {
        this.isClearViewsEnabled = builder.isClearViewsEnabled;
        this.isTransparencyEnabled = builder.isTransparencyEnabled;
    }

    public static class Builder {

        public boolean isClearViewsEnabled = true;

        public boolean isTransparencyEnabled = true;


        public SaveImageSettings build() {
            return new SaveImageSettings(this);
        }
    }
}
