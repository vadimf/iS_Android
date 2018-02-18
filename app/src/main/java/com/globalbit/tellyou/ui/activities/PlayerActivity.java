package com.globalbit.tellyou.ui.activities;

import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityPlayerBinding;
import com.globalbit.tellyou.model.BasePostComment;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.events.FollowingEvent;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by alex on 18/02/2018.
 */

public class PlayerActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG=PlayerActivity.class.getSimpleName();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 1000;
    private ActivityPlayerBinding mBinding;
    private BasePostComment mPost;
    private User mUser;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_player);
        mUser=SharedPrefsUtils.getUserDetails();
        mPost=getIntent().getParcelableExtra(Constants.DATA_POST);
        if(mPost!=null) {
            mBinding.imgViewCancel.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutMenu.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutInfo.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutComments.setOnClickListener(this);
            mBinding.layoutPlayerActions.frmLayoutShare.setOnClickListener(this);
            mBinding.layoutVideoInformation.btnAction.setOnClickListener(this);
            initiatePostInformation();
        }
        else {
            //TODO something wrong, the post is null, show error end finish
            Log.i(TAG, "Post is null");
        }
    }

    private void initiatePostInformation() {
        if(mPost!=null) {
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
            Uri uri=Uri.parse(mPost.getVideo().getUrl());
            mBinding.videoViewPlayer.setVideoURI(uri);
            mBinding.videoViewPlayer.requestFocus();
            mBinding.videoViewPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mBinding.videoViewPlayer.start();
                    NetworkManager.getInstance().viewPost(new IBaseNetworkResponseListener<BaseResponse>() {
                        @Override
                        public void onSuccess(BaseResponse response) {

                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {

                        }
                    },mPost.getId());
                    scheduleSeekbarUpdate();
                }
            });
            mBinding.videoViewPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopSeekbarUpdate();
                }
            });
            mBinding.videoViewPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    Log.i(TAG, "onError: ");
                    return false;
                }
            });
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgViewCancel:
                onBackPressed();
                break;
            case R.id.frmLayoutMenu:
                //TODO show menu;
                break;
            case R.id.frmLayoutInfo:
                //TODO show video details
                if(mBinding.layoutVideoInformation.lnrLayoutVideoInformation.getVisibility()==View.VISIBLE) {
                    mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.GONE);
                }
                else {
                    mBinding.layoutVideoInformation.lnrLayoutVideoInformation.setVisibility(View.VISIBLE);
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
                            EventBus.getDefault().post(new FollowingEvent(true));
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {

                        }
                    }, mPost.getUser().getUsername());

                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mBinding.videoViewPlayer.stopPlayback();
        stopSeekbarUpdate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.videoViewPlayer.stopPlayback();
    }

    private void updateProgress() {
        Log.i(TAG, "updateProgress: "+mElapsedTime);
        mBinding.progressBarPortrait.setProgress(mElapsedTime);
        mElapsedTime+=1000;
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
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
    }
}
