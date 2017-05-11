package com.sjsu.edu.schoolbustracker.driver.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.driver.fragments.ContactParentFragment;
import com.sjsu.edu.schoolbustracker.driver.fragments.CurrentTripFragment;
import com.sjsu.edu.schoolbustracker.driver.fragments.StudentAttendanceFragment;
import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.ContactCardFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.RealTimeFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.TripsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.UserProfileFragment;

public class DriverBottomNavigationActivity extends AppCompatActivity {



    private static final String TAG = DriverBottomNavigationActivity.class.getSimpleName();

    //Tag to keep track of the current fragment
    private static String sTagCurrent = "CurrentTag";

    //Creating Tag constants to replace the fragments at runtime
    private static String sTagCurrentTrip = "CurrentTrip";
    private static String sTagAttendance = "TripAttendance";
    private static String sTagContactParent = "ContactParent";
    private static String sTagProfile = "Profile";

    //Maintaining current index of the fragment
    private int mNavItemIndex = 0;

    //Retrieving corresponding string array
    private String[] mActivityTitles;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bottom_navigation);
        ActivityHelper.initialize(this);
        mActivityTitles = getResources().getStringArray(R.array.driver_fragment_titles);
        mHandler = new Handler();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.driver_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                return false;
            }
        });

        if(savedInstanceState == null){
            mNavItemIndex = 0;
            sTagCurrent = sTagCurrentTrip;
            loadFragment();
        }

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
                fragmentTransaction.replace(R.id.driver_bottom_nav_bar_container, fragment, sTagCurrent);
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
                return new CurrentTripFragment();
            case 1:
                return new StudentAttendanceFragment();
            case 2:
                return new ContactParentFragment();
            case 3:
                return new UserProfileFragment();
            default:
                return new CurrentTripFragment();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}
