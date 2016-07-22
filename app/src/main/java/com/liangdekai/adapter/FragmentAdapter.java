package com.liangdekai.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

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

    /*public void setFragments(List<Fragment> mFragments){
        if (this.mFragments != null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            for (Fragment fragment : this.mFragments){
                fragmentTransaction.remove(fragment);
            }
            fragmentTransaction.commit();
            fragmentTransaction = null ;
            fragmentManager.executePendingTransactions();
        }
        this.mFragments = mFragments ;
        notifyDataSetChanged();
    }*/
}
