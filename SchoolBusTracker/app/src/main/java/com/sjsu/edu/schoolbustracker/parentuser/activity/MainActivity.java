package com.sjsu.edu.schoolbustracker.parentuser.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sjsu.edu.schoolbustracker.parentuser.activity.abs.SingleFragmentActivity;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.UserLoginFragment;

import static android.view.Gravity.BOTTOM;

public class MainActivity extends SingleFragmentActivity implements
        UserLoginFragment.OnFragmentInteractionListener, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "MainActivity";


    @Override
    protected Fragment createFragment() {
        return new UserLoginFragment();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      //  mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
