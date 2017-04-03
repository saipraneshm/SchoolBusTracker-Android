package com.sjsu.edu.schoolbustracker.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.fragments.ContactCardFragment;

public class BottomNavigationActivity extends FragmentActivity
        implements ContactCardFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d("BottomNavigation","Fragment item selected");
                FragmentManager fm = getSupportFragmentManager();


                switch (item.getItemId()) {
                    case R.id.navigation_real_time_track_parent:
                        //mTextMessage.setText(R.string.title_track);
                        return true;
                    case R.id.navigation_trip_history_parent:
                        //mTextMessage.setText(R.string.title_history);
                        return true;
                    case R.id.navigation_contact_driver_parent:
                        //mTextMessage.setText(R.string.title_contact);

                        Log.d("BottomNavigation","Contact Fragment selected");
                        Fragment frag = new ContactCardFragment();
                        fm.beginTransaction().add(R.id.bottom_layout_fragment_holder, frag).commit();
                        return true;
                    case R.id.navigation_user_profile_parent:
                        //mTextMessage.setText(R.string.title_user_profile);
                        return true;
                }
                
                return false;
            }

        };
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
