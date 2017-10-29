package com.globalbit.isay.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class CreateEditPostRequest {

    @SerializedName("video")
    private String mVideo;  //TODO change to proper video upload format and send as multi part

    @SerializedName("text")
    private String mText;

    public String getVideo() {
        return mVideo;
    }

    public void setVideo(String video) {
        mVideo=video;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText=text;
    }
}
