package com.globalbit.tellyou.network.responses;

import com.globalbit.tellyou.model.Pagination;
import com.globalbit.tellyou.model.Post;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by alex on 29/10/2017.
 */

public class PostsResponse extends BaseResponse {

    @SerializedName("posts")
    private ArrayList<Post> mPosts;

    @SerializedName("pagination")
    private Pagination mPagination;

    @SerializedName("followingPostsExists")
    private boolean mFollowingPostsExists;

    public ArrayList<Post> getPosts() {
        return mPosts;
    }

    public void setPosts(ArrayList<Post> posts) {
        mPosts=posts;
    }

    public Pagination getPagination() {
        return mPagination;
    }

    public void setPagination(Pagination pagination) {
        mPagination=pagination;
    }

    public boolean isFollowingPostsExists() {
        return mFollowingPostsExists;
    }

    public void setFollowingPostsExists(boolean followingPostsExists) {
        mFollowingPostsExists=followingPostsExists;
    }
}
