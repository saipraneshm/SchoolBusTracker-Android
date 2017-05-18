package com.sjsu.edu.schoolbustracker.common.model;

/**
 * Created by sai pranesh on 31-Mar-17.
 */


public class ParentUsers extends Profile {


    private String mHouseAddress;
    private Student[] mChildren;


    public ParentUsers(){}

    public String getHouseAddress() {
        return mHouseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        mHouseAddress = houseAddress;
    }

    public Student[] getChildren() {
        return mChildren;
    }

    public void setChildren(Student[] children) {
        mChildren = children;
    }
}
