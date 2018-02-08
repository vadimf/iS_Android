package com.globalbit.tellyou.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class VerifySmsAuthenticationRequest {

    @SerializedName("code")
    private String mCode;

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode=code;
    }
}
