package com.sjsu.edu.schoolbustracker.helperclasses;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by sai pranesh on 13-Apr-17.
 */

public class QueryPreferences {

    //to check whether the home button of trip details has been clicked or not
    private static final String PREF_PARENT_TRIP_DETAILS_NAV = "parent_trip_details_nav";

    public static boolean getTripDetailsNavRef(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_PARENT_TRIP_DETAILS_NAV,false);
    }

    public static void setTripDetailsNavRef(Context context, boolean value){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_PARENT_TRIP_DETAILS_NAV, value)
                .commit();
    }
}
