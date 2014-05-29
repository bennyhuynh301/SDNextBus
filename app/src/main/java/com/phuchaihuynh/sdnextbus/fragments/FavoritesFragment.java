package com.phuchaihuynh.sdnextbus.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.phuchaihuynh.sdnextbus.adapter.FavoritesListAdapter;
import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.database.BusStopsDatabaseHelper;
import com.phuchaihuynh.sdnextbus.models.FavoriteTransportModel;

import java.util.List;

public class FavoritesFragment extends Fragment implements UpdateableFragment {

    private static final String TAG = FavoritesFragment.class.getName();

    ListView busListView;
    ListView trolleyListView;
    List<FavoriteTransportModel> bus_list;
    List<FavoriteTransportModel> trolley_list;
    FavoritesListAdapter busListAdapter;
    FavoritesListAdapter trolleyListAdapter;

    private BusStopsDatabaseHelper dbHelper = new BusStopsDatabaseHelper(this.getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        Log.d(TAG, "is onCreateView");

        View rootView = inflater.inflate(R.layout.favorites_fragment, container, false);

        busListView = (ListView) rootView.findViewById(R.id.fav_bus_list);
        trolleyListView = (ListView) rootView.findViewById(R.id.fav_trolley_list);
        updateFavoriteLists();

        busListAdapter = new FavoritesListAdapter(this.getActivity(), bus_list);
        trolleyListAdapter = new FavoritesListAdapter(this.getActivity(), trolley_list);

        busListView.setAdapter(busListAdapter);
        busListView.setOnItemClickListener(busItemListener);
        busListView.setOnItemLongClickListener(busItemLongListener);

        trolleyListView.setAdapter(trolleyListAdapter);
        trolleyListView.setOnItemClickListener(trolleyItemListener);
        trolleyListView.setOnItemLongClickListener(trolleyItemLongListener);

        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "is onResume");
        super.onResume();
        updateFavoriteLists();
        busListAdapter.updateDataSet(bus_list);
        trolleyListAdapter.updateDataSet(trolley_list);
    }

    private void updateFavoriteLists() {
        bus_list = dbHelper.getAllBusesFavorites();
        trolley_list = dbHelper.getAllTrolleysFavorites();
    }

    private OnFavoriteTransportSelectedListener favTransportListener;
    private OnFavoriteTransportLongSelectedListener favTransportLongListener;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "is onAttach");
        super.onAttach(activity);
        try {
            favTransportListener = (OnFavoriteTransportSelectedListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement FavoriteFragment.OnFavoriteTransportSelectedListener");
        }
        try {
            favTransportLongListener = (OnFavoriteTransportLongSelectedListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement FavoriteFragment.OnFavoriteTransportLongSelectedListener");
        }
    }

    @Override
    public void updateView() {
        updateFavoriteLists();
        busListAdapter.updateDataSet(bus_list);
        trolleyListAdapter.updateDataSet(trolley_list);
    }

    public interface OnFavoriteTransportSelectedListener {
        public void setOnFavoriteTransportClick(FavoriteTransportModel transportModel, String transportType);
    }

    public interface OnFavoriteTransportLongSelectedListener {
        public void setOnFavoriteTransportLongClick(FavoriteTransportModel transportModel, String transportType);
    }

    private final ListView.OnItemClickListener busItemListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            FavoriteTransportModel model = (FavoriteTransportModel) adapterView.getItemAtPosition(i);
            favTransportListener.setOnFavoriteTransportClick(model, "Bus");
        }
    };

    private final ListView.OnItemClickListener trolleyItemListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            FavoriteTransportModel model = (FavoriteTransportModel) adapterView.getItemAtPosition(i);
            favTransportListener.setOnFavoriteTransportClick(model, "Trolley");
        }
    };

    private final ListView.OnItemLongClickListener busItemLongListener = new ListView.OnItemLongClickListener() {


        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            FavoriteTransportModel model = (FavoriteTransportModel) adapterView.getItemAtPosition(i);
            favTransportLongListener.setOnFavoriteTransportLongClick(model, "Bus");
            return true;
        }
    };

    private final ListView.OnItemLongClickListener trolleyItemLongListener = new ListView.OnItemLongClickListener() {


        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            FavoriteTransportModel model = (FavoriteTransportModel) adapterView.getItemAtPosition(i);
            favTransportLongListener.setOnFavoriteTransportLongClick(model, "Trolley");
            return true;
        }
    };
}
