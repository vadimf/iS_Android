package com.globalbit.tellyou.network.callbacks;

import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.CommentsResponse;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 29/10/2017.
 */

public class CommentsNetworkCallback extends NetworkCallback implements Callback<CommentsResponse> {
    IBaseNetworkResponseListener<CommentsResponse> mListener;
    private Enums.RequestType mRequestType;

    public CommentsNetworkCallback(IBaseNetworkResponseListener<CommentsResponse> listener, Enums.RequestType requestType) {
        mListener=listener;
        mRequestType=requestType;
    }

    @Override
    public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {
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
    public void onFailure(Call<CommentsResponse> call, Throwable t) {
        handleError(mListener);
    }
}
