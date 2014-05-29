package com.phuchaihuynh.sdnextbus.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.database.BusStopsDatabaseHelper;
import com.phuchaihuynh.sdnextbus.utils.GTFSRequest;
import com.phuchaihuynh.sdnextbus.models.RouteModel;
import com.phuchaihuynh.sdnextbus.utils.RoutesParser;

import java.util.ArrayList;
import java.util.List;

public class RoutesFragment extends Fragment {

    private final static String TAG = RoutesFragment.class.getName();

    private Spinner transportTypeSpinner;
    private Spinner transportRouteSpinner;
    private Spinner transportDirectionSpinner;
    private Spinner transportStopSpinner;
    private Button searchButton;
    private ImageButton refreshButton;

    private String transportType;
    private String transportTable;
    private String transportRoute;
    private String transportDirection;
    private String transportStop;
    private String stopId;

    private RelativeLayout searchResultLt;
    private TextView stopIdTextView;
    private TextView routeTextView;
    private TextView stopNameTextView;
    private TextView directionTextView;
    private TextView timeTextView;
    private ProgressBar loadingIcon;
    private CheckBox favoriteCheckbox;

    private BusStopsDatabaseHelper dbHelper = new BusStopsDatabaseHelper(getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View rootView = inflater.inflate(R.layout.routes_fragment, container, false);

        transportTypeSpinner = (Spinner) rootView.findViewById(R.id.transport_type);
        transportRouteSpinner = (Spinner) rootView.findViewById(R.id.transport_route);
        transportDirectionSpinner = (Spinner) rootView.findViewById(R.id.transport_direction);
        transportStopSpinner = (Spinner) rootView.findViewById(R.id.transport_stop);

        transportTypeSpinner.setOnItemSelectedListener(transportTypeItemSelectedListener);
        transportRouteSpinner.setOnItemSelectedListener(transportRouteItemSelectedListener);
        transportDirectionSpinner.setOnItemSelectedListener(transportDirectionItemSelectedListener);
        transportStopSpinner.setOnItemSelectedListener(transportStopItemSelectedListener);

        searchResultLt = (RelativeLayout) rootView.findViewById(R.id.search_result);
        stopIdTextView = (TextView) rootView.findViewById(R.id.stop_id_text);
        stopNameTextView = (TextView) rootView.findViewById(R.id.stop_name_text);
        directionTextView = (TextView) rootView.findViewById(R.id.direction_text);
        routeTextView = (TextView) rootView.findViewById(R.id.transport_icon);
        timeTextView = (TextView) rootView.findViewById(R.id.transport_time_text);

        favoriteCheckbox = (CheckBox) rootView.findViewById(R.id.favorite_stop_checkbox);
        favoriteCheckbox.setOnClickListener(favoriteCheckBoxListener);

        loadingIcon = (ProgressBar) rootView.findViewById(R.id.loading_icon);

        searchButton = (Button) rootView.findViewById(R.id.search_route_button);
        searchButton.setOnClickListener(searchOnClickedListener);

        refreshButton = (ImageButton) rootView.findViewById(R.id.refresh_icon);
        refreshButton.setOnClickListener(refreshOnClickedListener);

        // Look up the AdView as a resource and load a request.
        AdView adView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStart(){
        super.onStart();
        getActivity().registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
        try {
            getActivity().unregisterReceiver(tickReceiver);
        }
        catch (IllegalArgumentException e) {} //skip
    }

    private final BroadcastReceiver tickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0) {
                update();
            }
        }
    };

    public void update() {
        (new AsyncCallWS()).execute(stopId);
    }

    private final CheckBox.OnClickListener favoriteCheckBoxListener = new CheckBox.OnClickListener(){
        @Override
        public void onClick(View v) {
            long id = dbHelper.getId(transportTable,stopId,transportRoute);

            if (((CheckBox) v).isChecked()) {
                if (transportType.equals("Bus")) {
                    String text = "Bus stop #" + stopId + " is added to favorite";
                    Toast.makeText(v.getContext(), text, Toast.LENGTH_LONG).show();
                    dbHelper.createFavorite(id, -1);
                }
                else {
                    String text = "Trolley stop #" + stopId + " is added to favorite";
                    Toast.makeText(v.getContext(), text, Toast.LENGTH_LONG).show();
                    dbHelper.createFavorite(-1, id);
                }
                Log.d(TAG, "Add the transport to favorite");
                onFavoriteSelectedListener.onFavoriteCheck();
            }
            else {
                if (transportType.equals("Bus")) {
                    String text = "Bus stop #" + stopId + " is removed from favorite";
                    Toast.makeText(v.getContext(), text, Toast.LENGTH_LONG).show();
                    dbHelper.deleteFavorite(dbHelper.getFavBusRowId(id));
                }
                else {
                    String text = "Trolley stop #" + stopId + " is removed from favorite";
                    Toast.makeText(v.getContext(), text, Toast.LENGTH_LONG).show();
                    dbHelper.deleteFavorite(dbHelper.getFavTrolleyRowId(id));
                }
                Log.d(TAG, "Remove the transport from favorite");
                onFavoriteSelectedListener.onFavoriteUncheck();
            }
        }
    };

    private final ImageButton.OnClickListener refreshOnClickedListener = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            update();
        }
    };

    private final Button.OnClickListener searchOnClickedListener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            try {
                stopId = dbHelper.getTransportStopId(transportTable,transportRoute,transportDirection,transportStop);
                stopIdTextView.setText("#"+stopId);
                routeTextView.setText(transportRoute.toUpperCase());
                if (transportRoute.equals("blue")) {
                    routeTextView.setBackgroundColor(Color.BLUE);
                }
                else if (transportRoute.equals("orange")) {
                    routeTextView.setBackgroundColor(Color.parseColor("#ff6600"));
                }
                else if (transportRoute.equals("green")) {
                    routeTextView.setBackgroundColor(Color.parseColor("#00cc00"));
                }
                else {
                    routeTextView.setBackgroundColor(Color.parseColor("#ffff254c"));
                }

                long id = dbHelper.getId(transportTable,stopId,transportRoute);
                if (transportType.equals("Bus")) {
                    if (dbHelper.isBusFavorite(id)) {
                        favoriteCheckbox.setChecked(true);
                    }
                    else {
                        favoriteCheckbox.setChecked(false);

                    }
                }
                else if (transportType.equals("Trolley")) {
                    if (dbHelper.isTrolleyFavorite(id)) {
                        favoriteCheckbox.setChecked(true);
                    }
                    else {
                        favoriteCheckbox.setChecked(false);
                    }
                }

                searchResultLt.setVisibility(View.VISIBLE);
                (new AsyncCallWS()).execute(stopId);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    };

    private final Spinner.OnItemSelectedListener transportTypeItemSelectedListener = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            transportType = parent.getItemAtPosition(position).toString();
            ArrayAdapter<String> dataAdapter;
            if (transportType.equals("Trolley")) {
                transportTable = "TrolleyRoutes";
                dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.trolley_routes));
            }
            else {
                transportTable = "BusRoutes";
                dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.bus_routes));
            }
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            transportRouteSpinner.setAdapter(dataAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final Spinner.OnItemSelectedListener transportRouteItemSelectedListener = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            transportRoute = parent.getItemAtPosition(position).toString().toLowerCase();
            try {
                List<String> directions = dbHelper.getTransportDirection(transportTable, transportRoute);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, directions);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                transportDirectionSpinner.setAdapter(dataAdapter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final Spinner.OnItemSelectedListener transportDirectionItemSelectedListener = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            transportDirection = parent.getItemAtPosition(position).toString();
            directionTextView.setText(transportDirection);
            try {
                List<String> stops = dbHelper.getTransportStops(transportTable, transportRoute, transportDirection);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stops);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                transportStopSpinner.setAdapter(dataAdapter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final Spinner.OnItemSelectedListener transportStopItemSelectedListener = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            transportStop = parent.getItemAtPosition(position).toString();
            stopNameTextView.setText(transportStop);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public interface OnFavoriteSelectedListener {
        public void onFavoriteCheck();
        public void onFavoriteUncheck();
    }

    private OnFavoriteSelectedListener onFavoriteSelectedListener;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "is onAttach");
        super.onAttach(activity);
        try {
            onFavoriteSelectedListener = (OnFavoriteSelectedListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException((activity.toString() + "must implement RouteFragment.OnFavoriteSelectedListener"));
        }
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {

        private String textResult;

        @Override
        protected Void doInBackground(String... stopIds) {
            GTFSRequest gtfsRequest = new GTFSRequest();
            gtfsRequest.requestRoutes(stopIds[0]);
            textResult = gtfsRequest.getProcessStopResults();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loadingIcon.setVisibility(View.INVISIBLE);
            RoutesParser parser = new RoutesParser(textResult);
            parser.parse();
            List<RouteModel> routes = parser.getRoutes();
            String timeText = "";
            if (routes.size() == 0) {
                timeText = "No schedule at this time";
                timeTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                timeTextView.setGravity(Gravity.CENTER);
                timeTextView.setText(timeText);
                timeTextView.setVisibility(View.VISIBLE);
                return;
            }

            int count = 0;
            ArrayList<String> times = new ArrayList<String>();
            for (RouteModel r : routes) {
                if (r.getRoute().toLowerCase().equals(transportRoute)) {
                    times.add(r.getArrivalTime());
                    count++;
                }
                if (count == 2) {
                    break;
                }
            }

            if (times.size() == 0) {
                timeText = "No schedule at this time";
                timeTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                timeTextView.setGravity(Gravity.CENTER);
                timeTextView.setText(timeText);
                timeTextView.setVisibility(View.VISIBLE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < times.size(); i++) {
                sb.append(times.get(i));
                if (i != times.size()-1) {
                    sb.append(",");
                }
            }
            timeText = sb.toString() + " mins";
            timeTextView.setText(timeText);
            timeTextView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            loadingIcon.setVisibility(View.VISIBLE);
            timeTextView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

    }

}
