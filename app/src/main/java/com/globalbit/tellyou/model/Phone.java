package com.globalbit.tellyou.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Phone implements Parcelable {

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

    public Phone() {}

    public Phone(Parcel in) {
        mCountryCode=in.readString();
        mAreaCode=in.readString();
        mNumber=in.readString();
    }

    public static final Creator<Phone> CREATOR=new Creator<Phone>() {
        @Override
        public Phone createFromParcel(Parcel in) {
            return new Phone(in);
        }

        @Override
        public Phone[] newArray(int size) {
            return new Phone[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCountryCode);
        dest.writeString(mAreaCode);
        dest.writeString(mNumber);
    }
}
