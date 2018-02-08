package com.globalbit.tellyou.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 05/11/2017.
 */

public class ForgotPasswordRequest {

    @SerializedName("email")
    private String mEmail;

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail=email;
    }
}
