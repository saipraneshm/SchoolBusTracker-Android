package com.sjsu.edu.schoolbustracker.driver.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjsu.edu.schoolbustracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentAttendanceFragment extends Fragment {


    public StudentAttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_attendance, container, false);
    }

}
