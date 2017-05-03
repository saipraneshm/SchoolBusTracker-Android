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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sjsu.edu.schoolbustracker.R;

/**
 * Created by sai pranesh on 28-Apr-17.
 */

public class ResetDialogFragment extends DialogFragment {


    private View view;
    private AppCompatButton mResetButton;
    private AppCompatEditText mUserEmailEt;
    public static final String EMAIL_ADDRESS = "ResetDialogFragment.EMAIL_ADDRESS";
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(getActivity());
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_reset_password,null);
        mResetButton = (AppCompatButton) view.findViewById(R.id.reset_password_btn);
        mUserEmailEt = (AppCompatEditText) view.findViewById(R.id.reg_email_tv);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult(Activity.RESULT_OK, mUserEmailEt.getText().toString());
                ResetDialogFragment.this.dismiss();
            }
        });
    }

    public static ResetDialogFragment newInstance(){
        return  new ResetDialogFragment();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(view);
        AlertDialog resetDialogFragment = alertDialog.create();
        return resetDialogFragment;
    }

    private void sendResult( int resultCode, String email){
        mProgressDialog.show();
        if(getTargetFragment() == null)
            return;
        Intent intent = new Intent();
        intent.putExtra(EMAIL_ADDRESS,email);
        getTargetFragment().onActivityResult(getTargetRequestCode() , resultCode, intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }
}
