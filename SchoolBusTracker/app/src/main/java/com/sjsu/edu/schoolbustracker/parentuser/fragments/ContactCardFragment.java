package com.sjsu.edu.schoolbustracker.parentuser.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.adapter.StudentFirebaseRecyclerAdapter;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments.StudentDetailFragment;
import com.sjsu.edu.schoolbustracker.parentuser.model.ParentUsers;
import com.sjsu.edu.schoolbustracker.parentuser.model.School;
import com.sjsu.edu.schoolbustracker.parentuser.model.Student;
import com.sjsu.edu.schoolbustracker.parentuser.model.TransportCoordinator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ContactCardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private AppCompatButton driver_call,driver_msg,school_call,school_msg;
    private AppCompatTextView driver_name,driver_phone,school_coordinator_name,school_coordinator_phone;
    private Toolbar mToolbar;
    private StudentFirebaseRecyclerAdapter mAdapter;
    private final String TAG = "ContactCardFragment";
    private DatabaseReference mStudentRef;
    private RecyclerView mChildImageLayout;
    private ConstraintLayout mConstraintLayout;

    public ContactCardFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_card, container, false);


        mToolbar = (Toolbar) view.findViewById(R.id.contacts_toolbar);
        mToolbar.setTitle("Contacts");
        mToolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(),R.color.black, null));

        driver_call = (AppCompatButton) view.findViewById(R.id.driver_call_button);
        driver_msg = (AppCompatButton) view.findViewById(R.id.driver_msg_button);
        school_call = (AppCompatButton) view.findViewById(R.id.school_call_button);
        school_msg = (AppCompatButton) view.findViewById(R.id.school_msg_button);

        driver_name = (AppCompatTextView) view.findViewById(R.id.driver_name_text);
        driver_phone = (AppCompatTextView) view.findViewById(R.id.driver_phnumber);
        school_coordinator_name = (AppCompatTextView) view.findViewById(R.id.school_coordinator_name);
        school_coordinator_phone = (AppCompatTextView) view.findViewById(R.id.school_coordinator_phnumber);

        mConstraintLayout = (ConstraintLayout) view.findViewById(R.id.contacts_constraint_layout); 

        mChildImageLayout = (RecyclerView) view.findViewById(R.id.student_list_view_contact);
        mStudentRef = FirebaseUtil.getStudentsRef();
        mChildImageLayout.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,false));
        mAdapter = new StudentFirebaseRecyclerAdapter(mStudentRef,getActivity(),true);
        mAdapter.setOnItemClickListener(new StudentFirebaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String studentId) {
                mConstraintLayout.setVisibility(View.VISIBLE);
                fetchContactDetails(studentId);

            }
        });

        mChildImageLayout.setAdapter(mAdapter);

        //SET phone and name values from firebase

        driver_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"+driver_phone.getText().toString()));
                startActivity(sendIntent);

            }
        });
        school_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"+school_coordinator_phone.getText().toString()));
                startActivity(sendIntent);

            }
        });

        school_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+school_coordinator_phone.getText().toString()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);

            }
        });
        driver_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+driver_phone.getText().toString()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);

            }
        });


        return view;
    }

    private void fetchContactDetails(String studentId) {
        DatabaseReference studentDetailRef = FirebaseUtil.getStudentsRef().child(studentId);
        studentDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Student student = dataSnapshot.getValue(Student.class);
                String schoolId = student.getSchoolId();
                fetchSchoolCoordinator(schoolId);
                String routeId = student.getRouteId();
                fetchRouteDetails(routeId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        
    }

    private void fetchRouteDetails(String routeId) {
    }

    private void fetchSchoolCoordinator(String schoolId) {
        DatabaseReference schoolDetailsRef = FirebaseUtil.getSchoolRef(schoolId);
        schoolDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                School school = dataSnapshot.getValue(School.class);
                school_coordinator_name.setText(school.getTransportCoordinator()
                        .getCoordinatorName());
                school_coordinator_phone.setText(school.getTransportCoordinator().getCoordinatorPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
