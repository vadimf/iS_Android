package com.globalbit.tellyou.model;

import android.net.Uri;

/**
 * Created by alex on 07/11/2017.
 */

public class Contact {
    private long mId;
    private String mName;
    private String mEmail;
    private String mPhone;
    private Uri mPhoto;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId=id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName=name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail=email;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone=phone;
    }

    public Uri getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Uri photo) {
        mPhoto=photo;
    }
}
