package com.sjsu.edu.schoolbustracker.parentuser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private static OnItemClickListener listener;
    private final static String TAG = "StudentFBRecyclerAdptr";
    private static boolean mHighlightSelected;


    public StudentFirebaseRecyclerAdapter(DatabaseReference databaseReference,Context context,boolean highlightSelected){
        super(Student.class, R.layout.student_recycler_view,StudentHolder.class,databaseReference);
        this.mContext = context;
        this.mHighlightSelected = highlightSelected;
    }

    public interface OnItemClickListener{
        void onItemClick(String studentId);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    @Override
    protected void populateViewHolder(StudentHolder viewHolder, Student model, int position) {
        viewHolder.bindView(model,mContext);
    }

    @Override
    public void onBindViewHolder(StudentHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
    }


    public static class StudentHolder extends RecyclerView.ViewHolder {
        private final CircleImageView mStudentImage;
        private final LinearLayout mybackground;
        private static View prevView;

        public StudentHolder(View itemView) {
            super(itemView);
            mStudentImage = (CircleImageView) itemView.findViewById(R.id.student_image_holder);
            mybackground = (LinearLayout) itemView.findViewById(R.id.recycler_view_background);
            prevView = null;
        }

        void bindView(final Student student, Context context){
            if(student.getStudentPicName()!=null){
                StorageReference photoReference = FirebaseUtil.getStudentPhotoRef(student.getStudentPicName());

                Glide.with(context /* context */)
                        .using(new FirebaseImageLoader())
                        .load(photoReference)
                        .into(mStudentImage);
            }
            else{
                mStudentImage.setImageResource(R.drawable.ic_person_black_24dp);
            }
            mStudentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mHighlightSelected)
                        changeSelectedItem();
                    listener.onItemClick(student.getStudentUUID());
                }
            });

        }

        private void changeSelectedItem(){
            if(prevView!=null){
                LinearLayout ll =(LinearLayout) prevView.findViewById(R.id.recycler_view_background);
                ll.setSelected(false);
            }
            mybackground.setSelected(true);
            prevView = itemView;

        }
    }
}



