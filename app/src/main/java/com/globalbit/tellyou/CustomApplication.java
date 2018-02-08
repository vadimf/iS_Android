package com.globalbit.tellyou;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.globalbit.tellyou.model.system.SystemPreferencesKT;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import io.fabric.sdk.android.Fabric;


/**
 * Created by Alex on 15/12/2016.
 */

public class CustomApplication extends Application {
    private static Context mApplicationContext;
    private static SystemPreferencesKT mSystemPreference;
    private static String mPostId;



    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mApplicationContext=getApplicationContext();
        SharedPrefsUtils.init(this);
    }

    public static Context getAppContext() {
        return mApplicationContext;
    }

    public static SystemPreferencesKT getSystemPreference() {
        return mSystemPreference;
    }

    public static void setSystemPreference(SystemPreferencesKT mSystemPreference) {
        CustomApplication.mSystemPreference=mSystemPreference;
    }

    public static String getPostId() {
        return mPostId;
    }

    public static void setPostId(String mPostId) {
        CustomApplication.mPostId=mPostId;
    }

}
