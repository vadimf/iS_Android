package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityVideoTrimmerBinding;
import com.globalbit.tellyou.ui.videotrimmer.interfaces.OnK4LVideoListener;
import com.globalbit.tellyou.ui.videotrimmer.interfaces.OnTrimVideoListener;
import com.globalbit.tellyou.utils.GeneralUtils;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.IOException;
import java.net.URISyntaxException;


public class VideoTrimmerActivity extends BaseActivity implements OnTrimVideoListener, OnK4LVideoListener {
    private static final String TAG=VideoTrimmerActivity.class.getSimpleName();
    private ActivityVideoTrimmerBinding mBinding;
    private Uri mUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_video_trimmer);
        mUri=getIntent().getParcelableExtra(Constants.DATA_URI);
        Log.i(TAG, "onCreate: "+mUri.getPath());
        mBinding.videoTrimmer.setVideoURI(mUri);
        mBinding.videoTrimmer.setMaxDuration(120);
        mBinding.videoTrimmer.setOnTrimVideoListener(this);
    }

    @Override
    public void getResult(final Uri uri) {
        Log.i(TAG, "getResult: "+uri.getPath());
        hideProgressDialog();
        Intent intent=new Intent(VideoTrimmerActivity.this, CreatePostActivity.class);
        intent.putExtra(Constants.DATA_VIDEO_FILE, uri.getPath());
        intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_POST_VIDEO_TRIMMING);
        startActivity(intent);
        finish();

    }

    @Override
    public void onTrimStarted() {
        showProgressDialog(getString(R.string.dialog_title_preparing_video), getString(R.string.dialog_loading_content));
        Log.i(TAG, "onTrimStarted: ");
    }

    @Override
    public void onError(String message) {
        Log.i(TAG, "onError: "+message);
    }

    @Override
    public void cancelAction() {
        Log.i(TAG, "cancelAction: ");
        finish();
    }

    @Override
    public void onVideoPrepared() {
        hideLoadingDialog();
        Log.i(TAG, "onVideoPrepared: ");
    }

    @Override
    public void onTrimmingUpdate(int progress) {
        updateProgressDialog(progress);
    }
}
