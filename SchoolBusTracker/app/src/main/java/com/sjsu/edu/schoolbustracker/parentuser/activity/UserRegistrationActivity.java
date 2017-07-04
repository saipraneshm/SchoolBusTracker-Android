package com.sjsu.edu.schoolbustracker.parentuser.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.helperclasses.QueryPreferences;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments.PhotoPickerFragment;
import com.sjsu.edu.schoolbustracker.common.model.ParentUsers;

public class UserRegistrationActivity extends AppCompatActivity implements View.OnClickListener,
        PhotoPickerFragment.OnPhotoPickerPathListener{


    private static final int REQUEST_PHOTO = 4561;
    private static final String PHOTO_PICKER_TAG = "PhotoPicker" ;
    private AppCompatEditText userName, userEmail, userPassword, userPhoneNo;
    private AppCompatButton back, done ,sign_in;
    private ConstraintLayout mCl;
    private ImageView mProfileImageView, mCameraImageView;
    private Uri mParentPhotoUri;

    //FireBase
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Final Variables
    private final String TAG = "UsrRegAct";
    private ParentUsers newParent;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        setupWindowAnimations();
        ActivityHelper.initialize(this);
        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(this);

        mCl = (ConstraintLayout) findViewById(R.id.user_registration_cl);
        userName = (AppCompatEditText) findViewById(R.id.reg_user_name_et);
        userEmail = (AppCompatEditText) findViewById(R.id.reg_user_email_et);
        userPassword = (AppCompatEditText) findViewById(R.id.reg_user_pass_et);
        userPhoneNo = (AppCompatEditText) findViewById(R.id.reg_user_phone_no_et);
        back = (AppCompatButton) findViewById(R.id.back_button);
        done = (AppCompatButton) findViewById(R.id.done_button);
        sign_in = (AppCompatButton) findViewById(R.id.sign_in_btn);
        mProfileImageView = (ImageView) findViewById(R.id.user_profile_image_view) ;
        mCameraImageView = (ImageView) findViewById(R.id.camera_image_view);

       mProfileImageView.setOnClickListener(this);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValidEmail = isValidEmail(userEmail.getText());
                if (!isValidEmail)
                    userEmail.setError(getResources().getString(R.string.invalid_email));
                else{
                    mAuth.signOut();
                    mProgressDialog.setMessage(getResources().getString(R.string.create_account_msg));
                    mProgressDialog.show();
                    fetchAndStoreDetails(view);
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserRegistrationActivity.this.finish();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG,"on auth state changed called");
                QueryPreferences.setSignUpPref(UserRegistrationActivity.this,true);
                //updateUI();
                //Getting the current user after successful sign up
                user = firebaseAuth.getCurrentUser();
                if(user!=null) {
                    if(mParentPhotoUri !=null){
                        StorageReference parentStorageRef = FirebaseUtil
                                .getParentUsersPhotoRef(mParentPhotoUri.getLastPathSegment());
                        parentStorageRef.putFile(mParentPhotoUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        mParentPhotoUri =  taskSnapshot.getDownloadUrl();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(userName.getText().toString())
                                                .setPhotoUri(mParentPhotoUri == null ? null : mParentPhotoUri)
                                                .build();
                                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Log.d(TAG,"update profile calling");
                                                    updateProfile(mCl);
                                                }
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG,"failure to upload photo " , e);
                            }
                        });
                    }
                    updateProfile(mCl);
                    //wrong place to upload the photo



                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth!=null)
            mAuth.removeAuthStateListener(mAuthStateListener);
        mProgressDialog.cancel();
    }

    private void setupWindowAnimations() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.slide);
            slide.setSlideEdge(Gravity.BOTTOM);
            getWindow().setEnterTransition(slide);

            Slide returnSlide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.slide);
            returnSlide.setSlideEdge(Gravity.BOTTOM);
            getWindow().setReturnTransition(slide);
            /*Explode explode = new Explode();
            explode.setDuration(1000);
            getWindow().setReturnTransition(explode);*/
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    //This method fetches the data from the user input fields and then stores them in firebase.
    private void fetchAndStoreDetails(final View view){
        final String user_name = String.valueOf(userName.getText());
        String user_email = String.valueOf(userEmail.getText());
        String user_password = String.valueOf(userPassword.getText());
        String user_phone = String.valueOf(userPhoneNo.getText());
        newParent = new ParentUsers();
        newParent.setName(user_name);
        newParent.setPhone(user_phone);
        if(mParentPhotoUri!= null){
            newParent.setPhotoUri(mParentPhotoUri.toString());
        }
        mAuth.createUserWithEmailAndPassword(user_email,user_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                //Showing the corresponding failed snackbar message
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    mProgressDialog.hide();
                    //Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "createUserWithEmail:Failed:" + task.isSuccessful());
                    final Snackbar snackbar = Snackbar.make(view, R.string.sign_up_incomplete, Snackbar.LENGTH_INDEFINITE)
                            .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                    snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }

                Log.d(TAG, "createWithEmail: " + task.isSuccessful());



            }
        });




    }

    //we are waiting for the profile to be updated after which we are showing the required snackbar.
    private void updateProfile(final View view){
        Log.d(TAG, " user _name " +  user.getDisplayName());
        newParent.setUUID(user.getUid());
        //newParent.setName(user.getDisplayName());
        newParent.setEmailId(user.getEmail());
        if(mParentPhotoUri != null)
            newParent.setPhotoUri(mParentPhotoUri.toString());
        /*if(user.getPhotoUrl() != null)
            newParent.setPhotoUri(user.getPhotoUrl().toString());*/
        FirebaseUtil.setUpInitialProfile(newParent);
      //  QueryPreferences.setSignUpPref(UserRegistrationActivity.this,true);
        mProgressDialog.hide();
        final Snackbar snackbar = Snackbar.make(view, R.string.sign_up_complete, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.setAction(R.string.common_signin_button_text, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                mAuth.signOut();
                finish();
            }
        });
        snackbar.show();
        updateUI();
    }


    //This method will be called after successful sign up
    private void updateUI(){

        userName.setEnabled(false);
        userEmail.setEnabled(false);
        userPassword.setEnabled(false);
        userPhoneNo.setEnabled(false);
        back.setVisibility(View.GONE);
        done.setVisibility(View.GONE);
        sign_in.setVisibility(View.VISIBLE);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
            }
        });


    }

    @Override
    public void onClick(View view) {
        int itemId = view.getId();
        switch(itemId){
            case R.id.user_profile_image_view:
            case R.id.camera_image_view:
                PhotoPickerFragment photoPickerFragment = PhotoPickerFragment.newInstance();
                photoPickerFragment.show(getSupportFragmentManager(),PHOTO_PICKER_TAG);
        }
    }

    @Override
    public void setCurrentPhotoPath(Uri photoPath) {
        mParentPhotoUri = photoPath;
        mProfileImageView.setImageURI(photoPath);
    }
}


