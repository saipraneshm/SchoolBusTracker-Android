package com.sjsu.edu.schoolbustracker.parentuser.activity.abs;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.Window;

import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;

import static android.view.Gravity.BOTTOM;

/**
 * Created by sai pranesh on 02-Apr-17.
 */

public abstract class SingleFragmentActivity extends FragmentActivity {


    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowEnterTransitionOverlap(true);
            setupWindowAnimations();
        }
        ActivityHelper.initialize(this);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

    }

    protected void setupWindowAnimations(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.fade);
            getWindow().setExitTransition(fade);

            Slide slide = (Slide) TransitionInflater.from(this).inflateTransition(R.transition.slide);
            slide.setSlideEdge(BOTTOM);
            getWindow().setReenterTransition(slide);
        }
    }
}
