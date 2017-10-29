package com.globalbit.isay.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class User {

    @SerializedName("username")
    private String mUsername;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("phone")
    private Phone mPhone;

    @SerializedName("profile")
    private Profile mProfile;

    @SerializedName("following")
    private int mFollowing;

    @SerializedName("followers")
    private int mFollowers;

    @SerializedName("isFollowing")
    private boolean mIsFollowing;

    @SerializedName("createdAt")
    private String mCreatedAt;

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername=username;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail=email;
    }

    public Phone getPhone() {
        return mPhone;
    }

    public void setPhone(Phone phone) {
        mPhone=phone;
    }

    public Profile getProfile() {
        return mProfile;
    }

    public void setProfile(Profile profile) {
        mProfile=profile;
    }

    public int getFollowing() {
        return mFollowing;
    }

    public void setFollowing(int following) {
        mFollowing=following;
    }

    public int getFollowers() {
        return mFollowers;
    }

    public void setFollowers(int followers) {
        mFollowers=followers;
    }

    public boolean isFollowing() {
        return mIsFollowing;
    }

    public void setFollowing(boolean following) {
        mIsFollowing=following;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt=createdAt;
    }

}
