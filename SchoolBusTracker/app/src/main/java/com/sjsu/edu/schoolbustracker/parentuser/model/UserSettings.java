package com.sjsu.edu.schoolbustracker.parentuser.model;

/**
 * Created by akshaymathur on 4/11/17.
 */

public class UserSettings {

    private Boolean mPushNotification;
    private Boolean mTextNotification;
    private Boolean mEmailNotification;

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
