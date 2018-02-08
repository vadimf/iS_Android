package com.globalbit.tellyou.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 05/11/2017.
 */

public class SignInUpRequest {

    @SerializedName("email")
    private String mEmail;

    @SerializedName("password")
    private String mPassword;

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail=email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword=password;
    }
}
