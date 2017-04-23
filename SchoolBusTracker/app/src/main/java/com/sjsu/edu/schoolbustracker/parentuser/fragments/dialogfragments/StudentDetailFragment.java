package com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.model.Student;

import java.io.File;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by akshaymathur on 4/15/17.
 */

public class StudentDetailFragment extends DialogFragment {

    private TextInputEditText mStudentName,mStudentId,mSchoolName,mSchoolAddress;
    private AppCompatButton mSaveButton;
    private CircleImageView mStudentPicture;
    private DatabaseReference mStudentReference;
    private Bundle args;
    private Student student,newStudent;
    private Uri mPhotoFilePath = null;
    private final String TAG = "StudentDetailFragment";
    private static final int REQUEST_DATE = 0;
    private UploadTask mUploadTask;
    private DatabaseReference parentStudentReference,studentRef;
    private ProgressDialog progressDialog;
    private Boolean isPhotoUpdated=false;
    private FrameLayout mFrameLayout;

    public static StudentDetailFragment newInstance(String studentId){
        StudentDetailFragment studentDetailFragment = new StudentDetailFragment();
        Bundle args = new Bundle();
        args.putString("studentid",studentId);
        studentDetailFragment.setArguments(args);

        return studentDetailFragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.student_detail_view,null);
        mStudentName = (TextInputEditText) v.findViewById(R.id.student_name);
        mStudentId = (TextInputEditText) v.findViewById(R.id.student_id);
        mSchoolName = (TextInputEditText) v.findViewById(R.id.school_name);
        mSchoolAddress = (TextInputEditText) v.findViewById(R.id.school_address);
        mStudentPicture = (CircleImageView) v.findViewById(R.id.student_picture);
        mFrameLayout = (FrameLayout) v.findViewById(R.id.student_picture_frame);
        mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPickerFragment photoPickerFragment = PhotoPickerFragment.newInstance();
                photoPickerFragment.setTargetFragment(StudentDetailFragment.this,REQUEST_DATE);
                photoPickerFragment.show(getFragmentManager(),"Photo Picker");

            }
        });
        args = getArguments();
        String studentId = args.getString("studentid");
        if(studentId !=null){
            mStudentReference = FirebaseUtil.getStudentsRef().child(studentId);
            setupUI(mStudentReference);
        }
        else{
            String studentUUID = UUID.randomUUID().toString();
            mStudentId.setText(studentUUID);
        }


        alertDialog.setView(v)
                .setPositiveButton(R.string.save_txt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String studentId = args.getString("studentid");
                        if(studentId!=null){
                            updateDataToFireBase(studentId);
                        }
                        else{
                            createNewStudent();
                        }

                    }
                })
                .setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return alertDialog.create();
    }

    private void createNewStudent(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Adding Student");
        progressDialog.show();
        parentStudentReference = FirebaseUtil.getStudentsRef();
        newStudent = new Student();
        newStudent.setSchoolAddress(mSchoolAddress.getText().toString());
        newStudent.setSchoolName(mSchoolName.getText().toString());
        newStudent.setStudentName(mStudentName.getText().toString());
        newStudent.setStudentUUID(mStudentId.getText().toString());
        if(mPhotoFilePath!=null){
            Uri file = mPhotoFilePath;
            StorageReference photoRef = FirebaseUtil.getStudentPhotoRef(file.getLastPathSegment());
            newStudent.setStudentPicName(file.getLastPathSegment());
            mUploadTask = photoRef.putFile(file);
            mUploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e(TAG,"File upload failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG,"File upload successfully");
                    parentStudentReference.child(mStudentId.getText().toString()).setValue(newStudent);
                    progressDialog.dismiss();
                }
            });
        }




    }

    private void updateDataToFireBase(String studentId){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating Student. Please wait...");
        progressDialog.show();
        studentRef = FirebaseUtil.getStudentsRef().child(studentId);
        final Student updateStudent = student;
        updateStudent.setSchoolAddress(mSchoolAddress.getText().toString());
        updateStudent.setSchoolName(mSchoolName.getText().toString());
        updateStudent.setStudentName(mStudentName.getText().toString());
        updateStudent.setStudentUUID(mStudentId.getText().toString());

        if(isPhotoUpdated){
            if(mPhotoFilePath!=null){
                Uri file = mPhotoFilePath;
                StorageReference photoRef = FirebaseUtil.getStudentPhotoRef(file.getLastPathSegment());
                updateStudent.setStudentPicName(file.getLastPathSegment());
                mUploadTask = photoRef.putFile(file);
                mUploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.e(TAG,"File upload failed");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d(TAG,"File upload successfully");
                        studentRef.setValue(updateStudent);
                        progressDialog.dismiss();
                    }
                });
            }


        }

    }

    private void setupUI(DatabaseReference studentReference) {

        studentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student = dataSnapshot.getValue(Student.class);
                mStudentName.setText(student.getStudentName());
                mStudentId.setText(student.getStudentUUID());
                //Set School details and Student picture using Glide.
                mSchoolAddress.setText(student.getSchoolAddress());
                mSchoolName.setText(student.getSchoolName());

                if(student.getStudentPicName()!=null){
                    StorageReference photoReference = FirebaseUtil.getStudentPhotoRef(student.getStudentPicName());
                    Glide.with(getActivity() /* context */)
                            .using(new FirebaseImageLoader())
                            .load(photoReference)
                            .into(mStudentPicture);
                }
                else{
                    mStudentPicture.setImageResource(R.drawable.ic_person_black_24dp);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            mPhotoFilePath = (Uri) data.getExtras().get("photopath");
            Log.d(TAG,"photo path--> "+mPhotoFilePath);
            mStudentPicture.setImageURI(mPhotoFilePath);
            isPhotoUpdated=true;
        }
    }
}
