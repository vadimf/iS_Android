package com.globalbit.tellyou.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalbit.androidutils.CollectionUtils;
import com.globalbit.androidutils.ConversionUtils;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentPostsBinding;
import com.globalbit.tellyou.model.Pagination;
import com.globalbit.tellyou.model.Post;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.requests.SearchRequest;
import com.globalbit.tellyou.network.responses.PostsResponse;
import com.globalbit.tellyou.ui.activities.DiscoverActivity;
import com.globalbit.tellyou.ui.activities.SearchActivity;
import com.globalbit.tellyou.ui.activities.VideoPlayerActivity;
import com.globalbit.tellyou.ui.activities.VideoRecordingActivity;
import com.globalbit.tellyou.ui.adapters.PostsAdapter;
import com.globalbit.tellyou.ui.events.CommentEvent;
import com.globalbit.tellyou.ui.events.FollowingEvent;
import com.globalbit.tellyou.ui.interfaces.IMainListener;
import com.globalbit.tellyou.ui.interfaces.IPostListener;
import com.globalbit.tellyou.ui.interfaces.IProfileListener;
import com.globalbit.tellyou.utils.Enums;
import com.globalbit.tellyou.utils.GridSpacingItemDecoration;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.globalbit.tellyou.utils.SimpleDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by alex on 23/11/2017.
 */

public class PostsFragment extends BaseFragment implements IBaseNetworkResponseListener<PostsResponse>, View.OnClickListener, IPostListener{
    private static final String TAG=PostsFragment.class.getSimpleName();
    private FragmentPostsBinding mBinding;
    private int mPage=1;
    //private int mPagePopular=1;
    private Pagination mPagination;
    private IMainListener mListener;

    private PostsAdapter mAdapter;
    private IProfileListener mProfileListener;

    private String mQuery=null;



    private boolean mLoading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int mFeedType=Constants.TYPE_FEED_HOME;
    private User mUser;
    private boolean mIsPopularVideos=false;


    private Post mCurrentPost=null;
    private int mCurrentPosition=-1;
    private int mLastPosition=-1;

