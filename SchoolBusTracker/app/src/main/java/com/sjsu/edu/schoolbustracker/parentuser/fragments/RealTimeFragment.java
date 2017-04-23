package com.sjsu.edu.schoolbustracker.parentuser.fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationRequest;
import com.sjsu.edu.schoolbustracker.Manifest;
import com.sjsu.edu.schoolbustracker.R;

import com.sjsu.edu.schoolbustracker.driver.services.CustomLocationService;
import com.sjsu.edu.schoolbustracker.driver.services.LocationUpdateService;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.MapTextFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.RealTimeMapFragment;
import com.sjsu.edu.schoolbustracker.helperclasses.CustomFragmentPagerAdapter;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;


/**
 * A simple {@link Fragment} subclass.
 */
public class RealTimeFragment extends Fragment {


    private static final String TAG = " RealTimeFragment" ;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private static final int REQUEST_LOCATION_UPDATES = 78;



    public RealTimeFragment() {
        // Required empty public constructor
    }


    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
           // Log.d(TAG, "requesting for location updates");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_UPDATES);

        }else{
            getActivity().startService(new Intent(getActivity(), LocationUpdateService.class));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_LOCATION_UPDATES: {
               // Log.d(TAG, "request came to requestPermissionResult");
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                  //  Log.d(TAG, "permission for accessing location granted by the user");
                    getActivity().startService(new Intent(getActivity(), LocationUpdateService.class));
                }
                return;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_real_time, container, false);

        mTabLayout = (TabLayout) view.findViewById(R.id.real_time_tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.real_time_view_pager);
        mToolbar = (Toolbar) view.findViewById(R.id.real_time_toolbar);
        mToolbar.setTitle("Real-Time Tracking");
        setUpViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    private void setUpViewPager(ViewPager viewPager){
        CustomFragmentPagerAdapter realTimePagerAdapter = new CustomFragmentPagerAdapter(getChildFragmentManager());
        realTimePagerAdapter.addFragment(new MapTextFragment(),"Map");
        realTimePagerAdapter.addFragment(new RealTimeMapFragment(),"Tracking");
        viewPager.setAdapter(realTimePagerAdapter);
    }




}
