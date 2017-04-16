package com.sjsu.edu.schoolbustracker.parentuser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.model.Student;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by akshaymathur on 4/15/17.
 */

public class StudentFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Student,StudentFirebaseRecyclerAdapter.StudentHolder>{

    Context mContext;

    public StudentFirebaseRecyclerAdapter(DatabaseReference databaseReference,Context context){
        super(Student.class, R.layout.student_recycler_view,StudentHolder.class,databaseReference);
        this.mContext = context;

    }

    @Override
    protected void populateViewHolder(StudentHolder viewHolder, Student model, int position) {
        viewHolder.bindView(model,mContext);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    public static class StudentHolder extends RecyclerView.ViewHolder {
        private final CircleImageView mStudentImage;

        public StudentHolder(View itemView) {
            super(itemView);
            mStudentImage = (CircleImageView) itemView.findViewById(R.id.student_image_holder);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });
        }

        void bindView(Student student, Context context){

            StorageReference photoReference = FirebaseUtil.getStudentPhotoRef(student.getStudentPicName());

            Glide.with(context /* context */)
                    .using(new FirebaseImageLoader())
                    .load(photoReference)
                    .into(mStudentImage);

        }
    }
}



