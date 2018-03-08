package com.globalbit.tellyou.ui.activities;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityInformationBinding;

/**
 * Created by alex on 06/11/2017.
 */

public class InformationActivity extends BaseActivity implements View.OnClickListener{
    private ActivityInformationBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_information);
        mBinding.imgViewClose.setOnClickListener(this);
        int type=getIntent().getIntExtra(Constants.DATA_INFORMATION, Constants.REQUEST_ABOUT);
        mBinding.webView.getSettings().setJavaScriptEnabled(true);
        mBinding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingDialog();
            }
        } );
        mBinding.webView.getSettings().setDomStorageEnabled(true);
        showLoadingDialog();
        switch(type) {
            case Constants.REQUEST_ABOUT:
                mBinding.txtViewTitle.setText(getString(R.string.label_about));
                if(!StringUtils.isEmpty(CustomApplication.getSystemPreference().getPages().getAbout())) {
                    mBinding.webView.loadUrl(CustomApplication.getSystemPreference().getPages().getAbout());
                }
                break;
            case Constants.REQUEST_PRIVACY:
                mBinding.txtViewTitle.setText(getString(R.string.label_privacy_policy));
                if(!StringUtils.isEmpty(CustomApplication.getSystemPreference().getPages().getPrivacy())) {
                    mBinding.webView.loadUrl(CustomApplication.getSystemPreference().getPages().getPrivacy());
                }
                break;
            case Constants.REQUEST_TERMS_OF_USE:
                mBinding.txtViewTitle.setText(getString(R.string.label_terms_of_service));
                if(!StringUtils.isEmpty(CustomApplication.getSystemPreference().getPages().getTerms())) {
                    mBinding.webView.loadUrl(CustomApplication.getSystemPreference().getPages().getTerms());
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgViewClose:
                onBackPressed();
                break;
        }
    }
}
