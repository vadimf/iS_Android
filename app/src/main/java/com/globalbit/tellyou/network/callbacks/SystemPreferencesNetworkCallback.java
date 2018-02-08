package com.globalbit.tellyou.network.callbacks;


import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.model.system.SystemPreferencesResponseKT;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 05/02/2017.
 */

public class SystemPreferencesNetworkCallback extends NetworkCallback implements Callback<SystemPreferencesResponseKT> {
    IBaseNetworkResponseListener<SystemPreferencesResponseKT> mListener;

    public SystemPreferencesNetworkCallback(IBaseNetworkResponseListener<SystemPreferencesResponseKT> listener) {
        mListener=listener;
    }

    @Override
    public void onResponse(Call<SystemPreferencesResponseKT> call, Response<SystemPreferencesResponseKT> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
                if(response.body().getErrorCode()==0) {
                    CustomApplication.setSystemPreference(response.body().getSystemPreferences());
                    mListener.onSuccess(response.body());
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
    public void onFailure(Call<SystemPreferencesResponseKT> call, Throwable t) {
        handleError(mListener);
    }
}
