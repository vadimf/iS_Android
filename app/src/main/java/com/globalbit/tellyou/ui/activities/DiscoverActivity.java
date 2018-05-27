package com.globalbit.tellyou.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.databinding.ActivityDiscoverBinding;
import com.globalbit.tellyou.ui.adapters.DiscoverPagerAdapter;
import com.globalbit.tellyou.ui.fragments.ContactsFragment;
import com.globalbit.tellyou.ui.fragments.SuggestionsFragment;

/**
 * Created by alex on 07/11/2017.
 */

public class DiscoverActivity extends BaseActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener{
    private ActivityDiscoverBinding mBinding;
    private DiscoverPagerAdapter mDiscoverPagerAdapter;
    private boolean mIsFirstTIme=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_discover);
        mBinding.toolbar.txtViewTitle.setText(R.string.title_discover);
        mIsFirstTIme=getIntent().getBooleanExtra(Constants.DATA_FIRST_TIME, false);
        if(mIsFirstTIme) {
            mBinding.btnContinue.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.btnContinue.setVisibility(View.GONE);
        }
        mDiscoverPagerAdapter=new DiscoverPagerAdapter(getSupportFragmentManager(), this);
        mBinding.viewpager.setAdapter(mDiscoverPagerAdapter);
        mBinding.viewpager.setOffscreenPageLimit(1);
        mBinding.tabDiscover.setupWithViewPager(mBinding.viewpager);
        mBinding.tabDiscover.setOnTabSelectedListener(this);
        mBinding.btnContinue.setOnClickListener(this);
        mBinding.toolbar.btnBack.setOnClickListener(this);
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position=tab.getPosition();
        Fragment fragment=mDiscoverPagerAdapter.getRegisteredFragment(position);
        if(fragment instanceof ContactsFragment) {
            ((ContactsFragment) fragment).refresh();
        }
        else if(fragment instanceof SuggestionsFragment) {
            ((SuggestionsFragment) fragment).refresh();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnContinue:
                if(mIsFirstTIme) {
                    Intent intent=new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mIsFirstTIme) {
            Intent intent=new Intent(DiscoverActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