    public static PostsFragment newInstance(int feedType, User user, IProfileListener listener) {
        PostsFragment fragment=new PostsFragment();
        fragment.mProfileListener=listener;
        Bundle args=new Bundle();
        args.putInt(Constants.DATA_FEED, feedType);
        args.putParcelable(Constants.DATA_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            mFeedType=getArguments().getInt(Constants.DATA_FEED);
            mUser=getArguments().getParcelable(Constants.DATA_USER);
            if(mUser==null) {
                mUser=SharedPrefsUtils.getUserDetails();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_posts, container, false);
        final GridLayoutManager layoutManager=new GridLayoutManager(getActivity(), 2);
        mBinding.recyclerViewPosts.setLayoutManager(layoutManager);
        mBinding.recyclerViewPosts.addItemDecoration(new GridSpacingItemDecoration(2, (int)ConversionUtils.convertDpToPixel(2, getActivity()), true));
        mAdapter=new PostsAdapter(getActivity(), mFeedType, this);
        mBinding.recyclerViewPosts.setAdapter(mAdapter);
        if(mFeedType==Constants.TYPE_FEED_HOME) {
            mBinding.lnrLayoutHeader.setVisibility(View.VISIBLE);
            mBinding.imgViewDiscover.setOnClickListener(this);
            mBinding.imgViewSearch.setOnClickListener(this);
        }
        else if(mFeedType==Constants.TYPE_FEED_USER) {
            mBinding.lnrLayoutHeader.setVisibility(View.GONE);
        }
        else if(mFeedType==Constants.TYPE_FEED_SEARCH) {
            mBinding.lnrLayoutHeader.setVisibility(View.GONE);
        }
        mBinding.recyclerViewPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        /*if(mFeedType==Constants.TYPE_FEED_HOME && mIsPopularVideos) {
                            mPagePopular++;
                        }
                        else {
                            mPage++;
                        }*/
                        mPage++;
                        mBinding.swipeLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.swipeLayout.setRefreshing(true);
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
                mIsPopularVideos=false;
                mPage=1;
                mLastPosition=-1;
                mAdapter.clear();
                loadItems();
            }
        });

        mBinding.btnDiscover.setOnClickListener(this);
        mBinding.swipeLayout.setRefreshing(true);
        loadItems();

        return mBinding.getRoot();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof IMainListener) {
            mListener = (IMainListener)context;
        }
        else {
            throw new ClassCastException(context.toString() + " must implement IMainListener.");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        if(mFeedType==Constants.TYPE_FEED_HOME) {
            if(CustomApplication.getPost()!=null) {
                //TODO show post on separate screen
                Intent intent=new Intent(getActivity(), VideoPlayerActivity.class);
                ArrayList<Post> tmpPosts=new ArrayList<>();
                tmpPosts.add(CustomApplication.getPost());
                intent.putExtra(Constants.DATA_POSTS, tmpPosts);
                intent.putExtra(Constants.DATA_INDEX, -1);
                intent.putExtra(Constants.DATA_PAGE, mPage);
                startActivityForResult(intent, Constants.REQUEST_VIDEO_PLAYER);
            }
        }
    }

    private void loadItems() {
        mBinding.swipeLayout.setEnabled(true);
        mBinding.swipeLayout.setRefreshing(true);
        switch(mFeedType) {
            case Constants.TYPE_FEED_HOME:
                if(mIsPopularVideos) {
                    NetworkManager.getInstance().getPopularPosts(this, mPage);
                }
                else {
                    NetworkManager.getInstance().getFeedPosts(this, mPage);
                }
                break;
            case Constants.TYPE_FEED_USER:
                User user=SharedPrefsUtils.getUserDetails();
                if(mUser.getUsername().equals(user.getUsername())) {
                    NetworkManager.getInstance().getMyPosts(this, mPage);
                }
                else {
                    NetworkManager.getInstance().getUserPosts(this, mUser.getUsername(), mPage);
                }
                break;
            case Constants.TYPE_FEED_BOOKMARKS:
                NetworkManager.getInstance().getBookmarkedPosts(this, mPage);
                break;
            case Constants.TYPE_FEED_SEARCH:
                NetworkManager.getInstance().getPopularPosts(this, mPage);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_DISCOVER) {
            onRefreshPosts();
        }
        else if(requestCode==Constants.REQUEST_VIDEO_RECORDING) {
            if(resultCode==Activity.RESULT_OK) {
                onRefreshPosts();
            }
        }
        else if(requestCode==Constants.REQUEST_COMMENTS) {
            if(data!=null) {
                int comments=data.getIntExtra(Constants.DATA_POST_COMMENTS_COUNT, -1);
                if(comments!=-1&&mCurrentPosition!=-1&&mCurrentPost!=null) {
                    mCurrentPost.setComments(comments);
                    mAdapter.notifyItemChanged(mCurrentPosition);
                }
            }
        }
        else if(requestCode==Constants.REQUEST_CAKE_IT&&resultCode==Activity.RESULT_OK) {
            onRefreshPosts();
        }
        else if(requestCode==Constants.REQUEST_POST) {
            onRefreshPosts();
        }
        else if(requestCode==Constants.REQUEST_USER_PROFILE) {
            onRefreshPosts();
        }
        else if(requestCode==Constants.REQUEST_VIDEO_PLAYER) {
            try {
                HashMap<String, Boolean> usersFollowingStatus=(HashMap<String, Boolean>) data.getSerializableExtra(Constants.DATA_USERS_FOLLOW_STATUS);
                if(usersFollowingStatus!=null&&usersFollowingStatus.size()>0) {
                    mAdapter.updateFollowState(usersFollowingStatus);
                    if(mProfileListener!=null) {
                        mProfileListener.onUsersFollowingStatus(usersFollowingStatus);
                    }
                    //EventBus.getDefault().post(new FollowingEvent(usersFollowingStatus));
                }
            }
            catch(Exception ex){}
            //TODO something when returning from video player
            onRefreshPosts();
        }
        else if(requestCode==Constants.REQUEST_SEARCH) {
            onRefreshPosts();
        }
    }

    @Override
    public void onSuccess(PostsResponse response, Object object) {
        if(mFeedType==Constants.TYPE_FEED_SEARCH&&(StringUtils.isEmpty(mQuery)||mQuery.equals(object))||mFeedType!=Constants.TYPE_FEED_SEARCH) {
            mBinding.swipeLayout.setRefreshing(false);
            if(mFeedType==Constants.TYPE_FEED_SEARCH) {
                mBinding.swipeLayout.setEnabled(false);
            }
            mLoading=true;
            if(!CollectionUtils.isEmpty(response.getPosts())) {
                mAdapter.addItems(response.getPosts());
            }
            mPagination=response.getPagination();
            if(!mIsPopularVideos||mFeedType==Constants.TYPE_FEED_SEARCH) {
                isEmpty();
            }
            if(mFeedType==Constants.TYPE_FEED_HOME&&mLastPosition!=-1&& mAdapter.getItemCount()>mLastPosition) {
                mBinding.recyclerViewPosts.scrollToPosition(mLastPosition);
                mLastPosition=-1;
            }
        }
    }

    public void searchPosts(String query) {
        mQuery=query;
        if(mAdapter!=null) {
            mAdapter.clear();
        }
        if(StringUtils.isEmpty(mQuery)) {
            mPage=1;
            loadItems();
        }
        else {
            mPage=1;
            NetworkManager.getInstance().searchPosts(this, mQuery, mPage);
        }

    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        mBinding.swipeLayout.setRefreshing(false);
        mLoading=true;
        showErrorMessage(errorCode, getString(R.string.error), errorMessage);
    }


    private void isEmpty() {
        if(mAdapter.getItemCount()==0) {
            mBinding.swipeLayout.setVisibility(View.GONE);
            mBinding.txtViewEmpty.setVisibility(View.VISIBLE);
            mBinding.btnDiscover.setVisibility(View.GONE);
            switch(mFeedType) {
                case Constants.TYPE_FEED_HOME:
                    mBinding.txtViewEmpty.setText(R.string.label_feed_posts_empty);
                    mBinding.txtViewPopularVideos.setVisibility(View.VISIBLE);
                    mPage=1;
                    mIsPopularVideos=true;
                    mBinding.swipeLayout.setVisibility(View.VISIBLE);
                    mBinding.swipeLayout.setRefreshing(true);
                    loadItems();
                    break;
                case Constants.TYPE_FEED_USER:
                    User user=SharedPrefsUtils.getUserDetails();
                    if(mUser.getUsername().equals(user.getUsername())) {
                        mBinding.btnDiscover.setVisibility(View.VISIBLE);
                        mBinding.txtViewEmpty.setText(R.string.label_my_posts_empty);
                        mBinding.btnDiscover.setText(R.string.btn_ask_first_question);
                    }
                    else {
                        mBinding.txtViewEmpty.setText(String.format(Locale.getDefault(),"%s %s", mUser.getUsername(),getString(R.string.label_user_posts_empty)));
                    }
                    break;
                case Constants.TYPE_FEED_SEARCH:
                    mBinding.txtViewEmpty.setText(R.string.label_video_search_empty);
                    mBinding.txtViewPopularVideos.setVisibility(View.GONE);
                    mPage=1;
                    break;
            }

        }
        else {
            if(mFeedType==Constants.TYPE_FEED_SEARCH) {
                mBinding.txtViewEmpty.setVisibility(View.GONE);
                if(StringUtils.isEmpty(mQuery)) {
                    mBinding.swipeLayout.setVisibility(View.VISIBLE);
                    mBinding.txtViewPopularVideos.setVisibility(View.VISIBLE);
                }
                else {
                    mBinding.txtViewPopularVideos.setVisibility(View.GONE);
                }
            }
            else {
                mBinding.swipeLayout.setVisibility(View.VISIBLE);
                mBinding.txtViewEmpty.setVisibility(View.GONE);
                mBinding.btnDiscover.setVisibility(View.GONE);
                mBinding.txtViewPopularVideos.setVisibility(View.GONE);
            }
        }
    }

    public void onRefreshPosts() {
        mBinding.swipeLayout.setRefreshing(true);
        mPage=1;
        //mPagePopular=1;
        mAdapter.clear();
        loadItems();
    }

    @Override
    public void onShowComments(Post post, int position) {

    }


    //Showing user profile
    @Override
    public void onUserProfile(String username) {
        mListener.onUserProfile(username);
    }

    @Override
    public void onVideoPayer(Post post, int position) {
        mLastPosition=position;
        Intent intent=new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra(Constants.DATA_POSTS, mAdapter.getItems());
        intent.putExtra(Constants.DATA_INDEX, position);
        intent.putExtra(Constants.DATA_PAGE, mPage);
        /*if(mFeedType==Constants.TYPE_FEED_USER) {
            intent.putExtra(Constants.DATA_USER, mUser);
        }*/
        startActivityForResult(intent, Constants.REQUEST_VIDEO_PLAYER);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch(view.getId()) {
            case R.id.imgViewDiscover:
                intent=new Intent(getActivity(), DiscoverActivity.class);
                startActivityForResult(intent,Constants.REQUEST_DISCOVER);
                break;
            case R.id.imgViewSearch:
                intent=new Intent(getActivity(), SearchActivity.class);
                startActivityForResult(intent,Constants.REQUEST_SEARCH);
                break;
            case R.id.btnDiscover:
                if(mFeedType==Constants.TYPE_FEED_HOME) {
                    intent=new Intent(getActivity(), DiscoverActivity.class);
                    startActivityForResult(intent, Constants.REQUEST_DISCOVER);
                }
                else if(mFeedType==Constants.TYPE_FEED_USER) {
                    intent=new Intent(getActivity(), VideoRecordingActivity.class);
                    intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_POST_VIDEO_RECORDING);
                    startActivityForResult(intent, Constants.REQUEST_VIDEO_RECORDING);
                }
                break;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowingEvent(FollowingEvent event) {
        HashMap<String, Boolean> usersFollowingState=new HashMap<>();
        usersFollowingState.put(event.user.getUsername(), event.user.isFollowing());
        mAdapter.updateFollowState(usersFollowingState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentEvent(CommentEvent event) {
        mAdapter.updateComments(event.id);
    }
}
