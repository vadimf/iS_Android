package com.globalbit.tellyou.ui.interfaces;

import java.util.HashMap;

public interface IProfileListener {
    void onUsersFollowingStatus(HashMap<String, Boolean> usersFollowingState);
}
