package com.globalbit.tellyou.network.callbacks;

import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.FacebookFriendsResponse;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 09/11/2017.
 */

public class FacebookFriendsNetworkCallback extends NetworkCallback implements Callback<FacebookFriendsResponse> {
    IBaseNetworkResponseListener<FacebookFriendsResponse> mListener;

    public FacebookFriendsNetworkCallback(IBaseNetworkResponseListener<FacebookFriendsResponse> listener) {
        mListener=listener;
    }

    @Override
    public void onResponse(Call<FacebookFriendsResponse> call, Response<FacebookFriendsResponse> response) {
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
    public void onFailure(Call<FacebookFriendsResponse> call, Throwable t) {
        handleError(mListener);
    }
}
