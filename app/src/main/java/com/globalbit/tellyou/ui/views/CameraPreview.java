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
    private Camera.CameraInfo mCameraInfo;
    private static final String CAMERA_PARAM_ORIENTATION = "orientation";
    private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
    private static final String CAMERA_PARAM_PORTRAIT = "portrait";
    protected Camera.Size mPreviewSize;
    private int mAngle;
    private ISurfaceListener mListener;

    public CameraPreview(Context context, Camera camera, Camera.CameraInfo cameraInfo, ISurfaceListener listener) {
        super(context);
        mCamera=camera;
        mCameraInfo=cameraInfo;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mListener=listener;
    }

    public void setCamera(Camera camera) {
        mCamera=camera;
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
        Log.i("Test", "surfaceChanged: ");
        /*if(mListener!=null) {
            mListener.show();
        }*/
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
        if(mListener!=null) {
            mListener.hide();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    protected void configureCameraParameters(Camera.Parameters cameraParams, boolean portrait, int i1, int i2) {
        //Log.v("CameraPreview", "angle: " +getAngle()+" "+squareSize);
        mCamera.setDisplayOrientation(getCorrectCameraOrientation());

        List<Camera.Size> allSizes = cameraParams.getSupportedPreviewSizes();
        Camera.Size size = allSizes.get(0); // get top size
        for (int i = 0; i < allSizes.size(); i++) {
            //Log.i("CameraPreview", "Preview Actual Size - w: " + allSizes.get(i).width + ", h: " + allSizes.get(i).height);
            if (allSizes.get(i).width > size.width)
                size = allSizes.get(i);
        }


//set max Picture Size
        cameraParams.setPictureSize(size.width, size.height);

        Log.i("CameraPreview", "Preview Actual Size - w: " + size.width + ", h: " + size.height);

        mCamera.setParameters(cameraParams);
    }

    public int getCorrectCameraOrientation() {

        int rotation = ((Activity)getContext()).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch(rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;

        }

        int result;
        Log.i("Test", "Orintation: "+rotation);
        Log.i("Test", "Camerainfo: "+mCameraInfo.orientation);
        if(mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
            result = (mCameraInfo.orientation + degrees) % 360;
            result = (360-result)% 360;
        }else{
            result = (mCameraInfo.orientation-degrees+360)%360;
        }
        Log.i("Test", "getCorrectCameraOrientation: "+result);
        return result;
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

    public interface  ISurfaceListener {
        void show();
        void hide();
    }
}
