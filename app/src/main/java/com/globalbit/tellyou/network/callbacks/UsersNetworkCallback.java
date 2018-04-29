package com.globalbit.tellyou.network.callbacks;

import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.UsersResponse;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 29/10/2017.
 */

public class UsersNetworkCallback extends NetworkCallback implements Callback<UsersResponse> {
    IBaseNetworkResponseListener<UsersResponse> mListener;

    public UsersNetworkCallback(IBaseNetworkResponseListener<UsersResponse> listener) {
        mListener=listener;
    }

    @Override
    public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
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
    public void onFailure(Call<UsersResponse> call, Throwable t) {
        handleError(mListener);
    }
}
