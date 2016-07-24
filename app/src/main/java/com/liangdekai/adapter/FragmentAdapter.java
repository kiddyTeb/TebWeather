package com.liangdekai.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class FragmentAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private FragmentManager fragmentManager ;

    public FragmentAdapter(FragmentManager fm , List<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
        this.fragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
