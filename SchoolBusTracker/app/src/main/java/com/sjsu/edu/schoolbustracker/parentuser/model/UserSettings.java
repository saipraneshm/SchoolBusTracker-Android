package com.sjsu.edu.schoolbustracker.parentuser.model;

/**
 * Created by akshaymathur on 4/11/17.
 */

public class UserSettings {

    private Boolean mPushNotification;
    private Boolean mTextNotification;
    private Boolean mEmailNotification;
    private String mContactPreference;
    private Boolean mAccountEnabled;


    public void setAccountEnabled(Boolean accountEnabled) {
        mAccountEnabled = accountEnabled;
    }

    public Boolean isAccountEnabled(){
        return mAccountEnabled;
    }

    public String getContactPreference() {
        return mContactPreference;
    }

    public void setContactPreference(String contactPreference) {
        mContactPreference = contactPreference;
    }

    public Boolean getPushNotification() {
        return mPushNotification;
    }

    public void setPushNotification(Boolean pushNotification) {
        mPushNotification = pushNotification;
    }

    public Boolean getTextNotification() {
        return mTextNotification;
    }

    public void setTextNotification(Boolean textNotification) {
        mTextNotification = textNotification;
    }

    public Boolean getEmailNotification() {
        return mEmailNotification;
    }

    public void setEmailNotification(Boolean emailNotification) {
        mEmailNotification = emailNotification;
    }
}
