package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityConnectionBinding;

/**
 * Created by alex on 06/11/2017.
 */

public class ConnectionActivity extends BaseActivity implements View.OnClickListener {
    private ActivityConnectionBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_connection);
        mBinding.btnSignIn.setOnClickListener(this);
        mBinding.btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnSignIn:
                Intent intentSignIn=new Intent(this,LoginActivity.class);
                intentSignIn.putExtra(Constants.DATA_LOGIN, Constants.REQUEST_SIGN_IN);
                startActivity(intentSignIn);
                finish();
                break;
            case R.id.btnSignUp:
                Intent intentSignUp=new Intent(this,LoginActivity.class);
                intentSignUp.putExtra(Constants.DATA_LOGIN, Constants.REQUEST_SIGN_UP);
                startActivity(intentSignUp);
                finish();
                break;
        }
    }
}
