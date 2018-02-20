package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityMainBinding;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.UserResponse;
import com.globalbit.tellyou.ui.fragments.PostsFragment;
import com.globalbit.tellyou.ui.fragments.ProfileFragment;
import com.globalbit.tellyou.ui.interfaces.IMainListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

import java.util.Locale;

public class MainActivity extends BaseActivity implements IMainListener, View.OnClickListener {
    private ActivityMainBinding mBinding;
    private MenuItem mLastNavigationItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mBinding.imgViewMenuClose.setOnClickListener(this);
        String versionName=null;
        try {
            versionName=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(!StringUtils.isEmpty(versionName)) {
            mBinding.txtViewVersionName.setText(String.format(Locale.getDefault(), "%s%s",getString(R.string.v),versionName));
        }
        else {
            mBinding.txtViewVersionName.setVisibility(View.GONE);
        }
        mBinding.txtViewAbout.setOnClickListener(this);
        mBinding.txtViewPrivacy.setOnClickListener(this);
        mBinding.txtViewTermsOfUse.setOnClickListener(this);
        mBinding.txtViewLogout.setOnClickListener(this);
        mBinding.txtViewGlobalbitLink.setOnClickListener(this);
        mLastNavigationItem=mBinding.navigation.getMenu().getItem(0);
        mBinding.navigation.getMenu().getItem(1).setCheckable(false);
        mBinding.navigation.setItemIconTintList(null);
        mBinding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch(item.getItemId()) {
                    case R.id.action_home:
                        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        fragment=PostsFragment.newInstance(Constants.TYPE_FEED_HOME, null);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "HomeTag").commit();
                        mLastNavigationItem=item;
                        return true;
                    case R.id.action_add:
                        Intent intent=new Intent(MainActivity.this, VideoRecordingActivity.class);
                        intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_POST_VIDEO_RECORDING);
                        startActivityForResult(intent, Constants.REQUEST_VIDEO_RECORDING);
                        return false;
                    case R.id.action_profile:
                        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        fragment=ProfileFragment.newInstance(null);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "ProfileTag").commit();
                        mLastNavigationItem=item;
                        return true;
                }

                return true;
            }
        });
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        PostsFragment fragment=PostsFragment.newInstance(Constants.TYPE_FEED_HOME, null);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "HomeTag").commit();

        /*int pushType=getIntent().getIntExtra(Constants.DATA_PUSH,-1);
        if(pushType==FCMHandler.PUSH_NOTIFICATION_FOLLOW) {
            User user=getIntent().getParcelableExtra(Constants.DATA_USER);
            if(user!=null) {
                Intent intent=new Intent(this, ProfileActivity.class);
                intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_USER_PROFILE);
                intent.putExtra(Constants.DATA_USER, user);
                startActivity(intent);
            }
        }
        else if(pushType==FCMHandler.PUSH_NOTIFICATION_COMMENT) {
            String postId=getIntent().getStringExtra(Constants.DATA_POST_ID);
            String commentId=getIntent().getStringExtra(Constants.DATA_COMMENT_ID);
            if(!StringUtils.isEmpty(postId)) {
                Intent intent=new Intent(this, CommentsActivity.class);
                intent.putExtra(Constants.DATA_POST_ID, postId);
                intent.putExtra(Constants.DATA_COMMENT_ID, commentId);
                startActivityForResult(intent, Constants.REQUEST_COMMENTS);
            }
        }
        else if(pushType==FCMHandler.PUSH_NOTIFICATION_MENTION) {
            String postId=getIntent().getStringExtra(Constants.DATA_POST_ID);
            String commentId=getIntent().getStringExtra(Constants.DATA_COMMENT_ID);
            if(!StringUtils.isEmpty(postId)) {
                Intent intent=new Intent(this, CommentsActivity.class);
                intent.putExtra(Constants.DATA_POST_ID, postId);
                intent.putExtra(Constants.DATA_COMMENT_ID, commentId);
                startActivityForResult(intent, Constants.REQUEST_COMMENTS);
            }
        }
        else if(pushType==FCMHandler.PUSH_NOTIFICATION_SHARE) {
            String postId=getIntent().getStringExtra(Constants.DATA_POST_ID);
            if(!StringUtils.isEmpty(postId)) {
                Intent intent=new Intent(this, PostActivity.class);
                intent.putExtra(Constants.DATA_POST_ID, postId);
                startActivityForResult(intent, Constants.REQUEST_POST);
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_VIDEO_RECORDING) {
            mLastNavigationItem.setChecked(true);
            if(resultCode==RESULT_OK) {
                Fragment fragment=getSupportFragmentManager().findFragmentByTag("HomeTag");
                if(fragment!=null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
                else {
                    fragment=getSupportFragmentManager().findFragmentByTag("ProfileTag");
                    if(fragment!=null) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
            if(mLastNavigationItem.getItemId()==R.id.action_profile) {
                mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
            else {
                mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }
        else if(requestCode==Constants.REQUEST_POST) {
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("HomeTag");
            if(fragment!=null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        else if(requestCode==Constants.REQUEST_USER_PROFILE) {
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("HomeTag");
            if(fragment!=null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onOpenDrawer() {
        mBinding.drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onUserProfile(String username) {
        if(SharedPrefsUtils.getUserDetails().getUsername().equals(username)) {
            if(mLastNavigationItem!=mBinding.navigation.getMenu().getItem(2)) {
                mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                Fragment fragment=ProfileFragment.newInstance(null);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "ProfileTag").commit();
                mLastNavigationItem=mBinding.navigation.getMenu().getItem(2);
                mBinding.navigation.getMenu().getItem(2).setChecked(true);
            }
        } else {
            final MaterialDialog loadingDialog=new MaterialDialog.Builder(this)
                    .title(R.string.dialog_loading_title)
                    .content(R.string.dialog_loading_content)
                    .progress(true, 0)
                    .show();
            NetworkManager.getInstance().getUserDetails(new IBaseNetworkResponseListener<UserResponse>() {
                @Override
                public void onSuccess(UserResponse response) {
                    if(loadingDialog!=null) {
                        loadingDialog.dismiss();
                    }
                    Intent intent=new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_USER_PROFILE);
                    intent.putExtra(Constants.DATA_USER, response.getUser());
                    startActivityForResult(intent,Constants.REQUEST_USER_PROFILE);
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
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.imgViewMenuClose:
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.txtViewAbout:
                intent=new Intent(MainActivity.this, InformationActivity.class);
                intent.putExtra(Constants.DATA_INFORMATION, Constants.REQUEST_ABOUT);
                startActivity(intent);
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.txtViewPrivacy:
                intent=new Intent(MainActivity.this, InformationActivity.class);
                intent.putExtra(Constants.DATA_INFORMATION, Constants.REQUEST_PRIVACY);
                startActivity(intent);
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.txtViewTermsOfUse:
                intent=new Intent(MainActivity.this, InformationActivity.class);
                intent.putExtra(Constants.DATA_INFORMATION, Constants.REQUEST_TERMS_OF_USE);
                startActivity(intent);
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.txtViewLogout:
                showLoadingDialog();
                NetworkManager.getInstance().signOut(new IBaseNetworkResponseListener<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        hideLoadingDialog();
                        User.logout();
                        Intent intent=new Intent(MainActivity.this, ConnectionActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        hideLoadingDialog();
                        //TODO handle error
                    }
                });
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.txtViewGlobalbitLink:
                Intent browserIntent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.globalbit_website)));
                startActivity(browserIntent);
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mLastNavigationItem!=null&&mLastNavigationItem.getItemId()==R.id.action_profile) {
            mBinding.navigation.setSelectedItemId(R.id.action_home);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        CustomApplication.setPostId(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "onDestroy: ");
    }

}
