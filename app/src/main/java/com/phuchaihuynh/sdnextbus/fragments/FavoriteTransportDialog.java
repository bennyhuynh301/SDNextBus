package com.phuchaihuynh.sdnextbus.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;

import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.models.RouteModel;
import com.phuchaihuynh.sdnextbus.utils.GTFSRequest;
import com.phuchaihuynh.sdnextbus.utils.RoutesParser;

import java.util.ArrayList;
import java.util.List;

public class FavoriteTransportDialog extends DialogFragment {

    private final static String TAG = FavoriteTransportDialog.class.getName();

    private TextView stopNameTextView;
    private TextView stopIdTextView;
    private TextView routeTextView;
    private TextView directionTextView;
    private TextView timeTextView;

    private ProgressBar loadingIcon;
    private ImageButton refreshButton;

    private Bundle args;

    public static FavoriteTransportDialog newInstance(String type, String route, String direction, String stopName, String stopId) {
        FavoriteTransportDialog f = new FavoriteTransportDialog();
        Bundle args = new Bundle();
        args.putString("transport_type", type);
        args.putString("transport_route", route);
        args.putString("transport_direction", direction);
        args.putString("transport_stop_name", stopName);
        args.putString("transport_stop_id", stopId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fav_dialog_fragment, container, false);

        stopNameTextView = (TextView) rootView.findViewById(R.id.stop_name_text_dialog);
        stopIdTextView = (TextView) rootView.findViewById(R.id.stop_id_text_dialog);
        routeTextView = (TextView) rootView.findViewById(R.id.transport_icon_dialog);
        directionTextView = (TextView) rootView.findViewById(R.id.transport_direction_text_dialog);
        timeTextView = (TextView) rootView.findViewById(R.id.transport_time_text_dialog);
        loadingIcon = (ProgressBar) rootView.findViewById(R.id.loading_icon_dialog);
        refreshButton = (ImageButton) rootView.findViewById(R.id.refresh_icon_dialog);

        args = getArguments();
        stopNameTextView.setText(args.getString("transport_stop_name"));
        stopNameTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        stopIdTextView.setText("#" + args.getString("transport_stop_id"));
        if (args.getString("transport_route").equals("blue")) {
            routeTextView.setBackgroundColor(Color.BLUE);
        }
        else if (args.getString("transport_route").equals("green")) {
            routeTextView.setBackgroundColor(Color.parseColor("#00cc00"));
        }
        else if (args.getString("transport_route").equals("orange")) {
            routeTextView.setBackgroundColor(Color.parseColor("#ff6600"));
        }
        else {
            routeTextView.setBackgroundColor(Color.parseColor("#ffff254c"));
        }
        routeTextView.setText(args.getString("transport_route").toUpperCase());
        directionTextView.setText(args.getString("transport_direction"));
        directionTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);

        refreshButton.setOnClickListener(onRefreshIconClickListener);

        update(args.getString("transport_stop_id"));

        // Look up the AdView as a resource and load a request.

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        Log.d(TAG, "Layout width: " + width + "px");
        if (width > 480) {
            AdView adView = (AdView) rootView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        return rootView;
    }

    private final ImageButton.OnClickListener onRefreshIconClickListener = new ImageButton.OnClickListener() {

        @Override
        public void onClick(View view) {
            update(args.getString("transport_stop_id"));
        }
    };

    private final BroadcastReceiver tickReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK)==0) {
                update(args.getString("transport_stop_id"));
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_MinWidth);
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature((Window.FEATURE_NO_TITLE));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    private void update(String stopId) {
        (new AsyncCallWS()).execute(stopId);
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
                if (r.getRoute().toLowerCase().equals(args.getString("transport_route"))) {
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
