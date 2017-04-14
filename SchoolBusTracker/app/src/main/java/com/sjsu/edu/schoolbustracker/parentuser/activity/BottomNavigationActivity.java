package com.sjsu.edu.schoolbustracker.parentuser.activity;

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

import com.google.firebase.auth.FirebaseAuth;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.ContactCardFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.RealTimeFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.TripsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.AccountSettingsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.NotificationSettingsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.ProfileInfoFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.UserProfileFragment;

public class BottomNavigationActivity extends AppCompatActivity implements ContactCardFragment.OnFragmentInteractionListener
        ,UserProfileFragment.OnFragmentInteractionListener,
        ProfileInfoFragment.OnFragmentInteractionListener,
        AccountSettingsFragment.OnFragmentInteractionListener,
        NotificationSettingsFragment.OnFragmentInteractionListener{

    private TextView mTextMessage;

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

    private final String TAG ="BottomNavigationAct";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        mAuth = FirebaseAuth.getInstance();
        mActivityTitles = getResources().getStringArray(R.array.parent_fragment_titles);
        mHandler = new Handler();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
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

    public void loadFragment(){
        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(mActivityTitles[mNavItemIndex]);

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

        if(mNavItemIndex != 0){
                mNavItemIndex = 0;
                sTagCurrent = sTagRealTimeTrack;
                loadFragment();
                return;
        }
        super.onBackPressed();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
