package com.sjsu.edu.schoolbustracker.helperclasses;

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
import com.sjsu.edu.schoolbustracker.common.model.ParentUsers;
import com.sjsu.edu.schoolbustracker.common.model.Profile;
import com.sjsu.edu.schoolbustracker.common.model.UserSettings;

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
    private static final String ROUTES = "Routes";
    private static final String BUS_TRACKING = "BusTracking";


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
    public static DatabaseReference getDriverDetailRef(String driverId){
        if(driverId!=null){
            return getDriverRef().child(driverId);
        }
        return null;
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

    public static DatabaseReference getAllRouteRef(){
        return getBaseRef().child(ROUTES);
    }
    public static DatabaseReference getRouteRef(String routeId){
        if(routeId!=null){
            return getAllRouteRef().child(routeId);
        }
        return null;
    }


    public static void setUpInitialProfile(final Profile user){

        mCheckUserTypeRef = getCheckUserRef();
        mCheckUserTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean doesProfileExists = dataSnapshot.hasChild(user.getUUID());
                if(doesProfileExists){

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

                }else{
                    Log.d("ProfileExists", user.getEmailId() +
                            " profile doesn't exist in the database" );
                    mCheckUserTypeRef.child(user.getUUID()).child("isDriver").setValue(false);

                    userSettingsReference = FirebaseUtil.getBaseRef()
                            .child(APP_SETTINGS)
                            .child(user.getUUID());


                    UserSettings newSettings = new UserSettings();
                    newSettings.setEmailNotification(true);
                    newSettings.setPushNotification(true);
                    newSettings.setTextNotification(true);
                    newSettings.setAccountEnabled(true);
                    newSettings.setContactPreference("Mobile");

                    userSettingsReference.setValue(newSettings);
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

    public static DatabaseReference getBusTrackingRef(){
        return getBaseRef().child(BUS_TRACKING).getRef();
    }

    //Comment it out later
    public static DatabaseReference getBusTrackingDummyRef(){
        return getBusTrackingRef()
                .child("ADFF12")
                .child("Trips")
                .child("03-31-2017")
                .child("1")
                .child("Coordinates");
    }


    public static DatabaseReference getPlannedRoutesDummyRef(){
        return getBaseRef()
                .child("Sample_DB")
                .child("School")
                .child("school_id")
                .child("routes")
                .child("bus_no")
                .child("planned_route").getRef();
    }

    public static DatabaseReference getRegisteredChildrenRef(){
        return getBaseRef()
                .child("Sample_DB")
                .child("School")
                .child("school_id")
                .child("registeredStudents").getRef();
    }

}
