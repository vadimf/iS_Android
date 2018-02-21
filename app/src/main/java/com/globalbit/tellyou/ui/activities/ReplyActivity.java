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

import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityReplyBinding;
import com.globalbit.tellyou.model.Comment;
import com.globalbit.tellyou.model.Pagination;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.CommentsResponse;
import com.globalbit.tellyou.ui.adapters.RepliesAdapter;
import com.globalbit.tellyou.ui.interfaces.IReplyListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;


/**
 * Created by alex on 20/02/2018.
 */

public class ReplyActivity extends BaseActivity implements View.OnClickListener, IBaseNetworkResponseListener<CommentsResponse>, IReplyListener {
    private ActivityReplyBinding mBinding;
    private String mPostId;
    private RepliesAdapter mAdapter;
    private int mPage=1;
    private boolean mLoading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private Pagination mPagination;
    private int mCommentsCount=0;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_reply);
        mUser=SharedPrefsUtils.getUserDetails();
        mPostId=getIntent().getStringExtra(Constants.DATA_POST_ID);
        mBinding.imgViewAddReply.setOnClickListener(this);
        mBinding.toolbar.btnBack.setOnClickListener(this);
        mBinding.imgViewBackToStart.setOnClickListener(this);
        mAdapter=new RepliesAdapter(this, this);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.recyclerViewReplies.setLayoutManager(layoutManager);
        mBinding.recyclerViewReplies.setAdapter(mAdapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mBinding.recyclerViewReplies);
        mBinding.recyclerViewReplies.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                //TODO add new reply
                Intent intent=new Intent(ReplyActivity.this, VideoRecordingActivity.class);
                intent.putExtra(Constants.DATA_VIDEO_RECORDING_TYPE, Constants.TYPE_REPLY_VIDEO_RECORDING);
                intent.putExtra(Constants.DATA_POST_ID, mPostId);
                startActivityForResult(intent, Constants.REQUEST_VIDEO_RECORDING);
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.imgViewBackToStart:
                //TODO scroll to first item
                break;
        }
    }

    private void loadItems() {
        mBinding.swipeLayout.setEnabled(true);
        mBinding.swipeLayout.setRefreshing(true);
        NetworkManager.getInstance().getPostComments(this, mPostId, mPage);
    }

    @Override
    public void onSuccess(CommentsResponse response) {
        mBinding.swipeLayout.setRefreshing(false);
        mBinding.swipeLayout.setEnabled(false);
        mAdapter.addItems(response.getComments());
        mCommentsCount=response.getPagination().getResults();
        if(mCommentsCount==0) {
            mBinding.toolbar.txtViewTitle.setText(R.string.title_comments);
        }
        else {
            mBinding.toolbar.txtViewTitle.setText(getResources().getQuantityString(R.plurals.replies, mCommentsCount, mCommentsCount));
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
                }
                else {
                    Intent intent=new Intent(ReplyActivity.this, ReportActivity.class);
                    intent.putExtra(Constants.DATA_POST_ID, comment.getId());
                    startActivityForResult(intent, Constants.REQUEST_REPORT);
                }

            }
        });
    }

    @Override
    public void onProfile(Comment comment) {

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
}
