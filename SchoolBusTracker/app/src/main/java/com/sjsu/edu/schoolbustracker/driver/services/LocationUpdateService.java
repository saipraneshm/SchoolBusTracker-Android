package com.sjsu.edu.schoolbustracker.driver.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.model.Coordinates;


public class LocationUpdateService extends Service  {

    private static final String TAG ="LocationUpdateService";
    private LocationManager mLocationManager;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0f;
    CustomLocationListener[] mCustomLocationListeners = new CustomLocationListener[]{
            new CustomLocationListener(LocationManager.GPS_PROVIDER),
            new CustomLocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class CustomLocationListener implements LocationListener {

        Location mLastLocation;

        public CustomLocationListener(String provider){
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            Coordinates coordinates = new Coordinates();
            coordinates.setLat(mLastLocation.getLatitude());
            coordinates.setLng(mLastLocation.getLongitude());
            FirebaseUtil.getBusTrackingRef().child("ADFF12").child("Trips").child("03-31-2017").child("1").child("Coordinates").child(System.currentTimeMillis() + "").setValue(coordinates);
            Log.d(TAG,"mCurrentLocation " + mLastLocation.getLongitude() + " " + mLastLocation.getLatitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.d(TAG, "Status Changed "  + s);

        }

        @Override
        public void onProviderEnabled(String s) {
            Log.d(TAG, "Provider enabled "  + s);
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.d(TAG, "Provider disabled "  + s);
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"start Command");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"on create");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mCustomLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mCustomLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }


    private void initializeLocationManager(){
        if(mLocationManager == null){
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mCustomLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mCustomLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
}
