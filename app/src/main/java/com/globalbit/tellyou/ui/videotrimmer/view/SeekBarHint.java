package com.globalbit.tellyou.ui.videotrimmer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.TypedValue;

public class SeekBarHint extends AppCompatSeekBar {
    private String mText;
    private Paint mPaint;
    private int mLocation=0;
    private float mDelta=0;

    public SeekBarHint (Context context) {
        super(context);
        init();
    }

    public SeekBarHint (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SeekBarHint (Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mText="";
        mPaint=new Paint();
        mPaint.setColor(Color.WHITE);
        float pixel= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                14, getResources().getDisplayMetrics());
        mPaint.setTextSize(pixel);
        mDelta=mPaint.measureText("2:00");
    }

    public void setText(String s) {
        mText=s;
    }

    public void setLocation(int x) {
        mLocation=x;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        int thumb_x =(int)((double)this.getProgress()/this.getMax()* this.getWidth());
        int middle = this.getHeight()/2;
        c.drawText(mText, mLocation-mDelta/2, middle,mPaint);
    }

}
