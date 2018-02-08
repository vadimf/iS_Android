package com.globalbit.tellyou.network.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 26/11/2017.
 */

public class UsernameExistResponse extends BaseResponse {

    @SerializedName("exists")
    private boolean mIsExists;

    private String mUsername;

    public boolean isExists() {
        return mIsExists;
    }

    public void setExists(boolean exists) {
        mIsExists=exists;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername=username;
    }
}
