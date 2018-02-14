package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityCreatePostBinding;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.system.NewPost;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.PostResponse;
import com.globalbit.tellyou.utils.Enums;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by alex on 14/02/2018.
 */

public class CreatePostActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG=CreatePostActivity.class.getSimpleName();
    private ActivityCreatePostBinding mBinding;
    private String mVideoPath;
    private boolean mIsPlaying=false;
    private boolean mIsStarted=false;
    private int mElapsedTime=0;
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 1000;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_create_post);
        mBinding.btnTellIt.setOnClickListener(this);
        mBinding.btnBack.setOnClickListener(this);
        mBinding.imgViewPlay.setOnClickListener(this);
        //mBinding.frmLayoutController.setOnClickListener(this);
        mVideoPath=getIntent().getStringExtra(Constants.DATA_VIDEO_PATH);
        if(!StringUtils.isEmpty(mVideoPath)) {
            File file=new File(mVideoPath);
            Uri uri=Uri.fromFile(file);
            mBinding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.i(TAG, "onPrepared: "+mediaPlayer.getDuration());
                    mBinding.seekBar.setMax(mediaPlayer.getDuration());
                }
            });
            mBinding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopSeekbarUpdate();
                    mIsPlaying=false;
                    mIsStarted=false;
                }
            });
            mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    mBinding.videoView.seekTo(i);
                    mElapsedTime=i;
                    Log.i(TAG, "onProgressChanged: "+i);
                    if(!mIsPlaying) {
                        mBinding.imgViewPlay.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mBinding.videoView.setVideoURI(uri);
            mBinding.videoView.seekTo(100);

            //mBinding.videoView.setZOrderOnTop(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnTellIt:
                //TODO send the new video to server
                if(StringUtils.isEmpty(mBinding.inputTitle.getText().toString())) {
                    showMessage(getString(R.string.error),getString(R.string.error_new_video_title_empty));
                }
                else {
                    NewPost post=new NewPost();
                    post.setText(mBinding.inputTitle.getText().toString());
                    post.setDuration(mBinding.seekBar.getMax());
                    String json=new Gson().toJson(post);
                    File file=new File(mVideoPath);

// URI to your video file
                    Uri myVideoUri = Uri.parse(file.toString());

// MediaMetadataRetriever instance
                    MediaMetadataRetriever mmRetriever = new MediaMetadataRetriever();
                    mmRetriever.setDataSource(file.getAbsolutePath());

// Array list to hold your frames
                    ArrayList<Bitmap> frames = new ArrayList<>();

//Create a new Media Player
                    MediaPlayer mp = MediaPlayer.create(getBaseContext(), myVideoUri);

// Some kind of iteration to retrieve the frames and add it to Array list
                    Bitmap bitmap = mmRetriever.getFrameAtTime(1000);
                    Bitmap bitmap2 = mmRetriever.getFrameAtTime(2000);
                    Bitmap bitmap3 = mmRetriever.getFrameAtTime(3000);
                    Bitmap bitmap4 = mmRetriever.getFrameAtTime(4000);
                    Bitmap bitmap5 = mmRetriever.getFrameAtTime(5000);
                    frames.add(bitmap);
                    frames.add(bitmap2);
                    frames.add(bitmap3);
                    frames.add(bitmap4);
                    frames.add(bitmap5);

                    AnimationDrawable animatedGIF = new AnimationDrawable();

                    animatedGIF.addFrame(new BitmapDrawable(getResources(),bitmap), 50);
                    animatedGIF.addFrame(new BitmapDrawable(getResources(),bitmap2), 50);
                    animatedGIF.addFrame(new BitmapDrawable(getResources(),bitmap3), 50);
                    animatedGIF.addFrame(new BitmapDrawable(getResources(),bitmap4), 50);
                    animatedGIF.addFrame(new BitmapDrawable(getResources(),bitmap5), 50);


                    NetworkManager.getInstance().createPost(new IBaseNetworkResponseListener<PostResponse>() {
                        @Override
                        public void onSuccess(PostResponse response) {

                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {

                        }
                    }, MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)),
                            MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file)),
                            RequestBody.create(MediaType.parse("multipart/form-data"), json));
                }
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.imgViewPlay:
                if(mIsStarted) {
                    mBinding.videoView.resume();
                }
                else {
                    mBinding.videoView.start();
                    mIsStarted=true;
                    scheduleSeekbarUpdate();
                }
                mBinding.videoView.start();
                mIsStarted=true;
                scheduleSeekbarUpdate();
                mIsPlaying=true;
                mBinding.imgViewPlay.setVisibility(View.GONE);
                break;
        }
    }

    private void updateProgress() {
        Log.i(TAG, "updateProgress: "+mElapsedTime);
        mBinding.seekBar.setProgress(mElapsedTime);
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

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .content(R.string.dialog_discard_video)
                .positiveText(R.string.btn_discard)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        stopSeekbarUpdate();
                        File file=new File(mVideoPath);
                        if(file.exists()) {
                            if(file.delete()) {
                                Log.i(TAG, "File deleted successfully: "+mVideoPath);
                            }
                            else {
                                Log.i(TAG, "Couldn't delete the file: "+mVideoPath);
                            }
                        }
                        Intent intent=new Intent(CreatePostActivity.this, VideoRecordingActivity.class);
                        startActivityForResult(intent, Constants.REQUEST_VIDEO_RECORDING);
                        finish();
                    }
                })
                .negativeText(R.string.btn_cancel)
                .show();
    }
}
