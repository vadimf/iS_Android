package com.globalbit.tellyou.ui.interfaces;

import com.globalbit.tellyou.model.Post;

/**
 * Created by alex on 21/11/2017.
 */

public interface IPostListener {
    void onShowComments(Post post, int position);
    void onRefreshPosts();
    void onUserProfile(String username);
    void onVideoPayer(Post post, int position);
}
