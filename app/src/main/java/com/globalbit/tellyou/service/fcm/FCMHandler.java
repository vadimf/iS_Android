package com.globalbit.tellyou.service.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.model.Notification;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.UserResponse;
import com.globalbit.tellyou.ui.activities.ProfileActivity;
import com.globalbit.tellyou.ui.activities.SplashScreenActivity;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

/**
 * Created by alex on 21/11/2017.
 */

public class FCMHandler {
    public static final int PUSH_NOTIFICATION_GENERAL=0;
    public static final int PUSH_NOTIFICATION_FOLLOW=1;
    public static final int PUSH_NOTIFICATION_COMMENT=2;
    public static final int PUSH_NOTIFICATION_MENTION=3;
    public static final int PUSH_NOTIFICATION_SHARE=4;
    public static final String GROUP_GENERAL="GroupGeneral";
    public static final String GROUP_FOLLOW="GroupFollow";
    public static final String GROUP_COMMENT="GroupComment";
    public static final String GROUP_MENTION="GroupMention";
    public static final String GROUP_SHARE="GroupShare";


    public static void pushHandle(final Context context, final Notification notificationData) {
        NotificationCompat.Builder builder;
        NotificationManager notificationManager;
        // The id of the channel.
        final String CHANNEL_ID = "cake_channel_1";
        switch(notificationData.getNotificationType()) {
            case PUSH_NOTIFICATION_GENERAL: //Open application
                builder=createNotification(context,notificationData, CHANNEL_ID, GROUP_GENERAL);
                builder=createPendingIntent(context, builder, notificationData.getNotificationType(), notificationData.getNotificationMessage());
                notificationManager =
                        (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notificationData.getNotificationType(), builder.build());
                break;
            case PUSH_NOTIFICATION_FOLLOW: //Open profile
                NetworkManager.getInstance().getMyDetails(new IBaseNetworkResponseListener<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {
                        SharedPrefsUtils.setUserDetails(response.getUser());
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {

                    }
                });
                if(!StringUtils.isEmpty(notificationData.getUsername())) {
                    NetworkManager.getInstance().getUserDetails(new IBaseNetworkResponseListener<UserResponse>() {
                        @Override
                        public void onSuccess(UserResponse response) {
                            NotificationCompat.Builder builder=createNotification(context,notificationData, CHANNEL_ID, GROUP_FOLLOW);
                            builder=createPendingProfileIntent(context, builder, notificationData.getNotificationType(), response.getUser(),SharedPrefsUtils.isInBackground());
                            NotificationManager mNotificationManager =
                                    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(notificationData.getNotificationType(), builder.build());
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {

                        }
                    }, notificationData.getUsername());
                }
                break;
            /*case PUSH_NOTIFICATION_COMMENT: //TODO open comments and highlight the proper comment
                if(!StringUtils.isEmpty(notificationData.getPostId())) {
                    builder=createNotification(context, notificationData, CHANNEL_ID, GROUP_COMMENT);
                    builder=createPendingCommentsIntent(context, builder, notificationData.getNotificationType(), notificationData.getPostId(), SharedPrefsUtils.isInBackground(), notificationData.getCommentId());
                    notificationManager=
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(notificationData.getNotificationType(), builder.build());
                }
                break;
            case PUSH_NOTIFICATION_MENTION: //TODO open comments and highlight the proper comment
                if(!StringUtils.isEmpty(notificationData.getPostId())) {
                    builder=createNotification(context, notificationData, CHANNEL_ID, GROUP_MENTION);
                    builder=createPendingCommentsIntent(context, builder, notificationData.getNotificationType(), notificationData.getPostId(), SharedPrefsUtils.isInBackground(), notificationData.getCommentId());
                    notificationManager=
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(notificationData.getNotificationType(), builder.build());
                }
                break;
            case PUSH_NOTIFICATION_SHARE: // Open post
                if(!StringUtils.isEmpty(notificationData.getPostId())) {
                    builder=createNotification(context, notificationData, CHANNEL_ID, GROUP_SHARE);
                    builder=createPendingForwardIntent(context, builder, notificationData.getNotificationType(), notificationData.getPostId(), SharedPrefsUtils.isInBackground());
                    notificationManager=
                            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(notificationData.getNotificationType(), builder.build());
                }
                break;*/
        }

    }

    private static NotificationCompat.Builder createPendingProfileIntent(Context context, NotificationCompat.Builder builder, int type, User user, boolean isBackground) {
        Intent intent;
        if(isBackground) {
            intent=new Intent(context, SplashScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        else {
            intent=new Intent(context, ProfileActivity.class);
        }
        intent.putExtra(Constants.DATA_PUSH, type);
        intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_USER_PROFILE);
        intent.putExtra(Constants.DATA_USER, user);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        return builder;
    }

   /* private static NotificationCompat.Builder createPendingCommentsIntent(Context context, NotificationCompat.Builder builder, int type, String postId, boolean isBackground, String commentId) {
        Intent intent;
        if(isBackground) {
            intent=new Intent(context, SplashScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        else {
            intent=new Intent(context, CommentsActivity.class);
        }
        intent.putExtra(Constants.DATA_PUSH, type);
        intent.putExtra(Constants.DATA_POST_ID, postId);
        intent.putExtra(Constants.DATA_COMMENT_ID, commentId);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        return builder;
    }

    private static NotificationCompat.Builder createPendingForwardIntent(Context context, NotificationCompat.Builder builder, int type, String postId, boolean isBackground) {
        Intent intent;
        if(isBackground) {
            intent=new Intent(context, SplashScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        else {
            intent=new Intent(context, PostActivity.class);
        }
        intent.putExtra(Constants.DATA_PUSH, type);
        intent.putExtra(Constants.DATA_POST_ID, postId);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        return builder;
    }*/

    private static NotificationCompat.Builder createPendingIntent(Context context, NotificationCompat.Builder builder, int type, String message) {
        Intent splashScreenIntent=new Intent(context, SplashScreenActivity.class);
        splashScreenIntent.putExtra("PushType", type);
        splashScreenIntent.putExtra("PushMessage", message);
        splashScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, type, splashScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        return builder;
    }

    private static NotificationCompat.Builder createNotification(Context context, Notification notification, String channelID, String group) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, channelID)
                        .setSmallIcon(R.drawable.ic_cake_status)
                        .setContentTitle(notification.getNotificationTitle())
                        .setContentText(notification.getNotificationMessage())
                        .setAutoCancel(true)
                        .setGroup(group)
                        .setGroupSummary(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        return mBuilder;
    }
}
