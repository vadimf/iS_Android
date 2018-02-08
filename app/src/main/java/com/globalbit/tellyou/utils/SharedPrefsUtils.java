package com.globalbit.tellyou.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.PushNotificationTokenRequest;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by alex on 29/10/2017.
 */

public class SharedPrefsUtils {
    private static SharedPreferences mSharedPreferences;
    private static final String KEY_BACKGROUND = "InBackground";
    private static final String KEY_FCM_TOKEN = "FCMToken";
    private static final String KEY_AUTHORIZATION = "Authorization";
    private static final String KEY_USER = "User";
    private static final String KEY_FACEBOOK_TOKEN = "FacebookToken";
    private static final String KEY_NETWORK_URL="NetworkURL";

    public static void init(Context context)
    {
        mSharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setAuthorization(String id)
    {
        mSharedPreferences.edit().putString(KEY_AUTHORIZATION, id).apply();
    }

    public static String getAuthorization()
    {
        return mSharedPreferences.getString(KEY_AUTHORIZATION,"");
    }

    public static void setInBackground(boolean inBackground)
    {
        mSharedPreferences.edit().putBoolean(KEY_BACKGROUND, inBackground).apply();
    }

    public static boolean isInBackground()
    {
        return mSharedPreferences.getBoolean(KEY_BACKGROUND,true);
    }

    public static void setFCMToken(String token) {
        mSharedPreferences.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    public static String getFCMToken() {
        return mSharedPreferences.getString(KEY_FCM_TOKEN, null);
    }

    public static void setUserDetails(User user) {
        if(user!=null) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if(!StringUtils.isEmpty(refreshedToken)) {
                Log.i("SharedUtils", "Token: "+refreshedToken);
                PushNotificationTokenRequest request=new PushNotificationTokenRequest();
                request.setToken(refreshedToken);
                NetworkManager.getInstance().sendToken(new IBaseNetworkResponseListener<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {

                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {

                    }
                }, request);
            }

        }
        Gson gson=new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();
        String json = gson.toJson(user);
        mSharedPreferences.edit().putString(KEY_USER, json).apply();
    }

    public static User getUserDetails() {
        User user=null;
        String json=mSharedPreferences.getString(KEY_USER, null);
        if(!StringUtils.isEmpty(json)) {
            Gson gson=new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .create();
            Type type = new TypeToken<User>() {}.getType();
            user=gson.fromJson(json, type);
        }
        return user;
    }

    public static void setFacebookToken(String token) {
        mSharedPreferences.edit().putString(KEY_FACEBOOK_TOKEN, token).apply();
    }

    public static String getFacebookToken() {
        return mSharedPreferences.getString(KEY_FACEBOOK_TOKEN, null);
    }

    public static void setNetworkUrl(String url) {
        mSharedPreferences.edit().putString(KEY_NETWORK_URL, url).apply();
    }

    public static String getNetworkUrl() {
        return mSharedPreferences.getString(KEY_NETWORK_URL, null);
    }
}
