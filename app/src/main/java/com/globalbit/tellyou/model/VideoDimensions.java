package com.globalbit.tellyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class VideoDimensions implements Parcelable {

    @SerializedName("width")
    private int mWidth;

    @SerializedName("height")
    private int mHeight;

    @SerializedName("orientation")
    private int mOrientation; //0 - Unknown, 1 - Square, 2 - Landscape, 3 - Portrait

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth=width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight=height;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int orientation) {
        mOrientation=orientation;
    }

    public VideoDimensions(Parcel in) {
        mWidth=in.readInt();
        mHeight=in.readInt();
        mOrientation=in.readInt();
    }

    public static final Parcelable.Creator<VideoDimensions> CREATOR=new Parcelable.Creator<VideoDimensions>() {
        @Override
        public VideoDimensions createFromParcel(Parcel in) {
            return new VideoDimensions(in);
        }

        @Override
        public VideoDimensions[] newArray(int size) {
            return new VideoDimensions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
        dest.writeInt(mOrientation);
    }
}
