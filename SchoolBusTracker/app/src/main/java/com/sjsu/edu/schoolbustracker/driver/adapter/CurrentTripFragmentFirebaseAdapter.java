package com.sjsu.edu.schoolbustracker.driver.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.common.model.ParentUsers;
import com.sjsu.edu.schoolbustracker.common.model.StudentDetail;
import com.sjsu.edu.schoolbustracker.common.model.StudentTemp;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * Created by sai pranesh on 12-May-17.
 * This is a generic adapter that is used by Current trip and Contact fragment of driver
 */

public class CurrentTripFragmentFirebaseAdapter extends FirebaseRecyclerAdapter<StudentTemp,
        CurrentTripFragmentFirebaseAdapter.CurrentTripViewHolder> {

    private static final String TAG = CurrentTripFragmentFirebaseAdapter.class.getSimpleName();
    private Context mContext;
    public OnCurrentTripClickListener mOnCurrentTripClickListener;

    public interface OnCurrentTripClickListener{
        void onTripCompleted(View view,int pos);
    }
    public CurrentTripFragmentFirebaseAdapter(Context context, int modelLayout,
                                              Class<CurrentTripViewHolder> viewHolderClass,
                                              Query ref) {
        super(StudentTemp.class , modelLayout, viewHolderClass, ref);
        mContext = context;
    }


    @Override
    protected void populateViewHolder(CurrentTripViewHolder viewHolder, final StudentTemp model,
                                      int position) {
        viewHolder.bindView(mContext, position, model, mOnCurrentTripClickListener);

    }

    public void setOnCurrentTripClickListener(OnCurrentTripClickListener onCurrentTripClickListener) {
        mOnCurrentTripClickListener = onCurrentTripClickListener;
    }

    public static class CurrentTripViewHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView mStudentNameTv, mAddressTv, mCurrentTripTv;
        private AppCompatButton mStartNavBtn, mCompletedTripBtn;
        private CardView mCardView;

        //Related to Database and models
        private DatabaseReference mRegisteredChildRef, mParentUserRef, mPlannedRouteRef;
        private StudentDetail studentDetail;
        private ParentUsers mParentUsers;


        public CurrentTripViewHolder(View itemView) {
            super(itemView);
            mStudentNameTv = (AppCompatTextView) itemView.findViewById(R.id.studentNameTv);
            mAddressTv = (AppCompatTextView) itemView.findViewById(R.id.addressTv);
            mCurrentTripTv = (AppCompatTextView) itemView.findViewById(R.id.currentTripTv);
            mStartNavBtn = (AppCompatButton) itemView.findViewById(R.id.startTripBtn);
            mCompletedTripBtn = (AppCompatButton) itemView.findViewById(R.id.completedTripBtn);
            mCardView = (CardView) itemView.findViewById(R.id.currentTripCardView);

            //Firebase related references
            mRegisteredChildRef = FirebaseUtil.getRegisteredChildrenRef();
            mParentUserRef = FirebaseUtil.getParentUserRef();
            mPlannedRouteRef = FirebaseUtil.getPlannedRoutesDummyRef();
        }

        void bindView(final Context context, final int position, final StudentTemp model,
                      final OnCurrentTripClickListener listener){
            if(getAdapterPosition() == 0){
                mCurrentTripTv.setText(context.getResources()
                        .getString(R.string.current_trip_text));
                mCardView.setBackgroundColor(Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mCardView.setElevation(12);
                }
                mStartNavBtn.setVisibility(View.VISIBLE);
                mStartNavBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri intentUri = Uri.parse("google.navigation:q=" +
                                        Uri.encode(mAddressTv.getText().toString()));

                        //Geocoding - Converting address into latitude and longitude, remember to set min,mx lat and lng
                        //incomplete implementation of the feature.
                        /*Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(mAddressTv.getText().toString(),1);
                            for(Address adr : addresses){
                                Log.d(TAG, "Addresses geo coding them " + adr.getLatitude() + ", " + adr.getLongitude());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        context.startActivity(mapIntent);
                        mStartNavBtn.setVisibility(View.GONE);
                        mCompletedTripBtn.setVisibility(View.VISIBLE);
                    }
                });

                mCompletedTripBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPlannedRouteRef.child( model.getKey()).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG," completed the task of removing the item " +
                                        task.isSuccessful());
                                listener.onTripCompleted(itemView, (getAdapterPosition() + 1));
                            }
                        });
                    }
                });
            }else{

                mCardView.setClickable(false);
                mCardView.setBackgroundColor(context.getResources()
                        .getColor(R.color.com_facebook_button_background_color_disabled));
            }
            mRegisteredChildRef.child(model.getStudent_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    studentDetail = dataSnapshot.child("student_details")
                                            .getValue(StudentDetail.class);
                   if(studentDetail != null){
                       mStudentNameTv.setText(studentDetail.getStudentName());
                       mParentUserRef.child(studentDetail.getParentId())
                               .addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               mParentUsers = dataSnapshot.getValue(ParentUsers.class);
                               mAddressTv.setText(mParentUsers.getHouseAddress());
                           }
                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });
                   }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
