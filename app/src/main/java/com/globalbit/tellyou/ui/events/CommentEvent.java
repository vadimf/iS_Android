package com.globalbit.tellyou.ui.events;

import com.globalbit.tellyou.model.Post;

public class CommentEvent {
    public final String id;

    public CommentEvent(String id) {
        this.id = id;
    }
}
