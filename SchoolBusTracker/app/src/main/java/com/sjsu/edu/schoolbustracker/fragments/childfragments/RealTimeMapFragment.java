package com.sjsu.edu.schoolbustracker.fragments.childfragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.sjsu.edu.schoolbustracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RealTimeMapFragment extends Fragment implements OnMapReadyCallback{


    public RealTimeMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_real_time_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                                                .findFragmentById(R.id.real_time_map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
