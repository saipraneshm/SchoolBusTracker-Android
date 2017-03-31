package com.sjsu.edu.schoolbustracker.model;

import java.util.UUID;

/**
 * Created by sai pranesh on 31-Mar-17.
 */

public class Driver {

    private String mDriverName;
    private String mPhoneNumber;

    //For image purposes
    private UUID mDriverId;

    public String getDriverName() {
        return mDriverName;
    }

    public void setDriverName(String driverName) {
        mDriverName = driverName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public UUID getDriverId() {
        return mDriverId;
    }

    public void setDriverId(UUID driverId) {
        mDriverId = driverId;
    }
}
