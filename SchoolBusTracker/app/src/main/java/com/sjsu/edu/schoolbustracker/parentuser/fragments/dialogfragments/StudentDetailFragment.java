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
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.common.model.School;
import com.sjsu.edu.schoolbustracker.common.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by akshaymathur on 4/15/17.
 */

public class StudentDetailFragment extends DialogFragment {

    private TextInputEditText mStudentName,mStudentId,mSchoolName,mSchoolAddress,mRouteNumber;
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
    private AppCompatSpinner mSchoolSpinner;
    private Map<String,School> schoolMap;
    private ArrayList<String> schools;
    private ArrayList<String> schoolIds;

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
        mSchoolSpinner = (AppCompatSpinner) v.findViewById(R.id.school_spinner);
        mRouteNumber = (TextInputEditText) v.findViewById(R.id.route_number_edittext);
        mSchoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSchoolName.setText(schoolMap.get(schoolIds.get(i)).getSchoolName());
                mSchoolAddress.setText(schoolMap.get(schoolIds.get(i)).getSchoolAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPickerFragment photoPickerFragment = PhotoPickerFragment.newInstance();
                photoPickerFragment.setTargetFragment(StudentDetailFragment.this,REQUEST_DATE);
                photoPickerFragment.show(getFragmentManager(),"Photo Picker");

            }
        });
        args = getArguments();
        schoolMap = new HashMap<>();
        schools = new ArrayList<>();
        schoolIds = new ArrayList<>();
        fetchAllSchools();


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

    private void fetchAllSchools() {
        DatabaseReference schoolsRef = FirebaseUtil.getAllSchoolsRef();
        schoolsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> d =dataSnapshot.getChildren();
                for (DataSnapshot data:d) {
                    Log.d(TAG,"School ID--> "+data.getKey());
                    School school = data.getValue(School.class);
                    Log.d(TAG,"School Name--> "+school.getSchoolName());

                    schools.add(school.getSchoolName());
                    schoolIds.add(school.getSchoolId());
                    schoolMap.put(school.getSchoolId(),school);

                }
                ArrayAdapter<String> schoolsAdapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,schools);
                mSchoolSpinner.setAdapter(schoolsAdapter);
                String studentId = args.getString("studentid");
                if(studentId !=null){
                    mStudentReference = FirebaseUtil.getStudentsRef().child(studentId);
                    setupUI(mStudentReference);
                }
                else{
                    String studentUUID = UUID.randomUUID().toString();
                    mStudentId.setText(studentUUID);
                    mRouteNumber.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createNewStudent(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Adding Student. Please wait...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        parentStudentReference = FirebaseUtil.getStudentsRef();
        newStudent = new Student();
        newStudent.setSchoolAddress(mSchoolAddress.getText().toString());
        newStudent.setSchoolName(mSchoolName.getText().toString());
        newStudent.setStudentName(mStudentName.getText().toString());
        newStudent.setStudentUUID(mStudentId.getText().toString());
        newStudent.setSchoolId(schoolMap.get(schoolIds.get(mSchoolSpinner.getSelectedItemPosition())).getSchoolId());
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
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests")
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.
                            getTotalByteCount();
                    Log.d(TAG,"Upload is " + progress + "% done");
                    progressDialog.setProgress((int) progress);

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG,"File upload successfully");
                    parentStudentReference.child(mStudentId.getText().toString()).setValue(newStudent);
                    addStudentToSchool(newStudent.getSchoolId(),newStudent.getStudentUUID());
                    progressDialog.dismiss();
                }
            });
        }




    }

    private void updateDataToFireBase(String studentId){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating Student. Please wait...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        studentRef = FirebaseUtil.getStudentsRef().child(studentId);
        final Student updateStudent = student;
        updateStudent.setSchoolAddress(mSchoolAddress.getText().toString());
        updateStudent.setSchoolName(mSchoolName.getText().toString());
        updateStudent.setStudentName(mStudentName.getText().toString());
        updateStudent.setStudentUUID(mStudentId.getText().toString());
        Log.d(TAG,"before set old school-->"+student.getSchoolId());
        Log.d(TAG,"before set new school-->"+updateStudent.getSchoolId());
        final String oldSchoolId = updateStudent.getSchoolId();
        updateStudent.setSchoolId(schoolMap.get(schoolIds.get(mSchoolSpinner.getSelectedItemPosition())).getSchoolId());
        Log.d(TAG,"before update old school-->"+oldSchoolId);
        Log.d(TAG,"before update new school-->"+updateStudent.getSchoolId());
        if(isPhotoUpdated){
            if(mPhotoFilePath!=null){
                progressDialog.show();
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
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests")
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) /
                                taskSnapshot.getTotalByteCount();
                        Log.d(TAG,"Upload is " + progress + "% done");
                        progressDialog.setProgress((int) progress);

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d(TAG,"File upload successfully");
                        studentRef.setValue(updateStudent);
                        updateStudentSchool(oldSchoolId,updateStudent.getSchoolId(),updateStudent.getStudentUUID());
                        progressDialog.dismiss();
                    }
                });
            }


        }
        else {
            studentRef.setValue(updateStudent);
            updateStudentSchool(oldSchoolId,updateStudent.getSchoolId(),updateStudent.getStudentUUID());

        }

    }

    private void setupUI(DatabaseReference studentReference) {

        studentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                student = dataSnapshot.getValue(Student.class);
                mStudentName.setText(student.getStudentName());
                mStudentId.setText(student.getStudentUUID());
                mSchoolSpinner.setSelection(schoolIds.indexOf(student.getSchoolId()));
                //Set School details and Student picture using Glide.
                mSchoolAddress.setText(student.getSchoolAddress());
                mSchoolName.setText(student.getSchoolName());
                mRouteNumber.setText(student.getRouteNumber() == null ? getString(R.string.route_ask_school):student.getRouteNumber());
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

    private void updateStudentSchool(final String oldSchoolId, final String newSchoolId, final String studentId){
        Log.d(TAG,"new schoolid ---> "+newSchoolId);
        Log.d(TAG,"old schoolid ---> "+oldSchoolId);
        final DatabaseReference oldSchoolRef = FirebaseUtil.getSchoolRef(oldSchoolId);
        final DatabaseReference newSchoolRef = FirebaseUtil.getSchoolRef(newSchoolId);
        oldSchoolRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                School school = dataSnapshot.getValue(School.class);
                Map<String,String>  students = school.getRegisteredStudents();
                Log.d(TAG,"/*** Old School ***/");
                if(students!=null){
                    Log.d(TAG,"Removing old School-->"+oldSchoolId);
                    students.remove(studentId);
                    school.setRegisteredStudents(students);
                    oldSchoolRef.setValue(school);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        newSchoolRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                School school = dataSnapshot.getValue(School.class);
                Map<String,String>  students = school.getRegisteredStudents();
                if(students!=null){
                    Log.d(TAG,"Adding new School-->"+newSchoolId);
                    students.put(studentId,studentId);
                    school.setRegisteredStudents(students);
                    newSchoolRef.setValue(school);
                }
                else{
                    students = new HashMap<String, String>();
                    students.put(studentId, studentId);
                    school.setRegisteredStudents(students);
                    newSchoolRef.setValue(school);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addStudentToSchool(String schoolId, final String studentId){
        final DatabaseReference schoolRef = FirebaseUtil.getSchoolRef(schoolId);
        schoolRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                School school = dataSnapshot.getValue(School.class);
                Map<String,String>  students = school.getRegisteredStudents();
                if(students!=null) {
                    Log.d(TAG, students.keySet().toString());
                    students.put(studentId, studentId);
                    school.setRegisteredStudents(students);
                    schoolRef.setValue(school);
                }
                else{
                    students = new HashMap<String, String>();
                    students.put(studentId, studentId);
                    school.setRegisteredStudents(students);
                    schoolRef.setValue(school);
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
