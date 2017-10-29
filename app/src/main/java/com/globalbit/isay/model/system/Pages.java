package com.globalbit.isay.model.system;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Pages {

    @SerializedName("about")
    private String mAbout;

    @SerializedName("privacy")
    private String mPrivacy;

    @SerializedName("terms")
    private String mTerms;

    @SerializedName("libraries")
    private String mLibraries;

    public String getAbout() {
        return mAbout;
    }

    public void setAbout(String about) {
        mAbout=about;
    }

    public String getPrivacy() {
        return mPrivacy;
    }

    public void setPrivacy(String privacy) {
        mPrivacy=privacy;
    }

    public String getTerms() {
        return mTerms;
    }

    public void setTerms(String terms) {
        mTerms=terms;
    }

    public String getLibraries() {
        return mLibraries;
    }

    public void setLibraries(String libraries) {
        mLibraries=libraries;
    }
}
