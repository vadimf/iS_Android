package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.androidutils.StringUtils;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityReplyBinding;
import com.globalbit.tellyou.model.Comment;
import com.globalbit.tellyou.model.Pagination;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.BaseResponse;
import com.globalbit.tellyou.network.responses.CommentsResponse;
import com.globalbit.tellyou.ui.adapters.RepliesAdapter;
import com.globalbit.tellyou.ui.events.NextVideoEvent;
import com.globalbit.tellyou.ui.events.RefreshEvent;
import com.globalbit.tellyou.ui.interfaces.IReplyListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by alex on 20/02/2018.
 */

public class ReplyActivity extends BaseActivity implements View.OnClickListener, IBaseNetworkResponseListener<CommentsResponse>, IReplyListener {
    private static final String TAG=ReplyActivity.class.getSimpleName();
    private ActivityReplyBinding mBinding;
    private String mPostId;
    private String mCommentId;
    private RepliesAdapter mAdapter;
    private int mPage=1;
    private boolean mLoading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int mCurrentActiveReply;
    private int mLastActiveReply;
    private Pagination mPagination;
    private int mCommentsCount=0;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_reply);
        mUser=SharedPrefsUtils.getUserDetails();
        mPostId=getIntent().getStringExtra(Constants.DATA_POST_ID);
        mCommentId=getIntent().getStringExtra(Constants.DATA_COMMENT_ID);
        mBinding.imgViewAddReply.setOnClickListener(this);
        mBinding.toolbar.btnBack.setOnClickListener(this);
        mBinding.imgViewBackToStart.setOnClickListener(this);
        mAdapter=new RepliesAdapter(this, this);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public int getPaddingLeft() {
                //int position=this.findFirstCompletelyVisibleItemPosition();
                if(mAdapter.getItemCount()==1) {
                    return mBinding.recyclerViewReplies.getMeasuredWidth()/2-(mAdapter.mImgWidth/2);
                }
                else {
                    return super.getPaddingLeft();
                }
                /*//if(mCurrentActiveReply==0) {
                    int t=mBinding.recyclerViewReplies.getMeasuredWidth()/2-(mAdapter.mImgWidth/2+10);
                    Log.i(TAG, "getPaddingLeft: "+mCurrentActiveReply+":"+t);
                    return mBinding.recyclerViewReplies.getMeasuredWidth()/2-(mAdapter.mImgWidth/2+10);
                //}*/
                /*else {
                    int t=(int)(mBinding.recyclerViewReplies.getMeasuredWidth()/2-(mAdapter.mImgWidth/2+ConversionUtils.convertDpToPixel(12, ReplyActivity.this)));
                    Log.i(TAG, "getPaddingLeft: "+mCurrentActiveReply+":"+t);
                    return (int)(mBinding.recyclerViewReplies.getMeasuredWidth()/2-(mAdapter.mImgWidth/2+ConversionUtils.convertDpToPixel(12, ReplyActivity.this)));
                }*/
            }

            /*@Override
            public int getPaddingRight() {
                //if(mLastActiveReply==mAdapter.getItemCount()-1) {
                    int t=mBinding.recyclerViewReplies.getMeasuredWidth()/2-(mAdapter.mImgWidth/2+10);
                    Log.i(TAG, "getPaddingRight: "+mCurrentActiveReply+":"+t);
                    return mBinding.recyclerViewReplies.getMeasuredWidth()/2-(mAdapter.mImgWidth/2+10);
                //}
               *//* else {
                    int t=(int)(mBinding.recyclerViewReplies.getMeasuredWidth()/2-(mAdapter.mImgWidth/2+ConversionUtils.convertDpToPixel(12, ReplyActivity.this)));
                    Log.i(TAG, "getPaddingRight: "+mCurrentActiveReply+":"+t);
                    return (int) (mBinding.recyclerViewReplies.getMeasuredWidth()/2-(mAdapter.mImgWidth/2+ConversionUtils.convertDpToPixel(12, ReplyActivity.this)));
                }*//*
            }*/



        };
        mBinding.recyclerViewReplies.setLayoutManager(layoutManager);
        mBinding.recyclerViewReplies.setAdapter(mAdapter);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        itemDecorator.setDrawable(getResources().getDrawable(R.drawable.horizontal_divider));
        mBinding.recyclerViewReplies.addItemDecoration(itemDecorator);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mBinding.recyclerViewReplies);
        mBinding.recyclerViewReplies.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrentActiveReply= layoutManager.findFirstCompletelyVisibleItemPosition();
                mLastActiveReply=layoutManager.findLastCompletelyVisibleItemPosition();
                Log.i(TAG, "onScrolled: "+mCurrentActiveReply);
                if(mCurrentActiveReply==0||mAdapter.getItemCount()==1) {
                    mBinding.imgViewBackToStart.setVisibility(View.GONE);
                }
                else {
                    mBinding.imgViewBackToStart.setVisibility(View.VISIBLE);
                }
                if(dx<0) {
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
        mBinding.swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mPage=1;
                loadItems();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.imgViewAddReply:
                Intent intent=new Intent(ReplyActivity.this, VideoRecordingActivity.class);
                intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_REPLY_VIDEO_RECORDING);
                intent.putExtra(Constants.DATA_POST_ID, mPostId);
                startActivityForResult(intent, Constants.REQUEST_VIDEO_RECORDING);
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.imgViewBackToStart:
                mBinding.recyclerViewReplies.smoothScrollToPosition(0);
                mBinding.imgViewBackToStart.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra(Constants.DATA_POST_ID, mPostId);
        if(mAdapter!=null) {
            intent.putExtra(Constants.DATA_POST_COMMENTS_COUNT, mAdapter.getItemCount());
        }
        setResult(RESULT_OK, intent);
        finish();

    }

    private void loadItems() {
        mBinding.swipeLayout.setEnabled(true);
        mBinding.swipeLayout.setRefreshing(true);
        NetworkManager.getInstance().getPostComments(this, mPostId, mPage);
    }

    @Override
    public void onSuccess(CommentsResponse response, Object object) {
        mBinding.swipeLayout.setRefreshing(false);
        mBinding.swipeLayout.setEnabled(false);
        mAdapter.addItems(response.getComments());
        if(!StringUtils.isEmpty(mCommentId)) {
            int index=mAdapter.getIndex(mCommentId);
            if(index!=-1) {
                mBinding.recyclerViewReplies.scrollToPosition(index);
            }
            mCommentId=null;
        }
        mCommentsCount=response.getPagination().getResults();
        if(mCommentsCount==0) {
            mBinding.toolbar.txtViewTitle.setText(R.string.title_comments);
            mBinding.swipeLayout.setVisibility(View.GONE);
            mBinding.txtViewEmpty.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.toolbar.txtViewTitle.setText(getResources().getQuantityString(R.plurals.replies, mCommentsCount, mCommentsCount));
            mBinding.swipeLayout.setVisibility(View.VISIBLE);
            mBinding.txtViewEmpty.setVisibility(View.GONE);
        }
        mPagination=response.getPagination();

    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        mBinding.swipeLayout.setRefreshing(false);
        mBinding.swipeLayout.setEnabled(false);
        showErrorMessage(errorCode,getString(R.string.error), errorMessage);
    }

    @Override
    public void onReport(final Comment comment) {
        if(comment.getUser().getUsername().equals(mUser.getUsername())) {
            new MaterialDialog.Builder(this)
                    .content(R.string.dialog_delete_reply)
                    .positiveText(R.string.btn_delete)
                    .negativeText(R.string.btn_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            final MaterialDialog loadingDialog=new MaterialDialog.Builder(ReplyActivity.this)
                                    .title(R.string.dialog_loading_title)
                                    .content(R.string.dialog_loading_content)
                                    .progress(true, 0)
                                    .show();
                            NetworkManager.getInstance().deletePost(new IBaseNetworkResponseListener<BaseResponse>() {
                                @Override
                                public void onSuccess(BaseResponse response, Object object) {
                                    loadingDialog.dismiss();
                                    if(mAdapter.removeItem(comment)) {
                                        if(mCommentsCount>0) {
                                            mCommentsCount--;
                                        }
                                        if(mAdapter.getItemCount()==0) {
                                            mBinding.toolbar.txtViewTitle.setText(R.string.title_comments);
                                            mBinding.swipeLayout.setVisibility(View.GONE);
                                            mBinding.txtViewEmpty.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            mBinding.toolbar.txtViewTitle.setText(getResources().getQuantityString(R.plurals.replies, mCommentsCount, mCommentsCount));
                                            mBinding.swipeLayout.setVisibility(View.VISIBLE);
                                            mBinding.txtViewEmpty.setVisibility(View.GONE);
                                        }
                                    }

                                }

                                @Override
                                public void onError(int errorCode, String errorMessage) {
                                    loadingDialog.dismiss();
                                    new MaterialDialog.Builder(ReplyActivity.this)
                                            .title(R.string.error)
                                            .content(errorMessage)
                                            .positiveText(R.string.btn_ok)
                                            .show();
                                }
                            }, comment.getId());
                        }
                    })
                    .show();
        }
        else {
            final MaterialDialog dialog=new MaterialDialog.Builder(this)
                    .customView(R.layout.dialog_reply_actions, false)
                    .show();
            View viewReportReply=dialog.findViewById(R.id.lnrLayoutReportReply);
            viewReportReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if(comment.getUser()!=null&&comment.getUser().getUsername().equals(mUser.getUsername())) {
                        showMessage(getString(R.string.error), getString(R.string.error_reporting_your_reply));
                    } else {
                        Intent intent=new Intent(ReplyActivity.this, ReportActivity.class);
                        intent.putExtra(Constants.DATA_POST_ID, comment.getId());
                        intent.putExtra(Constants.DATA_REPORT_TYPE, Constants.REQUEST_REPORT_REPLY);
                        startActivityForResult(intent, Constants.REQUEST_REPORT);
                    }

                }
            });
        }
    }

    @Override
    public void onProfile(Comment comment) {
        if(comment.getUser().getUsername().equals(SharedPrefsUtils.getUserDetails().getUsername())) {
            Intent intent=new Intent(this, MainActivity.class);
            intent.putExtra(Constants.DATA_HOME_TYPE, 2);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent=new Intent(this, ProfileActivity.class);
            intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_USER_PROFILE);
            intent.putExtra(Constants.DATA_USER, comment.getUser());
            startActivityForResult(intent, Constants.REQUEST_USER_PROFILE);
        }
    }

    @Override
    public void onPlayPause(Comment comment) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_REPORT) {
            if(resultCode==RESULT_OK) {
                Snackbar snackbar=Snackbar.make(mBinding.imgViewAddReply, getString(R.string.snack_bar_report_submitted), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
        else if(requestCode==Constants.REQUEST_VIDEO_RECORDING) {
            if(resultCode==RESULT_OK) {

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNextVideoEvent(NextVideoEvent event) {
        int position=event.position+1;
        if(position<mAdapter.getItemCount()) {
            mBinding.recyclerViewReplies.smoothScrollToPosition(position);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        mBinding.swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.clearItems();
                mPage=1;
                loadItems();
            }
        });
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
