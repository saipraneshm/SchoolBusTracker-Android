package com.sjsu.edu.schoolbustracker.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.sjsu.edu.schoolbustracker.R;

public class BottomNavigationActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_real_time_track_parent:
                    mTextMessage.setText(R.string.title_track);
                    return true;
                case R.id.navigation_trip_history_parent:
                    mTextMessage.setText(R.string.title_history);
                    return true;
                case R.id.navigation_contact_driver_parent:
                    mTextMessage.setText(R.string.title_contact);
                    return true;
                case R.id.navigation_user_profile_parent:
                    mTextMessage.setText(R.string.title_user_profile);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
