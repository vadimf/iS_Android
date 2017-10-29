package com.globalbit.isay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by alex on 29/10/2017.
 */

public class SharedPrefsUtils {
    private static SharedPreferences mSharedPreferences;
    private static final String KEY_BACKGROUND = "InBackground";
    private static final String KEY_FCM_TOKEN = "FCMToken";
    private static final String KEY_AUTHORIZATION = "Authorization";

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
}
