package com.globalbit.tellyou.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.globalbit.androidutils.ConversionUtils;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.model.InputData;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.ValidationUtils;

/**
 * Created by alex on 05/03/2018.
 */

public class CustomEditText extends LinearLayout {
    private TextView mTxtViewLabel;
    private EditText mInputValue;
    private ImageView mImgViewEye;
    private TextView mTxtViewError;
    private String mName;
    private boolean mIsRequired=false;
    private Enums.InputType mInputType=Enums.InputType.Text;
    private boolean mIsPasswordVisible=false;

    public CustomEditText(Context context) {
        super(context);
        init(context);
    }

    public CustomEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText, 0,
                0);
        try {
            mIsRequired=a.getBoolean(R.styleable.CustomEditText_required, false);
            mName=a.getString(R.styleable.CustomEditText_name);
            String label=a.getString(R.styleable.CustomEditText_label);
            String hint=a.getString(R.styleable.CustomEditText_hint);
            mTxtViewLabel.setText(label);
            getInputValue().setHint(hint);
            if(a.hasValue(R.styleable.CustomEditText_type)) {
                int value=a.getInt(R.styleable.CustomEditText_type, 2);
                switch(value) {
                    case 0:
                        mInputType=Enums.InputType.Email;
                        mInputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    case 1:
                        mInputType=Enums.InputType.Password;
                        mImgViewEye.setVisibility(VISIBLE);
                        mIsPasswordVisible=false;
                        mImgViewEye.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(mIsPasswordVisible) {
                                    mIsPasswordVisible=false;
                                    mInputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                    mImgViewEye.setImageResource(R.drawable.ic_eye_open_normal);
                                }
                                else {
                                    mIsPasswordVisible=true;
                                    mInputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                    mImgViewEye.setImageResource(R.drawable.ic_eye_open_active);
                                }
                                mInputValue.setSelection(mInputValue.getText().length());
                            }
                        });
                        break;
                    case 3:
                        //mInputValue.setFocusable(false);
                        //mInputValue.setFocusableInTouchMode(false);
                        mInputValue.setCursorVisible(false);
                        //mInputValue.setClickable(false);
                        break;
                    default:
                        mInputType=Enums.InputType.Text;
                        mInputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                }
            }
        }
        finally {
            a.recycle();
        }
        if(mInputType==Enums.InputType.Password) {
            mInputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mInputValue.setTypeface(Typeface.DEFAULT);
            mInputValue.setTransformationMethod(new PasswordTransformationMethod());
        }
    }

    public CustomEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
        this.setOrientation(VERTICAL);
        mTxtViewLabel=new TextView(context);
        mTxtViewLabel.setTextAppearance(context, R.style.LabelTextStyle);
        mTxtViewLabel.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTxtViewLabel.setAllCaps(true);
        LinearLayout linearLayout=new LinearLayout(context);
        LayoutParams params=new LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        params.gravity=Gravity.CENTER_VERTICAL;
        linearLayout.setLayoutParams(params);
        setInputValue(new EditText(context));
        getInputValue().setTextAppearance(context, R.style.InputTextStyle);
        getInputValue().setBackgroundResource(android.R.color.transparent);
        LinearLayout.LayoutParams inputParams=new LinearLayout.LayoutParams(0, (int)ConversionUtils.convertDpToPixel(40,context));
        inputParams.weight=1;
        inputParams.setMarginEnd((int)ConversionUtils.convertDpToPixel(10, context));
        getInputValue().setLayoutParams(inputParams);
        getInputValue().setIncludeFontPadding(false);
        getInputValue().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mImgViewEye=new ImageView(context);
        mImgViewEye.setImageResource(R.drawable.ic_eye_open_normal);
        mImgViewEye.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mImgViewEye.setVisibility(GONE);
        ((LayoutParams)mImgViewEye.getLayoutParams()).gravity=Gravity.CENTER_VERTICAL;
        linearLayout.addView(getInputValue());
        linearLayout.addView(mImgViewEye);
        View view=new View(context);
        view.setBackgroundColor(context.getResources().getColor(R.color.separator));
        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)ConversionUtils.convertDpToPixel(1, context)));
        setTxtViewError(new TextView(context));
        getTxtViewError().setTextAppearance(context, R.style.ErrorTextStyle);
        LinearLayout.LayoutParams errorParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT/*(int)ConversionUtils.convertDpToPixel(20,context)*/);
        errorParams.setMargins(0, (int)ConversionUtils.convertDpToPixel(5,context),0,(int)ConversionUtils.convertDpToPixel(5,context));
        getTxtViewError().setLayoutParams(errorParams);
        getInputValue().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mInputType==Enums.InputType.Password) {
                    //Log.i("Test", "beforeTextChanged: "+charSequence+","+i+","+i1+","+i2);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.i("Test", "onTextChanged: "+charSequence+","+i+","+i1+","+i2);
                getTxtViewError().setText("");
                if(charSequence.length()>0) {
                    getInputValue().setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    if(mInputType==Enums.InputType.Password) {
                        if(mIsPasswordVisible) {
                            mInputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        } else {
                            mInputValue.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        mInputValue.setSelection(mInputValue.getText().length());
                    }
                }
                else {
                    if(mInputType==Enums.InputType.Password&&i1>0) {
                        mInputValue.setTypeface(Typeface.DEFAULT);
                        mInputValue.setTransformationMethod(new PasswordTransformationMethod());
                        mIsPasswordVisible=false;
                        mImgViewEye.setImageResource(R.drawable.ic_eye_open_normal);
                    }
                    getInputValue().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.i("Test", "afterTextChanged: ");
            }
        });

        addView(mTxtViewLabel);
        addView(linearLayout);
        addView(view);
        addView(getTxtViewError());

    }

    public EditText getInputValue() {
        return mInputValue;
    }

    public void setInputValue(EditText inputValue) {
        mInputValue=inputValue;
    }

    public boolean validate() {
        boolean isValid=true;
        InputData data=new InputData();
        data.setInputType(mInputType);
        data.setName(mName);
        data.setValue(mInputValue.getText().toString());
        data.setRequired(mIsRequired);
        String errorMessage=ValidationUtils.validate(data);
        if(!StringUtils.isEmpty(errorMessage)) {
            getTxtViewError().setText(errorMessage);
            isValid=false;
        }
        return isValid;
    }

    public TextView getTxtViewError() {
        return mTxtViewError;
    }

    public void setTxtViewError(TextView txtViewError) {
        mTxtViewError=txtViewError;
    }
}
