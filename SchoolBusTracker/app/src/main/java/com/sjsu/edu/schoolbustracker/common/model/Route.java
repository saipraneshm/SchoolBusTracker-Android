package com.sjsu.edu.schoolbustracker.common.model;

/**
 * Created by akshaymathur on 4/29/17.
 */

public class Route {
    private String mRouteNumber;

    public String getDriverId() {
        return mDriverId;
    }

    public void setDriverId(String driverId) {
        mDriverId = driverId;
    }

    private String mDriverId;

    public String getRouteNumber() {
        return mRouteNumber;
    }

    public void setRouteNumber(String routeNumber) {
        mRouteNumber = routeNumber;
    }
}
