package com.example.vineyard_2;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter_User extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();

    public void addFragments(Fragment fragments)
    {
        this.fragments.add(fragments);
    }

    public ViewPagerAdapter_User(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragments.get(position);
    }

    @Override
    public int getCount()
    {
        return fragments.size();
    }
}
