package com.globalbit.tellyou.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.globalbit.tellyou.ui.interfaces.IGestureEventsListener;
import com.globalbit.tellyou.utils.GestureListener;

/**
 * Created by alex on 19/02/2018.
 */

public class GestureControllerView extends View {
    private GestureDetector gestureDetector;
    private IGestureEventsListener listener;

    public GestureControllerView(Context context) {
        super(context);
    }

    public GestureControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickable(true);
        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mayNotifyGestureDetector(event);
        return true;
    }

    private void mayNotifyGestureDetector(MotionEvent event){
        gestureDetector.onTouchEvent(event);
    }

    public void setGesterEventsListener(IGestureEventsListener listener){
        gestureDetector = new GestureDetector(getContext(), new GestureListener(listener, ViewConfiguration.get(getContext())));
    }
}
