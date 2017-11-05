package com.globalbit.isay.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 05/11/2017.
 */

public class FacebookRequest {

    @SerializedName("facebookToken")
    private String mFacebookToken;

    public String getFacebookToken() {
        return mFacebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        mFacebookToken=facebookToken;
    }
}
