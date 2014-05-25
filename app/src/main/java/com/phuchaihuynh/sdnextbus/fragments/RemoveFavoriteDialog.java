package com.phuchaihuynh.sdnextbus.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;
import com.phuchaihuynh.sdnextbus.database.BusStopsDatabaseHelper;

public class RemoveFavoriteDialog extends DialogFragment {

    private static final String TAG = RemoveFavoriteDialog.class.getName();

    public static RemoveFavoriteDialog newInstance(String type, String stopId, String route) {
        RemoveFavoriteDialog f = new RemoveFavoriteDialog();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("stopId", stopId);
        args.putString("route", route);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] items = {"Remove the favorite"};
        Bundle args = getArguments();
        final String type = args.getString("type");
        final String route = args.getString("route");
        final String stopId = args.getString("stopId");
        Log.d(TAG, "Remove: <"+type+","+route+","+stopId+">");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BusStopsDatabaseHelper db = new BusStopsDatabaseHelper(getActivity());
                long fav_id = 0;
                if (type.equals("Bus")) {
                    long id = db.getId("BusRoutes",stopId,route);
                    Log.d(TAG, "BusRoutes row id: " + id);
                    fav_id = db.getFavBusRowId(id);
                }
                else {
                    long id = db.getId("TrolleyRoutes",stopId,route);
                    Log.d(TAG, "TrolleyRoutes row id: " + id);
                    fav_id = db.getFavTrolleyRowId(id);
                }
                Log.d(TAG, "Favorite row id: " + fav_id);
                db.deleteFavorite(fav_id);
                onRemoveFavoriteListener.onRemoveClick();
                String text = "Route " + route.toUpperCase() + " of the " +  type.toLowerCase() + " stop #" + stopId + " is removed from favorite";
                Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
            }
        });
        return builder.create();
    }

    public interface OnRemoveFavoriteListener {
        public void onRemoveClick();
    }

    private OnRemoveFavoriteListener onRemoveFavoriteListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onRemoveFavoriteListener = (OnRemoveFavoriteListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement RemoveFavoriteDialog.OnRemoveFavoriteListener");
        }
    }
}
