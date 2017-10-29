package com.globalbit.isay.network.callbacks;

import com.globalbit.isay.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.isay.network.responses.AuthenticateUserResponse;
import com.globalbit.isay.utils.ErrorUtils;
import com.globalbit.isay.utils.SharedPrefsUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 29/10/2017.
 */

public class AuthenticateUserNetworkCallback extends NetworkCallback implements Callback<AuthenticateUserResponse> {
    IBaseNetworkResponseListener<AuthenticateUserResponse> mListener;

    public AuthenticateUserNetworkCallback(IBaseNetworkResponseListener<AuthenticateUserResponse> listener) {
        mListener=listener;
    }

    @Override
    public void onResponse(Call<AuthenticateUserResponse> call, Response<AuthenticateUserResponse> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
                if(response.body().getErrorCode()==0) {
                    SharedPrefsUtils.setAuthorization(response.body().getAuth());
                    mListener.onSuccess(response.body());
                }
                else {
                    mListener.onError(response.body().getErrorCode(), ErrorUtils.getErrorMessage(response.body().getErrorCode()));
                }
            }
        }
    }

    @Override
    public void onFailure(Call<AuthenticateUserResponse> call, Throwable t) {
        handleError(mListener);
    }
}
