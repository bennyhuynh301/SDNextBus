package com.phuchaihuynh.sdnextbus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.phuchaihuynh.sdnextbus.fragments.BusStopsFragment;
import com.phuchaihuynh.sdnextbus.fragments.FavoritesFragment;
import com.phuchaihuynh.sdnextbus.fragments.RoutesFragment;

public class TabsPagerAdapter extends FragmentStatePagerAdapter{

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
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
        return 3;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof  FavoritesFragment) {
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
