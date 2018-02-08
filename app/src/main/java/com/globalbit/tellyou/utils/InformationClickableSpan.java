package com.globalbit.tellyou.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.ui.activities.InformationActivity;

/**
 * Created by alex on 26/11/2017.
 */

public class InformationClickableSpan extends ClickableSpan {
    private Context mContext;
    private int mInformationType;


    public InformationClickableSpan(Context context, int informationType) {
        mContext=context;
        mInformationType=informationType;
    }

    @Override
    public void onClick(View view) {
        switch(mInformationType) {
            case Constants.REQUEST_TERMS_OF_USE:
                Intent intentTermsOfUse=new Intent(mContext, InformationActivity.class);
                intentTermsOfUse.putExtra(Constants.DATA_INFORMATION, Constants.REQUEST_TERMS_OF_USE);
                mContext.startActivity(intentTermsOfUse);
                break;
            case Constants.REQUEST_PRIVACY:
                Intent intentPrivacyPolicy=new Intent(mContext, InformationActivity.class);
                intentPrivacyPolicy.putExtra(Constants.DATA_INFORMATION, Constants.REQUEST_PRIVACY);
                mContext.startActivity(intentPrivacyPolicy);
                break;
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        int linkColor = ContextCompat.getColor(mContext, R.color.border_active);
        ds.setColor(linkColor);
        ds.setUnderlineText(true);
        ds.bgColor=ContextCompat.getColor(mContext, android.R.color.transparent);
    }
}
