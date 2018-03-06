package com.globalbit.tellyou.model;

import com.globalbit.tellyou.utils.Enums;

/**
 * Created by alex on 05/03/2018.
 */

public class InputData {
    private String mName;
    private String mValue;
    private boolean mIsRequired;
    private Enums.InputType mInputType;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName=name;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue=value;
    }

    public boolean isRequired() {
        return mIsRequired;
    }

    public void setRequired(boolean required) {
        mIsRequired=required;
    }

    public Enums.InputType getInputType() {
        return mInputType;
    }

    public void setInputType(Enums.InputType inputType) {
        mInputType=inputType;
    }
}
