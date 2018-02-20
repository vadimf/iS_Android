package com.globalbit.tellyou.ui.interfaces;

import com.globalbit.tellyou.model.Comment;

/**
 * Created by alex on 20/02/2018.
 */

public interface IReplyListener {
    void onReport(Comment comment);
    void onProfile(Comment comment);
    void onPlayPause(Comment comment);
}
