package com.globalbit.isay.network.callbacks;

import com.globalbit.isay.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.isay.network.responses.BaseResponse;
import com.globalbit.isay.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Alex on 12/12/2016.
 */

public class BaseNetworkCallback extends NetworkCallback implements Callback<BaseResponse> {
    IBaseNetworkResponseListener<BaseResponse> mListener;

    public BaseNetworkCallback(IBaseNetworkResponseListener<BaseResponse> listener) {
        mListener=listener;
    }

    @Override
    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
                if(response.body().getErrorCode()==0) {
                    mListener.onSuccess(response.body());
                }
                else {
                    mListener.onError(response.body().getErrorCode(), ErrorUtils.getErrorMessage(response.body().getErrorCode()));
                }
            }
        }
    }

    @Override
    public void onFailure(Call<BaseResponse> call, Throwable t) {
        handleError(mListener);
    }
}
