package com.globalbit.tellyou.ui.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.FragmentSuggestionsBinding;
import com.globalbit.tellyou.model.Pagination;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.UsersResponse;
import com.globalbit.tellyou.ui.adapters.UsersAdapter;
import com.globalbit.tellyou.utils.SimpleDividerItemDecoration;

/**
 * Created by alex on 07/11/2017.
 */

public class SuggestionsFragment extends BaseFragment implements IBaseNetworkResponseListener<UsersResponse> {
    private static final String TAG=SuggestionsFragment.class.getSimpleName();
    private FragmentSuggestionsBinding mBinding;
    private UsersAdapter mAdapter;
    private int mPage=1;
    private Pagination mPagination;
    private boolean mLoading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean mIsFirstTime=true;

    public static SuggestionsFragment newInstance() {
        SuggestionsFragment fragment=new SuggestionsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_suggestions, container, false);
        final LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        mBinding.recyclerViewUsers.setLayoutManager(layoutManager);
        mAdapter=new UsersAdapter(getActivity(), null);
        mBinding.recyclerViewUsers.setAdapter(mAdapter);
        mBinding.recyclerViewUsers.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        //mBinding.imgViewClear.setOnClickListener(this);
        /*mBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!mIsFirstTime) {
                    if(charSequence.length()==0) {
                        mBinding.imgViewClear.setVisibility(View.GONE);
                        mBinding.swipeLayout.setEnabled(true);
                        mAdapter.clear();
                        mBinding.swipeLayout.setRefreshing(true);
                        mPage=1;
                        loadItems(charSequence.toString());
                    } else {
                        mBinding.imgViewClear.setVisibility(View.VISIBLE);
                        if(charSequence.length()>1) {
                            mBinding.swipeLayout.setEnabled(true);
                            mAdapter.clear();
                            mBinding.swipeLayout.setRefreshing(true);
                            mPage=1;
                            loadItems(charSequence.toString());
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
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

        return mBinding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsFirstTime=true;
    }

    private void loadItems() {
        NetworkManager.getInstance().getSuggestions(this, mPage);
        mBinding.swipeLayout.setEnabled(true);
    }

    @Override
    public void onSuccess(UsersResponse response) {
        mIsFirstTime=false;
        mBinding.swipeLayout.setRefreshing(false);
        mBinding.swipeLayout.setEnabled(false);
        mLoading=true;
        mAdapter.showStatus(true);
        mAdapter.addItems(response.getUsers());
        showEmpty();
        mPagination=response.getPagination();
    }

    private void showEmpty() {
        if(mAdapter.getItemCount()>0) {
            mBinding.txtViewEmpty.setVisibility(View.GONE);
            mBinding.swipeLayout.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.txtViewEmpty.setVisibility(View.VISIBLE);
            mBinding.swipeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        mBinding.swipeLayout.setRefreshing(false);
        mBinding.swipeLayout.setEnabled(false);
        mLoading=true;
        showErrorMessage(errorCode, getString(R.string.error), errorMessage);
    }
}
