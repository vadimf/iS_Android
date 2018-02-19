package com.globalbit.tellyou.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.coremedia.iso.boxes.Container;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityVideoRecordingBinding;
import com.globalbit.tellyou.ui.views.CameraPreview;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ObservableHelper;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by alex on 14/02/2018.
 */

public class VideoRecordingActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG=VideoRecordingActivity.class.getSimpleName();
    private ActivityVideoRecordingBinding mBinding;
    private static final int MAX_VIDEO_LENGTH=120000;
    private static final int ELAPSED_WARNING=10000;
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 0;
    public static final int MEDIA_TYPE_VIDEO = 1;
    private Camera mCamera;
    private int mCamerId=-1;
    private CameraPreview mPreview;
    private boolean mIsFrontCamera=false;
    private MediaRecorder mRecorder;
    private boolean mIsRecording=false;
    private ArrayList<String> mFilesPath=new ArrayList<>();
    private OrientationEventListener mOrientationListener;
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
    private Enums.RecordingState mRecordingState=Enums.RecordingState.NoPermissions;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_video_recording);
        mBinding.lnrLayoutVideoRecordingActions.imgViewExit.setOnClickListener(this);
        mBinding.lnrLayoutVideoRecordingActions.imgViewRecordStop.setOnClickListener(this);
        mBinding.lnrLayoutVideoRecordingActions.imgViewReShoot.setOnClickListener(this);
        mBinding.lnrLayoutVideoRecordingActions.imgViewSwitchCamera.setOnClickListener(this);
        mBinding.lnrLayoutVideoRecordingActions.imgViewFinish.setOnClickListener(this);
        setRecordingState();
        mBinding.progressBarPortrait.setMax(MAX_VIDEO_LENGTH);
        if(checkCameraHardware()) {
            checkForPermissions(1, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
        }
        else {
            new MaterialDialog.Builder(this)
                    .content("Your device doesn't have camera, you cannot record videos")
                    .cancelable(false)
                    .negativeText(R.string.btn_exit)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        switch(mRecordingState) {
            case Initial:
            case NoPermissions:
                finish();
                break;
            case Recording:
                break;
            case Stopped:
            case Finished:
                new MaterialDialog.Builder(this)
                        .content(R.string.dialog_discard_video)
                        .positiveText(R.string.btn_discard)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .negativeText(R.string.btn_cancel)
                        .show();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgViewExit:
                onBackPressed();
                break;
            case R.id.imgViewRecordStop:
                if(mIsRecording) {
                    if(mRecorder!=null) {
                        mRecorder.stop();
                        mIsRecording=false;
                        stopSeekbarUpdate();
                    }
                    mRecordingState=Enums.RecordingState.Stopped;
                    setRecordingState();
                }
                else {
                    mRecordingState=Enums.RecordingState.Recording;
                    setRecordingState();
                    if(mRecorder==null) {
                        mRecorder=new MediaRecorder();
                    }
                    try {
                        mCamera.unlock();
                        mRecorder.setCamera(mCamera);
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                        if(mIsFrontCamera) {
                            if(mPreview.getAngle()==0) {
                                mRecorder.setOrientationHint(90);
                            }
                            else if(mPreview.getAngle()==180) {
                                mRecorder.setOrientationHint(270);
                            }
                            else {
                                mRecorder.setOrientationHint(mPreview.getAngle());
                            }
                        }
                        else {
                            mRecorder.setOrientationHint(mPreview.getAngle()+90);
                        }

                        CamcorderProfile profile = null;
                        if (!mIsFrontCamera) {
                            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
                        } else {
                            try {
                                if(CamcorderProfile.hasProfile(mCamerId, CamcorderProfile.QUALITY_480P)) {
                                    profile=CamcorderProfile.get(mCamerId, CamcorderProfile.QUALITY_480P);
                                } else {
                                    profile=CamcorderProfile.get(mCamerId, CamcorderProfile.QUALITY_LOW);
                                }
                            }
                            catch(Exception ex) {
                                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
                            }
                        }
                        mRecorder.setProfile(profile);
                        mRecorder.setVideoEncodingBitRate(1000000);
                        mRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
                        String filePath=getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
                        mFilesPath.add(filePath);
                        Log.i("Test", "onClick: "+filePath);
                        mRecorder.setOutputFile(filePath);
                        mRecorder.prepare();
                        mRecorder.start();
                        mIsRecording=true;
                        scheduleSeekbarUpdate();
                        mBinding.lnrLayoutPortrait.setVisibility(View.VISIBLE);
                    }
                    catch (IllegalStateException e) {
                        Log.i("Test", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
                        if(mRecorder!=null) {
                            mRecorder.release();
                            mCamera.lock();
                        }
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                        if(mRecorder!=null) {
                            mRecorder.release();
                            mCamera.lock();
                        }
                    }
                }
                break;
            case R.id.imgViewReShoot:
                new MaterialDialog.Builder(this)
                        .content(R.string.dialog_reshoot_video)
                        .positiveText(R.string.btn_reshoot)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mRecordingState=Enums.RecordingState.Initial;
                                setRecordingState();
                                mElapsedTime=0;
                                deleteTempFiles(mFilesPath);
                                mFilesPath.clear();
                                mBinding.lnrLayoutPortrait.setVisibility(View.GONE);
                                mBinding.viewTimePortrait.setText(DateUtils.formatElapsedTime(mElapsedTime/1000));
                                mBinding.progressBarPortrait.setProgress(mElapsedTime);

                            }
                        })
                        .negativeText(R.string.btn_cancel)
                        .show();
                break;
            case R.id.imgViewFinish:
                //TODO process the video (show process loader) and open posting activity
                showLoadingDialog();
                //mergeMediaFiles(false, mFilesPath, getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
                mDisposable.add(ObservableHelper.mergeVideoFilesObservable(mFilesPath, getOutputMediaFile(MEDIA_TYPE_VIDEO).toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<String>() {
                            @Override
                            public void onNext(String videoPath) {
                                hideLoadingDialog();
                                Intent intent=new Intent(VideoRecordingActivity.this, CreatePostActivity.class);
                                intent.putExtra(Constants.DATA_VIDEO_FILE, videoPath);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        }));
                break;
            case R.id.imgViewSwitchCamera:
                mIsFrontCamera=!mIsFrontCamera;
                if(mCamera!=null) {
                    try {
                        mCamera.stopPreview();
                        mCamera.release();
                    }
                    catch(Exception ex){}
                }
                mCamera=getCameraInstance(mIsFrontCamera);
                mPreview = new CameraPreview(this, mCamera, mIsFrontCamera);
                mBinding.cameraPreview.removeAllViews();
                mBinding.cameraPreview.addView(mPreview);
                break;
        }
    }

    @Override
    protected void permissionAccepted() {
        // Create an instance of Camera
        mCamera = getCameraInstance(false);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera, mIsFrontCamera);
        mBinding.cameraPreview.addView(mPreview);
        mRecordingState=Enums.RecordingState.Initial;
        setRecordingState();
    }

    @Override
    protected void permissionDeclined() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_title_permissions_declined)
                .content(R.string.dialog_accept_video_recording_permissions)
                .cancelable(false)
                .positiveText(R.string.btn_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        checkForPermissions(1, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
                    }
                })
                .negativeText(R.string.btn_exit)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Test", "onDestroy: ");
        if(mRecorder!=null) {
            try {
                mRecorder.stop();
            }
            catch(Exception ex) {}
            mRecorder.release();
        }
        if(mCamera!=null) {
            try {
                mCamera.stopPreview();
            } catch(Exception e) {
                // ignore: tried to stop a non-existent preview
            }
            mCamera.release();
        }
        if(mOrientationListener!=null) {
            mOrientationListener.disable();
        }
        stopSeekbarUpdate();
        deleteTempFiles(mFilesPath);
        mDisposable.clear();
    }

    private void deleteTempFiles(ArrayList<String> filesPath) {
        for(int i=0; i<filesPath.size(); i++) {
            String filePath=filesPath.get(i);
            File file=new File(filePath);
            if(file.exists()) {
                if(file.delete()) {
                    Log.i(TAG, "File deleted successfully: "+filePath);
                }
                else {
                    Log.i(TAG, "Couldn't delete the file: "+filePath);
                }
            }
        }
    }

    private void setRecordingState() {
        switch(mRecordingState) {
            case Initial:
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutExit.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutRecordStop.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.imgViewRecordStop.setImageResource(R.drawable.ic_fiber_manual_record_black_24dp);
                mBinding.lnrLayoutVideoRecordingActions.imgViewReShoot.setVisibility(View.INVISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutFinish.setVisibility(View.GONE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutSwitchCamera.setVisibility(View.VISIBLE);
                break;
            case Recording:
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutExit.setVisibility(View.GONE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutRecordStop.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.imgViewRecordStop.setImageResource(R.drawable.ic_stop_black_24dp);
                mBinding.lnrLayoutVideoRecordingActions.imgViewReShoot.setVisibility(View.INVISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutFinish.setVisibility(View.GONE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutSwitchCamera.setVisibility(View.GONE);
                break;
            case Stopped:
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutExit.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutRecordStop.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.imgViewRecordStop.setImageResource(R.drawable.ic_fiber_manual_record_black_24dp);
                mBinding.lnrLayoutVideoRecordingActions.imgViewReShoot.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutFinish.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutSwitchCamera.setVisibility(View.GONE);
                break;
            case Finished:
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutExit.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutRecordStop.setVisibility(View.INVISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.imgViewReShoot.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutFinish.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutSwitchCamera.setVisibility(View.GONE);
                break;
            case NoPermissions:
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutExit.setVisibility(View.VISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutRecordStop.setVisibility(View.INVISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.imgViewReShoot.setVisibility(View.INVISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutFinish.setVisibility(View.INVISIBLE);
                mBinding.lnrLayoutVideoRecordingActions.frmLayoutSwitchCamera.setVisibility(View.GONE);
                break;
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance(boolean isFront){
        int cameraCount = 0;
        Camera c = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
            Camera.getCameraInfo( camIdx, cameraInfo );
            if (isFront && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
                try {
                    c = Camera.open( camIdx );
                    mCamerId=camIdx;
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
            else if(!isFront&&cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    c = Camera.open( camIdx );
                    mCamerId=camIdx;
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        super.setRequestedOrientation(requestedOrientation);

        int rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.rotationAnimation = rotationAnimation;
        win.setAttributes(winParams);

    }

    /** Create a File for saving an video */
    public  static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Tellyou");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void updateProgress() {
        Log.i(TAG, "updateProgress: "+mElapsedTime);
        mBinding.viewTimePortrait.setText(DateUtils.formatElapsedTime(mElapsedTime/1000));
        mBinding.progressBarPortrait.setProgress(mElapsedTime);
        if(mElapsedTime>=(MAX_VIDEO_LENGTH-ELAPSED_WARNING)) {
            mBinding.progressBarPortrait.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_end));
        }
        if(mElapsedTime>=MAX_VIDEO_LENGTH) {
            if(mRecorder!=null) {
                mRecorder.stop();
                mIsRecording=false;
                stopSeekbarUpdate();
                mRecordingState=Enums.RecordingState.Finished;
                setRecordingState();
            }
        }
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

    public boolean mergeMediaFiles(boolean isAudio, ArrayList<String> sourceFiles, String targetFile) {
        Log.i("Test", "mergeMediaFiles: "+targetFile);
        try {
            //String mediaKey = isAudio ? "soun" : "vide";
            List<Movie> listMovies = new ArrayList<>();
            for (String filename : sourceFiles) {
                listMovies.add(MovieCreator.build(filename));
            }
            //List<Track> listTracks = new LinkedList<>();
            List<Track> videoTracks = new LinkedList<Track>();
            List<Track> audioTracks = new LinkedList<Track>();
            for (Movie movie : listMovies) {
                for (Track track : movie.getTracks()) {
                    if (track.getHandler().equals("soun")) {
                        audioTracks.add(track);
                    }
                    if (track.getHandler().equals("vide")) {
                        videoTracks.add(track);
                    }
                }
            }
            Movie outputMovie = new Movie();
            /*if (!listTracks.isEmpty()) {
                outputMovie.addTrack(new AppendTrack(listTracks.toArray(new Track[listTracks.size()])));
            }*/
            if (audioTracks.size() > 0) {
                outputMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                outputMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
            }
            Container container = new DefaultMp4Builder().build(outputMovie);
            FileChannel fileChannel = new RandomAccessFile(String.format(targetFile), "rw").getChannel();
            container.writeContainer(fileChannel);
            fileChannel.close();
            deleteTempFiles(sourceFiles);
            return true;
        }
        catch (IOException e) {
            Log.e("Test", "Error merging media files. exception: "+e.getMessage());
            return false;
        }
    }
}
