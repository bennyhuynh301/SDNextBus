package com.phuchaihuynh.sdnextbus.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.utils.BusStopsDatabaseHelper;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private BusStopsDatabaseHelper dbHelper = new BusStopsDatabaseHelper(this.getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View rootView = inflater.inflate(R.layout.favorites_fragment, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.fav_list);
        List<Long> list = dbHelper.getAllFavorites();



        ArrayAdapter<Long> adapter = new ArrayAdapter<Long>(this.getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        return rootView;
    }


}
