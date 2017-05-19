package com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
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
import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.common.model.UserSettings;



public class NotificationSettingsFragment extends Fragment {

    private SwitchCompat mPushNotificationSwitch,mEmailNotificationSwitch,mTextNotificationSwitch;
    private AppCompatButton saveSettings;

    private String mUserUID;
    private final String TAG = "NotificationSettings";
    private DatabaseReference mDatabaseReference;
    private DatabaseReference userSettingsReference;
    private UserSettings mUserSettings;
    private static final String IS_DRIVER = "isDriver";
    private static boolean isDriver = false;

    public NotificationSettingsFragment() {
        // Required empty public constructor
    }

    public static NotificationSettingsFragment newInstance(boolean isDriver) {
        NotificationSettingsFragment fragment = new NotificationSettingsFragment();
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
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_notification_settings, container, false);
        mPushNotificationSwitch = (SwitchCompat) v.findViewById(R.id.push_notification_switch);
        mEmailNotificationSwitch = (SwitchCompat) v.findViewById(R.id.email_notification_switch);
        mTextNotificationSwitch = (SwitchCompat) v.findViewById(R.id.text_notification_switch);
        saveSettings = (AppCompatButton) v.findViewById(R.id.save_settings_button);

        mUserUID = FirebaseUtil.getCurrentUserId();
        Log.d(TAG,"User UID -->"+ mUserUID);
        userSettingsReference = FirebaseUtil.getUserAppSettingRef();

        ifUserSettingsExists();

        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //UserSettings newSettings = new UserSettings();
                mUserSettings.setEmailNotification(mEmailNotificationSwitch.isChecked());
                mUserSettings.setPushNotification(mPushNotificationSwitch.isChecked());
                mUserSettings.setTextNotification(mTextNotificationSwitch.isChecked());

                userSettingsReference.setValue(mUserSettings);

            }
        });
        return v;
    }

    private void ifUserSettingsExists(){
        DatabaseReference userAppSettingRef = FirebaseUtil.getAppSettingRef();
        userAppSettingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())){
                    Log.d(TAG,"User settings exists");
                    setupSwitchButtonStates();
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
                    setupSwitchButtonStates();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupSwitchButtonStates() {
        userSettingsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserSettings = dataSnapshot.getValue(UserSettings.class);
                mPushNotificationSwitch.setChecked(mUserSettings.getPushNotification());
                mTextNotificationSwitch.setChecked(mUserSettings.getTextNotification());
                mEmailNotificationSwitch.setChecked(mUserSettings.getEmailNotification());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
