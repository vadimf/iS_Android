package com.globalbit.tellyou.model.system;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Validation {

    @SerializedName("username")
    private LimitationSettings mUsername;

    @SerializedName("firstName")
    private LimitationSettings mFirstName;

    @SerializedName("lastName")
    private LimitationSettings mLastName;

    @SerializedName("bio")
    private LimitationSettings mBio;

    @SerializedName("postText")
    private LimitationSettings mPostText;

    public LimitationSettings getUsername() {
        return mUsername;
    }

    public void setUsername(LimitationSettings username) {
        mUsername=username;
    }

    public LimitationSettings getFirstName() {
        return mFirstName;
    }

    public void setFirstName(LimitationSettings firstName) {
        mFirstName=firstName;
    }

    public LimitationSettings getLastName() {
        return mLastName;
    }

    public void setLastName(LimitationSettings lastName) {
        mLastName=lastName;
    }

    public LimitationSettings getBio() {
        return mBio;
    }

    public void setBio(LimitationSettings bio) {
        mBio=bio;
    }

    public LimitationSettings getPostText() {
        return mPostText;
    }

    public void setPostText(LimitationSettings postText) {
        mPostText=postText;
    }
}
