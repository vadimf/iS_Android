package com.globalbit.tellyou.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by alex on 29/10/2017.
 */

public class BasePostComment {

    @SerializedName("id")
    private String mId;

    @SerializedName("createdAt")
    private Date mCreatedAt;

    @SerializedName("creator")
    private User mUser;

    @SerializedName("video")
    private Video mVideo;

    @SerializedName("views")
    private int mViews;

    @SerializedName("uniqueViews")
    private int mUniqueViews;

    @SerializedName("comments")
    private int mComments;

    @SerializedName("text")
    private String mText;

    @SerializedName("viewed")
    private boolean mIsViewed;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId=id;
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

    public Video getVideo() {
        return mVideo;
    }

    public void setVideo(Video video) {
        mVideo=video;
    }

    public int getViews() {
        return mViews;
    }

    public void setViews(int views) {
        mViews=views;
    }

    public int getUniqueViews() {
        return mUniqueViews;
    }

    public void setUniqueViews(int uniqueViews) {
        mUniqueViews=uniqueViews;
    }

    public int getComments() {
        return mComments;
    }

    public void setComments(int comments) {
        mComments=comments;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText=text;
    }

    public boolean isViewed() {
        return mIsViewed;
    }

    public void setViewed(boolean viewed) {
        mIsViewed=viewed;
    }
}
