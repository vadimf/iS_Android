package com.globalbit.isay.network.responses;

import com.globalbit.isay.model.system.SystemPreferences;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class SystemPreferencesResponse extends BaseResponse {

    @SerializedName("vars")
    private SystemPreferences mSystemPreferences;

    public SystemPreferences getSystemPreferences() {
        return mSystemPreferences;
    }

    public void setSystemPreferences(SystemPreferences systemPreferences) {
        mSystemPreferences=systemPreferences;
    }
}
