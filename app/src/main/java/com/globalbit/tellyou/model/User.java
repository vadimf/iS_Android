package com.globalbit.tellyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by alex on 29/10/2017.
 */

public class User implements Parcelable {

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
    private Date mCreatedAt;

    private boolean mIsSelected;

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

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        mCreatedAt=createdAt;
    }

    public User() {}

    public User(Parcel in) {
        mUsername=in.readString();
        mEmail=in.readString();
        mPhone=in.readParcelable(Phone.class.getClassLoader());
        mProfile=in.readParcelable(Profile.class.getClassLoader());
        mFollowing=in.readInt();
        mFollowers=in.readInt();
        mIsFollowing=in.readByte() != 0;
        mCreatedAt=new Date(in.readLong());
        mIsSelected=in.readByte() != 0;
    }

    public static final Creator<User> CREATOR=new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUsername);
        dest.writeString(mEmail);
        dest.writeParcelable(mPhone, flags);
        dest.writeParcelable(mProfile, flags);
        dest.writeInt(mFollowing);
        dest.writeInt(mFollowers);
        dest.writeByte((byte) (mIsFollowing ? 1 : 0));
        dest.writeLong(mCreatedAt.getTime());
        dest.writeByte((byte) (mIsSelected ? 1 : 0));
    }

    public static void logout() {
        SharedPrefsUtils.setAuthorization(null);
        SharedPrefsUtils.setUserDetails(null);
        SharedPrefsUtils.setFCMToken(null);
        SharedPrefsUtils.setFacebookToken(null);
    }

    public static void unauthorized() {
        User.logout();
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected=selected;
    }
}
