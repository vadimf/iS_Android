package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivitySearchBinding;
import com.globalbit.tellyou.network.NetworkManager;
import com.globalbit.tellyou.network.interfaces.IBaseNetworkResponseListener;
import com.globalbit.tellyou.network.responses.UserResponse;
import com.globalbit.tellyou.ui.adapters.SearchPagerAdapter;
import com.globalbit.tellyou.ui.fragments.BaseFragment;
import com.globalbit.tellyou.ui.fragments.ContactsFragment;
import com.globalbit.tellyou.ui.fragments.PostsFragment;
import com.globalbit.tellyou.ui.interfaces.IMainListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;

public class SearchActivity extends BaseActivity implements View.OnClickListener, IMainListener, TabLayout.OnTabSelectedListener {
    private ActivitySearchBinding mBinding;
    private SearchPagerAdapter mSearchPagerAdapter;
    private int mPosition=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_search);
        mBinding.btnBack.setOnClickListener(this);
        mBinding.imgViewClear.setOnClickListener(this);
        mSearchPagerAdapter=new SearchPagerAdapter(getSupportFragmentManager(), this);
        mBinding.viewpager.setAdapter(mSearchPagerAdapter);
        mBinding.viewpager.setOffscreenPageLimit(1);
        mBinding.tabSearch.addOnTabSelectedListener(this);
        mBinding.tabSearch.setupWithViewPager(mBinding.viewpager);
        mBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                BaseFragment fragment=(BaseFragment) mSearchPagerAdapter.getRegisteredFragment(mPosition);
                if(fragment instanceof PostsFragment) {
                    ((PostsFragment) fragment).searchPosts(charSequence.toString());
                }
                else if(fragment instanceof ContactsFragment) {
                    //TODO search users
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.imgViewClear:
                mBinding.inputSearch.setText("");
                break;
        }
    }

    @Override
    public void onOpenDrawer() {

    }

    @Override
    public void onUserProfile(String username) {
        if(!SharedPrefsUtils.getUserDetails().getUsername().equals(username)) {
            final MaterialDialog loadingDialog=new MaterialDialog.Builder(this)
                    .title(R.string.dialog_loading_title)
                    .content(R.string.dialog_loading_content)
                    .progress(true, 0)
                    .show();
            NetworkManager.getInstance().getUserDetails(new IBaseNetworkResponseListener<UserResponse>() {
                @Override
                public void onSuccess(UserResponse response) {
                    if(loadingDialog!=null) {
                        loadingDialog.dismiss();
                    }
                    Intent intent=new Intent(SearchActivity.this, ProfileActivity.class);
                    intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_USER_PROFILE);
                    intent.putExtra(Constants.DATA_USER, response.getUser());
                    startActivity(intent);
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                    if(loadingDialog!=null) {
                        loadingDialog.dismiss();
                    }
                }
            }, username);
        }
        else {
            Intent intent=new Intent(this, MainActivity.class);
            intent.putExtra(Constants.DATA_HOME_TYPE, 2);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mPosition=tab.getPosition();
        if(mPosition==0) {
            mBinding.inputSearch.setHint(getString(R.string.hint_search_posts));
        }
        else {
            mBinding.inputSearch.setHint(R.string.hint_search_users);
        }
        mBinding.inputSearch.setText("");
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
