package com.globalbit.tellyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 14/11/2017.
 */

public class PostOption implements Parcelable {

    @SerializedName("id")
    private String mId;

    @SerializedName("text")
    private String mText;

    @SerializedName("image")
    private Picture mImage;

    @SerializedName("votes")
    private int mVotes;

    @SerializedName("voted")
    private boolean mVoted;

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

    public Picture getImage() {
        return mImage;
    }

    public void setImage(Picture image) {
        mImage=image;
    }

    public int getVotes() {
        return mVotes;
    }

    public void setVotes(int votes) {
        mVotes=votes;
    }

    public boolean isVoted() {
        return mVoted;
    }

    public void setVoted(boolean voted) {
        mVoted=voted;
    }

    public PostOption() {}

    public PostOption(Parcel in) {
        mId=in.readString();
        mText=in.readString();
        mImage=in.readParcelable(Picture.class.getClassLoader());
        mVotes=in.readInt();
        mVoted=in.readByte() != 0;
    }

    public static final Creator<PostOption> CREATOR=new Creator<PostOption>() {
        @Override
        public PostOption createFromParcel(Parcel in) {
            return new PostOption(in);
        }

        @Override
        public PostOption[] newArray(int size) {
            return new PostOption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mText);
        dest.writeParcelable(mImage, flags);
        dest.writeInt(mVotes);
        dest.writeByte((byte) (mVoted ? 1 : 0));
    }
}
