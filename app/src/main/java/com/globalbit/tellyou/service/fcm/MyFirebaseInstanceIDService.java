package com.globalbit.tellyou.service.fcm;

import android.util.Log;

import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.PushNotificationTokenRequest;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Alex on 15/12/2016.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements IBaseNetworkResponseListener<BaseResponse> {
    private static final String TAG=MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        PushNotificationTokenRequest request=new PushNotificationTokenRequest();
        request.setToken(token);
        NetworkManager.getInstance().sendToken(this, request);
    }

    @Override
    public void onError(int errorCode, String errorMessage) {

    }

    @Override
    public void onSuccess(BaseResponse response, Object object) {

    }
}
