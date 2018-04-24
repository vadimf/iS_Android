package com.globalbit.tellyou.ui.events;

import com.globalbit.tellyou.model.User;

/**
 * Created by alex on 07/01/2018.
 */

public class FollowingEvent {
    public final User user;

    public FollowingEvent(User user) {
        this.user = user;
    }
}
