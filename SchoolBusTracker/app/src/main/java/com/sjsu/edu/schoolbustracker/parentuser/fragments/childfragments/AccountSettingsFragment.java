package com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments;

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
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.model.UserSettings;


public class AccountSettingsFragment extends Fragment {

    private final String TAG = "AccountSettings";
    private RadioButton mRadioEmail,mRadioMobile;
    private String mUserUID;
    private DatabaseReference userSettingsReference;
    private UserSettings mUserSettings;
    private AppCompatButton mEnableAccountButton,mDisableAccountButton;
    private static final String IS_DRIVER = "isDriver";
    private static boolean isDriver = false;

    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    public static AccountSettingsFragment newInstance(boolean isDriver) {
        AccountSettingsFragment fragment = new AccountSettingsFragment();
        Bundle arg = new Bundle();
        arg.putBoolean(IS_DRIVER, isDriver);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDriver = getArguments().getBoolean(IS_DRIVER);
        Log.d(TAG, isDriver + " : is the current user a driver?");
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

        mUserUID = FirebaseUtil.getCurrentUserId();
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

                if(mUserSettings.getContactPreference()!=null && mUserSettings.getContactPreference().equals("Email")){
                    mRadioEmail.setChecked(true);

                }
                else if(mUserSettings.getContactPreference()!=null && mUserSettings.getContactPreference().equals("Mobile")){
                    mRadioMobile.setChecked(true);
                }

                if(mUserSettings.isAccountEnabled()!=null && mUserSettings.isAccountEnabled()){
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
}
