package com.sjsu.edu.schoolbustracker.parentuser.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.widget.AppCompatButton;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.activity.TripDetailActivity;
import com.sjsu.edu.schoolbustracker.parentuser.adapter.TripsFirebaseRecyclerAdapter;
import com.sjsu.edu.schoolbustracker.parentuser.model.Coordinates;
import com.sjsu.edu.schoolbustracker.parentuser.model.TripDetails;

import java.security.Timestamp;
import java.util.Date;

import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripsFragment extends Fragment{


    private GoogleMap mMap;
    private RecyclerView mRecyclerView;

    private Toolbar mToolbar;
    private TripsFirebaseRecyclerAdapter mAdapter;


    public TripsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       /* DatabaseReference previousTripRef = FirebaseUtil.getPreviousTripRef();

        TripDetails tripDetails = new TripDetails();
        tripDetails.setBusNo("20");
        tripDetails.setDestination("San Jose");
        tripDetails.setSource("Home");
        tripDetails.setRouteNo("45");
        tripDetails.setDate(new Date().toString());
        tripDetails.setDropTime(new Date().toString());
        tripDetails.setPickUptime(new Date().toString());
        tripDetails.setDriverName("Akshay Mathur");
        tripDetails.setTripId("23");
        tripDetails.setSourceCoordinates(new Coordinates(37.3382,-121.8863));
        tripDetails.setDestinationCoordinates(new Coordinates(37.3382,-121.8863));
        String timestamp = System.currentTimeMillis()+"";
        tripDetails.setTimestamp(timestamp);

        previousTripRef
                .child(FirebaseUtil.getCurrentUserId())
                .child(timestamp)
                .setValue(tripDetails);*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.trips_toolbar);
        mToolbar.setTitle("Trips");
        mToolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(),R.color.cardview_light_background, null));
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_trips);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DatabaseReference reference = FirebaseUtil.getExisitingPreviousTripsRef();
        mAdapter = new TripsFirebaseRecyclerAdapter(reference,getActivity());
        mAdapter.setOnItemClickListener(new TripsFirebaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tripTimeStamp) {
                startActivity(TripDetailActivity.newInstance(getActivity(),tripTimeStamp));
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }


}
