package com.sjsu.edu.schoolbustracker.common.activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import com.sjsu.edu.schoolbustracker.R;

import com.sjsu.edu.schoolbustracker.driver.fragments.ContactParentFragment;
import com.sjsu.edu.schoolbustracker.driver.fragments.CurrentTripFragment;
import com.sjsu.edu.schoolbustracker.driver.fragments.StudentAttendanceFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.ContactCardFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.RealTimeFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.TripsFragment;
import com.sjsu.edu.schoolbustracker.common.fragments.UserProfileFragment;

import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments.PhotoPickerFragment;

public class BottomNavigationActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        PhotoPickerFragment.OnPhotoPickerPathListener{


    private static final String TAG = BottomNavigationActivity.class.getSimpleName() ;
    private boolean isDriver;

    //Tag to keep track of the current fragment
    private static String sTagCurrent = "CurrentTag";

    //Creating Tag constants to replace the fragments at runtime
    private static String sTagRealTimeTrack = "RealTimeTrack";
    private static String sTagTripHistory = "TripHistory";
    private static String sTagContactDriver = "ContactDriver";
    private static String sTagProfile = "Profile";
    private static String sTagCurrentTrip = "CurrentTrip";
    private static String sTagAttendance = "TripAttendance";
    private static String sTagContactParent = "ContactParent";

    //Maintaining current index of the fragment
    private int mNavItemIndex = 0;

    //Retrieving corresponding string array
    private String[] mActivityTitles;

    private Handler mHandler;

    private BottomNavigationView navigation;


    private FirebaseAuth mAuth;




    //Saving data in bundle
    private static final String CURRENT_TAG = "currentTag";
    private static final String CURRENT_NAV_INDEX = "currentNavItemIndex";
    private static final String IS_DRIVER = "isDriver";


    public static Intent newIntent(Context context, boolean isDriver){
        Intent intent = new Intent(context, BottomNavigationActivity.class);
        intent.putExtra(IS_DRIVER, isDriver);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                |Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        ActivityHelper.initialize(this);
        mActivityTitles = getResources().getStringArray(R.array.parent_fragment_titles);
        mHandler = new Handler();
        isDriver = getIntent().getBooleanExtra(IS_DRIVER,false);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        if(isDriver)
            navigation.inflateMenu(R.menu.driver_navigation);
        else
            navigation.inflateMenu(R.menu.parent_navigation);
       /* if(QueryPreferences.getTripDetailsNavRef(this)){
            QueryPreferences.setTripDetailsNavRef(this,false);
            mNavItemIndex = 1;
            sTagCurrent = sTagTripHistory;
            loadFragment();
            parent_navigation.getMenu().getItem(1).setChecked(true);
        }*/

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(!isDriver)
                    switch (item.getItemId()) {
                        case R.id.navigation_real_time_track_parent:
                            mNavItemIndex = 0;
                            sTagCurrent = sTagRealTimeTrack;
                            loadFragment();
                            return true;
                        case R.id.navigation_trip_history_parent:
                            mNavItemIndex = 1;
                            sTagCurrent = sTagTripHistory;
                            loadFragment();
                            return true;
                        case R.id.navigation_contact_driver_parent:
                            mNavItemIndex = 2;
                            sTagCurrent = sTagContactDriver;
                            loadFragment();
                            return true;
                        case R.id.navigation_user_profile_parent:
                            mNavItemIndex = 3;
                            sTagCurrent = sTagProfile;
                            loadFragment();
                            return true;
                        default :
                            mNavItemIndex = 0;
                            sTagCurrent = sTagRealTimeTrack;
                            loadFragment();
                            return false;
                    }
                else {
                    switch (item.getItemId()) {
                        case R.id.navigation_current_trip:
                            mNavItemIndex = 0;
                            sTagCurrent = sTagCurrentTrip;
                            loadFragment();
                            return true;
                        case R.id.navigation_child_attendance:
                            mNavItemIndex = 1;
                            sTagCurrent = sTagAttendance;
                            loadFragment();
                            return true;
                        case R.id.navigation_contact_parent:
                            mNavItemIndex = 2;
                            sTagCurrent = sTagContactParent;
                            loadFragment();
                            return true;
                        case R.id.navigation_user_profile_driver:
                            mNavItemIndex = 3;
                            sTagCurrent = sTagProfile;
                            loadFragment();
                            return true;
                    }
                }
                return false;
            }
        });

        if(savedInstanceState == null){
            mNavItemIndex = 0;
            sTagCurrent =  !isDriver ? sTagRealTimeTrack :sTagCurrentTrip;
            loadFragment();
        }

    }

    /*void loadView(boolean isDriver){
        if(!isDriver){
            navigation = (BottomNavigationView) findViewById(R.id.navigation);
        }else{
            navigation = (BottomNavigationView) findViewById(R.id.navigation);
        }
    }*/


    public void loadFragment(){
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();


        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getOnScreenFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.bottomnnavbar_container, fragment, sTagCurrent);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

    }


    private Fragment getOnScreenFragment(){
        if(!isDriver)
            switch(mNavItemIndex){
                case 0:
                    return new RealTimeFragment();
                case 1:
                    return new TripsFragment();
                case 2:
                    return new ContactCardFragment();
                case 3:
                    return UserProfileFragment.newInstance(false);
                default:
                    return new RealTimeFragment();
            }
        else {
            switch (mNavItemIndex) {
                case 0:
                    return new CurrentTripFragment();
                case 1:
                    return new StudentAttendanceFragment();
                case 2:
                    return new ContactParentFragment();
                case 3:
                    return UserProfileFragment.newInstance(true);
                default:
                    return new CurrentTripFragment();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void setCurrentPhotoPath(Uri photoPath) {

    }
}
