package com.globalbit.tellyou.utils;

/**
 * Created by alex on 06/11/2017.
 */

public class Enums {

    public enum LoginState {
        SignUp,
        SignIn,
        ForgotPassword
    }

    public enum ProfileState {
        MyProfile,
        UserProfile
    }

    public enum QuestionState {
        General,
        GeneralQuestion,
        TextQuestion,
        PhotoQuestion
    }

    public enum PostPrivacyType {
        Public,
        Followers,
        Specific
    }

    public enum RegisterState {
        UsernameSate,
        ProfileState
    }

    public enum RequestType {
        PhoneVerification,
        CodeConfirmation,
        GetPost,
        GetComments,
        DeletePost,
        EndVoting,
        ForwardPost,
        CakePost,
        VotePost,
        BookmarkPost,
        DeleteBookmarkPost,
        GetUserDetails,
        SignIn,
        SignUp,
        ForgotPassword,
        Connection,
        General,
        UnFollow,
        Follow
    }

    public enum RecordingState {
        Initial,
        Recording,
        Stopped,
        Finished,
        NoPermissions
    }

    public enum InputType {
        Email,
        Password,
        Text
    }

}
