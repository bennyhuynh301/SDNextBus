package com.phuchaihuynh.sdnextbus.utils;

import android.util.Log;
import com.phuchaihuynh.sdnextbus.models.RouteModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RoutesParser {

    private final String TAG = RoutesParser.class.getName();

    private String text;
    private String busStopShortName;
    private String notice;
    private ArrayList<RouteModel> models;

    public RoutesParser(String text) {
        this.text = text;
        this.notice = "";
        this.busStopShortName = "";
        this.models = new ArrayList<RouteModel>();
    }

    public void parse() {
        String pattern = "#?(.*)-(.*) to (.*)";
        Pattern r = Pattern.compile(pattern);
        String lines[] = this.text.split("\\n");
        if (lines[0].matches(".*invalid stop.*")) {
            this.busStopShortName = "This stop is invalid";
        }
        else if (lines[0].matches(".*No scheduled routed for this stop.*")) {
            this.notice = "No scheduled routed for this stop";
            return;
        }
        else {
            this.busStopShortName = lines[0].replace("&", " & ");
        }
        Calendar curr = Calendar.getInstance();
        String nextTime, route, direction, arriveTime;
        for (int i = 1; i < lines.length; i++) {
            Matcher m = r.matcher(lines[i]);
            if (m.find()) {
                nextTime = m.group(1);
                route = m.group(2);
                direction = m.group(3);
                arriveTime = getArrivalTime(curr, nextTime);
                this.models.add(new RouteModel(route, direction, arriveTime));
            }
            else {
                if (!lines[i].equals("#=realtime")) {
                    this.notice += lines[i];
                }
            }
        }
        Log.d(TAG, "Notice: " + this.notice);
        Log.d(TAG, "List of transport models: " + models.toString());
    }

    public ArrayList<RouteModel> getRoutes() {
        return this.models;
    }

    public String getBusStopShortName() {
        return this.busStopShortName;
    }

    public String getNotice() {
        return this.notice;
    }

    private String getArrivalTime(Calendar curr, String nextTime) {
        String pattern = "(\\d+):(\\d+)([AP])";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(nextTime);
        Calendar next = (Calendar) curr.clone();
        if (m.find()) {
            int hour = Integer.parseInt(m.group(1));
            hour = hour == 12 ? 0 : hour;
            Log.d(TAG, "Next hour :" + hour);
            int min = Integer.parseInt(m.group(2));
            Log.d(TAG, "Next min :" + min);
            int am_pm = m.group(3).equals("A") ? Calendar.AM : Calendar.PM;
            Log.d(TAG, "Next am/pm: " + am_pm);
            next.set(Calendar.HOUR, hour);
            next.set(Calendar.MINUTE, min);
            next.set(Calendar.AM_PM, am_pm);
        }
        if (curr.get(Calendar.AM_PM)==Calendar.PM && next.get(Calendar.AM_PM)==Calendar.AM) {
            next.roll(Calendar.DATE, true);
        }
        Log.d(TAG, "Current time: " + curr.getTime().toString());
        Log.d(TAG, "Next bus time: " + next.getTime().toString());
        int arrivalTime = (int) (next.getTimeInMillis() - curr.getTimeInMillis())/(60*1000);
        return Integer.toString(arrivalTime);
    }
}
