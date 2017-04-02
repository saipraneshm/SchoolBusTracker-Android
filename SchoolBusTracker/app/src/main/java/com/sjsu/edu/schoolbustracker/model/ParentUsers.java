package com.sjsu.edu.schoolbustracker.model;

/**
 * Created by sai pranesh on 31-Mar-17.
 */

public class ParentUsers {

    private String mUUId;
    private String mEmailId ;
    private String mParentName ;
    private String mPhone;
    private String mHouseAddress;
    private Student[] mChildren;

    public String getEmailId() {
        return mEmailId;
    }

    public void setEmailId(String emailId) {
        mEmailId = emailId;
    }

    public String getParentName() {
        return mParentName;
    }

    public void setParentName(String parentName) {
        mParentName = parentName;
    }

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

    public String getUUId() {
        return mUUId;
    }

    public void setUUId(String UUId) {
        mUUId = UUId;
    }
}
