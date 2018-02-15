package com.globalbit.tellyou.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by alex on 30/10/2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean mIsFrontCamera;
    private static final String CAMERA_PARAM_ORIENTATION = "orientation";
    private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
    private static final String CAMERA_PARAM_PORTRAIT = "portrait";
    protected Camera.Size mPreviewSize;
    private int mAngle;

    public CameraPreview(Context context, Camera camera, boolean isFrontCamera) {
        super(context);
        mCamera=camera;
        mIsFrontCamera=isFrontCamera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (IOException e) {
            Log.d("CameraPreview", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            Camera.Parameters cameraParams = mCamera.getParameters();
            boolean portrait = isPortrait();
            configureCameraParameters(cameraParams, portrait, i1, i2);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("CameraPreview", "Error starting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    protected void configureCameraParameters(Camera.Parameters cameraParams, boolean portrait, int i1, int i2) {
        int angle;
        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        display.getMetrics(displaymetrics);
        //int width=displaymetrics.widthPixels;
        //int height=displaymetrics.heightPixels;
        //int squareSize =-1;
        switch (display.getRotation()) {
            case Surface.ROTATION_0: // This is display orientation
                Log.i("CameraPreview", "ROTATION_0");
                if(mIsFrontCamera) {
                    angle=270;
                }
                else {
                    angle=90; // This is camera orientation
                }
                break;
            case Surface.ROTATION_90:
                Log.i("CameraPreview", "ROTATION_90");
                if(mIsFrontCamera) {
                    angle=180;
                }
                else {
                    angle=0; // This is camera orientation
                }
                break;
            case Surface.ROTATION_180:
                Log.i("CameraPreview", "ROTATION_180");
                if(mIsFrontCamera) {
                    angle=90;
                }
                else {
                    angle=270; // This is camera orientation
                }
                break;
            case Surface.ROTATION_270:
                Log.i("CameraPreview", "ROTATION_270");
                if(mIsFrontCamera) {
                    angle=0;
                }
                else {
                    angle=180; // This is camera orientation
                }
                break;
            default:
                angle=90;
                break;
        }
        /*if(width<height) {
            squareSize=width;
        }
        else {
            squareSize=height;
        }*/
        //Log.v("CameraPreview", "angle: " +getAngle()+" "+squareSize);
        mCamera.setDisplayOrientation(angle);

        List<Camera.Size> allSizes = cameraParams.getSupportedPreviewSizes();
        Camera.Size size = allSizes.get(0); // get top size
        for (int i = 0; i < allSizes.size(); i++) {
            //Log.i("CameraPreview", "Preview Actual Size - w: " + allSizes.get(i).width + ", h: " + allSizes.get(i).height);
            if (allSizes.get(i).width > size.width)
                size = allSizes.get(i);
        }

        List<Camera.Size> allSizesVideo = cameraParams.getSupportedPreviewSizes();
        for (int i = 0; i < allSizesVideo.size(); i++) {
            //Log.i("CameraPreview", "Preview Actual Size - w: " + allSizesVideo.get(i).width + ", h: " + allSizesVideo.get(i).height);
        }

//set max Picture Size
        cameraParams.setPictureSize(size.width, size.height);

        Log.i("CameraPreview", "Preview Actual Size - w: " + size.width + ", h: " + size.height);

        mCamera.setParameters(cameraParams);
    }


    public boolean isPortrait() {
        return (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    public int getAngle() {
        return mAngle;
    }

    public void setAngle(int angle) {
        mAngle=angle;
    }
}
