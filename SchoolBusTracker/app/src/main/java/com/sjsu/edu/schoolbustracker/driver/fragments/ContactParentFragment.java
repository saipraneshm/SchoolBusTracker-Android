package com.sjsu.edu.schoolbustracker.driver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjsu.edu.schoolbustracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactParentFragment extends Fragment {


    private Toolbar mToolbar;
    public ContactParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_parent, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.contact_parent_toolbar);
        mToolbar.setTitle(R.string.title_parent_contact);
        mToolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(),R.color.black, null));
        return view;
    }

}
