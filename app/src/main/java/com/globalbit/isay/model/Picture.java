package com.globalbit.isay.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Picture {

    @SerializedName("url")
    private String mUrl;

    @SerializedName("thumbnail")
    private String mThumbnail;

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
}
