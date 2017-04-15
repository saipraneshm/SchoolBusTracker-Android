package com.sjsu.edu.schoolbustracker.parentuser.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.helperclasses.QueryPreferences;
import com.sjsu.edu.schoolbustracker.parentuser.model.TripDetails;

public class TripDetailActivity extends AppCompatActivity implements OnMapReadyCallback{


    TextView mDateTextView, mSourceTextView, mDestinationTextView;
    public static final String TRIP_TIMESTAMP = "trip_timestamp";
    private String mTripTimestamp;
    private DatabaseReference mCurrentTripRef;
    private TripDetails mCurrentTripDetail;
    private static final String TAG  = "TripDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        if(getSupportActionBar()!= null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       // mMapView = (MapView) findViewById(R.id.google_map_view);
        final SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_fragment);


        mDateTextView = (TextView) findViewById(R.id.date_tv);
        mSourceTextView = (TextView) findViewById(R.id.source_address_tv);
        mDestinationTextView = (TextView) findViewById(R.id.destination_address_tv);
        mTripTimestamp = getIntent().getStringExtra(TRIP_TIMESTAMP);
        mCurrentTripRef = FirebaseUtil.getExisitingPreviousTripsRef().child(mTripTimestamp).getRef();

        mCurrentTripRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentTripDetail = dataSnapshot.getValue(TripDetails.class);
                if(mCurrentTripDetail != null){
                    mapFragment.getMapAsync(TripDetailActivity.this);
                    mDateTextView.setText(mCurrentTripDetail.getDate());
                    mSourceTextView.setText(mCurrentTripDetail.getSource());
                    mDestinationTextView.setText(mCurrentTripDetail.getDestination());
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng currentLatLng = new LatLng(mCurrentTripDetail.getSourceCoordinates().getLat(), mCurrentTripDetail.getSourceCoordinates().getLng());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom( currentLatLng , 13));
        googleMap.addMarker(new MarkerOptions().position(currentLatLng));

        // Set the map type back to normal.
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                Log.d(TAG,"Back button pressed");
               // QueryPreferences.setTripDetailsNavRef(TripDetailActivity.this,true);
               // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
