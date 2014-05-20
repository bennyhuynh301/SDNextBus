package com.phuchaihuynh.sdnextbus.adapter;


import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.models.FavoriteTransportModel;

import java.util.List;

public class FavoritesListAdapter extends ArrayAdapter<FavoriteTransportModel> {
    private final Activity context;
    private final List<FavoriteTransportModel> values;

    static class ViewHolder {
        TextView icon;
        TextView stopName;
        TextView stopId;
        TextView direction;
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
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        FavoriteTransportModel model = values.get(position);
        holder.icon.setText(model.getRoute().toUpperCase());
        if (model.getRoute().equals("blue")) {
            holder.icon.setBackgroundColor(Color.BLUE);
        }
        else if (model.getRoute().equals("green")) {
            holder.icon.setBackgroundColor(Color.GREEN);
        }
        else if (model.getRoute().equals("orange")) {
            holder.icon.setBackgroundColor(Color.parseColor("#ffa500"));
        }
        else {
            holder.icon.setBackgroundColor(Color.parseColor("#ffff254c"));
        }
        holder.stopName.setText(model.getStopName());
        holder.stopId.setText("Stop no. #" + model.getStopId());
        holder.direction.setText("Direction: " + model.getDirection());
        return convertView;
    }
}
