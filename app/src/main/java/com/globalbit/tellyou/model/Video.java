package com.globalbit.tellyou.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Video {

    @SerializedName("url")
    private String mUrl;

    @SerializedName("thumbnail")
    private String mThumbnail;

    @SerializedName("duration")
    private int mDuration;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl=url;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail=thumbnail;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration=duration;
    }
}
