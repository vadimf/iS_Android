package com.globalbit.isay;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.globalbit.isay.model.system.SystemPreferences;
import com.globalbit.isay.utils.SharedPrefsUtils;
import io.fabric.sdk.android.Fabric;


/**
 * Created by Alex on 15/12/2016.
 */

public class CustomApplication extends Application {
    private static Context mApplicationContext;
    private static SystemPreferences mSystemPreference;



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

    public static SystemPreferences getSystemPreference() {
        return mSystemPreference;
    }

    public static void setSystemPreference(SystemPreferences mSystemPreference) {
        CustomApplication.mSystemPreference=mSystemPreference;
    }

}
