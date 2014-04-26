package com.phuchaihuynh.sdnextbus.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.phuchaihuynh.sdnextbus.app.R;
import com.phuchaihuynh.sdnextbus.realgtfs.RouteModel;

import java.util.ArrayList;

public class RoutesListAdapter extends ArrayAdapter<RouteModel> {

    private final Activity context;
    private final ArrayList<RouteModel> values;

    static class ViewHolder {
        public TextView icon;
        public TextView direction;
        public TextView time;
    }

    public RoutesListAdapter(Activity context, ArrayList<RouteModel> values) {
        super(context, R.layout.route_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.route_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.icon = (TextView) convertView.findViewById(R.id.bus_icon);
            viewHolder.direction = (TextView) convertView.findViewById(R.id.bus_direction);
            viewHolder.time = (TextView) convertView.findViewById(R.id.bus_arrival_time);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        RouteModel model = values.get(position);
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
        holder.direction.setText("to " + model.getDirection());
        if (Integer.parseInt(model.getArrivalTime()) <= 1) {
            holder.time.setTextColor(Color.RED);
        }
        else {
            holder.time.setTextColor(Color.WHITE);
        }
        holder.time.setText(model.getArrivalTime() + " mins");
        return convertView;
    }
}
