package com.globalbit.isay.network;

import com.globalbit.isay.network.requests.AuthenticateViaSmsRequest;
import com.globalbit.isay.network.requests.ContactsRequest;
import com.globalbit.isay.network.requests.CreateEditCommentRequest;
import com.globalbit.isay.network.requests.CreateEditPostRequest;
import com.globalbit.isay.network.requests.PushNotificationTokenRequest;
import com.globalbit.isay.network.requests.SearchRequest;
import com.globalbit.isay.network.requests.UserRequest;
import com.globalbit.isay.network.requests.VerifySmsAuthenticationRequest;
import com.globalbit.isay.network.responses.AuthenticateUserResponse;
import com.globalbit.isay.network.responses.BaseResponse;
import com.globalbit.isay.network.responses.CommentResponse;
import com.globalbit.isay.network.responses.CommentsResponse;
import com.globalbit.isay.network.responses.PostResponse;
import com.globalbit.isay.network.responses.PostsResponse;
import com.globalbit.isay.network.responses.SystemPreferencesResponse;
import com.globalbit.isay.network.responses.UserResponse;
import com.globalbit.isay.network.responses.UsersResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by alex on 29/10/2017.
 */

public interface IRetrofitApi {

    //System preferences

    @GET("system")
    Call<SystemPreferencesResponse> systemPreferences();


    //Authentication

    @POST("auth/phone/request")
    Call<BaseResponse> authenticateViaSms(@Body AuthenticateViaSmsRequest request);

    @POST("auth/phone/verify")
    Call<AuthenticateUserResponse> verifySmsAuthentication(@Body VerifySmsAuthenticationRequest request);

    @DELETE("auth")
    Call<BaseResponse> signOut();


    //User

    @GET("user")
    Call<UserResponse> getMyDetails();

    @POST("user")
    Call<UserResponse> updateUserDetails(@Body UserRequest request);

    @POST("user/posts")
    Call<PostsResponse> getMyPosts(@Query("page") int page);

    @POST("user/following")
    Call<UsersResponse> getMyFollowing(@Query("page") int page);

    @POST("user/followers")
    Call<UsersResponse> getMyFollowers(@Query("page") int page);

    @GET("user/{username}")
    Call<UserResponse> getUserDetails(@Path("username") String username);

    @POST("user/{username}/posts")
    Call<PostsResponse> getUserPosts(@Path("username") String username, @Query("page") int page);

    @POST("user/{username}/following")
    Call<UsersResponse> getUserFollowing(@Path("username") String username, @Query("page") int page);

    @POST("user/{username}/followers")
    Call<UsersResponse> getUserFollowers(@Path("username") String username, @Query("page") int page);

    @POST("user/{username}/follow")
    Call<BaseResponse> follow(@Path("username") String username);

    @DELETE("user/{username}/follow")
    Call<BaseResponse> unfollow(@Path("username") String username);

    @POST("user/{username}/report")
    Call<BaseResponse> reportUser(@Path("username") String username);  //TODO add proper request when service is ready


    //Feed

    @POST("feed")
    Call<PostsResponse> getFeedPosts(@Query("page") int page);


    //Post

    @POST("post")
    Call<PostResponse> createPost(@Body CreateEditPostRequest request);

    @GET("post/{post}")
    Call<PostResponse> getPostById(@Path("post") String postId);

    @PATCH("post/{post}")
    Call<PostResponse> editPost(@Path("post") String postId, @Body CreateEditPostRequest request);

    @DELETE("post/{post}")
    Call<BaseResponse> deletePost(@Path("post") String postId);

    @GET("post/{post}/bookmark")
    Call<BaseResponse> bookmarkPost(@Path("post") String postId);

    @POST("post/bookmarked")
    Call<PostsResponse> getBookmarkedPosts(@Query("page") int page);

    @POST("post/{post}/comment")
    Call<CommentResponse> createComment(@Path("post") String postId, @Body CreateEditCommentRequest request);

    @POST("post/{post}/comments")
    Call<CommentsResponse> getPostComments(@Path("post") String postId, @Query("page") int page);

    @DELETE("comment/{comment}")
    Call<BaseResponse> deleteComment(@Path("comment") String commentId);

    @DELETE("post/{post}/bookmark")
    Call<BaseResponse> removeBookmarkedPost(@Path("post") String postId);

    @POST("post/{post}/report")
    Call<BaseResponse> reportPost(@Path("post") String postId);

    @POST("post/{post}/view")
    Call<BaseResponse> viewPost(@Path("post") String postId);  //Use this to set that the post is viewed by user


    //Comment

    @PATCH("comment/{comment}")
    Call<CommentResponse> editComment(@Path("post") String commentId, @Body CreateEditCommentRequest request);

    @GET("comment/{comment}")
    Call<CommentResponse> getCommentById(@Path("post") String commentId);


    //Discover

    @POST("discover/contacts")
    Call<UsersResponse> getContacts(@Body ContactsRequest request);

    @POST("discover/suggestions")
    Call<UsersResponse> getSuggestions(@Query("page") int page);


    //Notification

    @PATCH("notifications")
    Call<BaseResponse> sendToken(@Body PushNotificationTokenRequest request);


    //Search

    @POST("search/posts")
    Call<PostsResponse> searchPosts(@Body SearchRequest request);

    @POST("search/users")
    Call<UsersResponse> searchUsers(@Body SearchRequest request);
}
