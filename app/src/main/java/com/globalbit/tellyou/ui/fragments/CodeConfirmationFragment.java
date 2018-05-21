package com.globalbit.tellyou.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentCodeConfirmationBinding;
import com.globalbit.tellyou.model.Phone;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.AuthenticateViaSmsRequest;
import com.globalbit.tellyou.network.requests.VerifySmsAuthenticationRequest;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.activities.DiscoverActivity;
import com.globalbit.tellyou.ui.interfaces.IProfileListener;

import java.util.Locale;

/**
 * Created by alex on 13/12/2017.
 */

public class CodeConfirmationFragment extends BaseFragment implements View.OnClickListener, IBaseNetworkResponseListener<BaseResponse>{
    private FragmentCodeConfirmationBinding mBinding;
    private Phone mPhone;
    private IProfileListener mListener;


    public static CodeConfirmationFragment newInstance(Phone phone) {
        CodeConfirmationFragment fragment=new CodeConfirmationFragment();
        Bundle args=new Bundle();
        args.putParcelable(Constants.DATA_PHONE, phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            mPhone=getArguments().getParcelable(Constants.DATA_PHONE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_code_confirmation, container, false);
        if(mPhone!=null) {
            mBinding.txtViewPhoneNumber.setText(String.format(Locale.getDefault(), "%s%s", mPhone.getCountryCode(), mPhone.getNumber()));
        }
        mBinding.btnBack.setOnClickListener(this);
        mBinding.txtViewSkip.setOnClickListener(this);
        mBinding.btnContinue.setOnClickListener(this);
        mBinding.txtViewResendCode.setOnClickListener(this);
        mBinding.inputCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBinding.txtViewError.setVisibility(View.GONE);
                if(charSequence.length()>0) {
                    mBinding.btnContinue.setEnabled(true);
                    mBinding.btnContinue.setTextColor(getResources().getColor(R.color.red_border));
                }
                else {
                    mBinding.btnContinue.setEnabled(false);
                    mBinding.btnContinue.setTextColor(getResources().getColor(R.color.grey_dark));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return mBinding.getRoot();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IProfileListener) {
            mListener = (IProfileListener) context;
        }
        else {
            throw new ClassCastException(context.toString() + " must implement IProfileListener.");
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnBack:
                mListener.showPhoneVerification();
                break;
            case R.id.txtViewResendCode:
                AuthenticateViaSmsRequest request=new AuthenticateViaSmsRequest();
                request.setPhone(mPhone);
                showLoadingDialog();
                NetworkManager.getInstance().authenticateViaSms(new IBaseNetworkResponseListener<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response, Object object) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        hideLoadingDialog();
                        showErrorMessage(errorCode,getString(R.string.error), errorMessage);
                    }
                }, request);
                break;
            case R.id.btnContinue:
                mBinding.txtViewError.setVisibility(View.GONE);
                if(StringUtils.isEmpty(mBinding.inputCode.getText().toString())) {
                    showMessage(getString(R.string.error), getString(R.string.error_confirmation_code_empty));
                }
                else {
                    VerifySmsAuthenticationRequest requestCode=new VerifySmsAuthenticationRequest();
                    requestCode.setCode(mBinding.inputCode.getText().toString());
                    showLoadingDialog();
                    NetworkManager.getInstance().verifySmsAuthentication(this, requestCode);
                }
                break;
            case R.id.txtViewSkip:
                Intent intent=new Intent(getActivity(), DiscoverActivity.class);
                intent.putExtra(Constants.DATA_FIRST_TIME, true);
                startActivity(intent);
                getActivity().finish();
        }
    }

    @Override
    public void onSuccess(BaseResponse response, Object object) {
        hideLoadingDialog();
        Intent intent=new Intent(getActivity(), DiscoverActivity.class);
        intent.putExtra(Constants.DATA_FIRST_TIME, true);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        hideLoadingDialog();
        mBinding.txtViewError.setVisibility(View.VISIBLE);
        //showMessage(getString(R.string.error), errorMessage);
    }


}
