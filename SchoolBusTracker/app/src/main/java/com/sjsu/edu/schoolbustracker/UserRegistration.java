package com.sjsu.edu.schoolbustracker;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserRegistration extends AppCompatActivity {

    //View Variables
    private AppCompatEditText userID,userPassword;
    private AppCompatButton cancel,submit;

    //FireBase
    private FirebaseAuth mAuth;

    //Final Variables
    private final String TAG = "UserRegistration";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        mAuth = FirebaseAuth.getInstance();

        userID = (AppCompatEditText) findViewById(R.id.RegisterUserEmail);
        userPassword = (AppCompatEditText) findViewById(R.id.RegisterUserPassword);

        cancel = (AppCompatButton) findViewById(R.id.RegisterCancel);
        submit = (AppCompatButton) findViewById(R.id.RegisterSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.createUserWithEmailAndPassword(userID.getText().toString(), userPassword.getText().toString())
                        .addOnCompleteListener(UserRegistration.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

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
                                    finish();
                                }

                                // ...
                            }
                        });
            }
        });


    }

    boolean validateEmailAddress(){
        return false;
    }

    int calculatePasswordStrength(){
        return 0;
    }

}
