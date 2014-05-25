package com.phuchaihuynh.sdnextbus.app;


import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.phuchaihuynh.sdnextbus.adapter.TabsPagerAdapter;
import com.phuchaihuynh.sdnextbus.database.BusStopsDatabaseHelper;
import com.phuchaihuynh.sdnextbus.fragments.FavoriteTransportDialog;
import com.phuchaihuynh.sdnextbus.fragments.FavoritesFragment;
import com.phuchaihuynh.sdnextbus.fragments.RemoveFavoriteDialog;
import com.phuchaihuynh.sdnextbus.fragments.RoutesFragment;
import com.phuchaihuynh.sdnextbus.models.FavoriteTransportModel;

import java.io.IOException;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, FavoritesFragment.OnFavoriteTransportSelectedListener,
                                        FavoritesFragment.OnFavoriteTransportLongSelectedListener,
                                        RoutesFragment.OnFavoriteSelectedListener,
                                        RemoveFavoriteDialog.OnRemoveFavoriteListener {

    private static final String TAG = MainActivity.class.getName();
    private static final String DATABASE_NAME = "SanDiegoBusStops.sqlite";

    private ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;

    private String[] tabs = {"Favorites", "Routes" ,"Bus Stops"};
    private static final int FAVORITES = 0;
    private static final int ROUTES = 1;
    private static final int BUSSTOPS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        actionBar = getSupportActionBar();

        mViewPager.setAdapter(mAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setHomeButtonEnabled(false);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name.toUpperCase()).setTabListener(this));
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Create a database for the app
        BusStopsDatabaseHelper mDbHelper = new BusStopsDatabaseHelper(this);
        try {
            Log.d(TAG, "Create application database");
            mDbHelper.createDataBase();
            Log.d(TAG, "Database path: " + this.getDatabasePath(DATABASE_NAME));
            //Test the database
            mDbHelper.openDataBase();
            mDbHelper.close();
        }
        catch (IOException e) {
            throw new Error("Unable to create database");
        }
        catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition(),false);

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void setOnFavoriteTransportClick(FavoriteTransportModel transportModel, String transportType) {
        FragmentManager fm = getSupportFragmentManager();
        FavoriteTransportDialog fav_dialog = FavoriteTransportDialog.newInstance(transportType, transportModel.getRoute(),
                transportModel.getDirection(), transportModel.getStopName(), transportModel.getStopId());
        fav_dialog.show(fm, "fav_dialog");
    }

    @Override
    public void setOnFavoriteTransportLongClick(FavoriteTransportModel transportModel, String transportType) {
        FragmentManager fm = getSupportFragmentManager();
        RemoveFavoriteDialog remove_fav_dialog = RemoveFavoriteDialog.newInstance(transportType, transportModel.getStopId(), transportModel.getRoute());
        remove_fav_dialog.show(fm, "remove_fav_dialog");
    }

    @Override
    public void onFavoriteCheck() {
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFavoriteUncheck() {
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onRemoveClick() {
        mViewPager.getAdapter().notifyDataSetChanged();
    }
}
