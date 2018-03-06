package com.globalbit.tellyou.utils;

import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.model.InputData;

import java.util.Locale;

/**
 * Created by alex on 06/03/2018.
 */

public class ValidationUtils {

    public static String validate(InputData data) {
        String errorMessage=null;
        if(StringUtils.isEmpty(data.getValue())) {
            errorMessage=String.format(Locale.getDefault(),"%s %s",data.getName(), CustomApplication.getAppContext().getString(R.string.error_empty));
        }
        else {
            switch(data.getInputType()) {
                case Email:
                    if(!android.util.Patterns.EMAIL_ADDRESS.matcher(data.getValue()).matches()) {
                        errorMessage=CustomApplication.getAppContext().getString(R.string.error_invalid_mail);
                    }
                    break;
                case Password:
                    if(CustomApplication.getSystemPreference()!=null) {
                        if(data.getValue().length()<CustomApplication.getSystemPreference().getValidations().getPassword().getMinLength()
                                ||data.getValue().length()>CustomApplication.getSystemPreference().getValidations().getPassword().getMaxLength()) {
                            errorMessage=String.format(Locale.getDefault(),  CustomApplication.getAppContext().getString(R.string.error_password_length),CustomApplication.getSystemPreference().getValidations().getPassword().getMinLength(),
                                    CustomApplication.getSystemPreference().getValidations().getPassword().getMaxLength())+"\n";
                        }
                    }
                    break;
                case Text:
                    break;
            }
        }
        return errorMessage;
    }
}
