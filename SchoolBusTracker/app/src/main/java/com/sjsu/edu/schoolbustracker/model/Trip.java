package com.sjsu.edu.schoolbustracker.model;

/**
 * Created by sai pranesh on 31-Mar-17.
 */

public class Trip {

    private String mDate;
    private String mLat;
    private String mLng;
    private String mTripId;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLng() {
        return mLng;
    }

    public void setLng(String lng) {
        mLng = lng;
    }

    public String getTripId() {
        return mTripId;
    }

    public void setTripId(String tripId) {
        mTripId = tripId;
    }
}
