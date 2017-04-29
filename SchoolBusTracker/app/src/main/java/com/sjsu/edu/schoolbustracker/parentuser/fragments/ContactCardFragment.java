package com.sjsu.edu.schoolbustracker.parentuser.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
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
    private AppCompatSpinner mSchoolSelectorSpinner;
    private ArrayList<String> schoolIds,schoolNames;
    private final String TAG = "ContactCardFragment";

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

        mSchoolSelectorSpinner = (AppCompatSpinner) view.findViewById(R.id.school_contact_selector);
        mSchoolSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                fetchDataForSelectedSchool(schoolIds.get(i));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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


        populateSpinnerFromFireBase();
        return view;
    }

    private void fetchDataForSelectedSchool(String s) {
        DatabaseReference transportCoordinatorRef = FirebaseUtil.getTransportCoordinator(s);
        transportCoordinatorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TransportCoordinator transportCoordinator = dataSnapshot.getValue(TransportCoordinator.class);
                school_coordinator_name.setText(transportCoordinator.getCoordinatorName());
                school_coordinator_phone.setText(transportCoordinator.getCoordinatorPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void populateSpinnerFromFireBase() {

        Log.d(TAG,"/*** Fetching Applicable Schools ***/");
        DatabaseReference studentsRef = FirebaseUtil.getStudentsRef();
        final Map<String,String> schoolsMap = new HashMap<>();
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot studentSnapshot:dataSnapshot.getChildren()){
                    Student student = studentSnapshot.getValue(Student.class);
                    Log.d(TAG,student.getSchoolId());
                    schoolsMap.put(student.getSchoolId(),student.getSchoolName());
                }

                Log.d(TAG,"/*** Setting Spinner Up ***/");
                schoolIds = new ArrayList<>();
                schoolNames = new ArrayList<>();

                for(String key:schoolsMap.keySet()){
                    schoolIds.add(key);
                    schoolNames.add(schoolsMap.get(key));
                }
                Log.d(TAG,schoolNames.toString());
                ArrayAdapter<String> schoolArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,schoolNames);
                mSchoolSelectorSpinner.setAdapter(schoolArrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
