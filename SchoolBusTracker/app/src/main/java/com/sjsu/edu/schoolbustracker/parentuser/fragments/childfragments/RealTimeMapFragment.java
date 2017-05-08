package com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments;


import android.Manifest;
import android.animation.TypeEvaluator;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.helperclasses.LatLngInterpolator;
import com.sjsu.edu.schoolbustracker.helperclasses.MarkerAnimation;
import com.sjsu.edu.schoolbustracker.helperclasses.QueryPreferences;
import com.sjsu.edu.schoolbustracker.parentuser.model.Coordinates;

/**
 * A simple {@link Fragment} subclass.
 */
public class RealTimeMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = RealTimeMapFragment.class.getSimpleName();
    DatabaseReference mCurrentBusTrackingDetails;

    //Map related initializations
    private GoogleMap mGoogleMap;
    private LatLng mLatLng;
    private CameraPosition mCameraPosition;
    private PolylineOptions mPolylineOptions;
    private Marker mMarker;

    //Entry point to Google Play services, used by Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    //Setting a default location and zoom level until permission is not granted
    private static final int DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(37.406,-120.388);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private boolean isFirstTimeLoad = false;

    //last known location retrieved using Fused location API
    private Location mLastLocation;


    public RealTimeMapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "connecting to Google API client ");
      //  QueryPreferences.setFirstTimePref(getActivity(),true);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mCurrentBusTrackingDetails = FirebaseUtil.getBusTrackingDummyRef();
        mCurrentBusTrackingDetails.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Coordinates coordinates = dataSnapshot.getValue(Coordinates.class);
              //  isFirstTimeLoad = QueryPreferences.getFirstTimePref(getActivity());
                if (coordinates != null) {
                    Log.d(TAG, coordinates.getLat() + ": latitude added");
                    Log.d(TAG, coordinates.getLng() + ": longitude added");
                    mLatLng = new LatLng(coordinates.getLat(), coordinates.getLng());
                    if (mGoogleMap != null) {
                        Log.d(TAG, "adding marker");
                        //Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(mLatLng));
                        //LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
                        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
                        //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 10));
                        //MarkerAnimation.animateMarkerToICS(marker, mLatLng, latLngInterpolator);

                        updatePolyline();
                        //updateCamera();
                        updateMarker();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_real_time_map, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        QueryPreferences.setFirstTimePref(getActivity(),false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "connected to google api client");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.real_time_map);
        mapFragment.getMapAsync(this);
        /*if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            LatLng currentLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,10));
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mGoogleMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mPolylineOptions = new PolylineOptions();
            mPolylineOptions.color(Color.CYAN).width(10);
        } else {
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastLocation = null;
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastLocation != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    private void updatePolyline(){
      //mGoogleMap.clear();
        mGoogleMap.addPolyline(mPolylineOptions.add(mLatLng));
    }

    private void updateCamera(){
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 12));
    }

    private void updateMarker(){
        if(mMarker == null){
            mMarker = mGoogleMap.addMarker(new MarkerOptions().position(mLatLng));
         //   MarkerAnimation.animateMarkerToGB(mMarker, mLatLng, new LatLngInterpolator.LinearFixed());
        }

        Log.d(TAG, mMarker.getPosition() + " sending to animator");
        MarkerAnimation.animateMarkerToICS(mMarker, mLatLng, new LatLngInterpolator.LinearFixed());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        if(mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
     //   QueryPreferences.setFirstTimePref(getActivity(), false);
    }



}
