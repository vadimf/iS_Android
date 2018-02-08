package com.globalbit.tellyou.network.responses;

import com.globalbit.tellyou.model.Comment;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class CommentResponse extends BaseResponse {

    @SerializedName("comment")
    private Comment mComment;

    public Comment getComment() {
        return mComment;
    }

    public void setComment(Comment comment) {
        mComment=comment;
    }
}
