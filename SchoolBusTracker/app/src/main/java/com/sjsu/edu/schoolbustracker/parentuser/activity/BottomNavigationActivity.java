package com.sjsu.edu.schoolbustracker.parentuser.activity;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import android.util.Log;
import android.view.MenuItem;

import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.helperclasses.QueryPreferences;

import com.sjsu.edu.schoolbustracker.parentuser.fragments.ContactCardFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.RealTimeFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.TripsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.AccountSettingsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.NotificationSettingsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.ProfileInfoFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.UserProfileFragment;

import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;

public class BottomNavigationActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{


    private TextView mTextMessage;

    private static final String TAG = "BottomNavigation" ;

    //Tag to keep track of the current fragment
    private static String sTagCurrent = "CurrentTag";

    //Creating Tag constants to replace the fragments at runtime
    private static String sTagRealTimeTrack = "RealTimeTrack";
    private static String sTagTripHistory = "TripHistory";
    private static String sTagContactDriver = "ContactDriver";
    private static String sTagProfile = "Profile";

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        ActivityHelper.initialize(this);
        mActivityTitles = getResources().getStringArray(R.array.parent_fragment_titles);
        mHandler = new Handler();

        
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
       /* if(QueryPreferences.getTripDetailsNavRef(this)){
            QueryPreferences.setTripDetailsNavRef(this,false);
            mNavItemIndex = 1;
            sTagCurrent = sTagTripHistory;
            loadFragment();
            navigation.getMenu().getItem(1).setChecked(true);
        }*/

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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


            }
        });

        if(savedInstanceState == null){
            mNavItemIndex = 0;
            sTagCurrent = sTagRealTimeTrack;
            loadFragment();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityHelper.saveUID(this, FirebaseUtil.getCurrentUserId());
    }

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
        switch(mNavItemIndex){
            case 0:
                return new RealTimeFragment();
            case 1:
                return new TripsFragment();
            case 2:
                return new ContactCardFragment();
            case 3:
                return new UserProfileFragment();
            default:
                return new RealTimeFragment();
        }
    }

    @Override
    public void onBackPressed() {


        super.onBackPressed();

    }
    

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
