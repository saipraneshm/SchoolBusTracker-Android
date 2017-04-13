package com.sjsu.edu.schoolbustracker.parentuser.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sjsu.edu.schoolbustracker.parentuser.activity.abs.SingleFragmentActivity;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.UserLoginFragment;

public class MainActivity extends SingleFragmentActivity implements
        UserLoginFragment.OnFragmentInteractionListener, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "MainActivity";

    @Override
    protected Fragment createFragment() {
        return new UserLoginFragment();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        /*Intent intent = new Intent(this,BottomNavigationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
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
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"Onstart has been called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"On resume has been called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"On pause has been called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"On stop has been called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"On destroy has been called");
    }
}
