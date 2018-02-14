package com.globalbit.tellyou.network.requests;

import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.system.NewPost;
import com.google.gson.annotations.SerializedName;

/**
 * Created by alex on 29/10/2017.
 */

public class CreateEditPostRequest {

    @SerializedName("post")
    private NewPost mPost;

    public NewPost getPost() {
        return mPost;
    }

    public void setPost(NewPost post) {
        mPost=post;
    }
}
