package com.globalbit.isay.network.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class SearchRequest {

    @SerializedName("query")
    private String mQuery;

    @SerializedName("page")
    private int mPage;

    public String getQuery() {
        return mQuery;
    }

    public void setQuery(String query) {
        mQuery=query;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage=page;
    }
}
