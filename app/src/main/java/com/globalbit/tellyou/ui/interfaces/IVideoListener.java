package com.globalbit.tellyou.ui.interfaces;

import com.globalbit.tellyou.model.Post;

/**
 * Created by alex on 21/02/2018.
 */

public interface IVideoListener {
    void onClose();
    void onReport(String id);
    void onComments(Post post);
    void onNextVideo(int position);
}
