package com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments;


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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.helperclasses.LatLngInterpolator;
import com.sjsu.edu.schoolbustracker.helperclasses.MarkerAnimation;
import com.sjsu.edu.schoolbustracker.helperclasses.QueryPreferences;
import com.sjsu.edu.schoolbustracker.common.model.Coordinates;

/**
 * A simple {@link Fragment} subclass.
 */
public class RealTimeMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = RealTimeMapFragment.class.getSimpleName();
    DatabaseReference mCurrentBusTrackingDetailsRef;
    ChildEventListener mChildEventListener;

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
    private int previousKey = 0;

    //last known location retrieved using Fused location API
    private Location mLastLocation;


    public RealTimeMapFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "connecting to Google API client ");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        mCurrentBusTrackingDetailsRef = FirebaseUtil.getBusTrackingDummyRef();
        Query myRecentCoordinate = mCurrentBusTrackingDetailsRef.orderByKey().limitToLast(1);
        myRecentCoordinate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "single value event listener " + dataSnapshot.getValue());
                /*Coordinates coordinates = dataSnapshot.getValue(Coordinates.class);
                String value = dataSnapshot.getValue().toString();
                int equalIndex = value.indexOf("=");
                String keyValue = value.substring(1,equalIndex);
                Log.d(TAG,"The last element in the list is " + coordinates.getLat()
                        + " " + coordinates.getLng() + " " + dataSnapshot.getKey() + " " +
                        dataSnapshot.getValue() + " " + keyValue);
               previousKey = Integer.parseInt(keyValue);*/
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
        Log.d(TAG,"Map Ready");
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
        Log.d(TAG,"In On RequestPermission");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Log.d(TAG,"Permission Granted");
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
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               Log.d(TAG, dataSnapshot.getValue() + " " +  " child event listener " + dataSnapshot.getKey());

                Coordinates coordinates = dataSnapshot.getValue(Coordinates.class);
                //  isFirstTimeLoad = QueryPreferences.getFirstTimePref(getActivity());
                //&& previousKey > 0 && previousKey < Integer.parseInt(dataSnapshot.getKey())
                if (coordinates != null ) {

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
        };
        mCurrentBusTrackingDetailsRef.addChildEventListener(mChildEventListener);

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
        if(mPolylineOptions != null)
            mGoogleMap.addPolyline(mPolylineOptions.add(mLatLng));
    }

    private void updateCamera(){
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 12));
    }

    private void updateMarker(){
        if(mMarker == null){
                mMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(mLatLng)
                    .icon(BitmapDescriptorFactory.fromAsset("images/school-bus _128.png"))
                    .flat(false)
                    .anchor(0.5f,0.5f));
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
        if(mCurrentBusTrackingDetailsRef != null && mChildEventListener != null)
            mCurrentBusTrackingDetailsRef.removeEventListener(mChildEventListener);
    }



}
