package com.globalbit.isay.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Profile {

    @SerializedName("firstName")
    private String mFirstName;

    @SerializedName("lastName")
    private String mLastName;

    @SerializedName("picture")
    private Picture mPicture;

    @SerializedName("bio")
    private String mBio;

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
}
