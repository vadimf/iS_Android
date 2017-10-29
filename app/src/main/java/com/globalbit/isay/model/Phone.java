package com.globalbit.isay.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Phone {

    @SerializedName("country")
    private String mCountryCode;

    @SerializedName("area")
    private String mAreaCode;

    @SerializedName("number")
    private String mNumber;

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode=countryCode;
    }

    public String getAreaCode() {
        return mAreaCode;
    }

    public void setAreaCode(String areaCode) {
        mAreaCode=areaCode;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber=number;
    }
}
