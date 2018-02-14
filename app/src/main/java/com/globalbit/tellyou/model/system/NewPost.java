package com.globalbit.tellyou.model.system;

import com.google.gson.annotations.SerializedName;

import okhttp3.MultipartBody;
import retrofit2.http.Multipart;

/**
 * Created by alex on 14/02/2018.
 */

public class NewPost {

    @SerializedName("text")
    private String mText;

    @SerializedName("duration")
    private int mDuration;

    @SerializedName("video")
    private MultipartBody.Part mVideo;

    @SerializedName("thumbnail")
    private MultipartBody.Part mThumbnail;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText=text;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration=duration;
    }

    public MultipartBody.Part getVideo() {
        return mVideo;
    }

    public void setVideo(MultipartBody.Part video) {
        mVideo=video;
    }

    public MultipartBody.Part getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(MultipartBody.Part thumbnail) {
        mThumbnail=thumbnail;
    }
}
