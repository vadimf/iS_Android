package com.globalbit.tellyou;

/**
 * Created by alex on 09/11/2017.
 */

public class Constants {
    //Sizes
    public static final int TITLE_SIZE_MAX=140;
    public static final int NAME_SIZE_MAX=50;
    public static final int BIO_SIZE_MAX=120;
    public static final int USERNAME_SIZE_MAX=25;
    public static final int POST_VIDEO_MAX_SIZE=120000;
    public static final int REPLY_VIDEO_MAX_SIZE=30000;


    //Requests
    public static final int REQUEST_VIDEO_RECORDING=100;
    public static final int REQUEST_ABOUT=101;
    public static final int REQUEST_PRIVACY=102;
    public static final int REQUEST_TERMS_OF_USE=103;
    public static final int REQUEST_CAMERA=104;
    public static final int REQUEST_GALLERY=105;
    public static final int REQUEST_USER_PROFILE=106;
    public static final int REQUEST_EDIT_PROFILE=107;
    public static final int REQUEST_SELECT_FOLLOWERS=108;
    public static final int REQUEST_CROP_IMAGE=109;
    public static final int REQUEST_SIGN_IN=110;
    public static final int REQUEST_SIGN_UP=111;
    public static final int REQUEST_DISCOVER=112;
    public static final int REQUEST_COMMENTS=113;
    public static final int REQUEST_CAKE_IT=114;
    public static final int REQUEST_FOLLOWERS=115;
    public static final int REQUEST_FOLLOWING=116;
    public static final int REQUEST_POST=117;
    public static final int REQUEST_PHONE_NUMBER=118;
    public static final int REQUEST_CONTACTS=119;
    public static final int REQUEST_PROFILE=120;
    public static final int REQUEST_VIDEO_PLAYER=121;
    public static final int REQUEST_REPORT=122;
    public static final int REQUEST_REPORT_POST=123;
    public static final int REQUEST_REPORT_REPLY=124;
    public static final int REQUEST_VIDEO_SELECT=125;
    public static final int REQUEST_VIDEO_TRIMMER=126;
    public static final int REQUEST_SEARCH=127;


    //Data
    public static final String DATA_INFORMATION="DataInformation";
    public static final String DATA_USER="DataUser";
    public static final String DATA_PUSH="DataPush";
    public static final String DATA_FEED="DataFeed";
    public static final String DATA_PROFILE="DataProfile";
    public static final String DATA_SEARCH="DataSearch";
    public static final String DATA_USERS="DataUsers";
    public static final String DATA_IMAGE_URI="DataImageUri";
    public static final String DATA_IMAGE="DataImage";
    public static final String DATA_IMAGE_CAPTION="DataImageCaption";
    public static final String DATA_LOGIN="DataLogin";
    public static final String DATA_FIRST_TIME="DataFirstTime";
    public static final String DATA_POST_ID="DataPostId";
    public static final String DATA_POST_COMMENTS_COUNT="DataPostCommentsCount";
    public static final String DATA_POST="DataPost";
    public static final String DATA_POSTS="DataPosts";
    public static final String DATA_INDEX="DataIndex";
    public static final String DATA_PAGE="DataPage";
    public static final String DATA_FOLLOW="DataFollow";
    public static final String DATA_PHONE="DataPhone";
    public static final String DATA_FRIENDS_TYPE="DataFriendsType";
    public static final String DATA_COMMENT_ID="DataCommentId";
    public static final String DATA_VIDEO_FILE="DataVideoFile";
    public static final String DATA_GIF_FILE="DataGifFile";
    public static final String DATA_TEXT="Text";
    public static final String DATA_HASHTAGS="HashTags";
    public static final String DATA_URI="Uri";
    public static final String DATA_WIDTH="Width";
    public static final String DATA_HEIGHT="Height";
    public static final String DATA_DURATION="Duration";
    public static final String DATA_VIDEO_RECORDING_TYPE="DataVideoRecordingType";
    public static final String DATA_REPORT_TYPE="DataReportType";
    public static final String DATA_USERS_FOLLOW_STATUS="DataUsersFollowStatus";
    public static final String DATA_HOME_TYPE="HomeType";
    public static final String DATA_IS_FRONT_CAMERA="IsFrontCamera"; //true-front, false-rear



    //Types
    public static final int TYPE_POST_GENERAL=2;
    public static final int TYPE_POST_TEXT=0;
    public static final int TYPE_POST_PHOTO=1;
    public static final int TYPE_POST_SHARE=4;
    public static final int TYPE_POST_CAKE_IT=5;

    public static final int TYPE_IMAGE_GENERAL=0;
    public static final int TYPE_IMAGE_PROFILE=1;
    public static final int TYPE_IMAGE_PHOTO_POLL=2;

    public static final int TYPE_FEED_HOME=0;
    public static final int TYPE_FEED_USER=1;
    public static final int TYPE_FEED_BOOKMARKS=2;
    public static final int TYPE_FEED_SEARCH=3;

    public static final int TYPE_FRIENDS_FACEBOOK=1;
    public static final int TYPE_FRIENDS_CONTACTS=2;

    public static final int TYPE_USERS_SUGGESTIONS=1;
    public static final int TYPE_USERS_SEARCH=2;

    public static final int TYPE_POST_VIDEO_RECORDING=0;
    public static final int TYPE_REPLY_VIDEO_RECORDING=1;
    public static final int TYPE_POST_VIDEO_TRIMMING=2;


    //Analytics
    public static final String UPLOADED_VIDEO_GALLERY="uploaded_video_gallery";
    public static final String UPLOADED_VIDEO_FRONT="uploaded_video_front";
    public static final String UPLOADED_VIDEO_REAR="uploaded_video_rear";
    public static final String UPLOADED_REPLY="uploaded_reply";
    public static final String UPLOADED_VIDEO_TITLE_LENGTH="uploaded_video_title_length";
    public static final String UPLOADED_VIDEO_HASH_TAGS_COUNT="uploaded_video_hash_tags_count";
    public static final String SEARCH="search";
    public static final String VIDEO_SHARE="video_share";
    public static final String REPLIES_CLICKED="replies_clicked";
    public static final String REPLY_PLAYED="reply_played";
    public static final String VIDEO_FORWARDED_REWINDED="video_forwarded";
    public static final String VIDEO_INFORMATION_CLICKED="video_information_clicked";

}
