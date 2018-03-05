package com.globalbit.tellyou.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.utils.Enums;

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
                    default:
                        mInputType=Enums.InputType.Text;
                        break;
                }
            }
        }
        finally {
            a.recycle();
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
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((LayoutParams)linearLayout.getLayoutParams()).gravity=Gravity.CENTER_VERTICAL;
        setInputValue(new EditText(context));
        getInputValue().setTextAppearance(context, R.style.InputTextStyle);
        getInputValue().setBackgroundResource(android.R.color.transparent);
        LinearLayout.LayoutParams inputParams=new LinearLayout.LayoutParams(0, (int)ConversionUtils.convertDpToPixel(40,context));
        inputParams.weight=1;
        inputParams.setMarginEnd((int)ConversionUtils.convertDpToPixel(10, context));
        getInputValue().setLayoutParams(inputParams);
        mImgViewEye=new ImageView(context);
        mImgViewEye.setImageResource(R.drawable.ic_eye_open_normal);
        mImgViewEye.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mImgViewEye.setVisibility(GONE);
        linearLayout.addView(getInputValue());
        linearLayout.addView(mImgViewEye);
        View view=new View(context);
        view.setBackgroundColor(context.getResources().getColor(R.color.separator));
        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)ConversionUtils.convertDpToPixel(1, context)));
        mTxtViewError=new TextView(context);
        mTxtViewError.setTextAppearance(context, R.style.ErrorTextStyle);
        LinearLayout.LayoutParams errorParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        errorParams.setMargins(0, (int)ConversionUtils.convertDpToPixel(5,context),0,0);
        mTxtViewError.setLayoutParams(errorParams);

        getInputValue().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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
                    mInputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                    getInputValue().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addView(mTxtViewLabel);
        addView(linearLayout);
        addView(view);
        addView(mTxtViewError);

    }

    public EditText getInputValue() {
        return mInputValue;
    }

    public void setInputValue(EditText inputValue) {
        mInputValue=inputValue;
    }
}
