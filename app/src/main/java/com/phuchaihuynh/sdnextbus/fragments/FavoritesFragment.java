package com.phuchaihuynh.sdnextbus.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.phuchaihuynh.sdnextbus.adapter.FavoritesListAdapter;
import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.database.BusStopsDatabaseHelper;
import com.phuchaihuynh.sdnextbus.models.FavoriteTransportModel;

import java.util.List;

public class FavoritesFragment extends Fragment {


    ListView busListView;
    ListView trolleyListView;
    List<FavoriteTransportModel> bus_list;
    List<FavoriteTransportModel> trolley_list;

    private BusStopsDatabaseHelper dbHelper = new BusStopsDatabaseHelper(this.getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View rootView = inflater.inflate(R.layout.favorites_fragment, container, false);

        busListView = (ListView) rootView.findViewById(R.id.fav_bus_list);
        trolleyListView = (ListView) rootView.findViewById(R.id.fav_trolley_list);

        bus_list = dbHelper.getAllBusesFavorites();
        trolley_list = dbHelper.getAllTrolleysFavorites();

        FavoritesListAdapter busListAdapter = new FavoritesListAdapter(this.getActivity(), bus_list);
        FavoritesListAdapter trolleyListAdapter = new FavoritesListAdapter(this.getActivity(), trolley_list);

        busListView.setAdapter(busListAdapter);
        busListAdapter.notifyDataSetChanged();
        busListView.setOnItemClickListener(busItemListener);

        trolleyListView.setAdapter(trolleyListAdapter);
        trolleyListAdapter.notifyDataSetChanged();
        trolleyListView.setOnItemClickListener(trolleyItemListener);

        return rootView;
    }

    private OnFavoriteTransportSelectedListener favTransportListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            favTransportListener = (OnFavoriteTransportSelectedListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement FavoriteFragment.OnFavoriteTransportSelectedListener");
        }
    }

    public interface OnFavoriteTransportSelectedListener {
        public void setOnFavoriteTransportClick(FavoriteTransportModel transportModel, String transportType);
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
}
