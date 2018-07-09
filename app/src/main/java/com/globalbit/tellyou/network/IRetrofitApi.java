package com.globalbit.tellyou.network;

import com.globalbit.tellyou.model.system.SystemPreferencesResponseKT;
import com.globalbit.tellyou.network.requests.AuthenticateViaSmsRequest;
import com.globalbit.tellyou.network.requests.ContactsRequest;
import com.globalbit.tellyou.network.requests.CreateEditCommentRequest;
import com.globalbit.tellyou.network.requests.CreateEditPostRequest;
import com.globalbit.tellyou.network.requests.FacebookRequest;
import com.globalbit.tellyou.network.requests.ForgotPasswordRequest;
import com.globalbit.tellyou.network.requests.PushNotificationTokenRequest;
import com.globalbit.tellyou.network.requests.ReportRequest;
import com.globalbit.tellyou.network.requests.SearchRequest;
import com.globalbit.tellyou.network.requests.SignInUpRequest;
import com.globalbit.tellyou.network.requests.UserRequest;
import com.globalbit.tellyou.network.requests.VerifySmsAuthenticationRequest;
import com.globalbit.tellyou.network.responses.AuthenticateUserResponse;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.CommentResponse;
import com.globalbit.tellyou.network.responses.CommentsResponse;
import com.globalbit.tellyou.network.responses.FacebookFriendsResponse;
import com.globalbit.tellyou.network.responses.PostResponse;
import com.globalbit.tellyou.network.responses.PostsResponse;
import com.globalbit.tellyou.network.responses.SystemPreferencesResponse;
import com.globalbit.tellyou.network.responses.UserResponse;
import com.globalbit.tellyou.network.responses.UsernameExistResponse;
import com.globalbit.tellyou.network.responses.UsersResponse;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by alex on 29/10/2017.
 */

public interface IRetrofitApi {

    //System preferences

    @GET("system")
    Call<SystemPreferencesResponseKT> systemPreferences();


    //Authentication

    @POST("user/phone/request")
    Call<BaseResponse> authenticateViaSms(@Body AuthenticateViaSmsRequest request);

    @POST("user/phone/verify")
    Call<BaseResponse> verifySmsAuthentication(@Body VerifySmsAuthenticationRequest request);

    @DELETE("auth")
    Call<BaseResponse> signOut();

    @POST("auth/facebook")
    Call<AuthenticateUserResponse> facebookAuthentication(@Body FacebookRequest request);

    @POST("auth/forgot-password")
    Call<BaseResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/manual/signin")
    Call<AuthenticateUserResponse> sigIn(@Body SignInUpRequest request);

    @POST("auth/manual/signup")
    Call<AuthenticateUserResponse> signUp(@Body SignInUpRequest request);

    @GET("user/username-exists")
    Call<UsernameExistResponse> usernameExist(@Query("username") String username);


    //User

    @GET("user")
    Call<UserResponse> getMyDetails();

    @PATCH("user")
    Call<UserResponse> updateUserDetails(@Body UserRequest request);

    @GET("user/posts")
    Call<PostsResponse> getMyPosts(@Query("page") int page);

    @GET("user/following")
    Call<UsersResponse> getMyFollowing(@Query("page") int page);

    @GET("user/followers")
    Call<UsersResponse> getMyFollowers(@Query("page") int page, @Query("query") String query);

    @GET("user/{username}")
    Call<UserResponse> getUserDetails(@Path("username") String username);

    @GET("user/{username}/posts")
    Call<PostsResponse> getUserPosts(@Path("username") String username, @Query("page") int page);

    @GET("user/{username}/following")
    Call<UsersResponse> getUserFollowing(@Path("username") String username, @Query("page") int page);

    @GET("user/{username}/followers")
    Call<UsersResponse> getUserFollowers(@Path("username") String username, @Query("page") int page);

    @POST("user/{username}/follow")
    Call<BaseResponse> follow(@Path("username") String username);

    @DELETE("user/{username}/follow")
    Call<BaseResponse> unfollow(@Path("username") String username);

    @POST("user/{username}/report")
    Call<BaseResponse> reportUser(@Path("username") String username, @Body ReportRequest request);


    //Feed

    /*@GET("feed")
    Call<PostsResponse> getFeedPosts(@Query("page") int page);*/

    @GET("feed/following")
    Call<PostsResponse> getFeedPosts(@Query("page") int page);

    @GET("feed/popular")
    Call<PostsResponse> getPopularPosts(@Query("page") int page);


    //Post

    @Multipart
    @POST("post")
    Call<PostResponse> createPost(@Part MultipartBody.Part video, @Part MultipartBody.Part thumbnail,  @Part("text") RequestBody text, @Part("tags") ArrayList<RequestBody> tags
            , @Part("duration") RequestBody duration, @Part("width") RequestBody width, @Part("height") RequestBody height);

    @GET("post/{post}")
    Call<PostResponse> getPostById(@Path("post") String postId);

    @PATCH("post/{post}")
    Call<PostResponse> editPost(@Path("post") String postId, @Body CreateEditPostRequest request);

    @DELETE("post/{post}")
    Call<BaseResponse> deletePost(@Path("post") String postId);

    @PUT("post/{post}/end")
    Call<PostResponse> endPostVote(@Path("post") String postId);

    @POST("post/{post}/bookmark")
    Call<BaseResponse> bookmarkPost(@Path("post") String postId);

    @GET("post/bookmarked")
    Call<PostsResponse> getBookmarkedPosts(@Query("page") int page);

    @Multipart
    @POST("post/{post}/comment")
    Call<CommentResponse> createComment(@Path("post") String postId, @Part MultipartBody.Part video, @Part MultipartBody.Part thumbnail, @Part("duration") RequestBody duration);

    @GET("post/{post}/comments")
    Call<CommentsResponse> getPostComments(@Path("post") String postId, @Query("page") int page);

    @DELETE("comment/{comment}")
    Call<BaseResponse> deleteComment(@Path("comment") String commentId);

    @DELETE("post/{post}/bookmark")
    Call<BaseResponse> removeBookmarkedPost(@Path("post") String postId);

    @POST("post/{post}/report")
    Call<BaseResponse> reportPost(@Path("post") String postId, @Body ReportRequest request);

    @POST("post/{post}/view")
    Call<BaseResponse> viewPost(@Path("post") String postId);  //Use this to set that the post is viewed by user



    //Comment

    @PATCH("comment/{comment}")
    Call<CommentResponse> editComment(@Path("post") String commentId, @Body CreateEditCommentRequest request);

    @GET("comment/{comment}")
    Call<CommentResponse> getCommentById(@Path("post") String commentId);


    //Discover

    @GET("discover/suggestions")
    Call<UsersResponse> getSuggestions(@Query("page") int page);

    @GET("discover/facebook")
    Call<FacebookFriendsResponse> getFacebookFriends(@Query("facebookToken") String facebookToken, @Query("pageToken") String pageToken);


    //Notification

    @PATCH("notifications")
    Call<BaseResponse> sendToken(@Body PushNotificationTokenRequest request);


    //Search

    @GET("search/posts")
    Call<PostsResponse> searchPosts(@Query("query") String query, @Query("page") int page);

    @GET("search/users")
    Call<UsersResponse> searchUsers(@Query("query") String query, @Query("page") int page);

    @POST("search/contacts")
    Call<UsersResponse> getContacts(@Body ContactsRequest request, @Query("page") int page);
}
