package com.sjsu.edu.schoolbustracker.common.model;

/**
 * Created by sai pranesh on 5/17/2017.
 */

public class StudentDetail {

    private String mParentId ;
    private String mStudentName;

    public String getParentId() {
        return mParentId;
    }

    public void setParentId(String parentId) {
        mParentId = parentId;
    }

    public String getStudentName() {
        return mStudentName;
    }

    public void setStudentName(String studentName) {
        mStudentName = studentName;
    }
}
