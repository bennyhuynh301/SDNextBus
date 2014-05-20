package com.phuchaihuynh.sdnextbus.models;

public class FavoriteTransportModel {
    private String stopName;
    private String stopId;
    private String route;
    private String direction;

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getStopName() {
        return this.stopName;
    }

    public String getStopId() {
        return this.stopId;
    }

    public String getRoute() {
        return this.route;
    }

    public String getDirection() {
        return this.direction;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append("route:").append(this.route).append(":")
                .append(this.stopName).append("(").append(this.stopId).append(")")
                .append(":").append(this.direction)
                .append("]");
        return sb.toString();
    }
}
