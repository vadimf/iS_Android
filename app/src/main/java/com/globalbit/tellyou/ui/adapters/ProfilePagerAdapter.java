package com.globalbit.tellyou.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.globalbit.tellyou.Constants;
import com.globalbit.tellyou.model.User;
import com.globalbit.tellyou.ui.fragments.BaseFragment;
import com.globalbit.tellyou.ui.fragments.PostsFragment;

/**
 * Created by alex on 23/11/2017.
 */

public class ProfilePagerAdapter extends FragmentPagerAdapter {
    private int PAGE_COUNT;
    private Context mContext;
    private String mTabTitles[];
    private SparseArray<Fragment> mRegisteredFragments;
    private User mUser;

    public ProfilePagerAdapter(FragmentManager fm, Context context, String[] tabTitles, User user) {
        super(fm);
        mContext=context;
        mUser=user;
        mRegisteredFragments=new SparseArray<>();
        mTabTitles=tabTitles;
        PAGE_COUNT=tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        BaseFragment fragment=null;
        switch(position) {
            case 0:
                fragment=PostsFragment.newInstance(Constants.TYPE_FEED_USER, mUser);
                break;
            case 1:
                fragment=PostsFragment.newInstance(Constants.TYPE_FEED_BOOKMARKS, null);
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
        return mTabTitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
