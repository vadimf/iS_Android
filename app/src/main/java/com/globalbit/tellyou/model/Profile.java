package com.globalbit.tellyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by alex on 29/10/2017.
 */

public class Profile implements Parcelable {

    @SerializedName("firstName")
    private String mFirstName;

    @SerializedName("lastName")
    private String mLastName;

    @SerializedName("picture")
    private Picture mPicture;

    @SerializedName("bio")
    private String mBio;

    @SerializedName("website")
    private String mWebsite;

    @SerializedName("birthday")
    private String mBirthday;

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName=firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName=lastName;
    }

    public Picture getPicture() {
        return mPicture;
    }

    public void setPicture(Picture picture) {
        mPicture=picture;
    }

    public String getBio() {
        return mBio;
    }

    public void setBio(String bio) {
        mBio=bio;
    }

    public Profile() {}

    public Profile(Parcel in) {
        mFirstName=in.readString();
        mLastName=in.readString();
        mPicture=in.readParcelable(Picture.class.getClassLoader());
        mBio=in.readString();
        mBirthday=in.readString();
    }

    public static final Creator<Profile> CREATOR=new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeParcelable(mPicture, flags);
        dest.writeString(mBio);
        dest.writeString(mBirthday);
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite=website;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public void setBirthday(String birthday) {
        mBirthday=birthday;
    }
}
