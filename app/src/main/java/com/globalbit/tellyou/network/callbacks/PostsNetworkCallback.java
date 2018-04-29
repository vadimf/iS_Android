package com.globalbit.tellyou.network.callbacks;


import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.PostsResponse;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 29/10/2017.
 */

public class PostsNetworkCallback extends NetworkCallback implements Callback<PostsResponse> {
    IBaseNetworkResponseListener<PostsResponse> mListener;
    String mQuery=null;

    public PostsNetworkCallback(IBaseNetworkResponseListener<PostsResponse> listener, String query) {
        mListener=listener;
        mQuery=query;
    }

    @Override
    public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
                if(response.body().getErrorCode()==0) {
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
    public void onFailure(Call<PostsResponse> call, Throwable t) {
        handleError(mListener);
    }
}
