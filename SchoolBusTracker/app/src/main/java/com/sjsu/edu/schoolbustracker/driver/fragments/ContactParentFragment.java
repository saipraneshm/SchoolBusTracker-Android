package com.sjsu.edu.schoolbustracker.driver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.parentuser.adapter.StudentFirebaseRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactParentFragment extends Fragment {

    private RecyclerView mStudentRecyclerView;
    public ContactParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_parent, container, false);
        mStudentRecyclerView = (RecyclerView) v.findViewById(R.id.students_recycler_view);

        return v;
    }

}
