package com.globalbit.tellyou.network.callbacks;

import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.UsernameExistResponse;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 26/11/2017.
 */

public class UsernameExistNetworkCallback extends NetworkCallback implements Callback<UsernameExistResponse> {
    IBaseNetworkResponseListener<UsernameExistResponse> mListener;
    private String mUsername;

    public UsernameExistNetworkCallback(IBaseNetworkResponseListener<UsernameExistResponse> listener, String username) {
        mListener=listener;
        mUsername=username;
    }

    @Override
    public void onResponse(Call<UsernameExistResponse> call, Response<UsernameExistResponse> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
                response.body().setUsername(mUsername);
                if(response.body().getErrorCode()==0) {
                    mListener.onSuccess(response.body(), null);
                }
                else {
                    mListener.onError(response.body().getErrorCode(), ErrorUtils.getErrorMessage(response.body().getErrorCode(), Enums.RequestType.General));
                }
            }
        }
        else if(response.errorBody()!=null) {
            BaseResponse error=ErrorUtils.parseError(response);
            mListener.onError(error.getErrorCode(), ErrorUtils.getErrorMessage(error.getErrorCode(), Enums.RequestType.General));
        }
    }

    @Override
    public void onFailure(Call<UsernameExistResponse> call, Throwable t) {
        handleError(mListener);
    }
}
