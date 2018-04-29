package com.globalbit.tellyou.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.CustomApplication;
import com.globalbit.tellyou.R;
import com.globalbit.tellyou.ui.fragments.BaseFragment;
import com.globalbit.tellyou.ui.fragments.ContactsFragment;
import com.globalbit.tellyou.ui.fragments.PostsFragment;
import com.globalbit.tellyou.ui.fragments.SuggestionsFragment;

public class SearchPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private Context mContext;
    private String tabTitles[] = new String[] {CustomApplication.getAppContext().getString(R.string.tab_video),  CustomApplication.getAppContext().getString(R.string.tab_people)};
    SparseArray<Fragment> mRegisteredFragments;

    public SearchPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext=context;
        mRegisteredFragments=new SparseArray<>();
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment=null;
        switch(position) {
            case 0:
                fragment=PostsFragment.newInstance(Constants.TYPE_FEED_SEARCH, null, null);
                break;
            case 1:
                fragment=SuggestionsFragment.newInstance(Constants.TYPE_USERS_SEARCH);
                break;
        }

        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
