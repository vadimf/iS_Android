package com.globalbit.tellyou.ui.interfaces;

import com.globalbit.tellyou.model.Phone;

public interface IPhoneListener {
    void showPhoneVerification();
    void showCodeConfirmation(Phone phone);
}
