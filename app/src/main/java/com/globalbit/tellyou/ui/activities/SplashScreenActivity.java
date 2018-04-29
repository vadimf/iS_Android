package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivitySplashBinding;
import com.globalbit.tellyou.model.system.SystemPreferencesResponseKT;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.PushNotificationTokenRequest;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.PostResponse;
import com.globalbit.tellyou.network.responses.UserResponse;
import com.globalbit.tellyou.service.fcm.FCMHandler;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by alex on 06/11/2017.
 */

public class SplashScreenActivity extends BaseActivity implements IBaseNetworkResponseListener<SystemPreferencesResponseKT>{
    private static final String TAG=SplashScreenActivity.class.getSimpleName();
    private ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_splash);
        if(com.globalbit.androidutils.GeneralUtils.isNetworkAvailable(this)) {
            NetworkManager.getInstance().systemPreferences(this);
        }
        else {
            showMessage(getString(R.string.dialog_title_internet_connection),getString(R.string.dialog_content_error_internet_connection));
        }

    }

    @Override
    public void onSuccess(SystemPreferencesResponseKT response, Object object) {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                String deepLink=null;
                if(pendingDynamicLinkData!=null) {
                    deepLink=pendingDynamicLinkData.getLink().toString();
                    Log.i(TAG, "onSuccess: "+deepLink);
                    if(!TextUtils.isEmpty(deepLink)) {
                        String[] array=deepLink.split("/share/");
                        if(array.length==2) {
                            Log.i(TAG, "Post id "+array[1]);
                            if(!StringUtils.isEmpty(array[1])) {
                                NetworkManager.getInstance().getPostById(new IBaseNetworkResponseListener<PostResponse>() {
                                    @Override
                                    public void onSuccess(PostResponse response, Object object) {
                                        CustomApplication.setPost(response.getPost());
                                        next();
                                    }

                                    @Override
                                    public void onError(int errorCode, String errorMessage) {
                                        next();
                                    }
                                }, array[1]);
                            }
                            else {
                                next();
                            }
                            /*String[] array2=array[1].split("/");
                            if(array2.length==2) {
                                Log.i(TAG, "Post id "+array2[1]);
                                CustomApplication.setPostId(array2[1]);
                            }*/
                        }
                        else {
                            next();
                        }
                    }
                    else {
                        next();
                    }
                }
                else {
                    next();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                next();
            }
        });

    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        //showErrorMessage(errorCode, getString(R.string.error), errorMessage); //TODO change it when server will be available
        Intent intent=new Intent(SplashScreenActivity.this, ConnectionActivity.class);
        startActivity(intent);
        finish();
    }

    private void next() {
        if(!StringUtils.isEmpty(SharedPrefsUtils.getAuthorization())) {
            NetworkManager.getInstance().getMyDetails(new IBaseNetworkResponseListener<UserResponse>() {
                @Override
                public void onSuccess(UserResponse response, Object object) {
                    SharedPrefsUtils.setUserDetails(response.getUser());
                    if(StringUtils.isEmpty(response.getUser().getUsername())) {
                        Intent intent=new Intent(SplashScreenActivity.this, ProfileActivity.class);
                        intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_EDIT_PROFILE);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        if(response.getUser()!=null) {
                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                            if(!StringUtils.isEmpty(refreshedToken)) {
                                Log.i("SharedUtils", "Token: "+refreshedToken);
                                PushNotificationTokenRequest request=new PushNotificationTokenRequest();
                                request.setToken(refreshedToken);
                                NetworkManager.getInstance().sendToken(new IBaseNetworkResponseListener<BaseResponse>() {
                                    @Override
                                    public void onSuccess(BaseResponse response, Object object) {

                                    }

                                    @Override
                                    public void onError(int errorCode, String errorMessage) {

                                    }
                                }, request);
                            }

                        }
                        Intent intent=new Intent(SplashScreenActivity.this, MainActivity.class);
                        int pushType=getIntent().getIntExtra(Constants.DATA_PUSH, -1);
                        intent.putExtra(Constants.DATA_PUSH, pushType);
                        if(pushType==FCMHandler.PUSH_NOTIFICATION_FOLLOW) {
                            intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_USER_PROFILE);
                            intent.putExtra(Constants.DATA_USER, getIntent().getParcelableExtra(Constants.DATA_USER));
                        }
                        else if(pushType==FCMHandler.PUSH_NOTIFICATION_COMMENT) {
                            intent.putExtra(Constants.DATA_POST_ID, getIntent().getStringExtra(Constants.DATA_POST_ID));
                            intent.putExtra(Constants.DATA_POST, getIntent().getParcelableExtra(Constants.DATA_POST));
                            intent.putExtra(Constants.DATA_COMMENT_ID, getIntent().getStringExtra(Constants.DATA_COMMENT_ID));
                        }
                        else if(pushType==FCMHandler.PUSH_NOTIFICATION_MENTION) {
                            intent.putExtra(Constants.DATA_POST_ID, getIntent().getStringExtra(Constants.DATA_POST_ID));
                            intent.putExtra(Constants.DATA_COMMENT_ID, getIntent().getStringExtra(Constants.DATA_COMMENT_ID));
                        }
                        else if(pushType==FCMHandler.PUSH_NOTIFICATION_SHARE) {
                            intent.putExtra(Constants.DATA_POST_ID, getIntent().getStringExtra(Constants.DATA_POST_ID));
                        }
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    Intent intent=new Intent(SplashScreenActivity.this, ConnectionActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {
            Intent intent=new Intent(this, ConnectionActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
