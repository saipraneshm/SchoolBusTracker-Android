package com.sjsu.edu.schoolbustracker.parentuser.model;

/**
 * Created by sai pranesh on 31-Mar-17.
 */

public class BusTracking {

    private String mBusRegistrationNumber;
    private Trip mTripDetails;

    public String getBusRegistrationNumber() {
        return mBusRegistrationNumber;
    }

    public void setBusRegistrationNumber(String busRegistrationNumber) {
        mBusRegistrationNumber = busRegistrationNumber;
    }

    public Trip getTripDetails() {
        return mTripDetails;
    }

    public void setTripDetails(Trip tripDetails) {
        mTripDetails = tripDetails;
    }
}
