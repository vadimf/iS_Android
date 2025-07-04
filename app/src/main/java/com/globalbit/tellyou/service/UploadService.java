package com.globalbit.tellyou.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.CommentResponse;
import com.globalbit.tellyou.network.responses.PostResponse;
import com.globalbit.tellyou.ui.activities.ReplyActivity;
import com.globalbit.tellyou.ui.activities.SplashScreenActivity;
import com.globalbit.tellyou.ui.activities.VideoTrimmerActivity;
import com.globalbit.tellyou.ui.events.CommentEvent;
import com.globalbit.tellyou.ui.events.RefreshEvent;
import com.globalbit.tellyou.ui.videotrimmer.interfaces.OnTrimVideoListener;
import com.globalbit.tellyou.ui.videotrimmer.utils.BackgroundExecutor;
import com.globalbit.tellyou.ui.videotrimmer.utils.TrimVideoUtils;
import com.globalbit.tellyou.utils.GeneralUtils;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iceteck.silicompressorr.SiliCompressor;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

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
            final String filePath=intent.getStringExtra(Constants.DATA_VIDEO_FILE);
            final File file=new File(filePath);
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(file.getPath());
            String hh = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String ww = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String metaRotation = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            Log.i(TAG, "onStartCommand: "+ww+","+hh+":"+metaRotation);
            int w=0;
            int h=0;
            int rotation=0;
            try {
                w=Integer.parseInt(ww);
                h=Integer.parseInt(hh);
                rotation=Integer.parseInt(metaRotation);
            }
            catch(Exception ex) {

            }
            if(rotation==90||rotation==270) {
                int tmp=w;
                w=h;
                h=tmp;
            }
            final int width=w;
            final int height=h;
            //final File gif=(File)intent.getSerializableExtra(Constants.DATA_GIF_FILE);
            final String text=intent.getStringExtra(Constants.DATA_TEXT);
            ArrayList<String> tags=intent.getStringArrayListExtra(Constants.DATA_HASHTAGS);
            final int duration=(int)intent.getLongExtra(Constants.DATA_DURATION, -1);
            final int videoRecordingType=intent.getIntExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_POST_VIDEO_RECORDING);
            final String postId=intent.getStringExtra(Constants.DATA_POST_ID);
            //final int width=intent.getIntExtra(Constants.DATA_WIDTH, 0);
            //final int height=intent.getIntExtra(Constants.DATA_HEIGHT, 0);
            final boolean isFrontCamera=intent.getBooleanExtra(Constants.DATA_IS_FRONT_CAMERA, true);
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(filePath,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            File gifFile=null;
            try {
                gifFile=GeneralUtils.createImageFile("jpg");
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thumb.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(gifFile);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
            final File gif=gifFile;
            final RequestBody requestFile =RequestBody.create(
                    MediaType.parse("video/mp4"),
                    file
            );
            final RequestBody requestGif =RequestBody.create(
                    MediaType.parse("image/jpg"),
                    gif
            );
            final ArrayList<RequestBody> hashtags=new ArrayList<>();
            if(tags!=null&&tags.size()>0) {
                for(String tag : tags) {
                    hashtags.add(RequestBody.create(MultipartBody.FORM, tag));
                }
            }
            Notification notification;
            switch(videoRecordingType) {
                case Constants.TYPE_POST_VIDEO_RECORDING:
                    recordedVideo(isFrontCamera, videoRecordingType, file, requestFile, gif, requestGif, text, hashtags, duration, width, height);
                    notification =
                            new NotificationCompat.Builder(this, "UploadChannel")
                                    .setContentTitle(getString(R.string.label_uploading))
                                    .setContentText(getString(R.string.notification_uploading))
                                    .setSmallIcon(R.drawable.ic_status)
                                    .setContentIntent(null)
                                    .build();
                    startForeground(1, notification);
                    break;
                case Constants.TYPE_POST_VIDEO_TRIMMING:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String filePathNew = SiliCompressor.with(UploadService.this).compressVideo(Uri.fromFile(file), Environment.getExternalStorageDirectory().getPath(),720, 480, 1000000);
                                final File newFile=new File(filePathNew);
                                File gifFileNew=null;
                                try {
                                    Bitmap thumbNew = ThumbnailUtils.createVideoThumbnail(filePathNew,
                                            MediaStore.Images.Thumbnails.MINI_KIND);
                                    gifFileNew=GeneralUtils.createImageFile("jpg");
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    thumbNew.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                                    byte[] bitmapdata = bos.toByteArray();
                                    FileOutputStream fos = new FileOutputStream(gifFileNew);
                                    fos.write(bitmapdata);
                                    fos.flush();
                                    fos.close();
                                } catch(IOException e) {
                                    e.printStackTrace();
                                }
                                final RequestBody requestFileNew =RequestBody.create(
                                        MediaType.parse("video/mp4"),
                                        newFile
                                );
                                final File gifFileNewFinal=gifFileNew;
                                final RequestBody requestGifNew =RequestBody.create(
                                        MediaType.parse("image/jpg"),
                                        gifFileNewFinal
                                );
                                recordedVideo(isFrontCamera, videoRecordingType, newFile, requestFileNew, gifFileNewFinal, requestGifNew, text, hashtags, duration, width, height);
                            } catch(URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    notification =
                            new NotificationCompat.Builder(this, "UploadChannel")
                                    .setContentTitle(getString(R.string.label_uploading))
                                    .setContentText(getString(R.string.notification_uploading))
                                    .setSmallIcon(R.drawable.ic_status)
                                    .setContentIntent(null)
                                    .build();

                    startForeground(1, notification);
                    break;
                case Constants.TYPE_REPLY_VIDEO_RECORDING:
                    NetworkManager.getInstance().createComment(new IBaseNetworkResponseListener<CommentResponse>() {
                            @Override
                            public void onSuccess(final CommentResponse response, Object object) {
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
                                    public void onSuccess(PostResponse postResponse, Object object) {
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
                                        Bundle params = new Bundle();
                                        params.putString(FirebaseAnalytics.Param.VALUE, String.valueOf(duration));
                                        CustomApplication.getAnalytics().logEvent(Constants.UPLOADED_REPLY, params);
                                        EventBus.getDefault().post(new CommentEvent(postId));
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
                                                .setSmallIcon(R.drawable.ic_status)
                                                .setContentIntent(null)
                                                .setAutoCancel(true)
                                                .build();
                                final NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                                        .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

                                notificationManager.notify(1, notification);
                                stopForeground(false);
                            }
                        },postId , MultipartBody.Part.createFormData("video", file.getName(), requestFile),
                        MultipartBody.Part.createFormData("thumbnail", gif.getName(), requestGif),
                        RequestBody.create(MultipartBody.FORM, String.valueOf(duration)));
                    notification =
                            new NotificationCompat.Builder(this, "UploadChannel")
                                    .setContentTitle(getString(R.string.label_uploading))
                                    .setContentText(getString(R.string.notification_uploading))
                                    .setSmallIcon(R.drawable.ic_status)
                                    .setContentIntent(null)
                                    .build();

                    startForeground(1, notification);
                    break;
            }

        }

        return START_STICKY;
    }

    private void createGif(String[] command, ExecuteBinaryResponseHandler handler){
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(command, handler);
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    private void recordedVideo(final boolean isFrontCamera, final int videoRecordingType, final File file, RequestBody requestFile, File gif, RequestBody requestGif, final String text, final ArrayList<RequestBody> hashtags, final int duration, final int width, final int height) {
        NetworkManager.getInstance().createPost(new IBaseNetworkResponseListener<PostResponse>() {
                                                    @Override
                                                    public void onSuccess(PostResponse response, Object object) {
                                                        if(file.exists()) {
                                                            if(file.delete()) {
                                                                Log.i(TAG, "File deleted successfully");
                                                            }
                                                            else {
                                                                Log.i(TAG, "Couldn't delete the file");
                                                            }
                                                        }
                                                        Bundle paramsHashTags = new Bundle();
                                                        paramsHashTags.putString(FirebaseAnalytics.Param.VALUE, String.valueOf(hashtags.size()));
                                                        CustomApplication.getAnalytics().logEvent(Constants.UPLOADED_VIDEO_HASH_TAGS_COUNT, paramsHashTags);
                                                        Bundle paramsTitle = new Bundle();
                                                        paramsTitle.putString(FirebaseAnalytics.Param.VALUE, String.valueOf(text.length()));
                                                        CustomApplication.getAnalytics().logEvent(Constants.UPLOADED_VIDEO_TITLE_LENGTH, paramsTitle);
                                                        Bundle paramsDuration = new Bundle();
                                                        paramsDuration.putString(FirebaseAnalytics.Param.VALUE, String.valueOf(duration));
                                                        if(videoRecordingType==Constants.TYPE_POST_VIDEO_TRIMMING) {
                                                            CustomApplication.getAnalytics().logEvent(Constants.UPLOADED_VIDEO_GALLERY, paramsDuration);
                                                        }
                                                        else if(videoRecordingType==Constants.TYPE_POST_VIDEO_RECORDING) {
                                                            if(isFrontCamera) {
                                                                CustomApplication.getAnalytics().logEvent(Constants.UPLOADED_VIDEO_FRONT, paramsDuration);
                                                            }
                                                            else {
                                                                CustomApplication.getAnalytics().logEvent(Constants.UPLOADED_VIDEO_REAR, paramsDuration);
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
                                                                        .setSmallIcon(R.drawable.ic_status)
                                                                        .setContentIntent(null)
                                                                        .setAutoCancel(true)
                                                                        .build();
                                                        final NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                                                                .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

                                                        notificationManager.notify(1, notification);
                                                        stopForeground(false);
                                                    }
                                                }, MultipartBody.Part.createFormData("video", file.getName(), requestFile),
                MultipartBody.Part.createFormData("thumbnail", gif.getName(), requestGif),
                RequestBody.create(MultipartBody.FORM, text), hashtags,
                RequestBody.create(MultipartBody.FORM, String.valueOf(duration)),
                RequestBody.create(MultipartBody.FORM, String.valueOf(width)),
                RequestBody.create(MultipartBody.FORM, String.valueOf(height)));
    }

    private void showReplyNotification(PendingIntent pendingIntent) {
        Notification notification =
                new NotificationCompat.Builder(UploadService.this, "UploadChannel")
                        .setContentTitle(getString(R.string.label_uploaded))
                        .setContentText(getString(R.string.notification_video_uploaded))
                        .setSmallIcon(R.drawable.ic_status)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .build();
        final NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);
        stopForeground(false);
        EventBus.getDefault().post(new RefreshEvent());
    }

    private void getShortVideo(final File file, final int duration, final OnTrimVideoListener listener) {
        BackgroundExecutor.execute(
                new BackgroundExecutor.Task("", 0L, "") {
                    @Override
                    public void execute() {
                        try {
                            int startPosition=0;
                            int endPosition=duration-1;
                            if(duration>6) {
                                startPosition=duration/2-3;
                                endPosition=duration/2+2;
                            }
                            TrimVideoUtils.startTrim(file, getDestinationPath(), startPosition*1000, endPosition*1000, listener);
                        } catch (final Throwable e) {
                            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                        }
                    }
                }
        );
    }

    private String getDestinationPath() {
        String finalPath=null;
        File folder = Environment.getExternalStorageDirectory();
        finalPath = folder.getPath() + File.separator;
        return finalPath;
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
