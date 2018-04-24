package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityLoginBinding;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.PushNotificationTokenRequest;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.fragments.ForgotPasswordFragment;
import com.globalbit.tellyou.ui.fragments.SignInFragment;
import com.globalbit.tellyou.ui.fragments.SignUpFragment;
import com.globalbit.tellyou.ui.interfaces.ILoginListener;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by alex on 06/11/2017.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, ILoginListener {
    private static final String TAG=LoginActivity.class.getSimpleName();

    private ActivityLoginBinding mBinding;
    private Enums.LoginState mLoginState;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBinding.toolbar.btnBack.setOnClickListener(this);
        int loginType=getIntent().getIntExtra(Constants.DATA_LOGIN, Constants.REQUEST_SIGN_IN);
        Fragment fragment;
        switch(loginType) {
            case Constants.REQUEST_SIGN_IN:
                mLoginState=Enums.LoginState.SignIn;
                fragment=SignInFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "SignInTag").commit();
                break;
            case Constants.REQUEST_SIGN_UP:
                mLoginState=Enums.LoginState.SignUp;
                fragment=SignUpFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "SignUpTag").commit();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        switch(mLoginState) {
            case SignUp:
            case SignIn:
                Intent intent=new Intent(this,ConnectionActivity.class);
                startActivity(intent);
                finish();
                break;
            case ForgotPassword:
                mLoginState=Enums.LoginState.SignIn;
                Fragment fragment=SignInFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "SignInTag").commit();
                //super.onBackPressed();
                break;
        }
    }


    /**
     * Sign in/up was successfully, if user details contains username show feed, otherwise show first time screen
     * @param user
     */
    @Override
    public void onSignSuccess(User user) {
        SharedPrefsUtils.setUserDetails(user);
        if(user!=null) {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if(!StringUtils.isEmpty(refreshedToken)) {
                Log.i("SharedUtils", "Token: "+refreshedToken);
                PushNotificationTokenRequest request=new PushNotificationTokenRequest();
                request.setToken(refreshedToken);
                NetworkManager.getInstance().sendToken(new IBaseNetworkResponseListener<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {

                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {

                    }
                }, request);
            }

        }
        if(StringUtils.isEmpty(user.getUsername())) {
            Log.i(TAG, "onSignSuccess: First time screen");
            Intent intent=new Intent(this, ProfileActivity.class);
            intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_EDIT_PROFILE);
            startActivity(intent);
            finish();
        }
        else {
            Log.i(TAG, "onSignSuccess: Feed");
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Show forgot password screen
     */
    @Override
    public void onForgotPassword() {
        mLoginState=Enums.LoginState.ForgotPassword;
        ForgotPasswordFragment fragment=ForgotPasswordFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "ForgotPasswordTag").commit();
    }
}
