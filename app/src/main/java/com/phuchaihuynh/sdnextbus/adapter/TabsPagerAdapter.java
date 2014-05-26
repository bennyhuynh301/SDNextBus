package com.phuchaihuynh.sdnextbus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.phuchaihuynh.sdnextbus.fragments.BusStopsFragment;
import com.phuchaihuynh.sdnextbus.fragments.FavoritesFragment;
import com.phuchaihuynh.sdnextbus.fragments.RoutesFragment;

public class TabsPagerAdapter extends FragmentStatePagerAdapter{

    private final String[] TITLES = {"Favorites", "Routes", "Stops"};

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FavoritesFragment();
            case 1:
                return new RoutesFragment();
            case 2:
                return new BusStopsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof  FavoritesFragment) {
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
