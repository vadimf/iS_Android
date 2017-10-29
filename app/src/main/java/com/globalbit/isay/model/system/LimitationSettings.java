package com.globalbit.isay.model.system;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class LimitationSettings {

    @SerializedName("minLength")
    private int mMinLength;

    @SerializedName("maxLength")
    private int mMaxLength;

    @SerializedName("regex")
    private String mRegex;

    public int getMinLength() {
        return mMinLength;
    }

    public void setMinLength(int minLength) {
        mMinLength=minLength;
    }

    public int getMaxLength() {
        return mMaxLength;
    }

    public void setMaxLength(int maxLength) {
        mMaxLength=maxLength;
    }

    public String getRegex() {
        return mRegex;
    }

    public void setRegex(String regex) {
        mRegex=regex;
    }
}
