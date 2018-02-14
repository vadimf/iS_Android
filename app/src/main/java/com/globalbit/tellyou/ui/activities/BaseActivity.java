package com.globalbit.tellyou.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.utils.SharedPrefsUtils;


/**
 * Created by alex on 06/11/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG=BaseActivity.class.getSimpleName();
    protected int PERMISSION_REQUEST;
    private MaterialDialog mLoadingDialog=null;

    protected void showMessage(String title, String message) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(R.string.btn_ok)
                .show();
    }

    protected void showErrorMessage(final int errorCode, String title, String message) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .cancelable(false)
                .positiveText(R.string.btn_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        if(errorCode==300) {
                            User.unauthorized();
                            Intent connectionIntent = new Intent(BaseActivity.this, ConnectionActivity.class);
                            connectionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(connectionIntent);
                        }
                    }
                })
                .show();
    }

    protected void showLoadingDialog() {
        mLoadingDialog=new MaterialDialog.Builder(this)
                .title(R.string.dialog_loading_title)
                .content(R.string.dialog_loading_content)
                .progress(true, 0)
                .show();
    }

    protected void hideLoadingDialog() {
        if(mLoadingDialog!=null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPrefsUtils.setInBackground(false);
    }

    @Override
    protected void onPause() {
        SharedPrefsUtils.setInBackground(true);
        super.onPause();
    }


    protected void checkForPermissions(int requestCode, String[] permissions) {
        PERMISSION_REQUEST=requestCode;
        boolean isHasPermission=true;
        for(String permission : permissions) {
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED) {
                isHasPermission=false;
                break;
            }
        }
        if(!isHasPermission) {
            ActivityCompat.requestPermissions(this,permissions, requestCode);
        }
        else {
            permissionAccepted();
        }
    }

    protected void permissionAccepted() {
        if(PERMISSION_REQUEST==2) {
            Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.REQUEST_GALLERY);
        }
    }

    protected void permissionDeclined() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissions.length==0) {
            return;
        }
        if(requestCode==PERMISSION_REQUEST) {
            boolean flag=true;
            if(grantResults.length>0) {
                for(int grantResult : grantResults) {
                    if(grantResult!=PackageManager.PERMISSION_GRANTED) {
                        flag=false;
                        break;
                    }
                }
            }
            else {
                flag=false;
            }
            if(flag) {
                permissionAccepted();
            }
            else {
                permissionDeclined();
            }
        }
    }

}
