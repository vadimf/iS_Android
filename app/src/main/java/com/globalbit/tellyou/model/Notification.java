package com.globalbit.tellyou.model;

/**
 * Created by alex on 21/11/2017.
 */

public class Notification {
    private String mNotificationMessage;
    private int mNotificationType;
    private String mNotificationTitle;
    private String mUsername;
    private String mPostId;
    private String mCommentId;

    public String getNotificationMessage() {
        return mNotificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        mNotificationMessage=notificationMessage;
    }

    public int getNotificationType() {
        return mNotificationType;
    }

    public void setNotificationType(int notificationType) {
        mNotificationType=notificationType;
    }

    public String getNotificationTitle() {
        return mNotificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        mNotificationTitle=notificationTitle;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername=username;
    }

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String postId) {
        mPostId=postId;
    }

    public String getCommentId() {
        return mCommentId;
    }

    public void setCommentId(String commentId) {
        mCommentId=commentId;
    }
}
