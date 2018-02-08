package com.globalbit.tellyou.ui.interfaces;

import com.globalbit.tellyou.model.User;

/**
 * Created by alex on 06/11/2017.
 */

public interface ILoginListener {
    void onSignSuccess(User user);
    void onForgotPassword();
}
