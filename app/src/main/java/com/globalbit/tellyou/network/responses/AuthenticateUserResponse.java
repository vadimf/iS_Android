package com.globalbit.tellyou.network.responses;

import com.globalbit.tellyou.model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class AuthenticateUserResponse extends BaseResponse {

    @SerializedName("user")
    private User mUser;

    @SerializedName("token")
    private String mAuth;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser=user;
    }

    public String getAuth() {
        return mAuth;
    }

    public void setAuth(String auth) {
        mAuth=auth;
    }
}
