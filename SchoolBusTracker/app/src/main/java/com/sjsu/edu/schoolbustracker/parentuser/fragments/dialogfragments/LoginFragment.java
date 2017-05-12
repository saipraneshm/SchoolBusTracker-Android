package com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sjsu.edu.schoolbustracker.R;

/**
 * This custom dialog handles login of existing users
 */
public class LoginFragment extends DialogFragment {


    private AppCompatButton mLoginButton;
    private AppCompatEditText mUserEmail , mUserPassword;
    private View view;
    private FirebaseAuth mAuth;
    private static final String TAG  = "LoginFragment";
    private ProgressDialog mProgressDialog;
    public static final String LOGIN_RESULT = "LOGIN_RESULT";


    public static LoginFragment newInstance(){
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.signin_please_wait));
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_login,null);
        mLoginButton = (AppCompatButton) view.findViewById(R.id.login_btn);
        mUserEmail = (AppCompatEditText) view.findViewById(R.id.reg_user_email_et);
        mUserPassword = (AppCompatEditText) view.findViewById(R.id.reg_user_pass_et);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidEmail(mUserEmail.getText().toString())){
                    mProgressDialog.show();
                    mAuth.signInWithEmailAndPassword(mUserEmail.getText().toString()
                            , mUserPassword.getText().toString())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    mProgressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        Log.d(TAG,"sign in successful");
                                        sendResult(Activity.RESULT_OK, true);
                                    }else{
                                        Log.d(TAG,"sign in failed");
                                        sendResult(Activity.RESULT_OK, false);
                                    }
                                    LoginFragment.this.dismiss();
                                }
                            });
                }
                else
                    mUserEmail.setError(getResources().getString(R.string.invalid_email));


            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder loginDialog = new AlertDialog.Builder(getActivity());
        loginDialog.setView(view);
        return loginDialog.create();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (target!=null && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void sendResult(int resultCode, boolean loginResult){
        if(getTargetFragment()== null)
            return;

        Intent intent = new Intent();
        intent.putExtra(LOGIN_RESULT,loginResult);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }
}
