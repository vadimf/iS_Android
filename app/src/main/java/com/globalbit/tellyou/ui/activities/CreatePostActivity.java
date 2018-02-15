package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityCreatePostBinding;
import com.globalbit.tellyou.service.UploadService;
import com.globalbit.tellyou.utils.GeneralUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
        mVideoPath=getIntent().getStringExtra(Constants.DATA_VIDEO_FILE);
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
                if(StringUtils.isEmpty(mBinding.inputTitle.getText().toString())) {
                    showMessage(getString(R.string.error),getString(R.string.error_new_video_title_empty));
                }
                else {
                    File file=new File(mVideoPath);
                    /*NewPost post=new NewPost();
                    post.setText(mBinding.inputTitle.getText().toString());
                    post.setDuration(mBinding.seekBar.getMax()/1000);
                    File file=new File(mVideoPath);
                    RequestBody requestFile =RequestBody.create(
                            MediaType.parse("video/mp4"),
                            file
                    );*/
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mVideoPath,
                            MediaStore.Images.Thumbnails.MINI_KIND);

                    /*MediaMetadataRetriever mmRetriever = new MediaMetadataRetriever();
                    mmRetriever.setDataSource(file.getAbsolutePath());
                    Bitmap bitmap = mmRetriever.getFrameAtTime(100);*/
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

                    Intent intent=new Intent(this, UploadService.class);
                    intent.putExtra(Constants.DATA_VIDEO_FILE, file);
                    intent.putExtra(Constants.DATA_GIF_FILE, gifFile);
                    intent.putExtra(Constants.DATA_TEXT, mBinding.inputTitle.getText().toString());
                    intent.putExtra(Constants.DATA_DURATION, mBinding.seekBar.getMax()/1000);
                    startService(intent);
                    new MaterialDialog.Builder(this)
                            .content(R.string.dialog_video_uploading)
                            .cancelable(false)
                            .positiveText(R.string.btn_ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    finish();
                                }
                            })
                            .show();
                    /*RequestBody requestGif =RequestBody.create(
                            MediaType.parse("image/jpg"),
                            gifFile
                    );

                    showLoadingDialog();
                    NetworkManager.getInstance().createPost(new IBaseNetworkResponseListener<PostResponse>() {
                        @Override
                        public void onSuccess(PostResponse response) {
                            hideLoadingDialog();
                            finish();
                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            hideLoadingDialog();
                            showErrorMessage(errorCode, null, errorMessage);

                        }
                    }, MultipartBody.Part.createFormData("video", file.getName(), requestFile),
                            MultipartBody.Part.createFormData("thumbnail", gifFile.getName(), requestGif),
                            RequestBody.create(okhttp3.MultipartBody.FORM, post.getText()),
                            RequestBody.create(MultipartBody.FORM, String.valueOf(post.getDuration())));*/
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
