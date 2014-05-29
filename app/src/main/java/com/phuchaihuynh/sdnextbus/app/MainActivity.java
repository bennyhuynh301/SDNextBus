package com.phuchaihuynh.sdnextbus.app;

import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import com.astuetz.PagerSlidingTabStrip;
import com.phuchaihuynh.sdnextbus.adapter.TabsPagerAdapter;
import com.phuchaihuynh.sdnextbus.database.BusStopsDatabaseHelper;
import com.phuchaihuynh.sdnextbus.fragments.FavoriteTransportDialog;
import com.phuchaihuynh.sdnextbus.fragments.FavoritesFragment;
import com.phuchaihuynh.sdnextbus.fragments.RemoveFavoriteDialog;
import com.phuchaihuynh.sdnextbus.fragments.RoutesFragment;
import com.phuchaihuynh.sdnextbus.models.FavoriteTransportModel;

import java.io.IOException;

public class MainActivity extends FragmentActivity implements FavoritesFragment.OnFavoriteTransportSelectedListener,
                                  FavoritesFragment.OnFavoriteTransportLongSelectedListener,
                                  RoutesFragment.OnFavoriteSelectedListener,
                                  RemoveFavoriteDialog.OnRemoveFavoriteListener {

    private final static String TAG = MainActivity.class.getName();
    private static final String DATABASE_NAME = "SanDiegoBusStops.sqlite";

    private PagerSlidingTabStrip tabs;
    private ViewPager mViewPager;
    private TabsPagerAdapter mPageAdapter;

    private int color = 0xFF3F9FE0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPageAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mPageAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d(TAG, "The screen width (px):" + metrics.widthPixels);
        //Converting Screen resolution in pixels into dp
        float dp_w = ( metrics.widthPixels * 160 ) / metrics.densityDpi;
        Log.d(TAG, "The screen width (dp): " + dp_w);
        if (dp_w <= 320) {
            int textSize = 14;
            tabs.setTextSize(textSize);
        }

        tabs.setShouldExpand(true);
        tabs.setIndicatorColor(color);

        tabs.setViewPager(mViewPager);

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