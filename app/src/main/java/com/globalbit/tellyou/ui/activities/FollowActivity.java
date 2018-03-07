package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityFollowBinding;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.UsersResponse;
import com.globalbit.tellyou.ui.adapters.UsersAdapter;
import com.globalbit.tellyou.ui.interfaces.IUserListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.globalbit.tellyou.utils.SimpleDividerItemDecoration;

import java.util.Locale;

/**
 * Created by alex on 09/11/2017.
 */

public class FollowActivity extends BaseActivity implements IBaseNetworkResponseListener<UsersResponse>, View.OnClickListener, IUserListener {
    private static final String TAG=FollowActivity.class.getSimpleName();
    private User mUser;
    private ActivityFollowBinding mBinding;
    private UsersAdapter mAdapter;
    private int mPage=1;
    private boolean mLoading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int mTypeFollow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_follow);
        mTypeFollow=getIntent().getIntExtra(Constants.DATA_FOLLOW, Constants.REQUEST_FOLLOWERS);
        mUser=getIntent().getParcelableExtra(Constants.DATA_USER);
        if(mTypeFollow==Constants.REQUEST_FOLLOWERS) {
            if(mUser==null) {
                mBinding.txtViewTitle.setText(R.string.label_followers);
            }
            else {
                mBinding.txtViewTitle.setText(String.format(Locale.getDefault(),"%s %s",mUser.getUsername(),getString(R.string.label_followers)));
            }
        }
        else if(mTypeFollow==Constants.REQUEST_FOLLOWING) {
            if(mUser==null){
                mBinding.btnAdd.setVisibility(View.VISIBLE);
                mBinding.txtViewTitle.setText(R.string.label_following);
            }
            else {
                mBinding.txtViewTitle.setText(String.format(Locale.getDefault(),"%s %s",mUser.getUsername(),getString(R.string.label_following)));
            }
        }
        mBinding.btnBack.setOnClickListener(this);
        mBinding.btnAdd.setOnClickListener(this);
        mBinding.btnDiscover.setOnClickListener(this);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mBinding.recyclerViewUsers.setLayoutManager(layoutManager);
        if(mTypeFollow==Constants.REQUEST_FOLLOWING&&mUser==null) {
            mAdapter=new UsersAdapter(this, this, true);
        }
        else {
            mAdapter=new UsersAdapter(this, this);
        }
        mBinding.recyclerViewUsers.setAdapter(mAdapter);
        mBinding.recyclerViewUsers.addItemDecoration(new SimpleDividerItemDecoration(this));
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
                    if ( (visibleItemCount+pastVisiblesItems) >= totalItemCount) {
                        mLoading = false;
                        mPage++;
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

        mBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage=1;
                loadItems();
            }
        });
        mBinding.swipeLayout.setRefreshing(true);
        loadItems();
    }

    private void loadItems() {
        if(mUser==null) {
            if(mTypeFollow==Constants.REQUEST_FOLLOWERS) {
                NetworkManager.getInstance().getMyFollowers(this, mPage,"");
            }
            else if(mTypeFollow==Constants.REQUEST_FOLLOWING) {
                NetworkManager.getInstance().getMyFollowing(this, mPage);
            }
        }
        else {
            if(mTypeFollow==Constants.REQUEST_FOLLOWERS) {
                NetworkManager.getInstance().getUserFollowers(this, mUser.getUsername(), mPage);
            }
            else if(mTypeFollow==Constants.REQUEST_FOLLOWING) {
                NetworkManager.getInstance().getUserFollowing(this, mUser.getUsername(), mPage);
            }
        }
    }

    @Override
    public void onSuccess(UsersResponse response) {
        mBinding.swipeLayout.setRefreshing(false);
        mLoading=true;
        if(response.getPagination().getPage()==1) {
            mAdapter.setItems(response.getUsers());
        }
        else {
            mAdapter.addItems(response.getUsers());
        }
        isEmpty();
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        mBinding.swipeLayout.setRefreshing(false);
        mLoading=true;
        showErrorMessage(errorCode, getString(R.string.error), errorMessage);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnAdd:
            case R.id.btnDiscover:
                Intent intent=new Intent(this, DiscoverActivity.class);
                startActivityForResult(intent, Constants.REQUEST_DISCOVER);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_DISCOVER&&mTypeFollow==Constants.REQUEST_FOLLOWING) {
            mPage=1;
            mBinding.swipeLayout.setRefreshing(true);
            loadItems();
        }
    }

    private void isEmpty() {
        if(mAdapter.getItemCount()==0) {
            mBinding.swipeLayout.setVisibility(View.GONE);
            mBinding.txtViewEmpty.setVisibility(View.VISIBLE);
            mBinding.btnDiscover.setVisibility(View.GONE);
            if(mTypeFollow==Constants.REQUEST_FOLLOWERS) {
                if(mUser==null) {
                    mBinding.txtViewEmpty.setText(R.string.label_followers_empty);
                }
                else {
                    mBinding.txtViewEmpty.setText(String.format(getString(R.string.label_user_followers_empty),mUser.getUsername()));
                }
            }
            else if(mTypeFollow==Constants.REQUEST_FOLLOWING) {
                if(mUser==null) {
                    mBinding.txtViewEmpty.setText(R.string.label_feed_posts_empty);
                    mBinding.btnDiscover.setVisibility(View.VISIBLE);
                    mBinding.btnDiscover.setText(R.string.btn_discover_people);
                }
                else {
                    mBinding.txtViewEmpty.setText(String.format(getString(R.string.label_user_following_empty),mUser.getUsername()));
                }
            }
        }
        else {
            mBinding.swipeLayout.setVisibility(View.VISIBLE);
            mBinding.txtViewEmpty.setVisibility(View.GONE);
            mBinding.btnDiscover.setVisibility(View.GONE);
        }
    }

    @Override
    public void onShowEmpty() {
        isEmpty();
    }
}
