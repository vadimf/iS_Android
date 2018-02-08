package com.globalbit.tellyou.utils;


import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.responses.BaseResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by alex on 05/02/2017.
 */

public class ErrorUtils {

    public static String getErrorMessage(int errorCode, Enums.RequestType requestType) {
        Answers.getInstance().logCustom(new CustomEvent("Error event")
                .putCustomAttribute(String.format(Locale.getDefault(),"%d",errorCode), requestType.toString()));
        switch(errorCode) {
            case 100:
                return CustomApplication.getAppContext().getString(R.string.error_code_100_description);
            case 200:
                switch(requestType) {
                    case UnFollow:
                        return CustomApplication.getAppContext().getString(R.string.error_code_200_un_follow);
                    case CodeConfirmation:
                        return CustomApplication.getAppContext().getString(R.string.error_code_200_code_confirmation);
                    case GetComments:
                    case GetPost:
                    case DeletePost:
                    case EndVoting:
                    case ForwardPost:
                    case CakePost:
                    case VotePost:
                    case BookmarkPost:
                    case DeleteBookmarkPost:
                        return CustomApplication.getAppContext().getString(R.string.error_code_200_post_not_exist);
                    case GetUserDetails:
                        return CustomApplication.getAppContext().getString(R.string.error_code_200_get_user_details);
                    case SignIn:
                        return CustomApplication.getAppContext().getString(R.string.error_code_200_sign_in);
                    case ForgotPassword:
                        return CustomApplication.getAppContext().getString(R.string.error_code_200_forgot_password);
                    case Follow:
                        return CustomApplication.getAppContext().getString(R.string.error_code_200_get_user_details);
                }
                return CustomApplication.getAppContext().getString(R.string.error_code_200_description);
            case 202:
                switch(requestType) {
                    case PhoneVerification:
                        return CustomApplication.getAppContext().getString(R.string.error_code_202_phone_already_exist);
                    case SignUp:
                        return CustomApplication.getAppContext().getString(R.string.error_code_202_email_already_exist);
                    case Follow:
                        return CustomApplication.getAppContext().getString(R.string.error_code_202_follow);
                    case BookmarkPost:
                        return CustomApplication.getAppContext().getString(R.string.error_code_202_bookmark_post);
                    case CakePost:
                        return CustomApplication.getAppContext().getString(R.string.error_code_202_cake_post);
                }
                return CustomApplication.getAppContext().getString(R.string.error_code_202_description);
            case 300:
                return CustomApplication.getAppContext().getString(R.string.error_code_300_description);
            case 301:
                return CustomApplication.getAppContext().getString(R.string.error_code_301_description);
            case 500:
                return CustomApplication.getAppContext().getString(R.string.error_code_500_description);
            case 1000:
                return CustomApplication.getAppContext().getString(R.string.error_code_1000_description);
            case 1001:
                return CustomApplication.getAppContext().getString(R.string.error_code_1001_description);
            case 2000:
                return CustomApplication.getAppContext().getString(R.string.error_code_2000_description);
            case 3000:
                return CustomApplication.getAppContext().getString(R.string.error_code_3000_description);
            case 3001:
                return CustomApplication.getAppContext().getString(R.string.error_code_3001_description);
            case 3002:
                return CustomApplication.getAppContext().getString(R.string.error_code_3002_description);
            case 5000:
                return CustomApplication.getAppContext().getString(R.string.error_code_5000_description);
            case 5001:
                return CustomApplication.getAppContext().getString(R.string.error_code_5001_description);
            case 5002:
                return CustomApplication.getAppContext().getString(R.string.error_code_5002_description);
            case 5003:
                return CustomApplication.getAppContext().getString(R.string.error_code_5003_description);
            case 5004:
                return CustomApplication.getAppContext().getString(R.string.error_code_5004_description);
            case 9999:
                return CustomApplication.getAppContext().getString(R.string.dialog_content_error_internet_connection);
            default:
/*                Answers.getInstance().logCustom(new CustomEvent("Unknown error event")
                        .putCustomAttribute(errorCode+requestType.toString(), "Some error"));*/
                return CustomApplication.getAppContext().getString(R.string.error_code_100_description);
        }
    }

    public static BaseResponse parseError(Response<?> response) {
        Converter<ResponseBody, BaseResponse> converter =
                NetworkManager.getInstance().getRetrofit()
                        .responseBodyConverter(BaseResponse.class, new Annotation[0]);

        BaseResponse error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new BaseResponse();
        }

        return error;
    }
}
