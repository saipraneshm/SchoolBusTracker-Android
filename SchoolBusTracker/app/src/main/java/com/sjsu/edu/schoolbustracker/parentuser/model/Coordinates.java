package com.sjsu.edu.schoolbustracker.parentuser.model;

/**
 * Created by akshaymathur on 3/31/17.
 */

public class Coordinates {
    private double mLat;
    private double mLng;

    public Coordinates(){}

    public Coordinates(double lat , double lng){
        mLat = lat;
        mLng = lng;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLng() {
        return mLng;
    }

    public void setLng(double lng) {
        mLng = lng;
    }
}
