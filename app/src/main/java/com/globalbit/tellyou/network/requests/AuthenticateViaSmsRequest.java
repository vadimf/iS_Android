package com.globalbit.tellyou.network.requests;

import com.globalbit.tellyou.model.Phone;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class AuthenticateViaSmsRequest {

    @SerializedName("phone")
    private Phone mPhone;

    public Phone getPhone() {
        return mPhone;
    }

    public void setPhone(Phone phone) {
        mPhone=phone;
    }
}
