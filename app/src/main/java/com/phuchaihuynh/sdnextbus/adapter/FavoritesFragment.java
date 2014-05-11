package com.phuchaihuynh.sdnextbus.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.utils.BusStopsDatabaseHelper;
import com.phuchaihuynh.sdnextbus.utils.FavoriteTransportModel;

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

        trolleyListView.setAdapter(trolleyListAdapter);
        trolleyListAdapter.notifyDataSetChanged();

        return rootView;
    }


}
