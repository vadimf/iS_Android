package com.globalbit.tellyou.service.fcm;

import android.util.Log;

import com.globalbit.tellyou.model.Notification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by Alex on 15/12/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG=MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getData().toString());

        Notification notification=new Notification();
        if(remoteMessage.getData().containsKey("title")) {
            notification.setNotificationTitle(remoteMessage.getData().get("title"));
        }
        if(remoteMessage.getData().containsKey("message")) {
            notification.setNotificationMessage(remoteMessage.getData().get("message"));
        }
        if(remoteMessage.getData().containsKey("type")) {
            notification.setNotificationType(Integer.parseInt(remoteMessage.getData().get("type")));
        }
        if(remoteMessage.getData().containsKey("username")) {
            notification.setUsername(remoteMessage.getData().get("username"));
        }
        if(remoteMessage.getData().containsKey("postId")) {
            notification.setPostId(remoteMessage.getData().get("postId"));
        }
        if(remoteMessage.getData().containsKey("commentId")) {
            notification.setCommentId(remoteMessage.getData().get("commentId"));
        }

        FCMHandler.pushHandle(this, notification);
    }
}
