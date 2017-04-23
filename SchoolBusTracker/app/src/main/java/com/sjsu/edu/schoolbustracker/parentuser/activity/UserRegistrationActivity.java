package com.sjsu.edu.schoolbustracker.parentuser.activity;

import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Gravity;

import com.google.firebase.auth.FirebaseAuth;
import com.sjsu.edu.schoolbustracker.R;

public class UserRegistrationActivity extends AppCompatActivity {


    private AppCompatEditText userID,userPassword;
    private AppCompatButton cancel,submit;

    //FireBase
    private FirebaseAuth mAuth;

    //Final Variables
    private final String TAG = "UserRegistrationActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        setupWindowAnimations();

        mAuth = FirebaseAuth.getInstance();



    }


    private void setupWindowAnimations(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.slide);
            slide.setSlideEdge(Gravity.BOTTOM);
            getWindow().setEnterTransition(slide);

            /*Explode explode = new Explode();
            explode.setDuration(1000);
            getWindow().setReturnTransition(explode);*/
        }
    }
}
