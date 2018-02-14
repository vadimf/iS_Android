package com.globalbit.tellyou;

/**
 * Created by alex on 09/11/2017.
 */

public class Constants {
    //Sizes
    public static final int QUESTION_SIZE_MAX=140;
    public static final int TEXT_VOTE_SIZE_MAX=25;
    public static final int IMAGE_CAPTION_SIZE_MAX=15;
    public static final int QUESTION_TEXT_OPTION_MAX=5;
    public static final int QUESTION_IMAGE_OPTION_MAX=4;
    public static final int QUESTION_TEXT_OPTION_MIN=2;
    public static final int QUESTION_IMAGE_OPTION_MIN=2;


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
    public static final String DATA_FOLLOW="DataFollow";
    public static final String DATA_PHONE="DataPhone";
    public static final String DATA_FRIENDS_TYPE="DataFriendsType";
    public static final String DATA_COMMENT_ID="DataCommentId";


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

    public static final int TYPE_FRIENDS_FACEBOOK=1;
    public static final int TYPE_FRIENDS_CONTACTS=2;

}
