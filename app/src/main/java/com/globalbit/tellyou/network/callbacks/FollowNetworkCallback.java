package com.globalbit.tellyou.network.callbacks;

import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.UserResponse;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 07/01/2018.
 */

public class FollowNetworkCallback extends NetworkCallback implements Callback<BaseResponse> {
    IBaseNetworkResponseListener<BaseResponse> mListener;
    private Enums.RequestType mRequestType;

    public FollowNetworkCallback(IBaseNetworkResponseListener<BaseResponse> listener, Enums.RequestType requestType) {
        mListener=listener;
        mRequestType=requestType;
    }

    @Override
    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
                if(response.body().getErrorCode()==0) {
                    NetworkManager.getInstance().getMyDetails(new IBaseNetworkResponseListener<UserResponse>() {
                        @Override
                        public void onSuccess(UserResponse response) {
                            SharedPrefsUtils.setUserDetails(response.getUser());
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {

                        }
                    });
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
    public void onFailure(Call<BaseResponse> call, Throwable t) {
        handleError(mListener);
    }
}
