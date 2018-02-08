package com.globalbit.tellyou.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentProfileBinding;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.activities.ProfileActivity;
import com.globalbit.tellyou.ui.events.BookmarkEvent;
import com.globalbit.tellyou.ui.events.FollowingEvent;
import com.globalbit.tellyou.ui.interfaces.IMainListener;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

/**
 * Created by alex on 08/11/2017.
 */

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG=ProfileFragment.class.getSimpleName();
    private FragmentProfileBinding mBinding;
    private User mUser;
    private Enums.ProfileState mProfileState;
    private IMainListener mListener;

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment=new ProfileFragment();
        Bundle args=new Bundle();
        args.putParcelable(Constants.DATA_USER, user);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            mUser=getArguments().getParcelable(Constants.DATA_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        mBinding.btnBack.setOnClickListener(this);
        mBinding.btnAction.setOnClickListener(this);
        if(mUser==null) { //My profile
            mUser=SharedPrefsUtils.getUserDetails();
            mProfileState=Enums.ProfileState.MyProfile;
            mBinding.btnAction.setVisibility(View.GONE);
            mBinding.imgViewMenu.setImageResource(R.drawable.ic_menu);
        }
        else { //Other profile
            mProfileState=Enums.ProfileState.UserProfile;
            mBinding.imgViewMenu.setImageResource(R.drawable.ic_left_arrow);
            if(mUser.isFollowing()) {
                mBinding.btnAction.setBackgroundResource(R.drawable.button_share);
                mBinding.btnAction.setTextColor(getResources().getColor(R.color.share));
                mBinding.btnAction.setText(R.string.btn_following);
            }
            else {
                mBinding.btnAction.setBackgroundResource(R.drawable.button_regular);
                mBinding.btnAction.setTextColor(getResources().getColor(R.color.border_active));
                mBinding.btnAction.setText(R.string.btn_follow);
            }
        }
        if(mUser.getProfile()!=null) {
            if(mUser.getProfile().getPicture()!=null&&!StringUtils.isEmpty(mUser.getProfile().getPicture().getThumbnail())) {
                Picasso.with(getActivity()).load(mUser.getProfile().getPicture().getThumbnail()).into(mBinding.imgViewPhoto);
            }
        }
        mBinding.txtViewUserName.setText(String.format(Locale.getDefault(),"%s%s",getString(R.string.special),mUser.getUsername()));


        mBinding.txtViewFollowing.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowing()));
        mBinding.txtViewFollowers.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowers()));

        return mBinding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnBack:
                switch(mProfileState) {
                    case MyProfile:
                        mListener.onOpenDrawer();
                        break;
                    case UserProfile:
                        getActivity().finish();
                        break;
                }
                break;
            case R.id.btnAction:
                switch(mProfileState) {
                    case MyProfile:
                        Intent intent=new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_EDIT_PROFILE);
                        startActivityForResult(intent,Constants.REQUEST_EDIT_PROFILE);
                        break;
                    case UserProfile:
                        showLoadingDialog();
                        if(mUser.isFollowing()) {
                            NetworkManager.getInstance().unfollow(new IBaseNetworkResponseListener<BaseResponse>() {
                                @Override
                                public void onSuccess(BaseResponse response) {
                                    hideLoadingDialog();
                                    mUser.setFollowing(false);
                                    mBinding.btnAction.setBackgroundResource(R.drawable.button_regular);
                                    mBinding.btnAction.setTextColor(getResources().getColor(R.color.border_active));
                                    mBinding.btnAction.setText(getString(R.string.btn_follow));
                                }

                                @Override
                                public void onError(int errorCode, String errorMessage) {
                                    hideLoadingDialog();
                                }
                            }, mUser.getUsername());
                        }
                        else {
                            NetworkManager.getInstance().follow(new IBaseNetworkResponseListener<BaseResponse>() {
                                @Override
                                public void onSuccess(BaseResponse response) {
                                    hideLoadingDialog();
                                    mUser.setFollowing(true);
                                    mBinding.btnAction.setBackgroundResource(R.drawable.button_share);
                                    mBinding.btnAction.setTextColor(getResources().getColor(R.color.share));
                                    mBinding.btnAction.setText(getString(R.string.btn_following));
                                }

                                @Override
                                public void onError(int errorCode, String errorMessage) {
                                    hideLoadingDialog();
                                }
                            }, mUser.getUsername());
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IMainListener) {
            mListener = (IMainListener)context;
        }
        else {
            throw new ClassCastException(context.toString() + " must implement IMainListener.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_EDIT_PROFILE&& resultCode==Activity.RESULT_OK) {
            mUser=SharedPrefsUtils.getUserDetails();
            if(mUser.getProfile()!=null) {
                mBinding.txtViewUserName.setText(String.format(Locale.getDefault(), "%s%s", getString(R.string.special), mUser.getUsername()));
                if(mUser.getProfile().getPicture()!=null&&!StringUtils.isEmpty(mUser.getProfile().getPicture().getThumbnail())) {
                    Picasso.with(getActivity()).load(mUser.getProfile().getPicture().getThumbnail()).into(mBinding.imgViewPhoto);
                }
            }

            mBinding.txtViewFollowing.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowing()));
            mBinding.txtViewFollowers.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowers()));
        }
        else if(requestCode==Constants.REQUEST_FOLLOWING||requestCode==Constants.REQUEST_FOLLOWERS) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookmarkEvent(BookmarkEvent event) {
        if(mProfileState==Enums.ProfileState.MyProfile) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowingEvent(FollowingEvent event) {
        if(mProfileState==Enums.ProfileState.MyProfile) {
            if(event.isFollowing) {
                mUser.setFollowing(mUser.getFollowing()+1);
            }
            else {
                mUser.setFollowing(mUser.getFollowing()-1);
            }
            mBinding.txtViewFollowing.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowing()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
