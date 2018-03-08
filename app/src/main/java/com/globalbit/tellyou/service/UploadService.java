package com.globalbit.tellyou.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.CommentResponse;
import com.globalbit.tellyou.network.responses.PostResponse;
import com.globalbit.tellyou.ui.activities.ReplyActivity;
import com.globalbit.tellyou.ui.activities.SplashScreenActivity;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

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
            int duration=(int)intent.getLongExtra(Constants.DATA_DURATION, -1);
            int videoRecordingType=intent.getIntExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_POST_VIDEO_RECORDING);
            final String postId=intent.getStringExtra(Constants.DATA_POST_ID);
            RequestBody requestFile =RequestBody.create(
                    MediaType.parse("video/mp4"),
                    file
            );
            RequestBody requestGif =RequestBody.create(
                    MediaType.parse("image/jpg"),
                    gif
            );
            Notification notification;
            switch(videoRecordingType) {
                case Constants.TYPE_POST_VIDEO_RECORDING:
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
                                    CustomApplication.setPost(response.getPost());
                                    notificationIntent.setAction(MAIN_ACTION);
                                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(UploadService.this, 0,
                                            notificationIntent, 0);
                                    showReplyNotification(pendingIntent);
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
                    notification =
                            new NotificationCompat.Builder(this, "UploadChannel")
                                    .setContentTitle(getString(R.string.label_uploading))
                                    .setContentText(getString(R.string.notification_uploading))
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentIntent(null)
                                    .build();

                    startForeground(1, notification);
                    break;
                case Constants.TYPE_REPLY_VIDEO_RECORDING:
                    NetworkManager.getInstance().createComment(new IBaseNetworkResponseListener<CommentResponse>() {
                            @Override
                            public void onSuccess(final CommentResponse response) {
                                if(file.exists()) {
                                    if(file.delete()) {
                                        Log.i(TAG, "File deleted successfully");
                                    }
                                    else {
                                        Log.i(TAG, "Couldn't delete the file");
                                    }
                                }
                                NetworkManager.getInstance().getPostById(new IBaseNetworkResponseListener<PostResponse>() {
                                    @Override
                                    public void onSuccess(PostResponse postResponse) {
                                        PendingIntent pendingIntent;
                                        if(response.getComment()!=null) {
                                            Intent intent;
                                            if(SharedPrefsUtils.isInBackground()) {
                                                intent=new Intent(UploadService.this, SplashScreenActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            } else {
                                                intent=new Intent(UploadService.this, ReplyActivity.class);
                                            }
                                            intent.putExtra(Constants.DATA_PUSH, 2);
                                            intent.putExtra(Constants.DATA_POST_ID, postId);
                                            intent.putExtra(Constants.DATA_POST, postResponse.getPost());
                                            intent.putExtra(Constants.DATA_COMMENT_ID, response.getComment().getId());
                                            pendingIntent=PendingIntent.getActivity(UploadService.this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        }
                                        else {
                                            Intent notificationIntent=new Intent(UploadService.this, SplashScreenActivity.class);
                                            notificationIntent.setAction(MAIN_ACTION);
                                            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            pendingIntent=PendingIntent.getActivity(UploadService.this, 0,
                                                    notificationIntent, 0);
                                        }
                                        showReplyNotification(pendingIntent);
                                    }

                                    @Override
                                    public void onError(int errorCode, String errorMessage) {
                                        Intent notificationIntent=new Intent(UploadService.this, SplashScreenActivity.class);
                                        notificationIntent.setAction(MAIN_ACTION);
                                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        PendingIntent pendingIntent=PendingIntent.getActivity(UploadService.this, 0,
                                                notificationIntent, 0);
                                        showReplyNotification(pendingIntent);
                                    }
                                }, postId);

                            }

                            @Override
                            public void onError(int errorCode, String errorMessage) {
                                Notification notification =
                                        new NotificationCompat.Builder(UploadService.this, "UploadChannel")
                                                .setContentTitle(getString(R.string.error))
                                                .setContentText(getString(R.string.notification_error_uploading))
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentIntent(null)
                                                .setAutoCancel(true)
                                                .build();
                                final NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                                        .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

                                notificationManager.notify(1, notification);
                            }
                        },postId , MultipartBody.Part.createFormData("video", file.getName(), requestFile),
                        MultipartBody.Part.createFormData("thumbnail", gif.getName(), requestGif),
                        RequestBody.create(MultipartBody.FORM, String.valueOf(duration)));
                    notification =
                            new NotificationCompat.Builder(this, "UploadChannel")
                                    .setContentTitle(getString(R.string.label_uploading))
                                    .setContentText(getString(R.string.notification_uploading))
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentIntent(null)
                                    .build();

                    startForeground(1, notification);
                    break;
            }

        }

        return START_STICKY;
    }

    private void showReplyNotification(PendingIntent pendingIntent) {
        Notification notification =
                new NotificationCompat.Builder(UploadService.this, "UploadChannel")
                        .setContentTitle(getString(R.string.label_uploaded))
                        .setContentText(getString(R.string.notification_video_uploaded))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build();
        final NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);
        stopForeground(false);
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
