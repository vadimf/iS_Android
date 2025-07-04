package com.globalbit.tellyou.ui.interfaces;

import com.globalbit.tellyou.model.Phone;

import java.util.HashMap;

public interface IProfileListener {
    void onUsersFollowingStatus(HashMap<String, Boolean> usersFollowingState);
    void showPhoneVerification();
    void showCodeConfirmation(Phone phone);
}
