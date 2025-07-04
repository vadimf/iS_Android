package com.globalbit.tellyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Video implements Parcelable{

    @SerializedName("url")
    private String mUrl;

    @SerializedName("sample")
    private String mSample;

    @SerializedName("gif")
    private String mGif;

    @SerializedName("thumbnail")
    private String mThumbnail;

    @SerializedName("duration")
    private int mDuration;

    @SerializedName("dimensions")
    private VideoDimensions mVideoDimensions;

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

    public Video() {}

    public Video(Parcel in) {
        mUrl=in.readString();
        mThumbnail=in.readString();
        mDuration=in.readInt();
        mVideoDimensions=in.readParcelable(VideoDimensions.class.getClassLoader());
    }

    public static final Creator<Video> CREATOR=new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUrl);
        dest.writeString(mThumbnail);
        dest.writeInt(mDuration);
        dest.writeParcelable(mVideoDimensions, flags);
    }

    public String getSample() {
        return mSample;
    }

    public void setSample(String sample) {
        mSample=sample;
    }

    public String getGif() {
        return mGif;
    }

    public void setGif(String gif) {
        mGif=gif;
    }

    public VideoDimensions getVideoDimensions() {
        return mVideoDimensions;
    }

    public void setVideoDimensions(VideoDimensions videoDimensions) {
        mVideoDimensions=videoDimensions;
    }
}
