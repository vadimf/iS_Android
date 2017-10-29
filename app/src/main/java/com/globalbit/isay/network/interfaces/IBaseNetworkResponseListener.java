package com.globalbit.isay.network.interfaces;

/**
 * Created by Alex on 12/12/2016.
 */

public interface IBaseNetworkResponseListener<T> {
    void onSuccess(T response);
    void onError(int errorCode, String errorMessage);
}
