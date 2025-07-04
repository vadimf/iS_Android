package com.globalbit.tellyou.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentContactsBinding;
import com.globalbit.tellyou.model.Pagination;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.ContactsRequest;
import com.globalbit.tellyou.network.requests.FacebookRequest;
import com.globalbit.tellyou.network.responses.FacebookFriendsResponse;
import com.globalbit.tellyou.network.responses.UsersResponse;
import com.globalbit.tellyou.ui.adapters.UsersAdapter;
import com.globalbit.tellyou.utils.ObservableHelper;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.globalbit.tellyou.utils.SimpleDividerItemDecoration;

import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by alex on 07/11/2017.
 */

public class ContactsFragment extends BaseFragment implements IBaseNetworkResponseListener<FacebookFriendsResponse>, View.OnClickListener {
    private static final String TAG=ContactsFragment.class.getSimpleName();
    private ContactsRequest mRequest;
    private int mState;
    private FragmentContactsBinding mBinding;
    private UsersAdapter mAdapter;
    private String mPageToken=null;
    private int mPage=1;
    private boolean mLoading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String mToken;
    private CallbackManager mCallbackManager;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private Pagination mPagination;

    public static ContactsFragment newInstance(int state) {
        ContactsFragment fragment=new ContactsFragment();
        Bundle args=new Bundle();
        args.putInt(Constants.DATA_FRIENDS_TYPE, state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            mState=getArguments().getInt(Constants.DATA_FRIENDS_TYPE);
        }
        else {
            mState=Constants.TYPE_FRIENDS_FACEBOOK;
        }
        if(mState==Constants.TYPE_FRIENDS_FACEBOOK) {
            mCallbackManager=CallbackManager.Factory.create();
            mToken=SharedPrefsUtils.getFacebookToken();
            if(!StringUtils.isEmpty(mToken)) {
                if(AccessToken.getCurrentAccessToken()!=null) {
                    mToken=AccessToken.getCurrentAccessToken().getToken();
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container,false);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        mBinding.recyclerViewUsers.setLayoutManager(layoutManager);
        mAdapter=new UsersAdapter(getActivity(), null);
        mBinding.recyclerViewUsers.setAdapter(mAdapter);
        mBinding.recyclerViewUsers.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mBinding.recyclerViewUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy<0) {
                    return;
                }
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                if (mLoading) {
                    if(mPagination!=null&&mState==Constants.TYPE_FRIENDS_CONTACTS&&mPagination.getPage()<=mPagination.getPages()) {
                        return;
                    }
                    if ( (visibleItemCount+pastVisiblesItems) >= totalItemCount) {
                        mLoading = false;
                        if(mState==Constants.TYPE_FRIENDS_CONTACTS) {
                            mPage++;
                        }
                        mBinding.swipeLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                loadItems();
                            }
                        });
                    }
                }
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        if(mState==Constants.TYPE_FRIENDS_FACEBOOK) {
            mBinding.btnFacebook.setOnClickListener(this);
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.i(TAG, "onSuccess: ");
                    mToken=loginResult.getAccessToken().getToken();
                    SharedPrefsUtils.setFacebookToken(mToken);
                    //mToken=loginResult.getAccessToken();
                    FacebookRequest request=new FacebookRequest();
                    request.setFacebookToken(mToken);
                    mBinding.swipeLayout.setVisibility(View.VISIBLE);
                    mBinding.btnFacebook.setVisibility(View.GONE);
                    mBinding.txtViewHeader.setText(R.string.label_header_facebook_connected);
                    mBinding.swipeLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mPageToken=null;
                            if(mAdapter!=null) {
                                mAdapter.clear();
                            }
                            loadItems();
                        }
                    });
                }

                @Override
                public void onCancel() {
                    Log.i(TAG, "onCancel: ");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.i(TAG, "onError: "+error.getMessage());
                }
            });
            mBinding.btnFacebook.setOnClickListener(this);
            if(StringUtils.isEmpty(mToken)) {
                mBinding.swipeLayout.setVisibility(View.GONE);
                mBinding.btnFacebook.setVisibility(View.VISIBLE);
                mBinding.txtViewHeader.setText(R.string.label_header_facebook_connect);
            } else {
                mBinding.swipeLayout.setVisibility(View.VISIBLE);
                mBinding.btnFacebook.setVisibility(View.GONE);
                mBinding.txtViewHeader.setText(R.string.label_header_facebook_connected);
                mBinding.swipeLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mPageToken=null;
                        if(mAdapter!=null) {
                            mAdapter.clear();
                        }
                        loadItems();
                    }
                });
            }

        }
        else if(mState==Constants.TYPE_FRIENDS_CONTACTS){
            mBinding.swipeLayout.setVisibility(View.VISIBLE);
            mBinding.txtViewHeader.setText(R.string.label_header_contacts_connected);
            //mBinding.btnShare.setVisibility(View.GONE);
            checkForPermissions(Constants.REQUEST_CONTACTS, new String[]{Manifest.permission.READ_CONTACTS});
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
    }

    private void loadItems() {
        if(mState==Constants.TYPE_FRIENDS_FACEBOOK) {
            if(!StringUtils.isEmpty(mToken)) {
                NetworkManager.getInstance().getFacebookFriends(this, mToken, mPageToken);
                mBinding.swipeLayout.setEnabled(true);
            } else {
                mBinding.swipeLayout.setRefreshing(false);
            }
        }
        else if(mState==Constants.TYPE_FRIENDS_CONTACTS) {
            NetworkManager.getInstance().getContacts(new IBaseNetworkResponseListener<UsersResponse>() {
                @Override
                public void onSuccess(UsersResponse response, Object object) {
                    mPagination=response.getPagination();
                    mBinding.swipeLayout.setRefreshing(false);
                    mBinding.swipeLayout.setEnabled(false);
                    mLoading=true;
                    mAdapter.addItems(response.getUsers());
                    if(mAdapter.getItemCount()>0) {
                        mBinding.txtViewHeader.setText(R.string.label_header_contacts_connected);
                        //mBinding.btnShare.setVisibility(View.GONE);
                    }
                    else {
                        mBinding.txtViewHeader.setText(R.string.label_header_contacts_connected_empty);
                        //mBinding.btnShare.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    mBinding.swipeLayout.setRefreshing(false);
                    mBinding.swipeLayout.setEnabled(false);
                    mLoading=true;
                    showErrorMessage(errorCode,getString(R.string.error), errorMessage);
                }
            }, mRequest, mPage);
        }
    }

    public void refresh() {
        init();
    }

    @Override
    public void onSuccess(FacebookFriendsResponse response, Object object) {
        mBinding.swipeLayout.setRefreshing(false);
        mBinding.swipeLayout.setEnabled(false);
        mLoading=true;
        if(mPageToken!=null&&mPageToken.equals(response.getNextPageToken())) {
            mAdapter.setItems(response.getUsers());
        }
        else {
            mAdapter.addItems(response.getUsers());
        }
        mPageToken=response.getNextPageToken();
        if(mAdapter.getItemCount()>0) {
            mBinding.txtViewHeader.setText(R.string.label_header_facebook_connected);
            //mBinding.btnShare.setVisibility(View.GONE);
        }
        else {
            mBinding.txtViewHeader.setText(R.string.label_header_facebook_connected_empty);
            //mBinding.btnShare.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        mBinding.swipeLayout.setRefreshing(false);
        mBinding.swipeLayout.setEnabled(false);
        mLoading=true;
        showErrorMessage(errorCode, getString(R.string.error), errorMessage);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnFacebook:
                LoginManager.getInstance().logInWithReadPermissions(this,  Arrays.asList("email", "user_friends"));
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void permissionAccepted() {
        super.permissionAccepted();

        mBinding.swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mBinding.swipeLayout.setRefreshing(true);
                mBinding.swipeLayout.setEnabled(true);
                mDisposable.add(ObservableHelper.contactsObservable(getActivity())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ContactsRequest>() {
                            @Override
                            public void onNext(ContactsRequest request) {
                                mRequest=request;
                                mPage=1;
                                if(mAdapter!=null) {
                                    mAdapter.clear();
                                }
                                loadItems();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        }));
            }
        });
    }

}
