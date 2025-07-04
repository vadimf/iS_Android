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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentProfileBinding;
import com.globalbit.tellyou.model.Phone;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.ui.activities.FollowActivity;
import com.globalbit.tellyou.ui.activities.MainActivity;
import com.globalbit.tellyou.ui.activities.ProfileActivity;
import com.globalbit.tellyou.ui.adapters.ProfilePagerAdapter;
import com.globalbit.tellyou.ui.events.BookmarkEvent;
import com.globalbit.tellyou.ui.events.FollowingEvent;
import com.globalbit.tellyou.ui.interfaces.IMainListener;
import com.globalbit.tellyou.ui.interfaces.IPostListener;
import com.globalbit.tellyou.ui.interfaces.IProfileListener;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by alex on 08/11/2017.
 */

public class ProfileFragment extends BaseFragment implements View.OnClickListener, IProfileListener {
    private static final String TAG=ProfileFragment.class.getSimpleName();
    private FragmentProfileBinding mBinding;
    private User mUser;
    private Enums.ProfileState mProfileState;
    private IMainListener mListener;
    private ProfilePagerAdapter mProfilePagerAdapter;

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
        mBinding.lnrLayoutFollowing.setOnClickListener(this);
        mBinding.lnrLayoutFollowers.setOnClickListener(this);
        User user=SharedPrefsUtils.getUserDetails();
        if(mUser!=null&&mUser.getUsername().equals(user.getUsername())) {
            mUser=null;
        }
        if(mUser==null) { //My profile
            mUser=SharedPrefsUtils.getUserDetails();
            mProfileState=Enums.ProfileState.MyProfile;
            if(mListener instanceof MainActivity) {
                mBinding.imgViewMenu.setImageResource(R.drawable.ic_menu);
            }
            else {
                mBinding.imgViewMenu.setImageResource(R.drawable.ic_back);
            }
            //mProfilePagerAdapter=new ProfilePagerAdapter(getChildFragmentManager(), getActivity(), new String[]{getString(R.string.tab_questions),getString(R.string.tab_bookmarks)} ,mUser );
        }
        else { //Other profile
            mProfileState=Enums.ProfileState.UserProfile;
            mBinding.imgViewMenu.setImageResource(R.drawable.ic_back);
            if(mUser.isFollowing()) {
                mBinding.btnAction.setBackgroundResource(R.drawable.button_share);
                mBinding.btnAction.setTextColor(getResources().getColor(R.color.red_border));
                mBinding.btnAction.setText(R.string.btn_following);
            }
            else {
                mBinding.btnAction.setBackgroundResource(R.drawable.background_button);
                mBinding.btnAction.setTextColor(getResources().getColor(R.color.white));
                mBinding.btnAction.setText(R.string.btn_follow);
            }
            mBinding.txtViewMyVideos.setText(R.string.label_videos);
            //mBinding.txtViewMyVideos.setVisibility(View.GONE);
        }
        mProfilePagerAdapter=new ProfilePagerAdapter(getChildFragmentManager(), getActivity(), new String[]{getString(R.string.tab_videos)} ,mUser , this);
        mBinding.viewpager.setAdapter(mProfilePagerAdapter);
        //mBinding.tabDiscover.setupWithViewPager(mBinding.viewpager);
        setProfile();

