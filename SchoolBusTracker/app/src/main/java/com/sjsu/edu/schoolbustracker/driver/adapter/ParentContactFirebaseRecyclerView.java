package com.sjsu.edu.schoolbustracker.driver.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by akshaymathur on 5/18/17.
 */

public class ParentContactFirebaseRecyclerView extends FirebaseRecyclerAdapter<String,ParentContactFirebaseRecyclerView.StudentsOnRoute> {


    public ParentContactFirebaseRecyclerView(DatabaseReference ref) {
        super(String.class, R.layout.student_list_view, StudentsOnRoute.class, ref);
    }

    @Override
    protected void populateViewHolder(StudentsOnRoute viewHolder, String model, int position) {
        viewHolder.bindView(model);
    }

    public static class StudentsOnRoute extends RecyclerView.ViewHolder {

        private CircleImageView mStudentImageView;
        private TextView mStudentName;

        public StudentsOnRoute(View itemView) {
            super(itemView);
            mStudentImageView = (CircleImageView) itemView.findViewById(R.id.driver_contact_student_image);
            mStudentName = (TextView) itemView.findViewById(R.id.driver_contact_student_name);
        }

        void bindView(String studentId){
            DatabaseReference mStudentDetailRef = FirebaseUtil.getS
        }
    }
}
