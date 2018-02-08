package com.globalbit.tellyou.model.system;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class SystemPreferences {

    @SerializedName("pages")
    private Pages mPages;

    @SerializedName("confirmationCodeLength")
    private int mConfirmationCodeLength;

    @SerializedName("validations")
    private Validation mValidations;

    public Pages getPages() {
        return mPages;
    }

    public void setPages(Pages pages) {
        mPages=pages;
    }

    public int getConfirmationCodeLength() {
        return mConfirmationCodeLength;
    }

    public void setConfirmationCodeLength(int confirmationCodeLength) {
        mConfirmationCodeLength=confirmationCodeLength;
    }

    public Validation getValidations() {
        return mValidations;
    }

    public void setValidations(Validation validations) {
        mValidations=validations;
    }
}
