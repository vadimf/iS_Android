package com.globalbit.tellyou.network.interfaces;

/**
 * Created by Alex on 12/12/2016.
 */

public interface IBaseNetworkResponseListener<T> {
    void onSuccess(T response, Object object);
    void onError(int errorCode, String errorMessage);
}
