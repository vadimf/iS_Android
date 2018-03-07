package com.globalbit.tellyou.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
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
import com.globalbit.tellyou.databinding.FragmentSigninBinding;
import com.globalbit.tellyou.model.InputData;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.FacebookRequest;
import com.globalbit.tellyou.network.requests.SignInUpRequest;
import com.globalbit.tellyou.network.responses.AuthenticateUserResponse;
import com.globalbit.tellyou.ui.interfaces.ILoginListener;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.InformationClickableSpan;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.globalbit.tellyou.utils.ValidationUtils;

import java.util.Locale;

/**
 * Created by alex on 06/11/2017.
 */

public class SignInFragment extends BaseFragment implements View.OnClickListener, IBaseNetworkResponseListener<AuthenticateUserResponse> {
    private static final String TAG=SignInFragment.class.getSimpleName();
    private FragmentSigninBinding mBinding;
    private CallbackManager mCallbackManager;
    private ILoginListener mListener;
    private boolean mIsPasswordVisible=false;
    private Typeface mTypeface;

    public static SignInFragment newInstance() {
        SignInFragment fragment=new SignInFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        mTypeface = ResourcesCompat.getFont(getActivity(), R.font.assistant_regular);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_signin, container, false);
        mBinding.btnFacebook.setReadPermissions("email","user_friends");
        mBinding.btnFacebook.setFragment(this);
        mBinding.btnFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess: ");
                SharedPrefsUtils.setFacebookToken(loginResult.getAccessToken().getToken());
                FacebookRequest request=new FacebookRequest();
                request.setFacebookToken(loginResult.getAccessToken().getToken());
                NetworkManager.getInstance().facebookAuthentication(SignInFragment.this, request);
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
        mBinding.btnSignIn.setOnClickListener(this);
        mBinding.txtViewForgotPassword.setOnClickListener(this);
        if(CustomApplication.getSystemPreference()!=null) {
            mBinding.inputPassword.inputValue.setHint(String.format(Locale.getDefault(),getString(R.string.password_hint),
                    CustomApplication.getSystemPreference().getValidations().getPassword().getMinLength()));
        }
        mBinding.inputEmail.txtViewTitle.setText(R.string.hint_email);
        mBinding.inputEmail.inputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mBinding.inputEmail.inputValue.setHint(R.string.label_email_hint);
        mBinding.inputPassword.txtViewTitle.setText(R.string.hint_password);
        mBinding.inputPassword.imgViewEye.setOnClickListener(this);
        mBinding.inputEmail.inputValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                    mBinding.btnSignIn.setEnabled(true);
                    mBinding.btnSignIn.setTextColor(getResources().getColor(R.color.red_border));
                }
                else {
                    mBinding.btnSignIn.setEnabled(false);
                    mBinding.btnSignIn.setTextColor(getResources().getColor(R.color.grey_dark));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mBinding.inputPassword.inputValue.setTypeface(mTypeface);
        mBinding.inputPassword.inputValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0) {

                }
                else {
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBinding.inputPassword.txtViewError.setText("");
                if(charSequence.length()>0) {
                    mBinding.inputPassword.imgViewEye.setVisibility(View.VISIBLE);
                    if(mIsPasswordVisible) {
                        mBinding.inputPassword.inputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    } else {
                        mBinding.inputPassword.inputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    mBinding.inputPassword.inputValue.setSelection(mBinding.inputPassword.inputValue.getText().length());
                }
                else {
                    if(i1>0) {
                        mBinding.inputPassword.imgViewEye.setVisibility(View.GONE);
                        mIsPasswordVisible=false;
                        mBinding.inputPassword.imgViewEye.setImageResource(R.drawable.ic_eye_open_normal);
                        mBinding.inputPassword.inputValue.setTypeface(mTypeface);
                        mBinding.inputPassword.inputValue.setTransformationMethod(new PasswordTransformationMethod());
                    }
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

        SpannableStringBuilder spannableString=new SpannableStringBuilder(getString(R.string.label_sign_in_agree));
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
            case R.id.btnSignIn:
                InputData inputData=new InputData();
                inputData.setName(mBinding.inputPassword.txtViewTitle.getText().toString());
                inputData.setValue(mBinding.inputPassword.inputValue.getText().toString());
                inputData.setInputType(Enums.InputType.Password);
                String errorMessage=ValidationUtils.validate(inputData);
                if(StringUtils.isEmpty(errorMessage)) {
                    showLoadingDialog();
                    SignInUpRequest request=new SignInUpRequest();
                    request.setEmail(mBinding.inputEmail.inputValue.getText().toString());
                    request.setPassword(mBinding.inputPassword.inputValue.getText().toString());
                    NetworkManager.getInstance().sigIn(this,request);
                }
                else {
                    mBinding.inputPassword.txtViewError.setText(errorMessage);
                }
                break;
            case R.id.txtViewForgotPassword:
                mListener.onForgotPassword();
                break;
            case R.id.imgViewEye:
                if(mIsPasswordVisible) {
                    mIsPasswordVisible=false;
                    mBinding.inputPassword.inputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mBinding.inputPassword.imgViewEye.setImageResource(R.drawable.ic_eye_open_normal);
                }
                else {
                    mIsPasswordVisible=true;
                    mBinding.inputPassword.inputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mBinding.inputPassword.imgViewEye.setImageResource(R.drawable.ic_eye_open_active);
                }
                mBinding.inputPassword.inputValue.setSelection(mBinding.inputPassword.inputValue.getText().length());
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
