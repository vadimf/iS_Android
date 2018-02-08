package com.globalbit.tellyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 14/11/2017.
 */

public class PostPrivacy implements Parcelable {

    @SerializedName("type")
    private int mType;

    @SerializedName("usernames")
    private ArrayList<String> mUsernames;

    @SerializedName("specific")
    private ArrayList<User> mSpecific;

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType=type;
    }

    public ArrayList<User> getSpecific() {
        return mSpecific;
    }

    public void setSpecific(ArrayList<User> specific) {
        mSpecific=specific;
    }

    public ArrayList<String> getUsernames() {
        return mUsernames;
    }

    public void setUsernames(ArrayList<String> usernames) {
        mUsernames=usernames;
    }

    public PostPrivacy() {}

    public PostPrivacy(Parcel in) {
        mType=in.readInt();
        mUsernames=new ArrayList<>();
        in.readList(mUsernames, String.class.getClassLoader());
        mSpecific=new ArrayList<>();
        in.readList(mSpecific, User.class.getClassLoader());
    }

    public static final Creator<PostPrivacy> CREATOR=new Creator<PostPrivacy>() {
        @Override
        public PostPrivacy createFromParcel(Parcel in) {
            return new PostPrivacy(in);
        }

        @Override
        public PostPrivacy[] newArray(int size) {
            return new PostPrivacy[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mType);
        dest.writeList(mUsernames);
        dest.writeList(mSpecific);
    }
}
