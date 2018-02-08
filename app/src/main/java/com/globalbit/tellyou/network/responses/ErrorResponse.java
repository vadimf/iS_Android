package com.globalbit.tellyou.network.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 28/11/2017.
 */

public class ErrorResponse {

    @SerializedName("text")
    private BaseResponse mError;

    public BaseResponse getError() {
        return mError;
    }

    public void setError(BaseResponse error) {
        mError=error;
    }
}
