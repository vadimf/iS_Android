package com.globalbit.tellyou.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentEditProfileBinding;
import com.globalbit.tellyou.model.Picture;
import com.globalbit.tellyou.model.Profile;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.UserRequest;
import com.globalbit.tellyou.network.responses.UserResponse;
import com.globalbit.tellyou.network.responses.UsernameExistResponse;
import com.globalbit.tellyou.ui.activities.CropActivity;
import com.globalbit.tellyou.ui.activities.DiscoverActivity;
import com.globalbit.tellyou.ui.dialogs.BirthdayPickerDialogFragment;
import com.globalbit.tellyou.utils.GeneralUtils;
import com.globalbit.tellyou.utils.ObservableHelper;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.shawnlin.numberpicker.NumberPicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by alex on 06/11/2017.
 */

public class EditProfileFragment extends BaseFragment implements View.OnClickListener, IBaseNetworkResponseListener<UserResponse> {
    private static final String TAG=EditProfileFragment.class.getSimpleName();
    private FragmentEditProfileBinding mBinding;
    private Pattern mUserNamePattern;
    private User mUser;
    private File mFile;
    private Uri mUri;
    private String mBirthDay=null;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private Calendar mCurrentDate=Calendar.getInstance();

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment=new EditProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(CustomApplication.getSystemPreference()!=null) {
            mUserNamePattern=Pattern.compile(CustomApplication.getSystemPreference().getValidations().getUsername().getRegex());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        if(CustomApplication.getSystemPreference()!=null) {
            mBinding.inputName.inputValue.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.NAME_SIZE_MAX) } );
            mBinding.inputName.inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
            mBinding.inputName.txtViewTitle.setText(R.string.label_name);
            mBinding.inputName.inputValue.setHint(R.string.hint_full_name);
            mBinding.inputBio.inputValue.setFilters(new InputFilter[] { new InputFilter.LengthFilter(CustomApplication.getSystemPreference().getValidations().getBio().getMaxLength()) } );
            mBinding.inputBio.txtViewTitle.setText(R.string.label_bio);
            mBinding.inputBio.inputValue.setHint(R.string.hint_bio);
            mBinding.inputUsername.inputValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(CustomApplication.getSystemPreference().getValidations().getUsername().getMaxLength())});
            mBinding.inputUsername.inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
            mBinding.inputUsername.txtViewTitle.setText(R.string.label_username);
            mBinding.inputUsername.inputValue.setHint(R.string.hint_user_name);
            mBinding.inputUsername.inputValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.length()>0) {
                        if(charSequence.length()>=CustomApplication.getSystemPreference().getValidations().getUsername().getMinLength()
                                &&charSequence.length()<=CustomApplication.getSystemPreference().getValidations().getUsername().getMaxLength()) {
                            if(mUser!=null/*&&!StringUtils.isEmpty(mUser.getUsername())*/&&!mUser.getUsername().equals(mBinding.inputUsername.inputValue.getText().toString())) {
                                NetworkManager.getInstance().usernameExist(new IBaseNetworkResponseListener<UsernameExistResponse>() {
                                    @Override
                                    public void onSuccess(UsernameExistResponse response, Object object) {
                                        if(mBinding.inputUsername.inputValue.getText().toString().equals(response.getUsername())) {
                                            if(response.isExists()) {
                                                
                                                mBinding.inputUsername.txtViewError.setText(R.string.validation_username_taken);
                                                mBinding.btnContinue.setTextColor(getResources().getColor(R.color.grey_dark));
                                                mBinding.btnContinue.setEnabled(false);
                                            } else {
                                                mBinding.inputUsername.txtViewError.setText("");
                                                mBinding.btnContinue.setEnabled(true);
                                                mBinding.btnContinue.setTextColor(getResources().getColor(R.color.red_border));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(int errorCode, String errorMessage) {
                                        if(!StringUtils.isEmpty(mBinding.inputUsername.inputValue.getText().toString())) {
                                            
                                            mBinding.btnContinue.setEnabled(false);
                                            mBinding.btnContinue.setTextColor(getResources().getColor(R.color.grey_dark));
                                            mBinding.inputUsername.txtViewError.setText(String.format(Locale.getDefault(), getString(R.string.error_username_response)
                                                    , CustomApplication.getSystemPreference().getValidations().getUsername().getMinLength()
                                                    , CustomApplication.getSystemPreference().getValidations().getUsername().getMaxLength()));
                                        } else {
                                            mBinding.inputUsername.txtViewError.setText("");
                                            mBinding.btnContinue.setEnabled(false);
                                            mBinding.btnContinue.setTextColor(getResources().getColor(R.color.grey_dark));
                                        }
                                    }
                                }, charSequence.toString());
                            }
                            else {
                                mBinding.inputUsername.txtViewError.setText("");
                                mBinding.btnContinue.setEnabled(true);
                                mBinding.btnContinue.setTextColor(getResources().getColor(R.color.red_border));
                            }

                        }
                        else {
                            if(!StringUtils.isEmpty(mBinding.inputUsername.inputValue.getText().toString())) {
                                
                                mBinding.btnContinue.setEnabled(false);
                                mBinding.btnContinue.setTextColor(getResources().getColor(R.color.border_inactive));
                                mBinding.inputUsername.txtViewError.setText(String.format(Locale.getDefault(), getString(R.string.error_username_response)
                                        , CustomApplication.getSystemPreference().getValidations().getUsername().getMinLength()
                                        , CustomApplication.getSystemPreference().getValidations().getUsername().getMaxLength()));
                            } else {
                                mBinding.inputUsername.txtViewError.setText("");
                                mBinding.btnContinue.setEnabled(false);
                                mBinding.btnContinue.setTextColor(getResources().getColor(R.color.grey_dark));
                            }
                        }
                    }
                    else {
                        mBinding.inputUsername.txtViewError.setText("");
                        mBinding.btnContinue.setEnabled(false);
                        mBinding.btnContinue.setTextColor(getResources().getColor(R.color.grey_dark));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            mBinding.inputName.inputValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mBinding.inputName.txtViewError.setText("");
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        else {
            mBinding.btnContinue.setEnabled(true);
            mBinding.btnContinue.setTextColor(getResources().getColor(R.color.grey_dark));
        }
        mUser=SharedPrefsUtils.getUserDetails();
        if(mUser!=null&&mUser.getProfile()!=null) {
            String fullName=null;
            if(!StringUtils.isEmpty(mUser.getProfile().getFirstName())&&!StringUtils.isEmpty(mUser.getProfile().getLastName())) {
                fullName=String.format(Locale.getDefault(),"%s %s", mUser.getProfile().getFirstName(), mUser.getProfile().getLastName());
            }
            else if(!StringUtils.isEmpty(mUser.getProfile().getFirstName())) {
                fullName=mUser.getProfile().getFirstName();
            }
            else if(!StringUtils.isEmpty(mUser.getProfile().getLastName())) {
                fullName=mUser.getProfile().getLastName();
            }
            if(!StringUtils.isEmpty(fullName)) {
                mBinding.inputName.inputValue.setText(fullName);
            }
            mBinding.inputBio.inputValue.setText(mUser.getProfile().getBio());
            if(mUser.getProfile().getPicture()!=null&&!StringUtils.isEmpty(mUser.getProfile().getPicture().getThumbnail())) {
                Picasso.with(getActivity()).load(mUser.getProfile().getPicture().getThumbnail()).into(mBinding.imgViewPhoto);
            }
            mBinding.inputUsername.inputValue.setText(mUser.getUsername());
            if(!StringUtils.isEmpty(mUser.getProfile().getBirthday())) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date =null;
                try {
                    date=sdf.parse(mUser.getProfile().getBirthday());
                } catch(ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                mBinding.inputBirthday.setText(String.format(Locale.getDefault(), "%s %02d, %d", months[cal.get(Calendar.MONTH)], cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR)));
                mBirthDay=String.format(Locale.getDefault(), "%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
            }
        }
        if(StringUtils.isEmpty(mUser.getUsername())) {
            mBinding.btnContinue.setText(R.string.btn_continue);
        }
        else {
            mBinding.btnContinue.setText(R.string.btn_save_profile);
        }
        mBinding.btnContinue.setOnClickListener(this);
        mBinding.lnrLayoutProfileImage.setOnClickListener(this);
        mBinding.lnrLayoutBirthday.setOnClickListener(this);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnContinue:
                if(validate()) {
                    showLoadingDialog();
                    final User user=new User();
                    user.setProfile(new Profile());
                    user.setUsername(mBinding.inputUsername.inputValue.getText().toString());
                    user.getProfile().setBirthday(mBirthDay);
                    String[] nameArray=mBinding.inputName.inputValue.getText().toString().trim().split(" ");
                    if(nameArray.length==1) {
                        user.getProfile().setFirstName(nameArray[0]);
                        user.getProfile().setLastName(null);
                    }
                    else if(nameArray.length==2) {
                        user.getProfile().setFirstName(nameArray[0]);
                        user.getProfile().setLastName(nameArray[1]);
                    }
                    else if(nameArray.length>2) {
                        user.getProfile().setFirstName(nameArray[0]);
                        StringBuilder builder=new StringBuilder();
                        for(int i=1; i<nameArray.length; i++) {
                            builder.append(nameArray[i]);
                            builder.append(" ");
                        }
                        user.getProfile().setLastName(builder.toString().trim());
                    }
                    user.getProfile().setBio(mBinding.inputBio.inputValue.getText().toString());
                    user.setCreatedAt(mUser.getCreatedAt());
                    if(mUri!=null) {
                        user.getProfile().setPicture(new Picture());
                        mDisposable.add(ObservableHelper.imageObservable(getActivity(), mUri)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(new DisposableObserver<String>() {
                                    @Override
                                    public void onNext(String image) {
                                        user.getProfile().getPicture().setUpload(image);
                                        UserRequest request=new UserRequest();
                                        request.setUser(user);
                                        NetworkManager.getInstance().updateUserDetails(EditProfileFragment.this, request);
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                }));
                    }
                    else {
                        UserRequest request=new UserRequest();
                        request.setUser(user);
                        NetworkManager.getInstance().updateUserDetails(EditProfileFragment.this, request);
                    }
                }
                break;
            case R.id.lnrLayoutProfileImage:
                final MaterialDialog dialog=new MaterialDialog.Builder(getActivity())
                        .customView(R.layout.dialog_image_selection, false)
                        .show();
                dialog.findViewById(R.id.lnrLayoutCamera).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        checkForPermissions(1, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA});

                    }
                });
                dialog.findViewById(R.id.lnrLayoutGallery).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        checkForPermissions(2, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
                    }
                });
                break;
            case R.id.lnrLayoutBirthday:
                final MaterialDialog dialogBirthday=new MaterialDialog.Builder(getActivity())
                        .customView(R.layout.dialog_birthday_picker, false)
                        .positiveText(R.string.btn_set)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                NumberPicker pickerDays=(NumberPicker) dialog.findViewById(R.id.pickerDays);
                                NumberPicker pickerMonths=(NumberPicker) dialog.findViewById(R.id.pickerMonths);
                                NumberPicker pickerYears=(NumberPicker) dialog.findViewById(R.id.pickerYears);
                                setDate(pickerDays, pickerMonths, pickerYears);
                            }
                        })
                        .negativeText(R.string.btn_cancel)
                        .show();
                Typeface typeface = ResourcesCompat.getFont(getActivity(), R.font.assistant_regular);
                final NumberPicker pickerDays=(NumberPicker) dialogBirthday.findViewById(R.id.pickerDays);
                final NumberPicker pickerMonths=(NumberPicker) dialogBirthday.findViewById(R.id.pickerMonths);
                final NumberPicker pickerYears=(NumberPicker) dialogBirthday.findViewById(R.id.pickerYears);
                pickerDays.setTypeface(typeface);
                pickerMonths.setTypeface(typeface);
                pickerYears.setTypeface(typeface);
                prepareDatePicker(pickerDays, pickerMonths, pickerYears);
                break;
        }
    }


    @Override
    protected void permissionAccepted() {
        super.permissionAccepted();
        if(PERMISSION_REQUEST==1) {
            try {
                mFile=GeneralUtils.createImageFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
            if(mFile!=null) {
                Uri uri=FileProvider.getUriForFile(getActivity(), CustomApplication.getAppContext().getPackageName()+".com.globalbit.tellyou.provider", mFile);
                if(uri!=null) {
                    Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(takePictureIntent.resolveActivity(getActivity().getPackageManager())!=null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(takePictureIntent, Constants.REQUEST_CAMERA);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_GALLERY) {
            if(resultCode==Activity.RESULT_OK) {
                mUri=data.getData();
                cropImage();
            }
            mFile=null;
        }
        else if(requestCode==Constants.REQUEST_CAMERA) {
            if(resultCode==Activity.RESULT_OK) {
                mUri=Uri.fromFile(mFile);
                cropImage();
            }
            mFile=null;
        }
        else if(requestCode==Constants.REQUEST_CROP_IMAGE&&resultCode==Activity.RESULT_OK) {
            if(data!=null) {
                mUri=data.getParcelableExtra(Constants.DATA_IMAGE_URI);
                Picasso.with(getActivity()).load(mUri).into(mBinding.imgViewPhoto);
            }
        }
    }

    private boolean validate() {
        boolean isValid=true;
        if(CustomApplication.getSystemPreference()!=null) {
            if(mBinding.inputUsername.inputValue.getText().length()>CustomApplication.getSystemPreference().getValidations().getUsername().getMaxLength()
                ||mBinding.inputUsername.inputValue.getText().length()<CustomApplication.getSystemPreference().getValidations().getUsername().getMinLength()) {
                mBinding.inputUsername.txtViewError.setText(String.format(Locale.getDefault(), getString(R.string.error_username_length)
                        ,CustomApplication.getSystemPreference().getValidations().getUsername().getMinLength()
                        ,CustomApplication.getSystemPreference().getValidations().getUsername().getMaxLength()));
                isValid=false;
            }
            Pattern pattern=Pattern.compile(CustomApplication.getSystemPreference().getValidations().getUsername().getRegex());
            Matcher m=pattern.matcher(mBinding.inputUsername.inputValue.getText());
            if(!m.matches()) {
                mBinding.inputUsername.txtViewError.setText(String.format(Locale.getDefault(),"%s",getString(R.string.error_username)));
                isValid=false;
            }
        }
        if(StringUtils.isEmpty(mBinding.inputName.inputValue.getText().toString())) {
            mBinding.inputName.txtViewError.setText(String.format(Locale.getDefault(),"%s",getString(R.string.error_name_empty)));
            isValid=false;
        }
        if(mBinding.inputName.inputValue.getText().length()<3) {
            mBinding.inputName.txtViewError.setText(String.format(Locale.getDefault(),"%s",getString(R.string.error_name_short)));
            isValid=false;
        }
        if(StringUtils.isEmpty(mBirthDay)) {
            mBinding.txtViewBirthdayError.setText(String.format(Locale.getDefault(),"%s",getString(R.string.error_birthday_empty)));
            isValid=false;
        }

        return isValid;
    }

    @Override
    public void onSuccess(UserResponse response, Object object) {
        hideLoadingDialog();
        SharedPrefsUtils.setUserDetails(response.getUser());
        if(StringUtils.isEmpty(mUser.getUsername())) {
            Intent intent=new Intent(getActivity(), DiscoverActivity.class);
            intent.putExtra(Constants.DATA_FIRST_TIME, true);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }

    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        hideLoadingDialog();
        new MaterialDialog.Builder(getActivity())
                .title(R.string.error)
                .content(String.format(Locale.getDefault(),"%d %s",errorCode, errorMessage))
                .positiveText(R.string.btn_ok)
                .show();
    }

    private void cropImage() {
        if(mUri!=null) {
            Intent intent=new Intent(getActivity(), CropActivity.class);
            intent.putExtra(Constants.DATA_IMAGE_URI, mUri);
            intent.putExtra(Constants.DATA_IMAGE, Constants.TYPE_IMAGE_PROFILE);
            startActivityForResult(intent, Constants.REQUEST_CROP_IMAGE);
        }
    }

    private void prepareDatePicker(final NumberPicker pickerDays, final NumberPicker pickerMonths, final NumberPicker pickerYears) {
        pickerMonths.setMinValue(1);
        pickerMonths.setMaxValue(months.length);
        pickerMonths.setDisplayedValues(months);
        pickerMonths.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal==1||newVal==3||newVal==5||newVal==7||newVal==8||newVal==10||newVal==12) {
                    pickerDays.setMaxValue(31);
                }
                else if(newVal==2) {
                    int year=pickerYears.getValue();
                    if(year%4==0) {
                        pickerDays.setMaxValue(29);
                        if(pickerDays.getValue()>29) {
                            pickerDays.setValue(29);
                        }
                    }
                    else {
                        pickerDays.setMaxValue(28);
                        if(pickerDays.getValue()>28) {
                            pickerDays.setValue(28);
                        }
                    }
                }
                else {
                    pickerDays.setMaxValue(30);
                    if(pickerDays.getValue()>30) {
                        pickerDays.setValue(30);
                    }
                }
            }
        });
        pickerYears.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(pickerMonths.getValue()==2) {
                    if(newVal%4==0) {
                        pickerDays.setMaxValue(29);
                        if(pickerDays.getValue()>29) {
                            pickerDays.setValue(29);
                        }
                    }
                    else {
                        pickerDays.setMaxValue(28);
                        if(pickerDays.getValue()>28) {
                            pickerDays.setValue(28);
                        }
                    }
                }
            }
        });
        int day=mCurrentDate.get(Calendar.DAY_OF_MONTH);
        int month=mCurrentDate.get(Calendar.MONTH);
        int year=mCurrentDate.get(Calendar.YEAR);
        Log.i(TAG, "onClick: "+day+","+month+","+year);
        pickerDays.setValue(day);
        pickerYears.setValue(year);
        pickerMonths.setValue(month+1);
    }

    private void setDate(NumberPicker pickerDays, NumberPicker pickerMonths, NumberPicker pickerYears) {
        int day=pickerDays.getValue();
        int month=pickerMonths.getValue();
        int year=pickerYears.getValue();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month-1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        if(c.getTimeInMillis()>=Calendar.getInstance().getTimeInMillis()) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.error)
                    .content(R.string.error_birthday_future)
                    .positiveText(R.string.btn_ok)
                    .show();
        }
        else {
            mBinding.txtViewBirthdayError.setText("");
            mBinding.inputBirthday.setText(String.format(Locale.getDefault(), "%s %02d, %d", months[month-1], day, year));
            mBirthDay=String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day);
        }
    }
}
