package com.sjsu.edu.schoolbustracker.common.model;

/**
 * Created by sai pranesh on 02-Apr-17.
 */

public class CheckUserType {

    private String mUUId;
    private Boolean mIsDriver;

    public String getUUId() {
        return mUUId;
    }

    public void setUUId(String UUId) {
        mUUId = UUId;
    }

    public Boolean getIsDriver() {
        return mIsDriver;
    }

    public void setIsDriver(Boolean isDriver) {
        mIsDriver = isDriver;
    }
}
