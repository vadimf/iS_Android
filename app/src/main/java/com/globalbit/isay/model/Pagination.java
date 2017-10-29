package com.globalbit.isay.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class Pagination {

    @SerializedName("page")
    private int mPage;

    @SerializedName("pages")
    private int mPages;

    @SerializedName("results")
    private int mResults;

    @SerializedName("resultsPerPage")
    private int mResultsPerPage;

    @SerializedName("offset")
    private int mOffset;

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage=page;
    }

    public int getPages() {
        return mPages;
    }

    public void setPages(int pages) {
        mPages=pages;
    }

    public int getResults() {
        return mResults;
    }

    public void setResults(int results) {
        mResults=results;
    }

    public int getResultsPerPage() {
        return mResultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        mResultsPerPage=resultsPerPage;
    }

    public int getOffset() {
        return mOffset;
    }

    public void setOffset(int offset) {
        mOffset=offset;
    }
}
