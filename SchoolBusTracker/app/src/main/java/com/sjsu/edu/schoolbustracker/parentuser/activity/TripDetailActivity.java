package com.sjsu.edu.schoolbustracker.parentuser.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.parentuser.model.TripDetails;

public class TripDetailActivity extends AppCompatActivity {


    TextView mDateTextView, mSourceTextView, mDestinationTextView;
    public static final String TRIP_TIMESTAMP = "trip_timestamp";
    private String mTripTimestamp;
    private DatabaseReference mCurrentTripRef;
    private TripDetails mCurrenTripDetail;
    private static final String TAG  = "TripDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        mDateTextView = (TextView) findViewById(R.id.date_tv);
        mSourceTextView = (TextView) findViewById(R.id.source_address_tv);
        mDestinationTextView = (TextView) findViewById(R.id.destination_address_tv);
        mTripTimestamp = getIntent().getStringExtra(TRIP_TIMESTAMP);
        mCurrentTripRef = FirebaseUtil.getExisitingPreviousTripsRef().child(mTripTimestamp).getRef();
        mCurrentTripRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrenTripDetail = dataSnapshot.getValue(TripDetails.class);
                if(mCurrenTripDetail != null){
                    mDateTextView.setText(mCurrenTripDetail.getDate());
                    mSourceTextView.setText(mCurrenTripDetail.getSource());
                    mDestinationTextView.setText(mCurrenTripDetail.getDestination());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static Intent newInstance(Context context, String tripTimeStamp){
        Intent intent = new Intent(context, TripDetailActivity.class);
        intent.putExtra(TRIP_TIMESTAMP,tripTimeStamp);
        return intent;
    }
}
