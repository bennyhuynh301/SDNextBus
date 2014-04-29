package com.phuchaihuynh.sdnextbus.adapter;

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

import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.realgtfs.BusStopsDatabaseHelper;
import com.phuchaihuynh.sdnextbus.realgtfs.GTFSRequest;
import com.phuchaihuynh.sdnextbus.realgtfs.RouteModel;
import com.phuchaihuynh.sdnextbus.realgtfs.RoutesParser;

import java.util.List;

public class RoutesFragments extends Fragment {

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
    private TextView timeTextView;
    private TextView updateTextView;
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
        routeTextView = (TextView) rootView.findViewById(R.id.transport_icon);
        timeTextView = (TextView) rootView.findViewById(R.id.transport_time_text);
        updateTextView = (TextView) rootView.findViewById(R.id.last_updated_text);

        favoriteCheckbox = (CheckBox) rootView.findViewById(R.id.favorite_stop_checkbox);
        favoriteCheckbox.setOnClickListener(favoriteCheckBoxListener);

        loadingIcon = (ProgressBar) rootView.findViewById(R.id.loading_icon);

        searchButton = (Button) rootView.findViewById(R.id.search_route_button);
        searchButton.setOnClickListener(searchOnClickedListener);

        refreshButton = (ImageButton) rootView.findViewById(R.id.refresh_icon);
        refreshButton.setOnClickListener(searchOnClickedListener);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop(){
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

    private void update() {
        (new AsyncCallWS()).execute(stopId);
    }

    private final CheckBox.OnClickListener favoriteCheckBoxListener = new CheckBox.OnClickListener(){
        @Override
        public void onClick(View v) {
            long id = dbHelper.getId(transportTable,stopId);
            if (((CheckBox) v).isChecked()) {
                if (transportType.equals("Bus")) {
                    String text = "Bus stop #" + stopId + "is added to favorite";
                    Toast.makeText(v.getContext(), text, Toast.LENGTH_LONG).show();
                    dbHelper.createFavorite(id, -1);
                }
                else {
                    String text = "Trolley stop #" + stopId + "is added to favorite";
                    Toast.makeText(v.getContext(), text, Toast.LENGTH_LONG).show();
                    dbHelper.createFavorite(-1, id);
                }
            }
            else {
                String text = "Bus stop #" + stopId + "is removed from favorite";
                Toast.makeText(v.getContext(),text,Toast.LENGTH_LONG).show();
            }
        }
    };

    private final Button.OnClickListener searchOnClickedListener = new Button.OnClickListener() {

        @Override
        public void onClick(View view) {
            try {
                stopId = dbHelper.getTransportStopId(transportTable,transportRoute,transportDirection,transportStop);
                stopIdTextView.setText("Stop No. " + stopId);
                routeTextView.setText(transportRoute.toUpperCase());
                if (transportRoute.equals("blue")) {
                    routeTextView.setBackgroundColor(Color.BLUE);
                }
                else if (transportRoute.equals("orange")) {
                    routeTextView.setBackgroundColor(Color.parseColor("#ffa500"));
                }
                else if (transportRoute.equals("green")) {
                    routeTextView.setBackgroundColor(Color.GREEN);
                }
                else {
                    routeTextView.setBackgroundColor(Color.parseColor("#ffff254c"));
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
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
            for (int i = 0; i < 2; i++ ) {
                if (routes.get(i).getRoute().toLowerCase().equals(transportRoute)) {
                    if (i == 0) {
                        timeText += routes.get(i).getArrivalTime();
                    }
                    else {
                        timeText += ", " + routes.get(i).getArrivalTime();
                    }
                }
            }
            timeText += " mins";
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
