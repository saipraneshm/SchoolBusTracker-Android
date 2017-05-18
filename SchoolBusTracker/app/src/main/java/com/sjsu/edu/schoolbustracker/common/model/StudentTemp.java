package com.sjsu.edu.schoolbustracker.common.model;

/**
 * Created by sai pranesh on 5/17/2017.
 */

public class StudentTemp {

    private String student_id;
    private String key;

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString(){
        return student_id;
    }
}
