package com.sjsu.edu.schoolbustracker.helperclasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import com.sjsu.edu.schoolbustracker.R;

/**
 * Created by sai pranesh on 02-Apr-17.
 */

//Other classes shall import this class and call initialize to set the orientation to portrait
public class ActivityHelper {
    public static void initialize(Activity activity){
        //setting the orientation to portrait programmatically
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void saveUID(Context context,String userUID){
        SharedPreferences pref = context.getSharedPreferences("AppPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putString(context.getString(R.string.prefUserUID), userUID);
        edt.apply();
    }

    public static String getUID(Context context){
        SharedPreferences pref = context.getSharedPreferences("AppPref",Context.MODE_PRIVATE);
        return pref.getString(context.getString(R.string.prefUserUID),"empty");

    }
}
