package com.phuchaihuynh.sdnextbus.adapter;


import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.utils.FavoriteTransportModel;

import java.util.List;

public class FavoritesListAdapter extends ArrayAdapter<FavoriteTransportModel> {
    private final Activity context;
    private final List<FavoriteTransportModel> values;

    static class ViewHolder {
        TextView icon;
        TextView stopName;
        TextView stopId;
        TextView direction;
        TextView arrivalTime;
    }

    public FavoritesListAdapter(Activity context, List<FavoriteTransportModel> values) {
        super(context, R.layout.fav_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.fav_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (TextView) convertView.findViewById(R.id.fav_bus_icon);
            viewHolder.stopName = (TextView) convertView.findViewById(R.id.fav_stop_name);
            viewHolder.stopId = (TextView) convertView.findViewById(R.id.fav_stop_id);
            viewHolder.direction = (TextView) convertView.findViewById(R.id.fav_direction);
            viewHolder.arrivalTime = (TextView) convertView.findViewById(R.id.fav_bus_arrival_time);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        FavoriteTransportModel model = values.get(position);
        holder.icon.setText(model.getRoute());
        if (model.getRoute().equals("Blue")) {
            holder.icon.setBackgroundColor(Color.BLUE);
        }
        else if (model.getRoute().equals("Green")) {
            holder.icon.setBackgroundColor(Color.GREEN);
        }
        else if (model.getRoute().equals("Orange")) {
            holder.icon.setBackgroundColor(Color.parseColor("#ffa500"));
        }
        else {
            holder.icon.setBackgroundColor(Color.parseColor("#ffff254c"));
        }
        holder.stopName.setText(model.getStopName());
        holder.stopId.setText(model.getStopId());
        holder.direction.setText("To: " + model.getDirection());
        if (Integer.parseInt(model.getArrivalTime()) <= 2) {
            holder.arrivalTime.setTextColor(Color.RED);
        }
        else {
            holder.arrivalTime.setTextColor(Color.WHITE);
        }
        holder.arrivalTime.setText(model.getArrivalTime() + " mins");
        return convertView;
    }
}
