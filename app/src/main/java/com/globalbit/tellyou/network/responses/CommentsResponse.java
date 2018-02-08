package com.globalbit.tellyou.network.responses;

import com.globalbit.tellyou.model.Comment;
import com.globalbit.tellyou.model.Pagination;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 29/10/2017.
 */

public class CommentsResponse extends BaseResponse {

    @SerializedName("comments")
    private ArrayList<Comment> mComments;

    @SerializedName("pagination")
    private Pagination mPagination;

    public ArrayList<Comment> getComments() {
        return mComments;
    }

    public void setComments(ArrayList<Comment> comments) {
        mComments=comments;
    }

    public Pagination getPagination() {
        return mPagination;
    }

    public void setPagination(Pagination pagination) {
        mPagination=pagination;
    }
}
