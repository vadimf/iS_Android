package com.globalbit.tellyou.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by alex on 29/10/2017.
 */

public class Comment {

    @SerializedName("id")
    private String mId;

    @SerializedName("text")
    private String mText;

    @SerializedName("createdAt")
    private Date mCreatedAt;

    @SerializedName("creator")
    private User mUser;

    private boolean mIsMarked=false;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId=id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText=text;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt=createdAt;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser=user;
    }

    public boolean isMarked() {
        return mIsMarked;
    }

    public void setMarked(boolean marked) {
        mIsMarked=marked;
    }
}
