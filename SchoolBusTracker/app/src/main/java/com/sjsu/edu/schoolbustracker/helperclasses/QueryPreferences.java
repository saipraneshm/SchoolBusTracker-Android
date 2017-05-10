package com.sjsu.edu.schoolbustracker.helperclasses;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by sai pranesh on 13-Apr-17.
 */

public class QueryPreferences {

    //to check whether the home button of trip details has been clicked or not
    private static final String PREF_PARENT_TRIP_DETAILS_NAV = "parent_trip_details_nav";

    private static final String PREF_SIGN_UP = "preference_sign_up";

    private static final String PREF_FIRST_TIME = "preference_first_time_load";

    public static boolean getTripDetailsNavRef(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_PARENT_TRIP_DETAILS_NAV,false);
    }

    public static void setTripDetailsNavRef(Context context, boolean value){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_PARENT_TRIP_DETAILS_NAV, value)
                .apply();
    }

    //This flag is used to stop loading the screen after sign up is successful
    public static boolean getSignUpPref(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_SIGN_UP,false);
    }

    public static void setSignUpPref(Context context , boolean value){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_SIGN_UP,value)
                .apply();
    }

    public static void setFirstTimePref(Context context, boolean value){
        PreferenceManager.getDefaultSharedPreferences(context)
                        .edit()
                        .putBoolean(PREF_FIRST_TIME, value)
                        .apply();
    }

    public static boolean getFirstTimePref(Context context){
       return PreferenceManager.getDefaultSharedPreferences(context)
                        .getBoolean(PREF_FIRST_TIME, false);
    }


}
