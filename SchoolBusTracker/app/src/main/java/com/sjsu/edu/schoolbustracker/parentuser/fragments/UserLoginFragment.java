package com.sjsu.edu.schoolbustracker.parentuser.fragments;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;

import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.activity.BottomNavigationActivity;
import com.sjsu.edu.schoolbustracker.parentuser.activity.UserRegistrationActivity;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments.LoginFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments.ResetDialogFragment;
import com.sjsu.edu.schoolbustracker.parentuser.model.ParentUsers;
import com.sjsu.edu.schoolbustracker.parentuser.model.Profile;

import java.util.Arrays;


public class UserLoginFragment extends Fragment {

    private static final String TAG = "UserLoginFragment";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_LOGIN_FRAGMENT = 9002;
    private static final int RC_RESET_DIALOG = 9003;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private AppCompatButton mFbLoginButton;
    private CallbackManager mCallbackManager;
    private TextView mSignUpTv, mLoginTv, mAppNameTxt, mForgotPassword;
    public ProgressDialog mProgressDialog;
    private LoginManager mLoginManager;
    private View view;
    private LinearLayout mLoginll;
    private FrameLayout mLoginFrameLayout;
    FirebaseUser user = null;


    //For retry logic
    private static String sTryAgain;
    //Google Auth
    GoogleApiClient mGoogleApiClient;
    private AppCompatButton mGoogleSignInBtn;
    // TODO: Rename and change types of parameters


    public UserLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        mLoginManager = LoginManager.getInstance();
        // Callback registration
        mLoginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

                hideProgressDialog();

