package com.globalbit.tellyou.network.requests;

import com.globalbit.tellyou.model.Phone;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 29/10/2017.
 */

public class ContactsRequest {

    @SerializedName("emails")
    private ArrayList<String> mEmails;

    @SerializedName("phones")
    private ArrayList<Phone> mPhones;

    public ArrayList<Phone> getPhones() {
        return mPhones;
    }

    public void setPhones(ArrayList<Phone> phones) {
        mPhones=phones;
    }

    public ArrayList<String> getEmails() {
        return mEmails;
    }

    public void setEmails(ArrayList<String> emails) {
        mEmails=emails;
    }
}
