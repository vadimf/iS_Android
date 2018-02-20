package com.globalbit.tellyou.utils;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.globalbit.tellyou.ui.interfaces.IGestureEventsListener;

/**
 * Created by alex on 19/02/2018.
 */

public class GestureListener implements GestureDetector.OnGestureListener {
    public static final String TAG = GestureListener.class.getSimpleName();
    private static final int SWIPE_THRESHOLD = 100;
    private int minFlingVelocity;
    private IGestureEventsListener mListener;
    private int mTimeMultiplier;


    public GestureListener(IGestureEventsListener listener, ViewConfiguration viewConfiguration, int timeMultiplier) {
        mListener=listener;
        minFlingVelocity=viewConfiguration.getScaledMinimumFlingVelocity();
        mTimeMultiplier=timeMultiplier;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.i(TAG, "onDown: ");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        Log.i(TAG, "Show Press");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        mListener.onTap();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //Log.i(TAG, "Scroll "+v+","+v1);
        float speed=Math.abs(v)*mTimeMultiplier;
        float deltaY = motionEvent1.getY() - motionEvent.getY();
        float deltaX = motionEvent1.getX() - motionEvent.getX();
        //Log.i(TAG, "Delta X "+deltaX);

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                mListener.onHorizontalScroll(motionEvent1, v,(int)speed);
                if (v < 0) {
                    Log.i(TAG, "Slide right");
                } else {
                    Log.i(TAG, "Slide left");
                }
            }
        } else {
            if (Math.abs(deltaY) > SWIPE_THRESHOLD) {
                mListener.onVerticalScroll(motionEvent1, v1, (int)speed);
                if (v1 < 0) {
                    //Log.i(TAG, "Slide down");
                } else {
                    //Log.i(TAG, "Slide up");
                }
            }
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.i(TAG, "onLongPress: ");
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //Log.i(TAG, "Fling");
        boolean result = false;
        try {
            float diffY = motionEvent1.getY() - motionEvent.getY();
            float diffX = motionEvent1.getX() - motionEvent.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(v) > minFlingVelocity) {
                    if (diffX > 0) {
                        mListener.onSwipeRight();
                    } else {
                        mListener.onSwipeLeft();
                    }
                }
                result = true;
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(v1) > minFlingVelocity) {
                if (diffY > 0) {
                    mListener.onSwipeBottom();
                } else {
                    mListener.onSwipeTop();
                }
            }
            result = true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
}