                Log.d(TAG, "Facebook cancelled the login process");
            }

            @Override
            public void onError(FacebookException exception) {

                hideProgressDialog();

                Log.e(TAG,"Facebook exception", new Exception());
            }

        });

    }


    //This is used to start the next activity
    private void startBottomNavigationActivity() {
        if(UserLoginFragment.this.isAdded()){
            Intent intent =new Intent(getActivity(),BottomNavigationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    |Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_login, container, false);
        mFbLoginButton = (AppCompatButton) view.findViewById(R.id.fb_login_btn);
        mLoginTv = (TextView) view.findViewById(R.id.login_txt);
        mSignUpTv = (TextView) view.findViewById(R.id.sign_up_txt);
        mGoogleSignInBtn = (AppCompatButton) view.findViewById(R.id.google_login_button);
        mAppNameTxt = (TextView) view.findViewById(R.id.app_name_txt);
        mLoginll  = (LinearLayout) view.findViewById(R.id.login_ll);
        mLoginFrameLayout = (FrameLayout) view.findViewById(R.id.login_frame_layout);
        mForgotPassword = (TextView) view.findViewById(R.id.click_here_txt);

        mLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginFragment();
            }
        });

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getChildFragmentManager();
                ResetDialogFragment resetDialogFragment = ResetDialogFragment.newInstance();
                resetDialogFragment.setTargetFragment(UserLoginFragment.this, RC_RESET_DIALOG);
                resetDialogFragment.show(fm,"ForgotPassword");
                /*if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null)
                    mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    showSnackBar(Message.RESET_MESSAGE, Action.DISMISS);
                                }
                            });*/
            }
        });


        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),"font/SERIO___.TTF");
        mAppNameTxt.setTypeface(typeface);


        //On Auth state changed, if the user log's in successfully then we create a new Parent user
        // and then call the setUpInitialProfile which adds the data to the firebase database, after
        //which we call the next activity
        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                            String classname = this.getClass().getName();
                            Log.d(TAG,classname + " is the classname from which it has been called");
                            Profile newParent = new ParentUsers();
                            newParent.setUUID(user.getUid());
                            newParent.setName(user.getDisplayName());
                            newParent.setEmailId(user.getEmail());
                            if(user.getPhotoUrl() != null)
                                newParent.setPhotoUri(user.getPhotoUrl().toString());
                            FirebaseUtil.setUpInitialProfile(getActivity(),newParent);
                            startBottomNavigationActivity();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //Facebook Authentication
        setUpFacebookLogin();

        //Google Authentication
        setUpGoogleLogin();


        //Start Activity to Register New User
        mSignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerNewUserActivity = new Intent(getActivity(),
                        UserRegistrationActivity.class);

                //Transitions only work above LOLLIPOP
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(registerNewUserActivity,
                            ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                }else{
                    startActivity(registerNewUserActivity);
                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Displaying the appropriate information, when the network isn't available or connected.
        if(!isNetworkAvailableAndConnected()){
            showSnackBar(Message.NETWORK_ISSUE, Action.DISMISS);
        }
    }

    //This method is used to display the dialog login fragment, which is responsible to take
    //email and password as inputs and authenticates them.
    private void showLoginFragment() {
        LoginFragment loginFragment = LoginFragment.newInstance();
        FragmentManager fragmentManager = getChildFragmentManager();
        loginFragment.setTargetFragment(this,RC_LOGIN_FRAGMENT);
        loginFragment.show(fragmentManager, TAG);
    }

    //Responsible for setting up the initializing parameters required for google sign in.
    private void setUpGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) getActivity() /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }


    //Set's a listener to the custom button and calls initializeFacebookLoginManager
    private void setUpFacebookLogin() {

        mFbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initializeFacebookLoginManager();
            }
        });

    }

    //Responsible to handle the permissions required for login into facebook.
    private void initializeFacebookLoginManager() {
        showProgressDialog();
        if(AccessToken.getCurrentAccessToken() != null){
            mLoginManager.logOut();
        }else{
            mLoginManager.setLoginBehavior(LoginBehavior.WEB_ONLY);
            mLoginManager.logInWithReadPermissions(UserLoginFragment.this, Arrays.asList("public_profile","email"));
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }



    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("ULF","On Detach has been called");
        // un-registering the listener before the fragment get's detached from the activity
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);

        //This handles the google sign in result
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }//Handles login fragment result of authentication status (Success or failure)
        else if( requestCode == RC_LOGIN_FRAGMENT){
            boolean loginResult = data.getBooleanExtra(LoginFragment.LOGIN_RESULT,false);
            //Display's snackbar only when authentication fails.
            if(!loginResult){
                sTryAgain = Login.Email;
                showSnackBar(Message.AUTHENTICATION_FAILURE, Action.TRY_AGAIN);
            }
        }else if( requestCode == RC_RESET_DIALOG){
            String email = data.getStringExtra(ResetDialogFragment.EMAIL_ADDRESS);
            if(email!= null){
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                showSnackBar(Message.RESET_MESSAGE, Action.DISMISS);
                            }
                        });
            }
        }
    }


    //Handling the google authentication
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
                            sTryAgain = Login.Google;
                            Log.w(TAG, "signInWithCredential", task.getException());
                            showSnackBar(Message.AUTHENTICATION_FAILURE, Action.TRY_AGAIN);
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    //handles the result for the google sign in result
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {

        }
    }

    //Handles facebook auth token
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
                            sTryAgain = Login.Facebook;
                            LoginManager.getInstance().logOut();
                            Log.w(TAG, "signInWithCredential", task.getException());
                            showSnackBar(Message.AUTHENTICATION_FAILURE, Action.TRY_AGAIN);

                        }
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                    }
                });
    }


    //Displays the progress dialog to the user when an operation is being handled in the background.
    public void showProgressDialog() {
        if (mProgressDialog == null && this.isAdded()) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        if(mProgressDialog != null)
            mProgressDialog.show();

    }

    //Hides the progress dialog when called.
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("ULF"," on stop has been called");
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideProgressDialog();
    }


    //A convenient method to display appropriate text to the users.
    private void showSnackBar(final String msg, final String action){
        if(view != null){
            final Snackbar snackbar = Snackbar.make(mLoginFrameLayout, msg , Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary))
                    .setAction(action , new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG,"you have clicked the snack bar" + action + " " + msg);
                    switch (action){
                        case Action.DISMISS: snackbar.dismiss();
                            break;
                        case Action.TRY_AGAIN:
                            switch (sTryAgain){
                                case Login.Facebook : initializeFacebookLoginManager();
                                                    break;
                                case Login.Email : showLoginFragment();
                                                    break;
                                case Login.Google: signIn();
                                                    break;
                            }

                            break;
                        case Action.LOG_IN: showLoginFragment();
                            break;
                    }
                }
            }).show();

        }

    }


    private static class Action{
        public static final String TRY_AGAIN = "TRY AGAIN";
        public static final String DISMISS = "DISMISS";
        public static final String LOG_IN = "LOG IN";
    }

    private static class Message{
        public static final String TRY_AGAIN = "Something went wrong. Please try again";
        public static final String NETWORK_ISSUE = "Not connected to internet.";
        public static final String LOG_OUT = "Logout successful";
        public static final String LOG_IN_FAIL = "Please check username and password.";
        public static final String AUTHENTICATION_FAILURE = "Authentication failure. Please try again";
        public static final String RESET_MESSAGE = "Reset email has been sent";
    }

    private static class Login{
        public static final String Facebook = "Facebook";
        public static final String Google ="Google";
        public static final String Email = "Email";
    }

    //method to check whether the network is connected or not.
    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm  = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    }

}
