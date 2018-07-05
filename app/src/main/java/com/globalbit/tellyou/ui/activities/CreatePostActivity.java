package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

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
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import im.ene.toro.exoplayer.Playable;
import im.ene.toro.exoplayer.ToroExo;

/**
 * Created by alex on 14/02/2018.
 */

public class CreatePostActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG=CreatePostActivity.class.getSimpleName();
    private ActivityCreatePostBinding mBinding;
    private String mVideoPath;
    private int mVideoRecordingType;
    private String mPostId;
    private Playable playerHelper;
    private boolean mIsFirstTime=true;
    private Timer mTimeoutTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_create_post);
        mVideoRecordingType=getIntent().getIntExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_POST_VIDEO_RECORDING);
        mPostId=getIntent().getStringExtra(Constants.DATA_POST_ID);
        mBinding.inputTitle.txtViewTitle.setVisibility(View.GONE);
        mBinding.inputTitle.inputValue.setHint(R.string.hint_add_title);
        mBinding.inputTitle.inputValue.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.TITLE_SIZE_MAX) } );
        switch(mVideoRecordingType) {
            case Constants.TYPE_POST_VIDEO_RECORDING:
            case Constants.TYPE_POST_VIDEO_TRIMMING:
                mBinding.inputTitle.lnrLayoutEdit.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutHashTags.setVisibility(View.VISIBLE);
                break;
            case Constants.TYPE_REPLY_VIDEO_RECORDING:
                mBinding.inputTitle.lnrLayoutEdit.setVisibility(View.GONE);
                mBinding.lnrLayoutHashTags.setVisibility(View.GONE);
                break;
        }
        mBinding.btnTellIt.setOnClickListener(this);
        mBinding.btnBack.setOnClickListener(this);
        mVideoPath=getIntent().getStringExtra(Constants.DATA_VIDEO_FILE);
        if(!StringUtils.isEmpty(mVideoPath)) {
            File file=new File(mVideoPath);
            Uri uri=Uri.fromFile(file);
            playerHelper = ToroExo.with(this).getDefaultCreator().createPlayable(uri,"mp4");
            playerHelper.prepare(true);
            playerHelper.setPlayerView(mBinding.videoViewPlayer);
            mBinding.videoViewPlayer.setOnClickListener(this);
        }
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))&&source.charAt(i)!='_') {
                        return "";
                    }
                }
                return null;
            }
        };
        mBinding.inputHashtag1.setFilters(new InputFilter[] { filter });
        mBinding.inputHashtag2.setFilters(new InputFilter[] { filter });
        mBinding.inputHashtag3.setFilters(new InputFilter[] { filter });
        mBinding.inputTitle.inputValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBinding.inputTitle.txtViewError.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnTellIt:
                if((mVideoRecordingType==Constants.TYPE_POST_VIDEO_RECORDING||mVideoRecordingType==Constants.TYPE_POST_VIDEO_TRIMMING)&&StringUtils.isEmpty(mBinding.inputTitle.inputValue.getText().toString())) {
                    mBinding.inputTitle.txtViewError.setText(R.string.error_new_video_title_empty);
                    //showMessage(getString(R.string.error),getString(R.string.error_new_video_title_empty));
                }
                else {
                    //File file=new File(mVideoPath);
                    /*Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mVideoPath,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    File gifFile=null;
                    try {
                        gifFile=GeneralUtils.createImageFile("jpg");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        thumb.compress(Bitmap.CompressFormat.JPEG, 100 *//*ignored for PNG*//*, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        FileOutputStream fos = new FileOutputStream(gifFile);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }*/

                    Intent intent=new Intent(this, UploadService.class);
                    intent.putExtra(Constants.DATA_VIDEO_FILE, mVideoPath);
                    //intent.putExtra(Constants.DATA_GIF_FILE, gifFile);
                    intent.putExtra(Constants.DATA_TEXT, mBinding.inputTitle.inputValue.getText().toString());
                    ArrayList<String> tags=new ArrayList<>();
                    if(!StringUtils.isEmpty(mBinding.inputHashtag1.getText().toString())) {
                        tags.add(mBinding.inputHashtag1.getText().toString());
                    }
                    if(!StringUtils.isEmpty(mBinding.inputHashtag2.getText().toString())) {
                        tags.add(mBinding.inputHashtag2.getText().toString());
                    }
                    if(!StringUtils.isEmpty(mBinding.inputHashtag3.getText().toString())) {
                        tags.add(mBinding.inputHashtag3.getText().toString());
                    }
                    intent.putExtra(Constants.DATA_HASHTAGS, tags);
                    intent.putExtra(Constants.DATA_DURATION, mBinding.videoViewPlayer.getPlayer().getDuration()/1000);
                    intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, mVideoRecordingType);
                    intent.putExtra(Constants.DATA_POST_ID, mPostId);
                    intent.putExtra(Constants.DATA_IS_FRONT_CAMERA, getIntent().getBooleanExtra(Constants.DATA_IS_FRONT_CAMERA, true));
                    startService(intent);
                    final MaterialDialog dialog=new MaterialDialog.Builder(this)
                            .content(R.string.dialog_video_uploading)
                            .cancelable(false)
                            /*.positiveText(R.string.btn_ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if(mTimeoutTimer != null){
                                        mTimeoutTimer.cancel();
                                        mTimeoutTimer = null;
                                    }
                                    finish();
                                }
                            })*/
                            .show();
                    TimerTask timerTask=new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(dialog!=null) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }
                            });
                        }
                    };
                    if (mTimeoutTimer == null) {
                        mTimeoutTimer = new Timer();
                    }
                    mTimeoutTimer.schedule(timerTask, 2000);
                }
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
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
                        File file=new File(mVideoPath);
                        if(file.exists()) {
                            if(file.delete()) {
                                Log.i(TAG, "File deleted successfully: "+mVideoPath);
                            }
                            else {
                                Log.i(TAG, "Couldn't delete the file: "+mVideoPath);
                            }
                        }
                        if(mVideoRecordingType!=Constants.TYPE_POST_VIDEO_TRIMMING) {
                            Intent intent=new Intent(CreatePostActivity.this, VideoRecordingActivity.class);
                            intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, mVideoRecordingType);
                            intent.putExtra(Constants.DATA_POST_ID, mPostId);
                            startActivityForResult(intent, Constants.REQUEST_VIDEO_RECORDING);
                        }
                        finish();
                    }
                })
                .negativeText(R.string.btn_cancel)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerHelper.setPlayerView(null);
        playerHelper.release();
        playerHelper = null;
        if(mTimeoutTimer != null){
            mTimeoutTimer.cancel();
            mTimeoutTimer = null;
        }
    }

    @Override protected void onStop() {
        super.onStop();
        playerHelper.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mIsFirstTime) {
            mIsFirstTime=false;
        }
        else {
            playerHelper.play();
        }
    }
}
