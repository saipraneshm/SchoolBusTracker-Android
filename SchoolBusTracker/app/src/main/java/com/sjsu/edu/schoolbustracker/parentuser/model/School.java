package com.sjsu.edu.schoolbustracker.parentuser.model;

import java.util.Map;

/**
 * Created by akshaymathur on 4/27/17.
 */

public class School {

    private Map<String,String> mRegisteredStudents;
    private String mSchoolName;
    private String mSchoolAddress;
    private String mSchoolId;
    private TransportCoordinator mTransportCoordinator;

    public TransportCoordinator getTransportCoordinator() {
        return mTransportCoordinator;
    }

    public void setTransportCoordinator(TransportCoordinator transportCoordinator) {
        mTransportCoordinator = transportCoordinator;
    }

    public Map<String, String> getRegisteredStudents() {
        return mRegisteredStudents;
    }

    public void setRegisteredStudents(Map<String, String> registeredStudents) {
        mRegisteredStudents = registeredStudents;
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

    public String getSchoolId() {
        return mSchoolId;
    }

    public void setSchoolId(String schoolId) {
        mSchoolId = schoolId;
    }
}
