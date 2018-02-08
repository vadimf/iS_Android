package com.globalbit.tellyou.network.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class BaseResponse {

    @SerializedName("errorCode")
    private int mErrorCode;

    @SerializedName("errorDescription")
    private String mErrorDescription;

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int errorCode) {
        mErrorCode=errorCode;
    }

    public String getErrorDescription() {
        return mErrorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        mErrorDescription=errorDescription;
    }
}
