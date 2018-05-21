package com.globalbit.tellyou.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentPhoneVerificationBinding;
import com.globalbit.tellyou.model.Phone;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.AuthenticateViaSmsRequest;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.activities.DiscoverActivity;
import com.globalbit.tellyou.ui.interfaces.IPhoneListener;
import com.globalbit.tellyou.ui.interfaces.IProfileListener;

import java.util.Locale;

/**
 * Created by alex on 13/12/2017.
 */

public class PhoneVerificationFragment extends BaseFragment implements View.OnClickListener, IBaseNetworkResponseListener<BaseResponse>{
    private static final String TAG = PhoneVerificationFragment.class.getSimpleName();
    private FragmentPhoneVerificationBinding mBinding;
    private IProfileListener mListener;
    private Phone mPhone;
    //private GoogleApiClient mCredentialsApiClient;

    public static PhoneVerificationFragment newInstance() {
        PhoneVerificationFragment fragment=new PhoneVerificationFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_phone_verification, container, false);
        setCountryDialCode();
        mBinding.txtViewSkip.setOnClickListener(this);
        mBinding.btnContinue.setOnClickListener(this);
        mBinding.inputCountryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBinding.txtViewCountryName.setVisibility(View.GONE);
                mBinding.txtViewCountryName.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mBinding.inputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnContinue:
                if(StringUtils.isEmpty(mBinding.inputCountryCode.getText().toString())||StringUtils.isEmpty(mBinding.inputPhone.getText().toString())) {
                    showMessage(getString(R.string.error), getString(R.string.error_phone_number_empty));
                }
                else {
                    String countryCode=mBinding.inputCountryCode.getText().toString();
                    if(!countryCode.startsWith("+")) {
                        countryCode=getString(R.string.plus)+countryCode;
                    }
                    String phone=mBinding.inputPhone.getText().toString();
                    if(phone.startsWith("0")) {
                        phone=phone.substring(1);
                    }
                    mPhone=new Phone();
                    mPhone.setCountryCode(countryCode);
                    mPhone.setNumber(phone);
                    AuthenticateViaSmsRequest request=new AuthenticateViaSmsRequest();
                    request.setPhone(mPhone);
                    showLoadingDialog();
                    NetworkManager.getInstance().authenticateViaSms(this, request);
                }
                break;
            case R.id.txtViewSkip:
                Intent intent=new Intent(getActivity(), DiscoverActivity.class);
                intent.putExtra(Constants.DATA_FIRST_TIME, true);
                startActivity(intent);
                getActivity().finish();
                break;
        }
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
    /*// Construct a request for phone numbers and show the picker
    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                mCredentialsApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    Constants.REQUEST_PHONE_NUMBER, null, 0, 0, 0, getArguments());
        }
        catch(Exception ex){}
    }

    // Obtain the phone number from the result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_PHONE_NUMBER) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if(credential!=null) {
                    Log.i(TAG, "onActivityResult: "+credential.getId());
                }
                // credential.getId();  <-- will need to process phone number string
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestHint();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/

    @Override
    public void onSuccess(BaseResponse response, Object object) {
        hideLoadingDialog();
        if(mPhone!=null) {
            mListener.showCodeConfirmation(mPhone);
        }
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        hideLoadingDialog();
        showErrorMessage(errorCode, getString(R.string.error), errorMessage);
    }

    public void setCountryDialCode(){
        String countryId = null;
        String countryDialCode = null;

        TelephonyManager telephonyMngr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if(telephonyMngr!=null) {
            countryId=telephonyMngr.getSimCountryIso().toUpperCase();
            String[] arrContryCode=this.getResources().getStringArray(R.array.DialingCountryCode);
            for(int i=0; i<arrContryCode.length; i++) {
                String[] arrDial=arrContryCode[i].split(",");
                if(arrDial[1].trim().equals(countryId.trim())) {
                    countryDialCode=arrDial[0];
                    break;
                }
            }
        }
        if(!StringUtils.isEmpty(countryId)&&!StringUtils.isEmpty(countryDialCode)) {
            mBinding.inputCountryCode.setText(String.format(Locale.getDefault(),"%s%s", getString(R.string.plus),countryDialCode));
            mBinding.txtViewCountryName.setVisibility(View.VISIBLE);
            mBinding.txtViewCountryName.setText(String.format(Locale.getDefault(),"(%s)",countryId));
        }
    }


}
