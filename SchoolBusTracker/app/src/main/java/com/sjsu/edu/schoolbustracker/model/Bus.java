package com.sjsu.edu.schoolbustracker.model;

import java.util.UUID;

/**
 * Created by sai pranesh on 31-Mar-17.
 */

public class Bus {

    private UUID mDriverUUID;

    //Unofficial number to identify the route
    private String mRouteNumber;

    //To store current location
    private String lat;
    private String lng;

    //Bus plate information
    private String mRegistrationNumber;

    public UUID getDriverUUID() {
        return mDriverUUID;
    }

    public void setDriverUUID(UUID driverUUID) {
        mDriverUUID = driverUUID;
    }

    public String getRouteNumber() {
        return mRouteNumber;
    }

    public void setRouteNumber(String routeNumber) {
        mRouteNumber = routeNumber;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getRegistrationNumber() {
        return mRegistrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        mRegistrationNumber = registrationNumber;
    }
}
