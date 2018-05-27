package com.globalbit.tellyou;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.system.SystemPreferencesKT;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;


/**
 * Created by Alex on 15/12/2016.
 */

public class CustomApplication extends MultiDexApplication {
    private static Context mApplicationContext;
    private static SystemPreferencesKT mSystemPreference;
    private static Post mPost;
    private static FirebaseAnalytics mFirebaseAnalytics;


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mApplicationContext=getApplicationContext();
        SharedPrefsUtils.init(this);
        Configuration configuration = new Configuration(Resources.getSystem().getConfiguration());
        configuration.setLocale(Locale.ENGLISH);
        mApplicationContext.getResources().updateConfiguration(configuration, null);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    public static Context getAppContext() {
        return mApplicationContext;
    }

    public static FirebaseAnalytics getAnalytics() {
        return  mFirebaseAnalytics;
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
