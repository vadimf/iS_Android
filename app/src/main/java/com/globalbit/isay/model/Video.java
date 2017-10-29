package com.globalbit.isay.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 29/10/2017.
 */

public class Video {

    @SerializedName("url")
    private String mUrl;

    @SerializedName("thumbnails")
    private ArrayList<String> mThumbnails;

    @SerializedName("duration")
    private int mDuration;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl=url;
    }

    public ArrayList<String> getThumbnails() {
        return mThumbnails;
    }

    public void setThumbnails(ArrayList<String> thumbnails) {
        mThumbnails=thumbnails;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration=duration;
    }
}
