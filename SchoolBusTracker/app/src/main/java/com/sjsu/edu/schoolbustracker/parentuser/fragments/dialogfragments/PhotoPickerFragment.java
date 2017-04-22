package com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sjsu.edu.schoolbustracker.R;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoPickerFragment extends DialogFragment {

    private AppCompatButton mCameraButton,mGalleryButton,mCancelButton,mConfirmButton;
    private LinearLayout mPickerLayout,mConfirmationLayout;
    private CircleImageView mCircleImageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;

    private final String TAG="PhotoPickerFragment";
    public PhotoPickerFragment() {
        // Required empty public constructor
    }

    public static PhotoPickerFragment newInstance(){
        return new PhotoPickerFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_photo_picker,null);
        mCameraButton = (AppCompatButton) v.findViewById(R.id.button_camera);
        mGalleryButton = (AppCompatButton) v.findViewById(R.id.button_gallery);
        mCancelButton = (AppCompatButton) v.findViewById(R.id.cancel_photo);
        mConfirmButton = (AppCompatButton) v.findViewById(R.id.confirm_photo);
        mPickerLayout = (LinearLayout) v.findViewById(R.id.photo_selection_view);
        mConfirmationLayout = (LinearLayout) v.findViewById(R.id.photo_conf_view);
        mCircleImageView = (CircleImageView) v.findViewById(R.id.photo_confirm_iv);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPhotoPath=null;
                mPickerLayout.setVisibility(View.VISIBLE);
                mConfirmationLayout.setVisibility(View.GONE);
            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData(Activity.RESULT_OK);
                dismiss();
            }
        });
        alertDialog.setTitle(R.string.pick_photo);
        alertDialog.setView(v);
        return alertDialog.create();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.sjsu.edu.schoolbustracker.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG,mCurrentPhotoPath);
            mPickerLayout.setVisibility(View.GONE);
            mConfirmationLayout.setVisibility(View.VISIBLE);
            mCircleImageView.setImageURI(Uri.fromFile(new File(mCurrentPhotoPath)));
        }


    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = UUID.randomUUID().toString();
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void sendData(int resultCode){
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra("photopath",mCurrentPhotoPath);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }

}