        return mBinding.getRoot();
    }

    private void setProfile() {
        if(mUser.getProfile()!=null) {
            if(mUser.getProfile().getPicture()!=null&&!StringUtils.isEmpty(mUser.getProfile().getPicture().getThumbnail())) {
                Picasso.with(getActivity()).load(mUser.getProfile().getPicture().getThumbnail()).into(mBinding.imgViewPhoto);
            }
        }
        if(mUser.getProfile()!=null&&!StringUtils.isEmpty(mUser.getProfile().getBio())) {
            mBinding.txtViewBio.setText(mUser.getProfile().getBio());
            mBinding.txtViewBio.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.txtViewBio.setVisibility(View.GONE);
        }
        mBinding.txtViewUserName.setText(String.format(Locale.getDefault(),"%s%s",getString(R.string.special),mUser.getUsername()));

        String fullName=null;
        if(mUser.getProfile()!=null&&!StringUtils.isEmpty(mUser.getProfile().getFirstName())&&!StringUtils.isEmpty(mUser.getProfile().getLastName())) {
            fullName=String.format(Locale.getDefault(),"%s %s", mUser.getProfile().getFirstName(), mUser.getProfile().getLastName());
        }
        else if(mUser.getProfile()!=null&&!StringUtils.isEmpty(mUser.getProfile().getFirstName())) {
            fullName=mUser.getProfile().getFirstName();
        }
        else if(mUser.getProfile()!=null&&!StringUtils.isEmpty(mUser.getProfile().getLastName())) {
            fullName=mUser.getProfile().getLastName();
        }
        if(!StringUtils.isEmpty(fullName)) {
            mBinding.txtViewName.setText(fullName);
        }
        mBinding.txtViewFollowing.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowing()));
        mBinding.txtViewFollowers.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowers()));

    }

    @Override
    public void onClick(View view) {
        User user=null;
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
                    case UserProfile:if(mUser.isFollowing()) {
                            new MaterialDialog.Builder(getActivity())
                                    .content(String.format(Locale.getDefault(),"%s %s%s?", getResources().getString(R.string.dialog_button_unfollow), getResources().getString(R.string.special), mUser.getUsername()))
                                    .positiveText(R.string.dialog_button_unfollow)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            showLoadingDialog();
                                            NetworkManager.getInstance().unfollow(new IBaseNetworkResponseListener<BaseResponse>() {
                                                @Override
                                                public void onSuccess(BaseResponse response, Object object) {
                                                    hideLoadingDialog();
                                                    mUser.setFollowing(false);
                                                    mBinding.btnAction.setBackgroundResource(R.drawable.background_button);
                                                    mBinding.btnAction.setTextColor(getResources().getColor(R.color.white));
                                                    mBinding.btnAction.setText(getString(R.string.btn_follow));
                                                    if(mUser.getFollowers()>0) {
                                                        mUser.setFollowers(mUser.getFollowers()-1);
                                                        mBinding.txtViewFollowers.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowers()));
                                                    }
                                                    EventBus.getDefault().post(new FollowingEvent(mUser));
                                                }

                                                @Override
                                                public void onError(int errorCode, String errorMessage) {
                                                    hideLoadingDialog();
                                                }
                                            }, mUser.getUsername());
                                        }
                                    })
                                    .negativeText(R.string.btn_cancel)
                                    .show();
                        }
                        else {
                            showLoadingDialog();
                            NetworkManager.getInstance().follow(new IBaseNetworkResponseListener<BaseResponse>() {
                                @Override
                                public void onSuccess(BaseResponse response, Object object) {
                                    hideLoadingDialog();
                                    mUser.setFollowing(true);
                                    mBinding.btnAction.setBackgroundResource(R.drawable.button_share);
                                    mBinding.btnAction.setTextColor(getResources().getColor(R.color.red_border));
                                    mBinding.btnAction.setText(getString(R.string.btn_following));
                                    mUser.setFollowers(mUser.getFollowers()+1);
                                    mBinding.txtViewFollowers.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowers()));
                                    EventBus.getDefault().post(new FollowingEvent(mUser));
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
            case R.id.lnrLayoutFollowing:
                Intent intentFollowing=new Intent(getActivity(), FollowActivity.class);
                intentFollowing.putExtra(Constants.DATA_FOLLOW, Constants.REQUEST_FOLLOWING);
                user=null;
                if(mProfileState==Enums.ProfileState.UserProfile) {
                    user=mUser;
                }
                intentFollowing.putExtra(Constants.DATA_USER, user);
                startActivityForResult(intentFollowing, Constants.REQUEST_FOLLOWING);
                break;
            case R.id.lnrLayoutFollowers:
                Intent intentFollowers=new Intent(getActivity(), FollowActivity.class);
                intentFollowers.putExtra(Constants.DATA_FOLLOW, Constants.REQUEST_FOLLOWERS);
                user=null;
                if(mProfileState==Enums.ProfileState.UserProfile) {
                    user=mUser;
                }
                intentFollowers.putExtra(Constants.DATA_USER, user);
                startActivityForResult(intentFollowers, Constants.REQUEST_FOLLOWERS);
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
            setProfile();
        }
        else if(requestCode==Constants.REQUEST_FOLLOWING||requestCode==Constants.REQUEST_FOLLOWERS) {
            if(mProfileState==Enums.ProfileState.MyProfile) {
                mUser=SharedPrefsUtils.getUserDetails();
                setProfile();
            }
        }
        else if(requestCode==Constants.REQUEST_VIDEO_RECORDING) {
            if(resultCode==Activity.RESULT_OK) {
                Fragment fragment=mProfilePagerAdapter.getRegisteredFragment(0);
                if(fragment!=null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onUsersFollowingStatus(HashMap<String, Boolean> usersFollowingState) {
        if(mProfileState==Enums.ProfileState.UserProfile) {
            if(usersFollowingState.containsKey(mUser.getUsername())) {
                boolean isFollowing=usersFollowingState.get(mUser.getUsername());
                if(isFollowing!=mUser.isFollowing()) {
                    mUser.setFollowing(isFollowing);
                    if(mUser.isFollowing()) {
                        mUser.setFollowers(mUser.getFollowers()+1);
                        mBinding.btnAction.setBackgroundResource(R.drawable.button_share);
                        mBinding.btnAction.setTextColor(getResources().getColor(R.color.red_border));
                        mBinding.btnAction.setText(getString(R.string.btn_following));
                    } else {
                        mUser.setFollowers(mUser.getFollowers()-1);
                        mBinding.btnAction.setBackgroundResource(R.drawable.background_button);
                        mBinding.btnAction.setTextColor(getResources().getColor(R.color.white));
                        mBinding.btnAction.setText(getString(R.string.btn_follow));
                    }
                    mBinding.txtViewFollowers.setText(String.format(Locale.getDefault(), "%d", mUser.getFollowers()));
                }
            }
        }
    }

    @Override
    public void showPhoneVerification() {

    }

    @Override
    public void showCodeConfirmation(Phone phone) {

    }
}
