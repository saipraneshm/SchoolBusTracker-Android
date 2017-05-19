package com.sjsu.edu.schoolbustracker.parentuser.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.common.model.TripDetails;

/**
 * Created by sai pranesh on 13-Apr-17.
 */

public class TripsFirebaseRecyclerAdapter extends
        FirebaseRecyclerAdapter<TripDetails, TripsFirebaseRecyclerAdapter.TripViewHolder>
        implements OnMapReadyCallback{

    Context context;
    GoogleMap mGoogleMap;
    UiSettings mUiSettings;
    MapView mMapView;
    private static OnItemClickListener listener;

    public TripsFirebaseRecyclerAdapter(DatabaseReference dataRef, Context context){
        super(TripDetails.class, R.layout.previous_trips_cardview, TripViewHolder.class, dataRef );
        this.context = context;
    }


    public interface OnItemClickListener{
        void onItemClick(String tripTimeStamp);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    protected void populateViewHolder(TripViewHolder holder, TripDetails model, int position) {

       if(model != null){
           boolean hasTag = holder.mCardView.getTag() != null;
           //NamedLocation item = mNamedLocations[position];
           mMapView = holder.mMapView;
           if(hasTag){
               holder = (TripViewHolder) holder.mCardView.getTag();
           }else{
               holder.bindView(model);
               if(holder.mMapView != null){
                   holder.mMapView.onCreate(null);
                   holder.mMapView.getMapAsync(this);
               }
           }

           if(holder.mMapView != null)
                holder.mMapView.setTag(model);
           if(holder.mMap != null){
               setMapLocation(holder.mMap, model);
           }
           holder.mDate.setText(model.getDate());
       }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(context);
        mGoogleMap = googleMap;
        mUiSettings = mGoogleMap.getUiSettings();
        mUiSettings.setMapToolbarEnabled(false);
        TripDetails data = (TripDetails) mMapView.getTag();
        if( data != null){
            setMapLocation(mGoogleMap, data);
        }
    }

    private static void setMapLocation(GoogleMap map, TripDetails data){
        // Add a marker for this item and set the camera
        LatLng source = new LatLng(data.getSourceCoordinates().getLat(),data.getSourceCoordinates().getLng());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom( source, 13f));
        map.addMarker(new MarkerOptions().position(source));

        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    static class TripViewHolder extends RecyclerView.ViewHolder{

        TextView mDate, mViewDetails;
        MapView mMapView;
        GoogleMap mMap;
        UiSettings mUiSettings;
        CardView mCardView;

        public TripViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.previous_trips_card_view);
            mDate = (TextView)itemView.findViewById(R.id.date_tv);
            mViewDetails = (TextView)itemView.findViewById(R.id.view_details_tv);
            mMapView = (MapView)itemView.findViewById(R.id.map_view);
            mMapView.setClickable(false);
        }

        void bindView(final TripDetails tripDetails) {
            mCardView.setTag(this);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(tripDetails.getTimestamp());
                }
            });
        }



    }
}



