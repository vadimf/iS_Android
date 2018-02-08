package com.globalbit.tellyou.network.callbacks;


import com.globalbit.androidutils.GeneralUtils;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ErrorUtils;


/**
 * Created by alex on 05/02/2017.
 */

public class NetworkCallback {
    void handleError(IBaseNetworkResponseListener listener) {
        if(!GeneralUtils.isNetworkAvailable(CustomApplication.getAppContext())) {
            listener.onError(9999, ErrorUtils.getErrorMessage(9999, Enums.RequestType.General));
        }
        else {
            listener.onError(9000, ErrorUtils.getErrorMessage(9000, Enums.RequestType.General));
        }
    }
}
