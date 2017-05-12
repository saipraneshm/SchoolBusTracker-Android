package com.sjsu.edu.schoolbustracker.common.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sjsu.edu.schoolbustracker.parentuser.activity.abs.SingleFragmentActivity;
import com.sjsu.edu.schoolbustracker.common.fragments.UserLoginFragment;

public class MainActivity extends SingleFragmentActivity implements GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "MainActivity";


    @Override
    protected Fragment createFragment() {
        return new UserLoginFragment();
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
