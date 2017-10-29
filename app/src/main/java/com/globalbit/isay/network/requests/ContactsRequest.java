package com.globalbit.isay.network.requests;

import com.globalbit.isay.model.Phone;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 29/10/2017.
 */

public class ContactsRequest {

    @SerializedName("phones")
    private ArrayList<Phone> mPhones;

    public ArrayList<Phone> getPhones() {
        return mPhones;
    }

    public void setPhones(ArrayList<Phone> phones) {
        mPhones=phones;
    }
}
