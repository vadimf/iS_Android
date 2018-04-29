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
 * Created by alex on 13/12/2017.
 */

public class UsersSearchNetworkCallback extends NetworkCallback implements Callback<UsersResponse> {
    IBaseNetworkResponseListener<UsersResponse> mListener;
    private String mQuery;

    public UsersSearchNetworkCallback(IBaseNetworkResponseListener<UsersResponse> listener, String query) {
        mListener=listener;
        mQuery=query;
    }

    @Override
    public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
                if(response.body().getErrorCode()==0) {
                    UsersResponse usersResponse=response.body();
                    if(usersResponse!=null) {
                        usersResponse.setQuery(mQuery);
                    }
                    mListener.onSuccess(response.body(), mQuery);
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
