package com.globalbit.isay.network;

import com.globalbit.isay.network.callbacks.AuthenticateUserNetworkCallback;
import com.globalbit.isay.network.callbacks.BaseNetworkCallback;
import com.globalbit.isay.network.callbacks.CommentNetworkCallback;
import com.globalbit.isay.network.callbacks.CommentsNetworkCallback;
import com.globalbit.isay.network.callbacks.PostNetworkCallback;
import com.globalbit.isay.network.callbacks.PostsNetworkCallback;
import com.globalbit.isay.network.callbacks.SystemPreferencesNetworkCallback;
import com.globalbit.isay.network.callbacks.UserNetworkCallback;
import com.globalbit.isay.network.callbacks.UsersNetworkCallback;
import com.globalbit.isay.network.interfaces.IBaseNetworkResponseListener;
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
import com.globalbit.isay.utils.SharedPrefsUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("X-Authorization", SharedPrefsUtils.getAuthorization())
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        };
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(httpLoggingInterceptor).connectTimeout(30, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build(); //TODO decide the proper connection and read timeout

        Gson gson=new Gson();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(NetworkConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mIRetrofitApiInterface =mRetrofit.create(IRetrofitApi.class);
    }


    public void systemPreferences(IBaseNetworkResponseListener<SystemPreferencesResponse> listener) {
        Call<SystemPreferencesResponse> call=mIRetrofitApiInterface.systemPreferences();
        SystemPreferencesNetworkCallback callback=new SystemPreferencesNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void authenticateViaSms(IBaseNetworkResponseListener<BaseResponse> listener, AuthenticateViaSmsRequest request) {
        Call<BaseResponse> call=mIRetrofitApiInterface.authenticateViaSms(request);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void verifySmsAuthentication(IBaseNetworkResponseListener<AuthenticateUserResponse> listener, VerifySmsAuthenticationRequest request) {
        Call<AuthenticateUserResponse> call=mIRetrofitApiInterface.verifySmsAuthentication(request);
        AuthenticateUserNetworkCallback callback=new AuthenticateUserNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void authenticateViaSms(IBaseNetworkResponseListener<BaseResponse> listener) {
        Call<BaseResponse> call=mIRetrofitApiInterface.signOut();
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getMyDetails(IBaseNetworkResponseListener<UserResponse> listener) {
        Call<UserResponse> call=mIRetrofitApiInterface.getMyDetails();
        UserNetworkCallback callback=new UserNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void updateUserDetails(IBaseNetworkResponseListener<UserResponse> listener, UserRequest request) {
        Call<UserResponse> call=mIRetrofitApiInterface.updateUserDetails(request);
        UserNetworkCallback callback=new UserNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getMyPosts(IBaseNetworkResponseListener<PostsResponse> listener, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.getMyPosts(page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getMyFollowing(IBaseNetworkResponseListener<UsersResponse> listener, int page) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getMyFollowing(page);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getMyFollowers(IBaseNetworkResponseListener<UsersResponse> listener, int page) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getMyFollowers(page);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getUserDetails(IBaseNetworkResponseListener<UserResponse> listener, String username) {
        Call<UserResponse> call=mIRetrofitApiInterface.getUserDetails(username);
        UserNetworkCallback callback=new UserNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getUserPosts(IBaseNetworkResponseListener<PostsResponse> listener, String username, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.getUserPosts(username, page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener);
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
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void unfollow(IBaseNetworkResponseListener<BaseResponse> listener, String username) {
        Call<BaseResponse> call=mIRetrofitApiInterface.unfollow(username);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void reportUser(IBaseNetworkResponseListener<BaseResponse> listener, String username) {
        Call<BaseResponse> call=mIRetrofitApiInterface.reportUser(username);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getFeedPosts(IBaseNetworkResponseListener<PostsResponse> listener, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.getFeedPosts(page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void createPost(IBaseNetworkResponseListener<PostResponse> listener, CreateEditPostRequest request) {
        Call<PostResponse> call=mIRetrofitApiInterface.createPost(request);
        PostNetworkCallback callback=new PostNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getPostById(IBaseNetworkResponseListener<PostResponse> listener, String id) {
        Call<PostResponse> call=mIRetrofitApiInterface.getPostById(id);
        PostNetworkCallback callback=new PostNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void editPost(IBaseNetworkResponseListener<PostResponse> listener, String id, CreateEditPostRequest request) {
        Call<PostResponse> call=mIRetrofitApiInterface.editPost(id, request);
        PostNetworkCallback callback=new PostNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void deletePost(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.deletePost(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void bookmarkPost(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.bookmarkPost(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getBookmarkedPosts(IBaseNetworkResponseListener<PostsResponse> listener, int page) {
        Call<PostsResponse> call=mIRetrofitApiInterface.getBookmarkedPosts(page);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void createComment(IBaseNetworkResponseListener<CommentResponse> listener, String postId, CreateEditCommentRequest request) {
        Call<CommentResponse> call=mIRetrofitApiInterface.createComment(postId, request);
        CommentNetworkCallback callback=new CommentNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getPostComments(IBaseNetworkResponseListener<CommentsResponse> listener, String postId, int page) {
        Call<CommentsResponse> call=mIRetrofitApiInterface.getPostComments(postId, page);
        CommentsNetworkCallback callback=new CommentsNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void deleteComment(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.deleteComment(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void removeBookmarkedPost(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.removeBookmarkedPost(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void reportPost(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.reportPost(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void viewPost(IBaseNetworkResponseListener<BaseResponse> listener, String id) {
        Call<BaseResponse> call=mIRetrofitApiInterface.viewPost(id);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void editComment(IBaseNetworkResponseListener<CommentResponse> listener, String commentId, CreateEditCommentRequest request) {
        Call<CommentResponse> call=mIRetrofitApiInterface.editComment(commentId, request);
        CommentNetworkCallback callback=new CommentNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getCommentById(IBaseNetworkResponseListener<CommentResponse> listener, String commentId) {
        Call<CommentResponse> call=mIRetrofitApiInterface.getCommentById(commentId);
        CommentNetworkCallback callback=new CommentNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getContacts(IBaseNetworkResponseListener<UsersResponse> listener, ContactsRequest request) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getContacts(request);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void getSuggestions(IBaseNetworkResponseListener<UsersResponse> listener, int page) {
        Call<UsersResponse> call=mIRetrofitApiInterface.getSuggestions(page);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void sendToken(IBaseNetworkResponseListener<BaseResponse> listener, PushNotificationTokenRequest request) {
        Call<BaseResponse> call=mIRetrofitApiInterface.sendToken(request);
        BaseNetworkCallback callback=new BaseNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void searchPosts(IBaseNetworkResponseListener<PostsResponse> listener, SearchRequest request) {
        Call<PostsResponse> call=mIRetrofitApiInterface.searchPosts(request);
        PostsNetworkCallback callback=new PostsNetworkCallback(listener);
        call.enqueue(callback);
    }

    public void searchUsers(IBaseNetworkResponseListener<UsersResponse> listener, SearchRequest request) {
        Call<UsersResponse> call=mIRetrofitApiInterface.searchUsers(request);
        UsersNetworkCallback callback=new UsersNetworkCallback(listener);
        call.enqueue(callback);
    }
}
