package com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sjsu.edu.schoolbustracker.R;

/**
 * Created by sai pranesh on 22-Apr-17.
 */

public class UserRegistrationDialogFragment extends DialogFragment {


    private AppCompatEditText userID,userPassword;
    private AppCompatButton cancel,submit;

    ConstraintLayout mCL;

    //FireBase
    private FirebaseAuth mAuth;

    //Final Variables
    private final String TAG = "UsrRegF";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fragment_user_registration, null);
        dialog.setView(dialogView);
        dialog.setTitle("Sign Up");
        dialog.setPositiveButton(android.R.string.ok, null);
        dialog.setNegativeButton(android.R.string.cancel, null);

        mAuth = FirebaseAuth.getInstance();

        mCL =(ConstraintLayout) dialogView.findViewById(R.id.user_registration_cl);
        userID = (AppCompatEditText) dialogView.findViewById(R.id.RegisterUserEmail);
        userPassword = (AppCompatEditText) dialogView.findViewById(R.id.RegisterUserPassword);

/*        cancel = (AppCompatButton) dialogView.findViewById(R.id.RegisterCancel);
        submit = (AppCompatButton) dialogView.findViewById(R.id.RegisterSubmit);*/

/*        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.createUserWithEmailAndPassword(userID.getText().toString(), userPassword.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createWithEmail: " + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {

                                    //Toast.makeText(EmailPasswordActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "createUserWithEmail:Failed:" + task.isSuccessful());
                                }
                                else{
                                    // Display snack bar or Toast or Dialog


                                    //Terminate this activity
                                    getActivity().finish();
                                }

                                // ...
                            }
                        });
            }
        });*/

        AlertDialog resultDialog = dialog.create();
        return resultDialog;

    }

    boolean validateEmailAddress(){
        return false;
    }

    int calculatePasswordStrength(){
        return 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
