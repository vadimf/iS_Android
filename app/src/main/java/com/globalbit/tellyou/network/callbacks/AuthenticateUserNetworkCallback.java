package com.globalbit.tellyou.network.callbacks;

import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.AuthenticateUserResponse;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 29/10/2017.
 */

public class AuthenticateUserNetworkCallback extends NetworkCallback implements Callback<AuthenticateUserResponse> {
    IBaseNetworkResponseListener<AuthenticateUserResponse> mListener;
    private Enums.RequestType mRequestType;

    public AuthenticateUserNetworkCallback(IBaseNetworkResponseListener<AuthenticateUserResponse> listener, Enums.RequestType requestType) {
        mListener=listener;
        mRequestType=requestType;
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
                    mListener.onError(response.body().getErrorCode(), ErrorUtils.getErrorMessage(response.body().getErrorCode(), mRequestType));
                }
            }
        }
        else if(response.errorBody()!=null) {
            BaseResponse error=ErrorUtils.parseError(response);
            mListener.onError(error.getErrorCode(), ErrorUtils.getErrorMessage(error.getErrorCode(), mRequestType));
        }
    }

    @Override
    public void onFailure(Call<AuthenticateUserResponse> call, Throwable t) {
        handleError(mListener);
    }
}
