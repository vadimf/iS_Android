package com.globalbit.tellyou.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.globalbit.tellyou.R;

/**
 * Created by alex on 16/11/2017.
 */

public class SimpleDividerItemDecoration  extends RecyclerView.ItemDecoration {
    private Drawable mDivider;


    public SimpleDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
    }

    public SimpleDividerItemDecoration(Context context, int resource) {
        mDivider = context.getResources().getDrawable(resource);
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();


        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);


            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();


            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();


            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
