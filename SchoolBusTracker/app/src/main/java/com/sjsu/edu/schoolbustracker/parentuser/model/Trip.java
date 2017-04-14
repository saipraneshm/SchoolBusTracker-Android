package com.sjsu.edu.schoolbustracker.parentuser.model;

/**
 * Created by sai pranesh on 31-Mar-17.
 */

public class Trip {

    private String mTripId;
    private Boolean mIsComplete;

    public String getTripId() {
        return mTripId;
    }

    public void setTripId(String tripId) {
        mTripId = tripId;
    }

    public Boolean getmIsComplete() {
        return mIsComplete;
    }

    public void setmIsComplete(Boolean mIsComplete) {
        this.mIsComplete = mIsComplete;
    }
}
