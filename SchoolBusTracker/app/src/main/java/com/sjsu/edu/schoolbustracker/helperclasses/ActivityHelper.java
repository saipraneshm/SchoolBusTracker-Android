package com.sjsu.edu.schoolbustracker.helperclasses;

import android.app.Activity;
import android.content.pm.ActivityInfo;

/**
 * Created by sai pranesh on 02-Apr-17.
 */

//Other classes shall import this class and call initialize to set the orientation to portrait
public class ActivityHelper {
    public static void initialize(Activity activity){
        //setting the orientation to portrait programmatically
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
