package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityProfileBinding;
import com.globalbit.tellyou.model.Phone;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.UserResponse;
import com.globalbit.tellyou.ui.fragments.CodeConfirmationFragment;
import com.globalbit.tellyou.ui.fragments.EditProfileFragment;
import com.globalbit.tellyou.ui.fragments.PhoneVerificationFragment;
import com.globalbit.tellyou.ui.fragments.ProfileFragment;
import com.globalbit.tellyou.ui.interfaces.IMainListener;
import com.globalbit.tellyou.ui.interfaces.IPhoneListener;
import com.globalbit.tellyou.ui.interfaces.IProfileListener;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

import java.util.HashMap;

/**
 * Created by alex on 08/11/2017.
 */

public class ProfileActivity extends AppCompatActivity implements IMainListener, IProfileListener{
    private static final String TAG=ProfileActivity.class.getSimpleName();
    private static Enums.RegisterState mState;
    private User mUser;

    private ActivityProfileBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_profile);
        int type=getIntent().getIntExtra(Constants.DATA_PROFILE,Constants.REQUEST_EDIT_PROFILE);
        switch(type) {
            case Constants.REQUEST_EDIT_PROFILE:
                mState=Enums.RegisterState.UsernameSate;
                EditProfileFragment editProfileFragment =EditProfileFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, editProfileFragment, "EditProfileTag").commit();
                break;
            case Constants.REQUEST_USER_PROFILE:
                mState=Enums.RegisterState.ProfileState;
                mUser=getIntent().getParcelableExtra(Constants.DATA_USER);
                ProfileFragment userProfileFragment =ProfileFragment.newInstance(mUser);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, userProfileFragment, "ProfileTag").commit();
                break;
        }
    }

    @Override
    public void onOpenDrawer() {
        finish();
    }

    @Override
    public void onUserProfile(String username) {
        if(!SharedPrefsUtils.getUserDetails().getUsername().equals(username)&&mUser!=null&&!StringUtils.isEmpty(mUser.getUsername())&&!mUser.getUsername().equals(username)) {
            final MaterialDialog loadingDialog=new MaterialDialog.Builder(this)
                    .title(R.string.dialog_loading_title)
                    .content(R.string.dialog_loading_content)
                    .progress(true, 0)
                    .show();
            NetworkManager.getInstance().getUserDetails(new IBaseNetworkResponseListener<UserResponse>() {
                @Override
                public void onSuccess(UserResponse response, Object object) {
                    if(loadingDialog!=null) {
                        loadingDialog.dismiss();
                    }
                    Intent intent=new Intent(ProfileActivity.this, ProfileActivity.class);
                    intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_USER_PROFILE);
                    intent.putExtra(Constants.DATA_USER, response.getUser());
                    startActivity(intent);
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    if(loadingDialog!=null) {
                        loadingDialog.dismiss();
                    }
                }
            }, username);
        }
    }

    @Override
    public void onBackPressed() {
        switch(mState) {
            case UsernameSate:
            case ProfileState:
                User user=SharedPrefsUtils.getUserDetails();
                if(user==null||StringUtils.isEmpty(user.getUsername())) {
                    Intent intent=new Intent(ProfileActivity.this, ConnectionActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            case PhoneVerificationState:
                Intent intent=new Intent(ProfileActivity.this, ConnectionActivity.class);
                startActivity(intent);
                finish();
                break;
            case CodeConfirmationState:
                showPhoneVerification();
                break;
        }

    }

    @Override
    public void showPhoneVerification() {
        mState=Enums.RegisterState.PhoneVerificationState;
        PhoneVerificationFragment fragment =PhoneVerificationFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "PhoneVerificationTag").commit();
    }

    @Override
    public void showCodeConfirmation(Phone phone) {
        if(phone!=null) {
            mState=Enums.RegisterState.CodeConfirmationState;
            CodeConfirmationFragment fragment=CodeConfirmationFragment.newInstance(phone);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "CodeConfirmationTag").commit();
        }
    }

    @Override
    public void onUsersFollowingStatus(HashMap<String, Boolean> usersFollowingState) {

    }
}
