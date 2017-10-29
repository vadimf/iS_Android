package com.globalbit.isay.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class PushNotificationTokenRequest {

    @SerializedName("token")
    private String mToken;

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken=token;
    }
}
