package com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.model.UserSettings;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = "AccountSettings";
    private RadioButton mRadioEmail,mRadioMobile;
    private String mUserUID;
    private DatabaseReference userSettingsReference;
    private UserSettings mUserSettings;
    private AppCompatButton mEnableAccountButton,mDisableAccountButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountSettingsFragment newInstance(String param1, String param2) {
        AccountSettingsFragment fragment = new AccountSettingsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_account_settings, container, false);
        mRadioEmail = (RadioButton) v.findViewById(R.id.radio_email);
        mRadioMobile = (RadioButton) v.findViewById(R.id.radio_mobile);
        mEnableAccountButton = (AppCompatButton) v.findViewById(R.id.enable_account_btn);
        mDisableAccountButton = (AppCompatButton) v.findViewById(R.id.disable_account_btn);

        mDisableAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserSettings.setAccountEnabled(false);
                userSettingsReference.setValue(mUserSettings);
                mDisableAccountButton.setVisibility(View.GONE);
                mEnableAccountButton.setVisibility(View.VISIBLE);
            }
        });

        mEnableAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserSettings.setAccountEnabled(true);
                userSettingsReference.setValue(mUserSettings);
                mEnableAccountButton.setVisibility(View.GONE);
                mDisableAccountButton.setVisibility(View.VISIBLE);
            }
        });

        mUserUID = ActivityHelper.getUID(getActivity());
        Log.d(TAG,"User UID -->"+ mUserUID);
        userSettingsReference = FirebaseUtil.getUserAppSettingRef();

        setUpUIifSettingsExists();




        mRadioEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserSettings.setContactPreference("Email");
                userSettingsReference.setValue(mUserSettings);

            }
        });
        mRadioMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserSettings.setContactPreference("Mobile");
                userSettingsReference.setValue(mUserSettings);

            }
        });
        return v;
    }

    private void setUpUIifSettingsExists() {
        DatabaseReference userAppSettingRef = FirebaseUtil.getAppSettingRef();
        userAppSettingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())){
                    Log.d(TAG,"User settings exists");
                    setupRadioButtonStates();
                }
                else{
                    Log.d(TAG,"User settings DOES NOT exists");
                    UserSettings newSettings = new UserSettings();
                    newSettings.setEmailNotification(true);
                    newSettings.setPushNotification(true);
                    newSettings.setTextNotification(true);
                    newSettings.setAccountEnabled(true);
                    newSettings.setContactPreference("Mobile");

                    userSettingsReference.setValue(newSettings);
                    setupRadioButtonStates();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupRadioButtonStates() {

        userSettingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserSettings = dataSnapshot.getValue(UserSettings.class);

                if(mUserSettings.getContactPreference().equals("Email")){
                    mRadioEmail.setChecked(true);

                }
                else if(mUserSettings.getContactPreference().equals("Mobile")){
                    mRadioMobile.setChecked(true);
                }

                if(mUserSettings.isAccountEnabled()){
                    mEnableAccountButton.setVisibility(View.GONE);
                    mDisableAccountButton.setVisibility(View.VISIBLE);
                }
                else{
                    mDisableAccountButton.setVisibility(View.GONE);
                    mEnableAccountButton.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
