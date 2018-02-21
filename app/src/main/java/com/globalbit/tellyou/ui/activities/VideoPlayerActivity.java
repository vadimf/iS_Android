package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityVideoBinding;
import com.globalbit.tellyou.model.Pagination;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.PostsResponse;
import com.globalbit.tellyou.ui.adapters.VideosAdapter;
import com.globalbit.tellyou.ui.events.FollowingEvent;
import com.globalbit.tellyou.ui.events.NextVideoEvent;
import com.globalbit.tellyou.ui.interfaces.IVideoListener;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by alex on 21/02/2018.
 */

public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener, IBaseNetworkResponseListener<PostsResponse>, IVideoListener {
    private static final String TAG=VideoPlayerActivity.class.getSimpleName();
    private ActivityVideoBinding mBinding;
    private VideosAdapter mAdapter;
    private int mPage=1;
    private boolean mLoading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private Pagination mPagination;
    private User mUser;
    private int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_video);
        mUser=SharedPrefsUtils.getUserDetails();
        mIndex=getIntent().getIntExtra(Constants.DATA_INDEX,0);
        mPage=getIntent().getIntExtra(Constants.DATA_PAGE, 1);
        ArrayList<Post> items=getIntent().getParcelableArrayListExtra(Constants.DATA_POSTS);
        mAdapter=new VideosAdapter(this, this);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mBinding.recyclerViewVideos.setLayoutManager(layoutManager);
        mBinding.recyclerViewVideos.setAdapter(mAdapter);
        mBinding.recyclerViewVideos.setCacheManager(mAdapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mBinding.recyclerViewVideos);
        if(items!=null) {
            mAdapter.addItems(items);
            if(mIndex<items.size()) {
                mBinding.recyclerViewVideos.scrollToPosition(mIndex);
            }
        }
        mBinding.recyclerViewVideos.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if(mPagination!=null&&mPagination.getPage()>=mPagination.getPages()) {
                        return;
                    }
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
        mBinding.swipeLayout.setEnabled(false);
        if(mAdapter.getItemCount()==0) {
            mBinding.swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    mPage=1;
                    loadItems();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

        }
    }

    private void loadItems() {
        mBinding.swipeLayout.setEnabled(true);
        mBinding.swipeLayout.setRefreshing(true);
        NetworkManager.getInstance().getFeedPosts(this,mPage);
    }

    @Override
    public void onSuccess(PostsResponse response) {
        mBinding.swipeLayout.setRefreshing(false);
        mBinding.swipeLayout.setEnabled(false);
        mAdapter.addItems(response.getPosts());
        mPagination=response.getPagination();
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        mBinding.swipeLayout.setRefreshing(false);
        mBinding.swipeLayout.setEnabled(false);
        //TODO maybe to show toast or snackbar that next posts cannot be loaded
    }

    @Override
    public void onClose() {
        onBackPressed();
    }

    @Override
    public void onReport(String id) {
        Intent intent=new Intent(this, ReportActivity.class);
        intent.putExtra(Constants.DATA_POST_ID, id);
        startActivityForResult(intent, Constants.REQUEST_REPORT);
    }

    @Override
    public void onComments(Post post) {
        Intent intentReplies=new Intent(this, ReplyActivity.class);
        intentReplies.putExtra(Constants.DATA_POST_ID, post.getId());
        startActivityForResult(intentReplies, Constants.REQUEST_COMMENTS);
    }

    @Override
    public void onNextVideo(int position) {
        position++;
        if(position<mAdapter.getItemCount()) {
            mBinding.recyclerViewVideos.scrollToPosition(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_REPORT) {
            if(resultCode==RESULT_OK) {
                Snackbar snackbar=Snackbar.make(mBinding.recyclerViewVideos, getString(R.string.snack_bar_report_submitted), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
        else if(requestCode==Constants.REQUEST_COMMENTS) {
            //TODO maybe load this post in background and update comments count
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNextVideoEvent(NextVideoEvent event) {
        int position=event.position+1;
        if(position<mAdapter.getItemCount()) {
            mBinding.recyclerViewVideos.scrollToPosition(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
