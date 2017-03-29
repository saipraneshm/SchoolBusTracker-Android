package com.sjsu.edu.schoolbustracker.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.fragments.FacebookLoginFragment;

import java.util.Arrays;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
