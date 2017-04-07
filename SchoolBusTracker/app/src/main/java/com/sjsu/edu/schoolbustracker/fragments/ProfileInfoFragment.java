package com.sjsu.edu.schoolbustracker.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.activity.MainActivity;
import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;
import com.sjsu.edu.schoolbustracker.model.ParentUsers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DatabaseReference mDatabaseReference;
    private DatabaseReference parentProfileRef;
    private TextInputEditText mPhoneNumber,mProfileName,mProfileEmail,mProfileAddress;
    private AppCompatButton mEditProfile,mSaveProfile;
    private String mUserUID;
    private ParentUsers parentUser;

    private final String TAG = "ProfileInfoFrag";

    public static final String ARG_OBJECT = "object";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileInfoFragment newInstance(String param1, String param2) {
        ProfileInfoFragment fragment = new ProfileInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
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

        mUserUID = ActivityHelper.getUID(getActivity());
        Log.d(TAG,"User UID -->"+ mUserUID);
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
        updatedProfile.setParentName(mProfileName.getText().toString());
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
        mProfileName.setText(parentUser.getParentName());
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
