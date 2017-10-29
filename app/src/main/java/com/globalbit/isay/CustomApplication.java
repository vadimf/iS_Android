package com.globalbit.isay;

import android.app.Application;
import android.content.Context;

import com.globalbit.isay.model.system.SystemPreferences;
import com.globalbit.isay.utils.SharedPrefsUtils;


/**
 * Created by Alex on 15/12/2016.
 */

public class CustomApplication extends Application {
    private static Context mApplicationContext;
    private static SystemPreferences mSystemPreference;



    @Override
    public void onCreate() {
        super.onCreate();
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
