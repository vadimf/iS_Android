package com.globalbit.tellyou.ui.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentForgotPasswordBinding;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.ForgotPasswordRequest;
import com.globalbit.tellyou.network.responses.BaseResponse;

/**
 * Created by alex on 06/11/2017.
 */

public class ForgotPasswordFragment extends BaseFragment implements View.OnClickListener, IBaseNetworkResponseListener<BaseResponse> {
    private FragmentForgotPasswordBinding mBinding;

    public static ForgotPasswordFragment newInstance() {
        ForgotPasswordFragment fragment=new ForgotPasswordFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container,false);
        mBinding.btnResetPassword.setOnClickListener(this);
        mBinding.inputEmail.txtViewTitle.setText(R.string.hint_email);
        mBinding.inputEmail.inputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mBinding.inputEmail.inputValue.setHint(R.string.label_email_hint);
        mBinding.inputEmail.inputValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(android.util.Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                    mBinding.btnResetPassword.setEnabled(true);
                    mBinding.btnResetPassword.setTextColor(getResources().getColor(R.color.red_border));
                }
                else {
                    mBinding.btnResetPassword.setEnabled(false);
                    mBinding.btnResetPassword.setTextColor(getResources().getColor(R.color.grey_dark));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnResetPassword:
                showLoadingDialog();
                ForgotPasswordRequest request=new ForgotPasswordRequest();
                request.setEmail(mBinding.inputEmail.inputValue.getText().toString());
                NetworkManager.getInstance().forgotPassword(this, request);
                break;
        }
    }

    @Override
    public void onSuccess(BaseResponse response) {
        hideLoadingDialog();
        mBinding.inputEmail.inputValue.setText("");
        //showMessage(getString(R.string.dialog_title_forgot_password),getString(R.string.dialog_content_forgot_password));
        new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_title_forgot_password)
                .content(R.string.dialog_content_forgot_password)
                .positiveText(R.string.btn_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        getActivity().onBackPressed();
                    }
                })
                .show();

    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        hideLoadingDialog();
        showMessage(getString(R.string.error), errorMessage);
    }
}
