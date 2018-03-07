package com.globalbit.tellyou.ui.activities;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityReportBinding;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.ReportRequest;
import com.globalbit.tellyou.network.responses.BaseResponse;

/**
 * Created by alex on 20/02/2018.
 */

public class ReportActivity extends BaseActivity implements View.OnClickListener, IBaseNetworkResponseListener<BaseResponse> {
    private ActivityReportBinding mBinding;
    private int mReportReason=-1;
    private String mPostId=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_report);
        mBinding.imgViewClose.setOnClickListener(this);
        mBinding.btnReport.setOnClickListener(this);
        mBinding.lnrLayoutReportReasonSpam.setOnClickListener(this);
        mBinding.lnrLayoutReportReasonInappropriate.setOnClickListener(this);
        mBinding.lnrLayoutReportReasonNotLike.setOnClickListener(this);
        mPostId=getIntent().getStringExtra(Constants.DATA_POST_ID);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgViewClose:
                onBackPressed();
                break;
            case R.id.btnReport:
                if(mReportReason!=-1&&!StringUtils.isEmpty(mPostId)) {
                    ReportRequest reportRequest=new ReportRequest();
                    reportRequest.setReason(mReportReason);
                    showLoadingDialog();
                    NetworkManager.getInstance().reportPost(this, mPostId, reportRequest);
                }
                break;
            case R.id.lnrLayoutReportReasonSpam:
                selectReportReason(1);
                break;
            case R.id.lnrLayoutReportReasonInappropriate:
                selectReportReason(2);
                break;
            case R.id.lnrLayoutReportReasonNotLike:
                selectReportReason(3);
                break;
        }
    }

    private void selectReportReason(int reportReason) {
        mReportReason=reportReason;
        switch(mReportReason) {
            case 1:
                mBinding.imgViewReportReasonSpam.setVisibility(View.VISIBLE);
                mBinding.imgViewReportReasonInappropriate.setVisibility(View.INVISIBLE);
                mBinding.imgViewReportReasonNotLike.setVisibility(View.INVISIBLE);
                mBinding.btnReport.setEnabled(true);
                mBinding.btnReport.setTextColor(getResources().getColor(R.color.red_border));
                break;
            case 2:
                mBinding.imgViewReportReasonSpam.setVisibility(View.INVISIBLE);
                mBinding.imgViewReportReasonInappropriate.setVisibility(View.VISIBLE);
                mBinding.imgViewReportReasonNotLike.setVisibility(View.INVISIBLE);
                mBinding.btnReport.setEnabled(true);
                mBinding.btnReport.setTextColor(getResources().getColor(R.color.red_border));
                break;
            case 3:
                mBinding.imgViewReportReasonSpam.setVisibility(View.INVISIBLE);
                mBinding.imgViewReportReasonInappropriate.setVisibility(View.INVISIBLE);
                mBinding.imgViewReportReasonNotLike.setVisibility(View.VISIBLE);
                mBinding.btnReport.setEnabled(true);
                mBinding.btnReport.setTextColor(getResources().getColor(R.color.red_border));
                break;
        }
    }

    @Override
    public void onSuccess(BaseResponse response) {
        hideLoadingDialog();
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        hideLoadingDialog();
        showErrorMessage(errorCode, getString(R.string.error), errorMessage);
    }
}
