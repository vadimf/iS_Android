package com.globalbit.isay.network.requests;

import com.globalbit.isay.model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class UserRequest {

    @SerializedName("user")
    private User mUser;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser=user;
    }
}
