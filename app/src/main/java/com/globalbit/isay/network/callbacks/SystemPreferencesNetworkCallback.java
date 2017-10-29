package com.globalbit.isay.network.callbacks;


import com.globalbit.isay.CustomApplication;
import com.globalbit.isay.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.isay.network.responses.SystemPreferencesResponse;
import com.globalbit.isay.utils.ErrorUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by alex on 05/02/2017.
 */

public class SystemPreferencesNetworkCallback extends NetworkCallback implements Callback<SystemPreferencesResponse> {
    IBaseNetworkResponseListener<SystemPreferencesResponse> mListener;

    public SystemPreferencesNetworkCallback(IBaseNetworkResponseListener<SystemPreferencesResponse> listener) {
        mListener=listener;
    }

    @Override
    public void onResponse(Call<SystemPreferencesResponse> call, Response<SystemPreferencesResponse> response) {
        if(response.isSuccessful()) {
            if(response.body()!=null) {
                if(response.body().getErrorCode()==0) {
                    CustomApplication.setSystemPreference(response.body().getSystemPreferences());
                    mListener.onSuccess(response.body());
                }
                else {
                    mListener.onError(response.body().getErrorCode(), ErrorUtils.getErrorMessage(response.body().getErrorCode()));
                }
            }
        }
    }

    @Override
    public void onFailure(Call<SystemPreferencesResponse> call, Throwable t) {
        handleError(mListener);
    }
}
