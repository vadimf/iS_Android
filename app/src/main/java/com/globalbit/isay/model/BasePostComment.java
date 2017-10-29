package com.globalbit.isay.model;

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
    private User mCreator;

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

    public User getCreator() {
        return mCreator;
    }

    public void setCreator(User creator) {
        mCreator=creator;
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
}
