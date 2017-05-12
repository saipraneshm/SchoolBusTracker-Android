package com.sjsu.edu.schoolbustracker.parentuser.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.model.Student;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sai pranesh on 12-May-17.
 */

public class StudentHolder extends RecyclerView.ViewHolder {

    private final CircleImageView mStudentImage;
    private final LinearLayout mHighlighter;
    static View prevView;

    public StudentHolder(View itemView) {
        super(itemView);
        mStudentImage = (CircleImageView) itemView.findViewById(R.id.student_image_holder);
        mHighlighter = (LinearLayout) itemView.findViewById(R.id.recycler_view_background);
        prevView = null;
    }

    void bindView(final Student student, Context context, final StudentFirebaseRecyclerAdapter.OnItemClickListener listener, final boolean highlightSelected){
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
                if(highlightSelected)
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
        mHighlighter.setSelected(true);
        itemView.setTag(R.id.recycler_view_background, itemView);
        prevView = itemView;

    }
}
