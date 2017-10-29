package com.globalbit.isay.utils;

import com.globalbit.isay.CustomApplication;
import com.globalbit.isay.R;

/**
 * Created by alex on 05/02/2017.
 */

public class ErrorUtils {

    public static String getErrorMessage(int errorCode) {
        switch(errorCode) {
            case 1000:
                return CustomApplication.getAppContext().getString(R.string.error_code_1000_description);
            case 1001:
                return CustomApplication.getAppContext().getString(R.string.error_code_1001_description);
            case 1004:
                return CustomApplication.getAppContext().getString(R.string.error_code_1004_description);
            case 1005:
                return CustomApplication.getAppContext().getString(R.string.error_code_1005_description);
            case 1010:
                return CustomApplication.getAppContext().getString(R.string.error_code_1010_description);
            case 1020:
                return CustomApplication.getAppContext().getString(R.string.error_code_1020_description);
            case 1030:
                return CustomApplication.getAppContext().getString(R.string.error_code_1030_description);
            case 1031:
                return CustomApplication.getAppContext().getString(R.string.error_code_1031_description);
            case 2000:
                return CustomApplication.getAppContext().getString(R.string.error_code_2000_description);
            case 2001:
                return CustomApplication.getAppContext().getString(R.string.error_code_2001_description);
            case 2010:
                return CustomApplication.getAppContext().getString(R.string.error_code_2010_description);
            case 2020:
                return CustomApplication.getAppContext().getString(R.string.error_code_2020_description);
            case 2030:
                return CustomApplication.getAppContext().getString(R.string.error_code_2030_description);
            case 2040:
                return CustomApplication.getAppContext().getString(R.string.error_code_2040_description);
            case 2050:
                return CustomApplication.getAppContext().getString(R.string.error_code_2050_description);
            case 2060:
                return CustomApplication.getAppContext().getString(R.string.error_code_2060_description);
            case 2061:
                return CustomApplication.getAppContext().getString(R.string.error_code_2061_description);
            case 2070:
                return CustomApplication.getAppContext().getString(R.string.error_code_2070_description);
            case 2080:
                return CustomApplication.getAppContext().getString(R.string.error_code_2080_description);
            case 2081:
                return CustomApplication.getAppContext().getString(R.string.error_code_2081_description);
            case 2090:
                return CustomApplication.getAppContext().getString(R.string.error_code_2090_description);
            case 2091:
                return CustomApplication.getAppContext().getString(R.string.error_code_2091_description);
            default:
                return CustomApplication.getAppContext().getString(R.string.error_code_1000_description);
        }
    }
}
