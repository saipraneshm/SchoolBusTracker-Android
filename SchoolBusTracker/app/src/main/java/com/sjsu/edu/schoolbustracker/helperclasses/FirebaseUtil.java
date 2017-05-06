package com.sjsu.edu.schoolbustracker.helperclasses;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.parentuser.model.ParentUsers;
import com.sjsu.edu.schoolbustracker.parentuser.model.Profile;
import com.sjsu.edu.schoolbustracker.parentuser.model.UserSettings;

/**
 * Created by sai pranesh on 12-Apr-17.
 */

public class FirebaseUtil {

    private static final String PROFILE = "Profile";
    private static final String CHECK_USER_TYPE = "CheckUserType";
    private static final String DRIVER = "Driver";
    private static final String PARENT_USER = "ParentUser";
    private static final String BUS_HISTORY = "BusHistory";
    private static final String STUDENTS = "Students";
    private static final String APP_SETTINGS = "AppSettings";
    private static final String SCHOOLS = "Schools";
    private static final String TRANSPORT_COORDINATOR = "transportCoordinator";

    private static DatabaseReference mDatabase;
    private static DatabaseReference mCheckUserTypeRef;
    private static DatabaseReference mProfileRef;
    private static DatabaseReference userSettingsReference;


    public static DatabaseReference getBaseRef(){
        return FirebaseDatabase.getInstance().getReference();
    }

    public static StorageReference getBaseStorageReference(){
        return FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    public static String getCurrentUserId(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
            return user.getUid();
        return null;
    }

    public static DatabaseReference getCheckUserRef(){
        return getBaseRef().child(CHECK_USER_TYPE);
    }

    @Nullable
    public static Profile getCurrentProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)
            return null;
        return new Profile(user.getDisplayName(),user.getUid(),user.getEmail());
    }

    public static DatabaseReference getDriverRef(){
        return getBaseRef().child(PROFILE).child(DRIVER).getRef();
    }

    public static DatabaseReference getParentUserRef(){
        return getBaseRef().child(PROFILE).child(PARENT_USER).getRef();
    }

    public static DatabaseReference getCurrentParentUserRef(){
        return getBaseRef().child(PROFILE).child(PARENT_USER).child(getCurrentUserId()).getRef();
    }

    //Fetches the current user's previous trip ref
    public static DatabaseReference getExisitingPreviousTripsRef(){
        String userId = getCurrentUserId();
        if(userId != null){
            return getBaseRef().child(BUS_HISTORY).child(userId).getRef();
        }
        return null;
    }

    //Fetches only the Bus history ref
    public static DatabaseReference getPreviousTripRef(){
        return getBaseRef().child(BUS_HISTORY);
    }

    //Fetches the reference to students of the current user;
    public static DatabaseReference getStudentsRef(){
        String userId = getCurrentUserId();
        if(userId!=null){
            return getBaseRef().child(STUDENTS).child(userId).getRef();
        }
        return null;
    }

    //Fetches References to App Settings
    public static DatabaseReference getAppSettingRef(){
        return getBaseRef().child(APP_SETTINGS);
    }
    //Fetches References to a User's App Settings
    public static DatabaseReference getUserAppSettingRef(){
        String userId = getCurrentUserId();
        if(userId!=null){
            return getBaseRef().child(APP_SETTINGS).child(userId).getRef();
        }
        return null;

    }

    //Fetches the Storage reference to students photos
    public static StorageReference getStudentPhotoRef(String fileName){

        if(fileName!=null){
            return getBaseStorageReference().child(STUDENTS).child(fileName);
        }
        return null;
    }


    //Fetches the Database reference to Schools
    public static DatabaseReference getAllSchoolsRef(){
        return getBaseRef().child(SCHOOLS);
    }

    public static DatabaseReference getSchoolRef(String schoolId) {
        if (schoolId != null) {
            return getAllSchoolsRef().child(schoolId);
        }
        return null;
    }

    public static StorageReference getParentUsersPhotoRef(String fileName){
        if(fileName != null){
            return getBaseStorageReference().child(PARENT_USER).child(fileName);

        }
        return null;
    }


    public static DatabaseReference getTransportCoordinator(String schoolId) {
        return getSchoolRef(schoolId).child(TRANSPORT_COORDINATOR);
    }


    public static void setUpInitialProfile(final Context context , final Profile user){

        mCheckUserTypeRef = FirebaseUtil.getCheckUserRef();
        mCheckUserTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean doesProfileExists = dataSnapshot.hasChild(user.getUUID());
                if(doesProfileExists){
                    Log.d("ProfileExists", user.getEmailId() + " profile exists in the database" );

                    Boolean isDriver = dataSnapshot.child(user.getUUID()).child("isDriver")
                            .getValue(Boolean.class);
                    Log.d("ProfileExists", isDriver.toString() + " is driver ?");
                    if(isDriver){

                        mProfileRef = FirebaseUtil.getDriverRef();

                    }else{
                        mProfileRef = FirebaseUtil.getParentUserRef().child(user.getUUID()).getRef();

                        mProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ParentUsers existingParent = dataSnapshot
                                        .getValue(ParentUsers.class);
                                if(existingParent != null){
                                    Log.d("ProfileExists", existingParent.getEmailId() +
                                            " email from existing parent");
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }else{
                    Log.d("ProfileExists", user.getEmailId() +
                            " profile doesn't exist in the database" );
                    mCheckUserTypeRef.child(user.getUUID()).child("isDriver").setValue(false);

                    userSettingsReference = FirebaseUtil.getBaseRef()
                            .child(context.getResources().getString(R.string.firebase_settings_node))
                            .child(user.getUUID());


                    UserSettings newSettings = new UserSettings();
                    newSettings.setEmailNotification(true);
                    newSettings.setPushNotification(true);
                    newSettings.setTextNotification(true);
                    newSettings.setAccountEnabled(true);
                    newSettings.setContactPreference("Mobile");

                    userSettingsReference.setValue(newSettings);

                    /*Profile newParent = new ParentUsers();
                    newParent.setUUID(user.getUUID());
                    newParent.setName(user.getName());
                    newParent.setEmailId(user.getEmail());
                    if(user.getPhotoUrl() != null)
                        newParent.setPhotoUri(user.getPhotoUrl().toString());*/
                    FirebaseUtil.getParentUserRef()
                            .child(user.getUUID())
                            .setValue(user);


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
