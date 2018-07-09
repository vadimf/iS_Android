package com.globalbit.tellyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alex on 29/10/2017.
 */

public class BasePostComment implements Parcelable {

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

    @SerializedName("dailyViews")
    private int mDailyViews;

    @SerializedName("comments")
    private int mComments;

    @SerializedName("text")
    private String mText;

    @SerializedName("tags")
    private ArrayList<String> mTags=new ArrayList<>();

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
        return mDailyViews;
    } //Currently using daily views

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

    public BasePostComment() {}

    public BasePostComment(Parcel in) {
        mId=in.readString();
        mCreatedAt=new Date(in.readLong());
        mUser=in.readParcelable(User.class.getClassLoader());
        mVideo=in.readParcelable(Video.class.getClassLoader());
        mViews=in.readInt();
        mUniqueViews=in.readInt();
        mComments=in.readInt();
        mText=in.readString();
        in.readList(mTags, String.class.getClassLoader());
        mIsViewed=in.readByte() != 0;

    }

    public static final Creator<BasePostComment> CREATOR=new Creator<BasePostComment>() {
        @Override
        public BasePostComment createFromParcel(Parcel in) {
            return new BasePostComment(in);
        }

        @Override
        public BasePostComment[] newArray(int size) {
            return new BasePostComment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeLong(mCreatedAt.getTime());
        dest.writeParcelable(mUser, flags);
        dest.writeParcelable(mVideo, flags);
        dest.writeInt(mViews);
        dest.writeInt(mUniqueViews);
        dest.writeInt(mComments);
        dest.writeString(mText);
        dest.writeList(mTags);
        dest.writeByte((byte) (mIsViewed ? 1 : 0));
    }

    public ArrayList<String> getTags() {
        return mTags;
    }

    public void setTags(ArrayList<String> tags) {
        mTags=tags;
    }

    public int getDailyViews() {
        return mDailyViews;
    }

    public void setDailyViews(int dailyViews) {
        mDailyViews=dailyViews;
    }
}
