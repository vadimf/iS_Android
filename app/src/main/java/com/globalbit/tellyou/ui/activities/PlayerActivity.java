package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.globalbit.androidutils.CollectionUtils;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityPlayerBinding;
import com.globalbit.tellyou.model.BasePostComment;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.PostsResponse;
import com.globalbit.tellyou.ui.interfaces.IGestureEventsListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by alex on 18/02/2018.
 */

public class PlayerActivity extends BaseActivity implements View.OnClickListener, IGestureEventsListener {
    private static final String TAG=PlayerActivity.class.getSimpleName();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 1000;
    private static final long HIDE_DETAILS_THRESHOLD=3000;
    private ActivityPlayerBinding mBinding;
    private BasePostComment mPost;
    private User mUser;
    private CountDownTimer mTimer;
    private ScheduledFuture<?> mScheduleFuture;
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };
    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    private final Handler mHandler = new Handler();
    private int mElapsedTime=0;
    private boolean mIsPlaying=false;
    private boolean mIsStarted=false;
    private MediaPlayer mMediaPlayer=null;
    private boolean mIsResumedActivity=false;
    private int mCurrentPosition=0;
    private int mPostIndex=0;
    private int mPage=1;
    private ArrayList<BasePostComment> mPosts;
    private User mCurrentUser=null;
    private boolean mLoadMore=true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_player);
        mBinding.layoutVideoMenu.switchAutoplay.setChecked(SharedPrefsUtils.isAutoplayNextVideo());
        mBinding.layoutVideoMenu.switchAutoplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPrefsUtils.setAutoplayNextVideo(b);
            }
        });
        mUser=SharedPrefsUtils.getUserDetails();
        mPosts=getIntent().getParcelableArrayListExtra(Constants.DATA_POSTS);
        mPostIndex=getIntent().getIntExtra(Constants.DATA_INDEX,0);
        mPage=getIntent().getIntExtra(Constants.DATA_PAGE, 1);
        mCurrentUser=getIntent().getParcelableExtra(Constants.DATA_USER);
        if(!CollectionUtils.isEmpty(mPosts)&&mPostIndex<mPosts.size()) {
            mPost=mPosts.get(mPostIndex);
        }
        if(mPost!=null) {
            mBinding.imgViewCancel.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutMenu.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutInfo.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutComments.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutShare.setOnClickListener(this);
            mBinding.layoutVideoInformation.btnAction.setOnClickListener(this);
            mBinding.layoutVideoMenu.txtViewReport.setOnClickListener(this);
            initiatePostInformation();
        }
        else {
            //TODO something wrong, the post is null, show error end finish
            Log.i(TAG, "Post is null");
        }
    }


    private void initiatePostInformation() {
        if(mPost!=null) {
            mBinding.gestureView.setGesterEventsListener(this, mPost.getVideo().getDuration());
            mBinding.txtViewViews.setText(String.format(Locale.getDefault(), "%d", mPost.getViews()));
            if(mPost.getComments()>0) {
                mBinding.layoutPlayerActions.txtViewComments.setText(String.format(Locale.getDefault(), "%d", mPost.getComments()));
            }
            else {
                mBinding.layoutPlayerActions.txtViewComments.setText("");
            }
            mBinding.layoutVideoInformation.txtViewUsername.setText(String.format(Locale.getDefault(),"@%s",mPost.getUser().getUsername()));
            if(mPost.getUser().getProfile()!=null&&mPost.getUser().getProfile().getPicture()!=null&&!StringUtils.isEmpty(mPost.getUser().getProfile().getPicture().getThumbnail())) {
                Picasso.with(this).load(mPost.getUser().getProfile().getPicture().getThumbnail()).into(mBinding.layoutVideoInformation.imgViewPhoto);
            }
            else {
                mBinding.layoutVideoInformation.imgViewPhoto.setImageResource(R.drawable.small_image_profile_default);
            }
            if(mPost.getCreatedAt()!=null) {
                mBinding.layoutVideoInformation.txtViewDate.setText(DateUtils.getRelativeTimeSpanString(mPost.getCreatedAt().getTime()));
            }
            mBinding.layoutVideoInformation.txtViewTitle.setText(mPost.getText());
            if(mUser.getUsername().equals(mPost.getUser().getUsername())) {
                mBinding.layoutVideoInformation.btnAction.setVisibility(View.GONE);
            }
            else {
                mBinding.layoutVideoInformation.btnAction.setVisibility(View.VISIBLE);
                if(mPost.getUser().isFollowing()) {
                    mBinding.layoutVideoInformation.btnAction.setBackgroundResource(R.drawable.button_share);
                    mBinding.layoutVideoInformation.btnAction.setTextColor(getResources().getColor(R.color.share));
                    mBinding.layoutVideoInformation.btnAction.setText(R.string.btn_following);
                }
                else {
                    mBinding.layoutVideoInformation.btnAction.setBackgroundResource(R.drawable.button_regular);
                    mBinding.layoutVideoInformation.btnAction.setTextColor(getResources().getColor(R.color.border_active));
                    mBinding.layoutVideoInformation.btnAction.setText(R.string.btn_follow);
                }
            }
            mElapsedTime=0;
            stopSeekbarUpdate();
            mBinding.progressBarPortrait.setMax(mPost.getVideo().getDuration()*1000);
            mBinding.progressBarPortrait.setProgress(0);
            mBinding.layoutVideoMenu.lnrLayoutVideoMenu.setVisibility(View.GONE);
            mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.VISIBLE);
            mTimer=new CountDownTimer(HIDE_DETAILS_THRESHOLD, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.GONE);
                }
            };
            mTimer.start();
            Uri uri=Uri.parse(mPost.getVideo().getUrl());
            mBinding.videoViewPlayer.setVideoURI(uri);
            mBinding.videoViewPlayer.requestFocus();
            mBinding.videoViewPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mMediaPlayer=mediaPlayer;
                    if(mIsResumedActivity) {
                        mMediaPlayer.seekTo(mCurrentPosition);
                        mIsResumedActivity=false;
                    }
                    else {
                        NetworkManager.getInstance().viewPost(new IBaseNetworkResponseListener<BaseResponse>() {
                            @Override
                            public void onSuccess(BaseResponse response) {
                                mPost.setViews(mPost.getViews()+1);
                            }

                            @Override
                            public void onError(int errorCode, String errorMessage) {

                            }
                        },mPost.getId());
                    }
                    Log.i(TAG, "onPrepared: ");
                }
            });
            mIsStarted=true;
            mBinding.videoViewPlayer.start();
            scheduleSeekbarUpdate();
            mBinding.videoViewPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopSeekbarUpdate();
                    mIsStarted=false;
                    mBinding.progressBarPortrait.setProgress(mPost.getVideo().getDuration()*1000);
                    if(mTimer!=null) {
                        mTimer.cancel();
                    }
                    if(SharedPrefsUtils.isAutoplayNextVideo()) {
                        onSwipeTop();
                    }
                }
            });
            mBinding.videoViewPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Log.i(TAG, "onError: ");
                    initiatePostInformation();
                    return true;
                }
            });
            loadItems();
        }
    }

    private void loadItems() {
        if(mLoadMore&&!CollectionUtils.isEmpty(mPosts)&&(mPosts.size()-mPostIndex)<5) {
            mPage++;
            if(mCurrentUser==null) {
                NetworkManager.getInstance().getFeedPosts(new IBaseNetworkResponseListener<PostsResponse>() {
                    @Override
                    public void onSuccess(PostsResponse response) {
                        addItems(response.getPosts());
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        mPage--;
                    }
                }, mPage);
            }
            else {
                if(mCurrentUser.getUsername().equals(mUser.getUsername())) {
                    NetworkManager.getInstance().getMyPosts(new IBaseNetworkResponseListener<PostsResponse>() {
                        @Override
                        public void onSuccess(PostsResponse response) {
                            addItems(response.getPosts());
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            mPage--;
                        }
                    }, mPage);
                }
                else {
                    NetworkManager.getInstance().getUserPosts(new IBaseNetworkResponseListener<PostsResponse>() {
                        @Override
                        public void onSuccess(PostsResponse response) {
                            addItems(response.getPosts());
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            mPage--;
                        }
                    }, mCurrentUser.getUsername(), mPage);
                }
            }
        }
    }

    private void addItems(ArrayList<Post> posts) {
        if(posts.size()==0) {
            mPage--;
            mLoadMore=false;
        }
        if(mPosts==null) {
            mPosts=new ArrayList<>();
        }
        mPosts.addAll(posts);
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgViewCancel:
                onBackPressed();
                break;
            case R.id.frmLayoutMenu:
                if(mBinding.layoutVideoMenu.lnrLayoutVideoMenu.getVisibility()==View.VISIBLE) {
                    mBinding.layoutVideoMenu.lnrLayoutVideoMenu.setVisibility(View.GONE);
                }
                else {
                    mBinding.layoutVideoMenu.lnrLayoutVideoMenu.setVisibility(View.VISIBLE);
                    mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.GONE);
                }
                break;
            case R.id.frmLayoutInfo:
                if(mTimer!=null) {
                    mTimer.cancel();
                }
                if(mBinding.layoutVideoInformation.lnrLayoutVideoInformation.getVisibility()==View.VISIBLE) {
                    mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.GONE);
                }
                else {
                    mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.VISIBLE);
                    mBinding.layoutVideoMenu.lnrLayoutVideoMenu.setVisibility(View.GONE);
                }
                break;
            case R.id.frmLayoutComments:
                //TODO open video comments
                break;
            case R.id.frmLayoutShare:
                //TODO share outside the application (Deep-linking)
                break;
            case R.id.btnAction:
                //TODO follow/un-follow user
                if(mPost.getUser().isFollowing()) {
                    NetworkManager.getInstance().unfollow(new IBaseNetworkResponseListener<BaseResponse>() {
                        @Override
                        public void onSuccess(BaseResponse response) {
                            mBinding.layoutVideoInformation.btnAction.setBackgroundResource(R.drawable.button_regular);
                            mBinding.layoutVideoInformation.btnAction.setTextColor(getResources().getColor(R.color.border_active));
                            mBinding.layoutVideoInformation.btnAction.setText(getString(R.string.btn_follow));
                            mPost.getUser().setFollowing(false);
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {

                        }
                    }, mPost.getUser().getUsername());
                }
                else {
                    NetworkManager.getInstance().follow(new IBaseNetworkResponseListener<BaseResponse>() {
                        @Override
                        public void onSuccess(BaseResponse response) {
                            mBinding.layoutVideoInformation.btnAction.setBackgroundResource(R.drawable.button_share);
                            mBinding.layoutVideoInformation.btnAction.setTextColor(getResources().getColor(R.color.share));
                            mBinding.layoutVideoInformation.btnAction.setText(getString(R.string.btn_following));
                            mPost.getUser().setFollowing(true);
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {

                        }
                    }, mPost.getUser().getUsername());

                }
                break;
            case R.id.txtViewReport:
                if(mPost!=null) {
                    if(mPost.getUser()!=null&&mPost.getUser().getUsername().equals(mUser.getUsername())) {
                        showMessage(getString(R.string.error), getString(R.string.error_reporting_your_video));
                    }
                    else {
                        mBinding.layoutVideoMenu.lnrLayoutVideoMenu.setVisibility(View.GONE);
                        Intent intent=new Intent(this, ReportActivity.class);
                        intent.putExtra(Constants.DATA_POST_ID, mPost.getId());
                        startActivityForResult(intent, Constants.REQUEST_REPORT);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_REPORT) {
            if(resultCode==RESULT_OK) {
                Snackbar snackbar=Snackbar.make(mBinding.layoutPlayerActions.lnrLayoutPlayerActions, getString(R.string.snack_bar_report_submitted), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
    }

    private void cancel() {
        mBinding.videoViewPlayer.stopPlayback();
        stopSeekbarUpdate();
        if(mTimer!=null) {
            mTimer.cancel();
        }
        if(mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
    }

    private void updateProgress() {
        //Log.i(TAG, "updateProgress: "+mElapsedTime);
        mBinding.progressBarPortrait.setProgress(mElapsedTime);
        mElapsedTime+=1000;
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
        mIsPlaying=false;
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
        mIsPlaying=true;
    }

    @Override
    public void onTap() {
        if(mIsPlaying) {
            if(mMediaPlayer!=null) {
                mMediaPlayer.pause();
            }
            stopSeekbarUpdate();
        }
        else {
            if(mMediaPlayer!=null) {
                mMediaPlayer.start();
            }
            if(!mIsStarted) {
                mBinding.progressBarPortrait.setProgress(mBinding.videoViewPlayer.getCurrentPosition());
                mElapsedTime=mBinding.videoViewPlayer.getCurrentPosition();
                mIsStarted=true;
            }
            scheduleSeekbarUpdate();
        }
    }

    @Override
    public void onHorizontalScroll(MotionEvent event, float delta, int speed) {
        if(delta<0) {
            int newPosition=mBinding.videoViewPlayer.getCurrentPosition()+speed;
            if(newPosition>mBinding.videoViewPlayer.getDuration()) {
                newPosition=mBinding.videoViewPlayer.getDuration();
            }
            Log.i(TAG, "onHorizontalScroll: "+newPosition);
            mBinding.videoViewPlayer.seekTo(newPosition);
            mElapsedTime=newPosition;
            mBinding.progressBarPortrait.setProgress(newPosition);
        }
        else {
            int newPosition=mBinding.videoViewPlayer.getCurrentPosition()-speed;
            if(newPosition<0) {
                newPosition=0;
            }
            Log.i(TAG, "onHorizontalScroll: "+newPosition);
            mBinding.videoViewPlayer.seekTo(newPosition);
            mElapsedTime=newPosition;
            mBinding.progressBarPortrait.setProgress(newPosition);
        }
    }

    @Override
    public void onVerticalScroll(MotionEvent event, float delta, int speed) {

    }

    @Override
    public void onSwipeRight() {

    }

    @Override
    public void onSwipeLeft() {

    }

    @Override
    public void onSwipeBottom() {
        if(!CollectionUtils.isEmpty(mPosts)&&mPostIndex>0) {
            cancel();
            mPostIndex--;
            Log.i(TAG, "onSwipeBottom: "+mPostIndex);
            mPost=mPosts.get(mPostIndex);
            initiatePostInformation();
        }
    }

    @Override
    public void onSwipeTop() {
        if(!CollectionUtils.isEmpty(mPosts)&&mPostIndex<(mPosts.size()-1)) {
            cancel();
            mPostIndex++;
            Log.i(TAG, "onSwipeTop: "+mPostIndex);
            mPost=mPosts.get(mPostIndex);
            initiatePostInformation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsPlaying) {
            stopSeekbarUpdate();
            if(mMediaPlayer!=null) {
                mMediaPlayer.pause();
            }
        }
        mIsResumedActivity=true;
        if(mMediaPlayer!=null) {
            mCurrentPosition=mMediaPlayer.getCurrentPosition();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
