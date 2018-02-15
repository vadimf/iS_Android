package com.globalbit.tellyou.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.PostResponse;
import com.globalbit.tellyou.ui.activities.SplashScreenActivity;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by alex on 15/02/2018.
 */

public class UploadService extends Service {
    private static final String TAG=UploadService.class.getSimpleName();
    public static String MAIN_ACTION = "com.globalbit.tellyou.action.main";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null) {
            final File file=(File)intent.getSerializableExtra(Constants.DATA_VIDEO_FILE);
            File gif=(File)intent.getSerializableExtra(Constants.DATA_GIF_FILE);
            String text=intent.getStringExtra(Constants.DATA_TEXT);
            int duration=intent.getIntExtra(Constants.DATA_DURATION, -1);

            RequestBody requestFile =RequestBody.create(
                    MediaType.parse("video/mp4"),
                    file
            );
            RequestBody requestGif =RequestBody.create(
                    MediaType.parse("image/jpg"),
                    gif
            );
            NetworkManager.getInstance().createPost(new IBaseNetworkResponseListener<PostResponse>() {
                    @Override
                    public void onSuccess(PostResponse response) {
                        if(file.exists()) {
                            if(file.delete()) {
                                Log.i(TAG, "File deleted successfully");
                            }
                            else {
                                Log.i(TAG, "Couldn't delete the file");
                            }
                        }
                        Intent notificationIntent = new Intent(UploadService.this, SplashScreenActivity.class);
                        notificationIntent.setAction(MAIN_ACTION);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(UploadService.this, 0,
                            notificationIntent, 0);
                        Notification notification =
                                new NotificationCompat.Builder(UploadService.this, "UploadChannel")
                                        .setContentTitle("Uploaded")
                                        .setContentText("Your video was uploaded")
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true)
                                        .build();
                        final NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                                .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

                        notificationManager.notify(1, notification);
                        stopForeground(false);
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        Notification notification =
                                new NotificationCompat.Builder(UploadService.this, "UploadChannel")
                                        .setContentTitle("Error")
                                        .setContentText("Your video failed to upload")
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentIntent(null)
                                        .setAutoCancel(true)
                                        .build();
                        final NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                                .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

                        notificationManager.notify(1, notification);
                    }
                }, MultipartBody.Part.createFormData("video", file.getName(), requestFile),
                MultipartBody.Part.createFormData("thumbnail", gif.getName(), requestGif),
                RequestBody.create(okhttp3.MultipartBody.FORM, text),
                RequestBody.create(MultipartBody.FORM, String.valueOf(duration)));


            Notification notification =
                    new NotificationCompat.Builder(this, "UploadChannel")
                            .setContentTitle("Uploading")
                            .setContentText("Your video is uploading")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(null)
                            .build();

            startForeground(1, notification);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
