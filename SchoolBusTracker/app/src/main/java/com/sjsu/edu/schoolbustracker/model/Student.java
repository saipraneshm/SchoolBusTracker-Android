package com.sjsu.edu.schoolbustracker.model;

import java.util.UUID;

/**
 * Created by sai pranesh on 31-Mar-17.
 */

public class Student {

    private String mStudentName ;
    private String mStudentId;

    //For student image and for uniquely identifying
    private UUID mStudentUUID;
    private String mSchoolName;
    private String mSchoolAddress;
    private ParentUsers mParentUsers;

    public String getStudentName() {
        return mStudentName;
    }

    public void setStudentName(String studentName) {
        mStudentName = studentName;
    }

    public String getStudentId() {
        return mStudentId;
    }

    public void setStudentId(String studentId) {
        mStudentId = studentId;
    }

    public UUID getStudentUUID() {
        return mStudentUUID;
    }

    public void setStudentUUID(UUID studentUUID) {
        mStudentUUID = studentUUID;
    }

    public String getSchoolName() {
        return mSchoolName;
    }

    public void setSchoolName(String schoolName) {
        mSchoolName = schoolName;
    }

    public String getSchoolAddress() {
        return mSchoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        mSchoolAddress = schoolAddress;
    }

    public ParentUsers getParentUsers() {
        return mParentUsers;
    }

    public void setParentUsers(ParentUsers parentUsers) {
        mParentUsers = parentUsers;
    }
}
