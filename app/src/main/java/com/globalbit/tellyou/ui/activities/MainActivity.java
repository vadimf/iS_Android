package com.globalbit.tellyou.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.androidutils.ActionUtils;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityMainBinding;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.UserResponse;
import com.globalbit.tellyou.service.fcm.FCMHandler;
import com.globalbit.tellyou.ui.fragments.PostsFragment;
import com.globalbit.tellyou.ui.fragments.ProfileFragment;
import com.globalbit.tellyou.ui.interfaces.IMainListener;
import com.globalbit.tellyou.utils.FilePickUtils;
import com.globalbit.tellyou.utils.GeneralUtils;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
        mBinding.txtViewContactUs.setOnClickListener(this);
        mBinding.txtViewLogout.setOnClickListener(this);
        mBinding.imgViewGlobalbit.setOnClickListener(this);
        mBinding.layoutVideoSelection.lnrLayoutCamera.setOnClickListener(this);
        mBinding.layoutVideoSelection.lnrLayoutLibrary.setOnClickListener(this);
        mBinding.layoutVideoSelection.rltvLayoutVideoSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mBinding.layoutVideoSelection.rltvLayoutVideoSelection.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });
        //mBinding.layoutVideoSelection.rltvLayoutVideoSelection.setOnClickListener(this);
        BottomNavigationMenuView bottomNavigationView=(BottomNavigationMenuView)mBinding.navigation.getChildAt(0);
        for (int i = 0; i < bottomNavigationView.getChildCount(); i++) {
            final View iconView=bottomNavigationView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams=iconView.getLayoutParams();
            final DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
            layoutParams.height=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            layoutParams.width=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
        int type=getIntent().getIntExtra(Constants.DATA_HOME_TYPE, 0);
        if(type==0) {
            mLastNavigationItem=mBinding.navigation.getMenu().getItem(0);
        }
        mBinding.navigation.getMenu().getItem(1).setCheckable(false);
        mBinding.navigation.setItemIconTintList(null);
        mBinding.navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch(item.getItemId()) {
                    case R.id.action_home:
                        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        fragment=PostsFragment.newInstance(Constants.TYPE_FEED_HOME, null, null);
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "HomeTag").commit();
                        mLastNavigationItem=item;
                        return true;
                    case R.id.action_add:
                        mBinding.layoutVideoSelection.rltvLayoutVideoSelection.setVisibility(View.VISIBLE);
                        /*final MaterialDialog dialog=new MaterialDialog.Builder(MainActivity.this)
                                .customView(R.layout.dialog_image_selection, false)
                                .show();
                        dialog.findViewById(R.id.lnrLayoutCamera).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                checkForPermissions(1, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});

                            }
                        });
                        dialog.findViewById(R.id.lnrLayoutGallery).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                checkForPermissions(2, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
                            }
                        });*/

                        //GeneralUtils.selectVideoFromGallery(MainActivity.this, Constants.REQUEST_VIDEO_SELECT);
                        /*Intent intent=new Intent(MainActivity.this, VideoRecordingActivity.class);
                        intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_POST_VIDEO_RECORDING);
                        startActivityForResult(intent, Constants.REQUEST_VIDEO_RECORDING);*/
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

        if(type==0) {
            PostsFragment fragment=PostsFragment.newInstance(Constants.TYPE_FEED_HOME, null, null);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment, "HomeTag").commit();
        }
        else if(type==2) {
            onUserProfile(SharedPrefsUtils.getUserDetails().getUsername());
        }
        int pushType=getIntent().getIntExtra(Constants.DATA_PUSH,-1);
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
            Post post=getIntent().getParcelableExtra(Constants.DATA_POST);
            String commentId=getIntent().getStringExtra(Constants.DATA_COMMENT_ID);
            if(!StringUtils.isEmpty(postId)) {
                if(post!=null) {
                    Intent intent=new Intent(this, VideoPlayerActivity.class);
                    ArrayList<Post> tmpPosts=new ArrayList<>();
                    tmpPosts.add(post);
                    intent.putExtra(Constants.DATA_POSTS, tmpPosts);
                    intent.putExtra(Constants.DATA_INDEX, -1);
                    intent.putExtra(Constants.DATA_PAGE, 1);
                    intent.putExtra(Constants.DATA_COMMENT_ID, commentId);
                    intent.putExtra(Constants.DATA_POST_ID, postId);
                    startActivityForResult(intent, Constants.REQUEST_VIDEO_PLAYER);
                }
                else {
                    Intent intent=new Intent(this, ReplyActivity.class);
                    intent.putExtra(Constants.DATA_POST_ID, postId);
                    intent.putExtra(Constants.DATA_COMMENT_ID, commentId);
                    startActivityForResult(intent, Constants.REQUEST_COMMENTS);
                }
            }
        }
        /*

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
    protected void permissionAccepted() {
        if(PERMISSION_REQUEST==1) {
            Intent intent=new Intent(MainActivity.this, VideoRecordingActivity.class);
            intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_POST_VIDEO_RECORDING);
            startActivityForResult(intent, Constants.REQUEST_VIDEO_RECORDING);
        }
        else if(PERMISSION_REQUEST==2) {
            GeneralUtils.selectVideoFromGallery(MainActivity.this, Constants.REQUEST_VIDEO_SELECT);
        }
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
        else if(requestCode==Constants.REQUEST_VIDEO_SELECT && resultCode==RESULT_OK) {
            if(data.getData()!=null) {
                final Uri uri=data.getData();
                Log.i("TEST", "onActivityResult: "+uri.getPath());
                String path=FilePickUtils.Companion.getSmartFilePath(this, uri);
                Log.i("TEST", "onActivityResult: "+path);
                if(path!=null) {
                    Intent intent=new Intent(this, VideoTrimmerActivity.class);
                    intent.putExtra(Constants.DATA_URI, Uri.parse(path));
                    startActivityForResult(intent, Constants.REQUEST_VIDEO_TRIMMER);
                }
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SiliCompressor.with(MainActivity.this).compressVideo(uri, Environment.getExternalStorageDirectory().getPath());
                        } catch(URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();*/

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
                public void onSuccess(UserResponse response, Object object) {
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
                    public void onSuccess(BaseResponse response, Object object) {
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
            case R.id.txtViewContactUs:
                ActionUtils.openEmail(this,getString(R.string.label_contact_us_email));
                break;
            case R.id.imgViewGlobalbit:
                Intent browserIntent=new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.globalbit_website)));
                startActivity(browserIntent);
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.lnrLayoutCamera:
                checkForPermissions(1, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
                mBinding.layoutVideoSelection.rltvLayoutVideoSelection.setVisibility(View.GONE);
                break;
            case R.id.lnrLayoutLibrary:
                checkForPermissions(2, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
                mBinding.layoutVideoSelection.rltvLayoutVideoSelection.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mBinding.layoutVideoSelection.rltvLayoutVideoSelection.getVisibility()==View.VISIBLE) {
            mBinding.layoutVideoSelection.rltvLayoutVideoSelection.setVisibility(View.GONE);
        }
        else {
            if(mLastNavigationItem!=null&&mLastNavigationItem.getItemId()==R.id.action_profile) {
                mBinding.navigation.setSelectedItemId(R.id.action_home);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        CustomApplication.setPost(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MainActivity", "onDestroy: ");
    }

}
