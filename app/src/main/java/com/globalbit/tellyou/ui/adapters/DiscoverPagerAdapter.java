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
import com.globalbit.tellyou.ui.fragments.SuggestionsFragment;

/**
 * Created by alex on 07/11/2017.
 */

public class DiscoverPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private Context mContext;
    private String tabTitles[] = new String[] {CustomApplication.getAppContext().getString(R.string.tab_facebook), CustomApplication.getAppContext().getString(R.string.tab_contacts), CustomApplication.getAppContext().getString(R.string.tab_suggestions)};
    SparseArray<Fragment> mRegisteredFragments;

    public DiscoverPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext=context;
        mRegisteredFragments=new SparseArray<>();
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment=null;
        switch(position) {
            case 0:
                fragment=ContactsFragment.newInstance(Constants.TYPE_FRIENDS_FACEBOOK);
                break;
            case 1:
                fragment=ContactsFragment.newInstance(Constants.TYPE_FRIENDS_CONTACTS);
                break;
            case 2:
                fragment=SuggestionsFragment.newInstance(Constants.TYPE_USERS_SUGGESTIONS);
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
