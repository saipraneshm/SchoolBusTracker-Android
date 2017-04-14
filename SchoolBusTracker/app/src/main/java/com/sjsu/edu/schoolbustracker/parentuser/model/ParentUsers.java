package com.sjsu.edu.schoolbustracker.parentuser.model;

/**
 * Created by sai pranesh on 31-Mar-17.
 */

public class ParentUsers extends Profile {

    private String mPhone;
    private String mHouseAddress;
    private Student[] mChildren;

    public ParentUsers(){}

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

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
