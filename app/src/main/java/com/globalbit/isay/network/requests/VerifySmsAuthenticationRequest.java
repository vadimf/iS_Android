package com.globalbit.isay.network.requests;

import com.globalbit.isay.model.Phone;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class VerifySmsAuthenticationRequest {

    @SerializedName("phone")
    private Phone mPhone;

    @SerializedName("code")
    private String mCode;

    public Phone getPhone() {
        return mPhone;
    }

    public void setPhone(Phone phone) {
        mPhone=phone;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode=code;
    }
}
