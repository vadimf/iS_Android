package com.globalbit.tellyou.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentSignupBinding;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.FacebookRequest;
import com.globalbit.tellyou.network.requests.SignInUpRequest;
import com.globalbit.tellyou.network.responses.AuthenticateUserResponse;
import com.globalbit.tellyou.ui.interfaces.ILoginListener;
import com.globalbit.tellyou.utils.InformationClickableSpan;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

import java.util.Locale;


/**
 * Created by alex on 06/11/2017.
 */

public class SignUpFragment extends BaseFragment implements View.OnClickListener, IBaseNetworkResponseListener<AuthenticateUserResponse> {
    private static final String TAG=SignUpFragment.class.getSimpleName();
    private FragmentSignupBinding mBinding;
    private CallbackManager mCallbackManager;
    private ILoginListener mListener;

    public static SignUpFragment newInstance() {
        SignUpFragment fragment=new SignUpFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);
        mBinding.btnFacebook.setReadPermissions("email","user_friends");
        mBinding.btnFacebook.setFragment(this);
        mBinding.btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess: ");
                SharedPrefsUtils.setFacebookToken(loginResult.getAccessToken().getToken());
                FacebookRequest request=new FacebookRequest();
                request.setFacebookToken(loginResult.getAccessToken().getToken());
                NetworkManager.getInstance().facebookAuthentication(SignUpFragment.this, request);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "onError: "+error.getMessage());
            }
        });
        mBinding.btnSignUp.setOnClickListener(this);
        if(CustomApplication.getSystemPreference()!=null) {
            mBinding.inputPassword.getInputValue().setHint(String.format(Locale.getDefault(),getString(R.string.password_hint),
                    CustomApplication.getSystemPreference().getValidations().getPassword().getMinLength()));
        }
        mBinding.inputEmail.getInputValue().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                    mBinding.btnSignUp.setEnabled(true);
                    mBinding.btnSignUp.setTextColor(getResources().getColor(R.color.red_border));
                }
                else {
                    mBinding.btnSignUp.setEnabled(false);
                    mBinding.btnSignUp.setTextColor(getResources().getColor(R.color.grey_dark));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        SpannableStringBuilder spannableStringTermsOfService=new SpannableStringBuilder(getString(R.string.label_terms_of_service));
        spannableStringTermsOfService.setSpan(new InformationClickableSpan(getActivity(), Constants.REQUEST_TERMS_OF_USE), 0, spannableStringTermsOfService.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder spannableStringPrivacyPolicy=new SpannableStringBuilder(getString(R.string.label_privacy_policy));
        spannableStringPrivacyPolicy.setSpan(new InformationClickableSpan(getActivity(), Constants.REQUEST_PRIVACY), 0, spannableStringPrivacyPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder spannableString=new SpannableStringBuilder(getString(R.string.label_sign_up_agree));
        spannableString.append(" ");
        spannableString.append(spannableStringTermsOfService);
        spannableString.append("\n");
        spannableString.append("&");
        spannableString.append("\n");
        spannableString.append(spannableStringPrivacyPolicy);

        mBinding.txtViewAgreement.setText(spannableString);
        mBinding.txtViewAgreement.setHighlightColor(Color.TRANSPARENT);
        mBinding.txtViewAgreement.setMovementMethod(LinkMovementMethod.getInstance());

        return mBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnSignUp:
                if(mBinding.inputPassword.validate()) {
                    showLoadingDialog();
                    SignInUpRequest request=new SignInUpRequest();
                    request.setEmail(mBinding.inputEmail.getInputValue().getText().toString());
                    request.setPassword(mBinding.inputPassword.getInputValue().getText().toString());
                    NetworkManager.getInstance().signUp(this,request);
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ILoginListener) {
            mListener = (ILoginListener)context;
        }
        else {
            throw new ClassCastException(context.toString() + " must implement ILoginListener.");
        }
    }

    @Override
    public void onSuccess(AuthenticateUserResponse response) {
        hideLoadingDialog();
        mListener.onSignSuccess(response.getUser());
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        hideLoadingDialog();
        showMessage(getString(R.string.error), errorMessage);
    }
}
