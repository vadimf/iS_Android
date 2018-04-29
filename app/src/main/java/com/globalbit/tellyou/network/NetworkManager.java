package com.globalbit.tellyou.network;

import com.globalbit.tellyou.model.system.SystemPreferencesResponseKT;
import com.globalbit.tellyou.network.callbacks.AuthenticateUserNetworkCallback;
import com.globalbit.tellyou.network.callbacks.BaseNetworkCallback;
import com.globalbit.tellyou.network.callbacks.CommentNetworkCallback;
import com.globalbit.tellyou.network.callbacks.CommentsNetworkCallback;
import com.globalbit.tellyou.network.callbacks.FacebookFriendsNetworkCallback;
import com.globalbit.tellyou.network.callbacks.FollowNetworkCallback;
import com.globalbit.tellyou.network.callbacks.PostNetworkCallback;
import com.globalbit.tellyou.network.callbacks.PostsNetworkCallback;
import com.globalbit.tellyou.network.callbacks.SystemPreferencesNetworkCallback;
import com.globalbit.tellyou.network.callbacks.UserNetworkCallback;
import com.globalbit.tellyou.network.callbacks.UsernameExistNetworkCallback;
import com.globalbit.tellyou.network.callbacks.UsersNetworkCallback;
import com.globalbit.tellyou.network.callbacks.UsersSearchNetworkCallback;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
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
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by alex on 29/10/2017.
 */

