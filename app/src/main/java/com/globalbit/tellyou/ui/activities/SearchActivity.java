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
import com.globalbit.androidutils.StringUtils;
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
import com.globalbit.tellyou.ui.fragments.SuggestionsFragment;
import com.globalbit.tellyou.ui.interfaces.IMainListener;
import com.globalbit.tellyou.utils.SharedPrefsUtils;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class SearchActivity extends BaseActivity implements View.OnClickListener, IMainListener, TabLayout.OnTabSelectedListener {
    private ActivitySearchBinding mBinding;
    private SearchPagerAdapter mSearchPagerAdapter;
    private int mPosition=0;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

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
        RxTextView.textChanges(mBinding.inputSearch)
                .doOnNext(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        if(charSequence.length()>0) {
                            mBinding.imgViewClear.setVisibility(View.VISIBLE);
                        }
                        else {
                            mBinding.imgViewClear.setVisibility(View.GONE);
                        }
                    }
                })
                /*.filter(new Predicate<CharSequence>() {
                    @Override
                    public boolean test(CharSequence charSequence) throws Exception {
                        if(charSequence.length()>=2) {
                            return true;
                        }
                        else {
                            return false;
                        }
                    }
                })*/
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CharSequence>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        BaseFragment fragment=(BaseFragment) mSearchPagerAdapter.getRegisteredFragment(mPosition);
                        if(fragment instanceof PostsFragment) {
                            ((PostsFragment) fragment).searchPosts(charSequence.toString());
                        }
                        else if(fragment instanceof SuggestionsFragment) {
                            ((SuggestionsFragment) fragment).searchPosts(charSequence.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        /*mBinding.inputSearch.addTextChangedListener(new TextWatcher() {
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
        });*/
        String searchQuery=getIntent().getStringExtra(Constants.DATA_SEARCH);
        if(!StringUtils.isEmpty(searchQuery)) {
            mBinding.inputSearch.setText(searchQuery);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
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
                public void onSuccess(UserResponse response, Object object) {
                    if(loadingDialog!=null) {
                        loadingDialog.dismiss();
                    }
                    Intent intent=new Intent(SearchActivity.this, ProfileActivity.class);
                    intent.putExtra(Constants.DATA_PROFILE, Constants.REQUEST_USER_PROFILE);
                    intent.putExtra(Constants.DATA_USER, response.getUser());
                    startActivityForResult(intent, Constants.REQUEST_USER_PROFILE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_USER_PROFILE) {
            mBinding.inputSearch.setText(mBinding.inputSearch.getText().toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mBinding.inputSearch.getText().length()>0) {
            mBinding.inputSearch.setText(mBinding.inputSearch.getText().toString());
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
