package com.globalbit.isay.network.callbacks;

import com.globalbit.isay.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.isay.network.responses.UserResponse;
import com.globalbit.isay.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 29/10/2017.
 */

public class UserNetworkCallback extends NetworkCallback implements Callback<UserResponse> {
    IBaseNetworkResponseListener<UserResponse> mListener;

    public UserNetworkCallback(IBaseNetworkResponseListener<UserResponse> listener) {
        mListener=listener;
    }

    @Override
    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
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
    public void onFailure(Call<UserResponse> call, Throwable t) {
        handleError(mListener);
    }
}
