package com.sjsu.edu.schoolbustracker.parentuser.fragments;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjsu.edu.schoolbustracker.R;

import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.MapTextFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.RealTimeMapFragment;
import com.sjsu.edu.schoolbustracker.helperclasses.CustomFragmentPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class RealTimeFragment extends Fragment {


    ViewPager mViewPager;
    TabLayout mTabLayout;
    Toolbar mToolbar;


    public RealTimeFragment() {
        // Required empty public constructor
    }


    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