public class NetworkManager {
    private Retrofit mRetrofit;
    private IRetrofitApi mIRetrofitApiInterface;
    private static NetworkManager ourInstance=new NetworkManager();

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    private NetworkManager() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(); //For logging the network request and responses
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("X-Authorization", SharedPrefsUtils.getAuthorization())
                        .header("Content-Type", "application/json; charset=utf-8")
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        };
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(httpLoggingInterceptor).connectTimeout(30, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build(); //TODO decide the proper connection and read timeout
        GsonBuilder gsonBuilder = new GsonBuilder();
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

            @Override
            public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    return df.parse(json.getAsString());
                } catch (ParseException e) {
                    return null;
                }
            }
        });
        Gson gson = gsonBuilder.create();
       /* Gson gson=new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();*/
        setRetrofit(new Retrofit.Builder()
                .baseUrl(NetworkConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build());
        mIRetrofitApiInterface=getRetrofit().create(IRetrofitApi.class);
    }




    public void systemPreferences(IBaseNetworkResponseListener<SystemPreferencesResponseKT> listener) {
        Call<SystemPreferencesResponseKT> call=mIRetrofitApiInterface.systemPreferences();
        SystemPreferencesNetworkCallback callback=new SystemPreferencesNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void authenticateViaSms(IBaseNetworkResponseListener<BaseResponse> listener, AuthenticateViaSmsRequest request) {
        Call<BaseResponse> call=mIRetrofitApiInterface.authenticateViaSms(request);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.PhoneVerification);
        call.enqueue(callback);
    }

    public void verifySmsAuthentication(IBaseNetworkResponseListener<BaseResponse> listener, VerifySmsAuthenticationRequest request) {
        Call<BaseResponse> call=mIRetrofitApiInterface.verifySmsAuthentication(request);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener,Enums.RequestType.CodeConfirmation);
        call.enqueue(callback);
    }

    public void signOut(IBaseNetworkResponseListener<BaseResponse> listener) {
        Call<BaseResponse> call=mIRetrofitApiInterface.signOut();
        BaseNetworkCallback callback=new BaseNetworkCallback(listener,Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void facebookAuthentication(IBaseNetworkResponseListener<AuthenticateUserResponse> listener, FacebookRequest request) {
        Call<AuthenticateUserResponse> call=mIRetrofitApiInterface.facebookAuthentication(request);
        AuthenticateUserNetworkCallback callback=new AuthenticateUserNetworkCallback(listener, Enums.RequestType.Connection);
        call.enqueue(callback);
    }

    public void forgotPassword(IBaseNetworkResponseListener<BaseResponse> listener, ForgotPasswordRequest request) {
        Call<BaseResponse> call=mIRetrofitApiInterface.forgotPassword(request);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.ForgotPassword);
        call.enqueue(callback);
    }

    public void sigIn(IBaseNetworkResponseListener<AuthenticateUserResponse> listener, SignInUpRequest request) {
        Call<AuthenticateUserResponse> call=mIRetrofitApiInterface.sigIn(request);
        AuthenticateUserNetworkCallback callback=new AuthenticateUserNetworkCallback(listener, Enums.RequestType.SignIn);
        call.enqueue(callback);
    }

    public void signUp(IBaseNetworkResponseListener<AuthenticateUserResponse> listener, SignInUpRequest request) {
        Call<AuthenticateUserResponse> call=mIRetrofitApiInterface.signUp(request);
        AuthenticateUserNetworkCallback callback=new AuthenticateUserNetworkCallback(listener, Enums.RequestType.SignUp);
        call.enqueue(callback);
    }

    public void usernameExist(IBaseNetworkResponseListener<UsernameExistResponse> listener, String username) {
        Call<UsernameExistResponse> call=mIRetrofitApiInterface.usernameExist(username);
        UsernameExistNetworkCallback callback=new UsernameExistNetworkCallback(listener, username);
        call.enqueue(callback);
    }

    public void getMyDetails(IBaseNetworkResponseListener<UserResponse> listener) {
        Call<UserResponse> call=mIRetrofitApiInterface.getMyDetails();
        UserNetworkCallback callback=new UserNetworkCallback(listener, Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void updateUserDetails(IBaseNetworkResponseListener<UserResponse> listener, UserRequest request) {
        Call<UserResponse> call=mIRetrofitApiInterface.updateUserDetails(request);
        UserNetworkCallback callback=new UserNetworkCallback(listener, Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void getMyPosts(IBaseNetworkResponseListener<PostsResponse> listener, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.getMyPosts(page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener, null);
        call.enqueue(callback);
    }

    public void getMyFollowing(IBaseNetworkResponseListener<UsersResponse> listener, int page) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getMyFollowing(page);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getMyFollowers(IBaseNetworkResponseListener<UsersResponse> listener, int page, String query) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getMyFollowers(page, query);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getUserDetails(IBaseNetworkResponseListener<UserResponse> listener, String username) {
        Call<UserResponse> call=mIRetrofitApiInterface.getUserDetails(username);
        UserNetworkCallback callback=new UserNetworkCallback(listener,Enums.RequestType.GetUserDetails);
        call.enqueue(callback);
    }

    public void getUserPosts(IBaseNetworkResponseListener<PostsResponse> listener, String username, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.getUserPosts(username, page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener, null);
        call.enqueue(callback);
    }

    public void getUserFollowing(IBaseNetworkResponseListener<UsersResponse> listener, String username, int page) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getUserFollowing(username, page);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getUserFollowers(IBaseNetworkResponseListener<UsersResponse> listener, String username, int page) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getUserFollowers(username, page);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void follow(IBaseNetworkResponseListener<BaseResponse> listener, String username) {
        Call<BaseResponse> call=mIRetrofitApiInterface.follow(username);
        FollowNetworkCallback callback=new FollowNetworkCallback(listener, Enums.RequestType.Follow);
        call.enqueue(callback);
    }

    public void unfollow(IBaseNetworkResponseListener<BaseResponse> listener, String username) {
        Call<BaseResponse> call=mIRetrofitApiInterface.unfollow(username);
        FollowNetworkCallback callback=new FollowNetworkCallback(listener, Enums.RequestType.UnFollow);
        call.enqueue(callback);
    }

    public void reportUser(IBaseNetworkResponseListener<BaseResponse> listener, String username, ReportRequest request) {
        Call<BaseResponse> call=mIRetrofitApiInterface.reportUser(username, request);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void getFeedPosts(IBaseNetworkResponseListener<PostsResponse> listener, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.getFeedPosts(page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener, null);
        call.enqueue(callback);
    }

    public void getPopularPosts(IBaseNetworkResponseListener<PostsResponse> listener, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.getPopularPosts(page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener, null);
        call.enqueue(callback);
    }

    public void createPost(IBaseNetworkResponseListener<PostResponse> listener, MultipartBody.Part video, MultipartBody.Part thumbnail, RequestBody text, ArrayList<RequestBody> tags, RequestBody duration) {

        Call<PostResponse> call=mIRetrofitApiInterface.createPost(video, thumbnail, text, tags, duration);
        PostNetworkCallback callback=new PostNetworkCallback(listener, Enums.RequestType.CakePost);
        call.enqueue(callback);
    }

    public void getPostById(IBaseNetworkResponseListener<PostResponse> listener, String id) {
        Call<PostResponse> call=mIRetrofitApiInterface.getPostById(id);
        PostNetworkCallback callback=new PostNetworkCallback(listener, Enums.RequestType.GetPost);
        call.enqueue(callback);
    }

    public void editPost(IBaseNetworkResponseListener<PostResponse> listener, String id, CreateEditPostRequest request) {
        Call<PostResponse> call=mIRetrofitApiInterface.editPost(id, request);
        PostNetworkCallback callback=new PostNetworkCallback(listener,Enums.RequestType.GetPost);
        call.enqueue(callback);
    }

    public void deletePost(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.deletePost(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.DeletePost);
        call.enqueue(callback);
    }

    public void endPostVote(IBaseNetworkResponseListener<PostResponse> listener, String id) {
        Call<PostResponse> call=mIRetrofitApiInterface.endPostVote(id);
        PostNetworkCallback callback=new PostNetworkCallback(listener,Enums.RequestType.EndVoting);
        call.enqueue(callback);
    }

    public void bookmarkPost(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.bookmarkPost(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.BookmarkPost);
        call.enqueue(callback);
    }

    public void getBookmarkedPosts(IBaseNetworkResponseListener<PostsResponse> listener, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.getBookmarkedPosts(page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener, null);
        call.enqueue(callback);
    }

    public void createComment(IBaseNetworkResponseListener<CommentResponse> listener, String postId, MultipartBody.Part video, MultipartBody.Part thumbnail, RequestBody duration) {
        Call<CommentResponse> call=mIRetrofitApiInterface.createComment(postId, video, thumbnail, duration);
        CommentNetworkCallback callback=new CommentNetworkCallback(listener, Enums.RequestType.GetComments);
        call.enqueue(callback);
    }

    public void getPostComments(IBaseNetworkResponseListener<CommentsResponse> listener, String postId, int page) {
        Call<CommentsResponse> call=mIRetrofitApiInterface.getPostComments(postId, page);
        CommentsNetworkCallback callback=new CommentsNetworkCallback(listener, Enums.RequestType.GetComments);
        call.enqueue(callback);
    }

    public void deleteComment(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.deleteComment(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void removeBookmarkedPost(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.removeBookmarkedPost(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.DeleteBookmarkPost);
        call.enqueue(callback);
    }

    public void reportPost(IBaseNetworkResponseListener<BaseResponse> listener, String id, ReportRequest request) {
        Call<BaseResponse> call=mIRetrofitApiInterface.reportPost(id, request);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void viewPost(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.viewPost(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void editComment(IBaseNetworkResponseListener<CommentResponse> listener, String commentId, CreateEditCommentRequest request) {
        Call<CommentResponse> call=mIRetrofitApiInterface.editComment(commentId, request);
        CommentNetworkCallback callback=new CommentNetworkCallback(listener, Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void getCommentById(IBaseNetworkResponseListener<CommentResponse> listener, String commentId) {
        Call<CommentResponse> call=mIRetrofitApiInterface.getCommentById(commentId);
        CommentNetworkCallback callback=new CommentNetworkCallback(listener, Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void getContacts(IBaseNetworkResponseListener<UsersResponse> listener, ContactsRequest request, int page) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getContacts(request, page);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getSuggestions(IBaseNetworkResponseListener<UsersResponse> listener, int page) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getSuggestions(page);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getFacebookFriends(IBaseNetworkResponseListener<FacebookFriendsResponse> listener, String facebookToken, String pageToken) {
        Call<FacebookFriendsResponse> call=mIRetrofitApiInterface.getFacebookFriends(facebookToken, pageToken);
        FacebookFriendsNetworkCallback callback=new FacebookFriendsNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void sendToken(IBaseNetworkResponseListener<BaseResponse> listener, PushNotificationTokenRequest request) {
        Call<BaseResponse> call=mIRetrofitApiInterface.sendToken(request);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener, Enums.RequestType.General);
        call.enqueue(callback);
    }

    public void searchPosts(IBaseNetworkResponseListener<PostsResponse> listener, String query, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.searchPosts(query, page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener, query);
        call.enqueue(callback);
    }

    public void searchUsers(IBaseNetworkResponseListener<UsersResponse> listener,String query, int page) {
        Call<UsersResponse> call=mIRetrofitApiInterface.searchUsers(query, page);
        UsersSearchNetworkCallback callback=new UsersSearchNetworkCallback(listener, query);
        call.enqueue(callback);
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public void setRetrofit(Retrofit retrofit) {
        mRetrofit=retrofit;
    }
}
