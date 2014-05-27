package com.phuchaihuynh.sdnextbus.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.phuchaihuynh.sdnextbus.adapter.RoutesListAdapter;
import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.utils.GTFSRequest;
import com.phuchaihuynh.sdnextbus.models.RouteModel;
import com.phuchaihuynh.sdnextbus.utils.RoutesParser;

import java.util.ArrayList;

public class BusStopsFragment extends Fragment {

    private TextView stopNameView;
    private TextView stopIdView;
    private TextView busNoticeView;
    private ListView routesView;
    private RelativeLayout rlLayoutLoading;
    private SearchView searchView;
    private ImageButton refreshIcon;

    private InputMethodManager imm;

    private String busStopId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View rootView = inflater.inflate(R.layout.busstops_fragment, container, false);

        rlLayoutLoading = (RelativeLayout) rootView.findViewById(R.id.loading);
        stopNameView = (TextView) rootView.findViewById(R.id.bus_stop_name);
        stopIdView = (TextView) rootView.findViewById(R.id.bus_stop_id);
        busNoticeView = (TextView) rootView.findViewById(R.id.bus_notice);
        refreshIcon = (ImageButton) rootView.findViewById(R.id.refresh_icon);
        routesView = (ListView) rootView.findViewById(R.id.routes_list);

        imm = (InputMethodManager) getActivity().getSystemService(
                getActivity().INPUT_METHOD_SERVICE);

        searchView = (SearchView) rootView.findViewById(R.id.bus_stop_search);
        if (Build.VERSION.SDK_INT >= 14) {
            searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        searchView.setOnQueryTextListener(queryTextListener);

        refreshIcon.setOnClickListener(imageButtonClickListener);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(tickReceiver);
    }

    private final BroadcastReceiver tickReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0) {
                update();
            }
        }
    };

    final ImageButton.OnClickListener imageButtonClickListener = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            (new AsyncCallWS()).execute(busStopId);
        }
    };

    final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String stopId) {
            if (stopId.length() != 5 && !stopId.matches("\\d+")) {
                return false;
            }
            BusStopsFragment.this.busStopId = stopId;
            AsyncCallWS task = new AsyncCallWS();
            task.execute(stopId);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            searchView.setQuery("", false);
            searchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String busStopId) {
            if (busStopId.length() > 5) {
                searchView.setQuery(busStopId.substring(0,5),false);
            }
            return false;
        }
    };

    private void update() {
        if (busStopId != null) {
            (new AsyncCallWS()).execute(busStopId);
        }
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {

        private String textResult;
        private String stopId;

        @Override
        protected Void doInBackground(String... stopIds) {
            stopId = stopIds[0];
            GTFSRequest gtfsRequest = new GTFSRequest();
            gtfsRequest.requestRoutes(stopId);
            textResult = gtfsRequest.getProcessStopResults();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            rlLayoutLoading.setVisibility(View.GONE);
            RoutesParser parser = new RoutesParser(textResult);
            parser.parse();
            stopNameView.setText(parser.getBusStopShortName());
            stopIdView.setText("Stop ID. #" + stopId);
            if (!parser.getNotice().equals("")) {
                busNoticeView.setText(parser.getNotice());
                busNoticeView.setVisibility(View.VISIBLE);
            }
            else {
                ArrayList<RouteModel> routes = parser.getRoutes();
                routesView.setAdapter(new RoutesListAdapter(getActivity(), routes));
            }
            refreshIcon.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            rlLayoutLoading.setVisibility(View.VISIBLE);
            busNoticeView.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}

