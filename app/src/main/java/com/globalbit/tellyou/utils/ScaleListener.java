package com.globalbit.tellyou.utils;

import android.view.ScaleGestureDetector;

/**
 * Created by alex on 19/02/2018.
 */

public class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }
}
