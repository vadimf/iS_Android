package com.globalbit.isay.network.callbacks;


import com.globalbit.androidutils.GeneralUtils;
import com.globalbit.isay.CustomApplication;
import com.globalbit.isay.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.isay.utils.ErrorUtils;

/**
 * Created by alex on 05/02/2017.
 */

public class NetworkCallback {
    void handleError(IBaseNetworkResponseListener listener) {
        if(!GeneralUtils.isNetworkAvailable(CustomApplication.getAppContext())) {
            listener.onError(9999, ErrorUtils.getErrorMessage(9999));
        }
        else {
            listener.onError(9000, ErrorUtils.getErrorMessage(9000));
        }
    }
}
