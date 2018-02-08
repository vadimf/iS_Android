package com.globalbit.tellyou.network.callbacks;

import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.PostResponse;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 29/10/2017.
 */

public class PostNetworkCallback extends NetworkCallback implements Callback<PostResponse> {
    IBaseNetworkResponseListener<PostResponse> mListener;
    private Enums.RequestType mRequestType;

    public PostNetworkCallback(IBaseNetworkResponseListener<PostResponse> listener, Enums.RequestType requestType) {
        mListener=listener;
        mRequestType=requestType;
    }

    @Override
    public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
                if(response.body().getErrorCode()==0) {
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
    public void onFailure(Call<PostResponse> call, Throwable t) {
        handleError(mListener);
    }
}
