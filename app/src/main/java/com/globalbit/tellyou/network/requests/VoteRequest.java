package com.globalbit.tellyou.network.requests;

import com.globalbit.tellyou.model.PostOption;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 20/11/2017.
 */

public class VoteRequest {

    @SerializedName("option")
    private PostOption mOption;


    public PostOption getOption() {
        return mOption;
    }

    public void setOption(PostOption option) {
        mOption=option;
    }
}
