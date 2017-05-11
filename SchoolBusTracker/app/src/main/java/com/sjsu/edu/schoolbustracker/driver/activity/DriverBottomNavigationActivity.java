package com.sjsu.edu.schoolbustracker.driver.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.sjsu.edu.schoolbustracker.R;

public class DriverBottomNavigationActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_current_trip:
                    mTextMessage.setText(R.string.title_trip_list);
                    return true;
                case R.id.navigation_child_attendance:
                    mTextMessage.setText(R.string.title_child_attendance);
                    return true;
                case R.id.navigation_contact_parent:
                    mTextMessage.setText(R.string.title_parent_contact);
                    return true;
                case R.id.navigation_user_profile_driver:
                    mTextMessage.setText(R.string.title_user_profile);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_bottom_navigation);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.driver_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
