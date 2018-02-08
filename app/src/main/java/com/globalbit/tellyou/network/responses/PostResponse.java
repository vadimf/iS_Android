package com.globalbit.tellyou.network.responses;

import com.globalbit.tellyou.model.Post;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class PostResponse extends BaseResponse {

    @SerializedName("post")
    private Post mPost;

    public Post getPost() {
        return mPost;
    }

    public void setPost(Post post) {
        mPost=post;
    }
}
