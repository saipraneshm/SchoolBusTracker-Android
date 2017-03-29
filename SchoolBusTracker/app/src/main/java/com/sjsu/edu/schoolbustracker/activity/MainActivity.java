package com.sjsu.edu.schoolbustracker.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.fragments.FacebookLoginFragment;

public class MainActivity extends FragmentActivity implements FacebookLoginFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = new FacebookLoginFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
