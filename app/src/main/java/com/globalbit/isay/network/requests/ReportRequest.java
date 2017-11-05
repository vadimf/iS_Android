package com.globalbit.isay.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 05/11/2017.
 */

public class ReportRequest {

    @SerializedName("reason")
    private int mReason;

    public int getReason() {
        return mReason;
    }

    public void setReason(int reason) {
        mReason=reason;
    }
}
