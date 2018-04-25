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
import com.globalbit.tellyou.ui.videocompressor.SiliCompressor;
import com.globalbit.tellyou.ui.videotrimmer.interfaces.OnK4LVideoListener;
import com.globalbit.tellyou.ui.videotrimmer.interfaces.OnTrimVideoListener;
import com.globalbit.tellyou.utils.GeneralUtils;

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
    public void getResult(Uri uri) {
        Log.i(TAG, "getResult: "+uri.getPath());
        try {
            String filePath = SiliCompressor.with(this).compressVideo(uri.getPath(), Environment.getExternalStorageDirectory().getPath(), 0, 0, 0);
            Log.i(TAG, "getResult: "+filePath);
            Intent intent=new Intent(this, CreatePostActivity.class);
            intent.putExtra(Constants.DATA_VIDEO_FILE, filePath);
            intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_POST_VIDEO_TRIMMING);
            startActivity(intent);
            finish();
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrimStarted() {
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
        Log.i(TAG, "onVideoPrepared: ");
    }
}
