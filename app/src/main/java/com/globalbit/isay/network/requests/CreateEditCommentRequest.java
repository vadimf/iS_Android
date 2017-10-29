package com.globalbit.isay.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class CreateEditCommentRequest {

    @SerializedName("text")
    private String mText;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText=text;
    }
}
