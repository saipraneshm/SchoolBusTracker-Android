package com.sjsu.edu.schoolbustracker.parentuser.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sai pranesh on 13-Apr-17.
 */

public class TripDetails {

    private String mSource;
    private String mDestination;
    private String mDropTime, mPickUptime;
    private String mStartTime;
    private String mBusNo;
    private String mTripId;
    private String mRouteNo;
    private String mDriverName;
    private String mBusRegistrationNo;
    private String mDate;
    private Coordinates mSourceCoordinates;
    private Coordinates mDestinationCoordinates;
    //Timestamp as key
    private String timestamp;

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        mDestination = destination;
    }

    public String getDropTime() {
        return mDropTime;
    }

    public void setDropTime(String dropTime) {
        mDropTime = dropTime;
    }

    public String getPickUptime() {
        return mPickUptime;
    }

    public void setPickUptime(String pickUptime) {
        mPickUptime = pickUptime;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getBusNo() {
        return mBusNo;
    }

    public void setBusNo(String busNo) {
        mBusNo = busNo;
    }

    public String getTripId() {
        return mTripId;
    }

    public void setTripId(String tripId) {
        mTripId = tripId;
    }

    public String getRouteNo() {
        return mRouteNo;
    }

    public void setRouteNo(String routeNo) {
        mRouteNo = routeNo;
    }

    public String getDriverName() {
        return mDriverName;
    }

    public void setDriverName(String driverName) {
        mDriverName = driverName;
    }

    public String getBusRegistrationNo() {
        return mBusRegistrationNo;
    }

    public void setBusRegistrationNo(String busRegistrationNo) {
        mBusRegistrationNo = busRegistrationNo;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public Coordinates getSourceCoordinates() {
        return mSourceCoordinates;
    }

    public void setSourceCoordinates(Coordinates sourceCoordinates) {
        mSourceCoordinates = sourceCoordinates;
    }

    public Coordinates getDestinationCoordinates() {
        return mDestinationCoordinates;
    }

    public void setDestinationCoordinates(Coordinates destinationCoordinates) {
        mDestinationCoordinates = destinationCoordinates;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
