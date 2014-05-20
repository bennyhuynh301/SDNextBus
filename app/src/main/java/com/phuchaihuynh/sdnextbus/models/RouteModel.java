package com.phuchaihuynh.sdnextbus.models;

public class RouteModel {
    private String route;
    private String direction;
    private String arrivalTime;

    public RouteModel(String route, String direction, String arrivalTime) {
        this.route = route;
        this.direction = direction;
        this.arrivalTime = arrivalTime;
    }

    public String getRoute() {
        return this.route;
    }

    public String getDirection() {
        return this.direction;
    }

    public String getArrivalTime() {
        return this.arrivalTime;
    }

    public String toString() {
        return "[" + this.route + ": to " + this.direction + " in " + this.arrivalTime + " mins]";
    }
}
