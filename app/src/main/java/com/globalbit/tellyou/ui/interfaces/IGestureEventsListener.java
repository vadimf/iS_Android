package com.globalbit.tellyou.ui.interfaces;

import android.view.MotionEvent;

/**
 * Created by alex on 19/02/2018.
 */

public interface IGestureEventsListener {
    void onTap();

    void onHorizontalScroll(MotionEvent event, float delta, int speed);

    void onVerticalScroll(MotionEvent event, float delta, int speed);

    void onSwipeRight();

    void onSwipeLeft();

    void onSwipeBottom();

    void onSwipeTop();
}
