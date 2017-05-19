package com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.common.model.ParentUsers;



public class ProfileInfoFragment extends Fragment {
    private DatabaseReference mDatabaseReference;
    private DatabaseReference parentProfileRef;
    private TextInputEditText mPhoneNumber,mProfileName,mProfileEmail,mProfileAddress;
    private AppCompatButton mEditProfile,mSaveProfile;
    private String mUserUID;
    private ParentUsers parentUser;
    private static final String IS_DRIVER = "isDriver";
    private static boolean isDriver = false;

    private final String TAG = "ProfileInfoFrag";


    public static final String ARG_OBJECT = "object";

    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileInfoFragment newInstance(boolean isDriver) {
        ProfileInfoFragment fragment = new ProfileInfoFragment();
        Bundle arg = new Bundle();
        arg.putBoolean(IS_DRIVER, isDriver);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        isDriver = getArguments().getBoolean(IS_DRIVER);
        Log.d(TAG, isDriver + " : is the current user a driver?");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile_info, container, false);

        mPhoneNumber = (TextInputEditText) v.findViewById(R.id.profile_phone_number);
        mProfileName = (TextInputEditText) v.findViewById(R.id.profile_name);
        mProfileEmail = (TextInputEditText) v.findViewById(R.id.profile_email);
        mProfileAddress = (TextInputEditText) v.findViewById(R.id.profile_address);
        mEditProfile = (AppCompatButton) v.findViewById(R.id.edit_btn);
        mSaveProfile = (AppCompatButton) v.findViewById(R.id.save_btn);

        mUserUID = FirebaseUtil.getCurrentUserId();
        Log.d(TAG,"User UID -->"+ mUserUID);

        if(isDriver)
            parentProfileRef = mDatabaseReference
                .child(getString(R.string.firebase_profile_node))
                .child(getString(R.string.firebase_driver_node)).child(mUserUID);
        else
            parentProfileRef = mDatabaseReference
                    .child(getString(R.string.firebase_profile_node))
                    .child(getString(R.string.firebase_parent_node)).child(mUserUID);
        setupDataFromFirebase();

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditTextViews();
            }
        });
        mSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDataInFirebase();

            }
        });
        // Inflate the layout for this fragment
        return v;
    }


    private void updateDataInFirebase() {

        ParentUsers updatedProfile = parentUser;
        updatedProfile.setHouseAddress(mProfileAddress.getText().toString());
        updatedProfile.setPhone(mPhoneNumber.getText().toString());
        updatedProfile.setEmailId(mProfileEmail.getText().toString());
        updatedProfile.setName(mProfileName.getText().toString());
        parentProfileRef.setValue(updatedProfile);

        disableEditTextViews();

    }

    public void setupDataFromFirebase(){


        parentProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parentUser = dataSnapshot.getValue(ParentUsers.class);
                setUpDataInUI(parentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setUpDataInUI(ParentUsers parentUser){
        mPhoneNumber.setText(parentUser.getPhone());
        mProfileName.setText(parentUser.getName());
        mProfileEmail.setText(parentUser.getEmailId());
        mProfileAddress.setText(parentUser.getHouseAddress());

        disableEditTextViews();
    }

    public void disableEditTextViews(){

        mPhoneNumber.setEnabled(false);
        mProfileName.setEnabled(false);
        mProfileEmail.setEnabled(false);
        mProfileAddress.setEnabled(false);


    }
    public void enableEditTextViews(){

        mPhoneNumber.setEnabled(true);
        mProfileName.setEnabled(true);
        mProfileEmail.setEnabled(true);
        mProfileAddress.setEnabled(true);

    }

}
