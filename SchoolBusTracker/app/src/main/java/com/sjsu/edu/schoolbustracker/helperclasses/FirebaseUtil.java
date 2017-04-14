package com.sjsu.edu.schoolbustracker.helperclasses;

import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sjsu.edu.schoolbustracker.parentuser.model.Profile;

/**
 * Created by sai pranesh on 12-Apr-17.
 */

public class FirebaseUtil {

    private static final String PROFILE = "Profile";
    private static final String CHECK_USER_TYPE = "CheckUserType";
    private static final String DRIVER = "Driver";
    private static final String PARENT_USER = "ParentUser";
    private static final String BUS_HISTORY = "BusHistory";

    public static DatabaseReference getBaseRef(){
        return FirebaseDatabase.getInstance().getReference();
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



}
