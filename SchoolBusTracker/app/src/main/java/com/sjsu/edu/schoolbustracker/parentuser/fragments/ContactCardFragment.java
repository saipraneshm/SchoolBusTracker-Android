package com.sjsu.edu.schoolbustracker.parentuser.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.adapter.StudentFirebaseRecyclerAdapter;
import com.sjsu.edu.schoolbustracker.common.model.Driver;
import com.sjsu.edu.schoolbustracker.common.model.Route;
import com.sjsu.edu.schoolbustracker.common.model.School;
import com.sjsu.edu.schoolbustracker.common.model.Student;


public class ContactCardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private AppCompatButton driver_call,driver_msg,school_call,school_msg;
    private AppCompatTextView driver_name,driver_phone,school_coordinator_name,school_coordinator_phone;
    private Toolbar mToolbar;
    private StudentFirebaseRecyclerAdapter mAdapter;
    private final String TAG = ContactCardFragment.class.getSimpleName();
    private DatabaseReference mStudentRef;
    private RecyclerView mChildImageLayout;
    private CardView mDriverCardView,mSchoolCoordinatorCardView;
    private LinearLayout mHintLinearLayout;

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

        mDriverCardView = (CardView) view.findViewById(R.id.driver_contact);
        mSchoolCoordinatorCardView =(CardView) view.findViewById(R.id.school_contact);
        mHintLinearLayout = (LinearLayout) view.findViewById(R.id.select_child_view);

        mChildImageLayout = (RecyclerView) view.findViewById(R.id.student_list_view_contact);
        mStudentRef = FirebaseUtil.getStudentsRef();
        final LinearLayoutManager mLm = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,false);
        mChildImageLayout.setLayoutManager(mLm);
        mAdapter = new StudentFirebaseRecyclerAdapter(mStudentRef,getActivity(),true);
        mAdapter.setOnItemClickListener(new StudentFirebaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String studentId, int pos) {
                mDriverCardView.setVisibility(View.VISIBLE);
                mSchoolCoordinatorCardView.setVisibility(View.VISIBLE);
                mHintLinearLayout.setVisibility(View.GONE);
                fetchContactDetails(studentId);
            }

            @Override
            public void getPrevPos(int pos) {
                View view =  mLm.findViewByPosition(pos);
                if(view != null){
                    Log.d(TAG, "view retreived successfully at position " + pos);
                    LinearLayout ll =(LinearLayout) view.findViewById(R.id.recycler_view_background);
                    ll.setSelected(false);
                }
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
                if (schoolId!=null)
                    fetchSchoolCoordinator(schoolId);
                String routeId = student.getRouteId();
                if(routeId!=null)
                    fetchRouteDetails(routeId);
                else{
                    Log.d(TAG,"Route id is NULL");
                    mDriverCardView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        
    }

    private void fetchRouteDetails(String routeId) {
        DatabaseReference routeRef =FirebaseUtil.getRouteRef(routeId);
        routeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Route routeDetails = dataSnapshot.getValue(Route.class);
                if(routeDetails.getDriverId()!=null)
                    fetchRouteDriver(routeDetails.getDriverId());
                else{
                    Log.d(TAG,"Route id is NULL");
                    mDriverCardView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchRouteDriver(String driverId) {
        DatabaseReference driverDetails = FirebaseUtil.getDriverDetailRef(driverId);
        driverDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Driver driverDetails = dataSnapshot.getValue(Driver.class);
                mDriverCardView.setVisibility(View.VISIBLE);
                driver_name.setText(driverDetails.getName());
                driver_phone.setText(driverDetails.getPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
