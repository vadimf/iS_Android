package com.globalbit.tellyou.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by alex on 11/03/2018.
 */

public class RepliesLinearLayoutManager extends LinearLayoutManager {
    private int mParentWidth;
    private int mItemWidth;

    public RepliesLinearLayoutManager(Context context, int parentWidth, int itemWidth) {
        super(context);
    }

    public RepliesLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public RepliesLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getPaddingLeft() {
        return Math.round(mParentWidth / 2f - mItemWidth / 2f);
    }

    @Override
    public int getPaddingRight() {
        return getPaddingLeft();
    }
}
