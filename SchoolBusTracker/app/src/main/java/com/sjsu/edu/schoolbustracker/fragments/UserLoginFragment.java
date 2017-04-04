package com.sjsu.edu.schoolbustracker.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.activity.BottomNavigationActivity;
import com.sjsu.edu.schoolbustracker.activity.MainActivity;
import com.sjsu.edu.schoolbustracker.activity.UserRegistration;
import com.sjsu.edu.schoolbustracker.model.CheckUserType;
import com.sjsu.edu.schoolbustracker.model.ParentUsers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserLoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserLoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "UserLoginFragment";
    private static final int RC_SIGN_IN = 9001;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference mDatabase, mCheckUserTypeRef, mProfileRef;

    private LoginButton mFbLoginButton;
    private CallbackManager callbackManager;
    private AppCompatEditText loginUserID,loginPassword;
    private AppCompatButton loginButton,signUpButton,signOutButton,testButton;
    public ProgressDialog mProgressDialog;


    //Google Auth
    GoogleApiClient mGoogleApiClient;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public UserLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserLoginFragment newInstance(String param1, String param2) {
        UserLoginFragment fragment = new UserLoginFragment();
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

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid() + " " + user.getEmail());
                    mCheckUserTypeRef = mDatabase.child("CheckUserType");
                    mCheckUserTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean doesProfileExists = dataSnapshot.hasChild(user.getUid());
                            if(doesProfileExists){
                                Log.d("ProfileExists", user.getEmail() + " profile exists in the database" );

                                Boolean isDriver = dataSnapshot.child(user.getUid()).child("isDriver").getValue(Boolean.class);
                                Log.d("ProfileExists", isDriver.toString());
                                if(isDriver){
                                    mProfileRef = mDatabase.child("Profile").child("Driver").getRef();

                                }else{
                                    mProfileRef = mDatabase.child("Profile").child("ParentUser").child(user.getUid()).getRef();
                                    mProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            ParentUsers existingParent = dataSnapshot.getValue(ParentUsers.class);
                                            Log.d("ProfileExists", existingParent.getEmailId() + " email from existing parent");
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }else{
                                Log.d("ProfileExists", user.getEmail() + " profile doesn't exist in the database" );
                                mCheckUserTypeRef.child(user.getUid()).child("isDriver").setValue(false);
                                ParentUsers newParent = new ParentUsers();
                                newParent.setUUId(user.getUid());
                                newParent.setParentName(user.getDisplayName());
                                newParent.setEmailId(user.getEmail());
                                mDatabase.child("Profile")
                                        .child("ParentUser")
                                        .child(newParent.getUUId())
                                        .setValue(newParent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    startActivity(new Intent(getActivity(), BottomNavigationActivity.class));
                    getActivity().finish();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_login, container, false);
        mFbLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginUserID =  (AppCompatEditText) view.findViewById(R.id.LoginUserEmail);
        loginPassword =  (AppCompatEditText) view.findViewById(R.id.LoginUserPassword);
        loginButton = (AppCompatButton) view.findViewById(R.id.LoginButton);
        signUpButton = (AppCompatButton) view.findViewById(R.id.SignUpButton);
        signOutButton = (AppCompatButton) view.findViewById(R.id.sign_out_button);
        testButton = (AppCompatButton) view.findViewById(R.id.button2);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Bus newBus = new Bus();
                UUID busuuid = UUID.randomUUID();
                newBus.setDriverUUID(busuuid.toString());
                newBus.setLat("121.13131231");
                newBus.setLng("36.0001212");
                newBus.setRegistrationNumber("ADFF12");
                newBus.setRouteNumber("123");
                mDatabase.child("Bus").child(newBus.getRegistrationNumber()).setValue(newBus);*/

               /* BusTracking busTracking = new BusTracking();
                Trip trip = new Trip();
                trip.setTripId("1");
                trip.setmIsComplete(false);
                Coordinates coord = new Coordinates();
                coord.setLat("121.123123");
                coord.setLng("36.123145");
                DatabaseReference mRef = mDatabase.child("BusTracking")
                        .child("ADFF12")
                        .child("Trips")
                        .child("03-31-2017")
                        .child(trip.getTripId());

                        mRef.child("Coordinates").child(System.currentTimeMillis() + "").setValue(coord);
                        mRef.child("isTripComplete").setValue("false");*/
               //startActivity(new Intent(getActivity(), BottomNavigationActivity.class));
                Intent intent =new Intent(getActivity(),BottomNavigationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        mFbLoginButton.setReadPermissions("email", "public_profile");
        // If using in a fragment
        mFbLoginButton.setFragment(this);
        // Other app specific specialization

        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        mFbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                       // Profile profile = Profile.getCurrentProfile();
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        /*AccessToken accessToken = loginResult.getAccessToken();

                        if(accessToken != null)
                            Log.d("FirstName", accessToken.getUserId());
                        else
                            Log.d("FirstName", "profile info not found");*/
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }

                });
        // Inflate the layout for this fragment

        //Google Authentication
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) getActivity() /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        view.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //Email Password Login using Firebase
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(loginUserID.getText().toString(), loginPassword.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                                    Toast.makeText(getActivity() , R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Log.w(TAG, "signInWithEmail:successful");
                                    // Start the Landing Activity
                                }

                                // ...
                            }
                        });
            }
        });

        //Start Activity to Register New User
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerNewUserActivity = new Intent(getActivity(),UserRegistration.class);
                startActivity(registerNewUserActivity);
            }
        });

        //Sign Out of Google
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // [START_EXCLUDE]
                              //  updateUI(false);
                                // [END_EXCLUDE]
                            }
                        });
            }
        });

        return view;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

  /*  private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }*/

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
           // updateUI(true);
            firebaseAuthWithGoogle(acct);

            Log.d(TAG, "auth successful:");
        } else {
            // Signed out, show unauthenticated UI.
           // updateUI(false);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
