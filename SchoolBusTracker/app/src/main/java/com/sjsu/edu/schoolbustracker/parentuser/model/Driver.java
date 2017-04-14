package com.sjsu.edu.schoolbustracker.parentuser.model;

import java.util.UUID;

/**
 * Created by sai pranesh on 31-Mar-17.
 */


public class Driver extends Profile {


    private String mPhoneNumber;
    //For image purposes
    private UUID mDriverId;


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
