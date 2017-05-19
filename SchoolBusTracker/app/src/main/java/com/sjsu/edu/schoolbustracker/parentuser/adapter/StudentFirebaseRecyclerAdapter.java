package com.sjsu.edu.schoolbustracker.parentuser.adapter;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.common.model.Student;

/**
 * Created by akshaymathur on 4/15/17.
 */

public class StudentFirebaseRecyclerAdapter extends FirebaseRecyclerAdapter<Student, StudentHolder>{

    private Context mContext;
    private static OnItemClickListener sListener;
    private final static String TAG = StudentFirebaseRecyclerAdapter.class.getSimpleName();
    private static boolean sHighlightSelected;

    public StudentFirebaseRecyclerAdapter(DatabaseReference databaseReference,Context context,boolean highlightSelected){
        super(Student.class, R.layout.student_recycler_view,StudentHolder.class,databaseReference);
        this.mContext = context;
        sHighlightSelected = highlightSelected;
    }

    public interface OnItemClickListener{
        void onItemClick(String studentId, int position);
        void getPrevPos(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        sListener = listener;
    }

    @Override
    protected void populateViewHolder(StudentHolder viewHolder, Student model, int position) {
        viewHolder.bindView(model,mContext, sListener, sHighlightSelected);
    }

}



