package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.View;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityCropBinding;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by alex on 19/11/2017.
 */

public class CropActivity extends BaseActivity implements View.OnClickListener, CropImageView.OnCropImageCompleteListener{

    private ActivityCropBinding mBinding;
    private Uri mUri;
    private int mImageType=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_crop);
        mBinding.imgViewBack.setOnClickListener(this);
        mBinding.imgViewSelect.setOnClickListener(this);
        mUri=getIntent().getParcelableExtra(Constants.DATA_IMAGE_URI);
        if(mUri!=null) {
            mBinding.imgViewCrop.setOnCropImageCompleteListener(this);
            mBinding.imgViewCrop.setImageUriAsync(mUri);
        }
        mImageType=getIntent().getIntExtra(Constants.DATA_IMAGE, Constants.TYPE_IMAGE_GENERAL);
        if(mImageType==Constants.TYPE_IMAGE_GENERAL) {
            mBinding.frmLayoutCaption.setVisibility(View.GONE);
            mBinding.imgViewCrop.setCropShape(CropImageView.CropShape.RECTANGLE);
        }
        else if(mImageType==Constants.TYPE_IMAGE_PHOTO_POLL) {
            mBinding.frmLayoutCaption.setVisibility(View.VISIBLE);
            mBinding.imgViewCrop.setCropShape(CropImageView.CropShape.RECTANGLE);
        }
        else if(mImageType==Constants.TYPE_IMAGE_PROFILE) {
            mBinding.frmLayoutCaption.setVisibility(View.GONE);
            mBinding.imgViewCrop.setCropShape(CropImageView.CropShape.OVAL);
        }
        mBinding.inputImageCaption.setFilters(new InputFilter[] { new InputFilter.LengthFilter(Constants.IMAGE_CAPTION_SIZE_MAX) } );
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgViewBack:
                finish();
                break;
            case R.id.imgViewSelect:
                showLoadingDialog();
                mBinding.imgViewCrop.getCroppedImageAsync();
                break;
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        hideLoadingDialog();
        if(result.isSuccessful()&&result.getBitmap()!=null) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            result.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), result.getBitmap(), "Title", null);
            Intent intent=new Intent();
            intent.putExtra(Constants.DATA_IMAGE_URI, Uri.parse(path));
            if(mImageType==Constants.TYPE_IMAGE_PHOTO_POLL) {
                intent.putExtra(Constants.DATA_IMAGE_CAPTION, mBinding.inputImageCaption.getText().toString());
            }
            setResult(RESULT_OK, intent);
            finish();
        }
        else {
            showMessage(getString(R.string.error),result.getError().getMessage());
        }
    }
}
