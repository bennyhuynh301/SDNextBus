package com.phuchaihuynh.sdnextbus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter{

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new FavoritesFragment();
            case 1:
                return new RoutesFragments();
            case 2:
                return new BusStopsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
