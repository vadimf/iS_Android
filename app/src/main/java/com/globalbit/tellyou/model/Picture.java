package com.globalbit.tellyou.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Picture implements Parcelable {

    @SerializedName("url")
    private String mUrl;

    @SerializedName("thumbnail")
    private String mThumbnail;

    @SerializedName("upload")
    private String mUpload;

    private Uri mUri;

    private String mCaption;

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

    public Picture() {}

    public Picture(Parcel in) {
        mUrl=in.readString();
        mThumbnail=in.readString();
        mUpload=in.readString();
    }

    public static final Creator<Picture> CREATOR=new Creator<Picture>() {
        @Override
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        @Override
        public Picture[] newArray(int size) {
            return new Picture[size];
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
        dest.writeString(mUpload);
    }

    public String getUpload() {
        return mUpload;
    }

    public void setUpload(String upload) {
        mUpload=upload;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri=uri;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption=caption;
    }
}
