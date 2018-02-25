package com.globalbit.tellyou;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.system.SystemPreferencesKT;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;


/**
 * Created by Alex on 15/12/2016.
 */

public class CustomApplication extends MultiDexApplication {
    private static Context mApplicationContext;
    private static SystemPreferencesKT mSystemPreference;
    private static Post mPost;


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mApplicationContext=getApplicationContext();
        SharedPrefsUtils.init(this);
        Configuration configuration = new Configuration(Resources.getSystem().getConfiguration());
        configuration.setLocale(Locale.ENGLISH);
        Resources.getSystem().updateConfiguration(configuration, null);
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

    public static Post getPost() {
        return mPost;
    }

    public static void setPost(Post mPost) {
        CustomApplication.mPost=mPost;
    }

}
