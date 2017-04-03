package com.sjsu.edu.schoolbustracker.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sjsu.edu.schoolbustracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripsFragment extends Fragment{


    private GoogleMap mMap;
    private RecyclerView mRecyclerView;

    public TripsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_trips);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new TripsRecyclerViewAdapter());
        return view;
    }


    private class TripsRecyclerViewAdapter extends RecyclerView.Adapter<TripsRecyclerViewAdapter.TripsViewHolder>{

        private String[] placeholders;

        public TripsRecyclerViewAdapter(){
            placeholders = new String[]{ "Date", "ViewDetails"};
        }

        @Override
        public TripsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.previous_trips_cardview,parent,false);
            TripsViewHolder tripsViewHolder = new TripsViewHolder(view);
            return tripsViewHolder;
        }

        @Override
        public void onBindViewHolder(TripsViewHolder holder, int position) {
            holder.mDate.setText(placeholders[0]);
            holder.mViewDetails.setText(placeholders[1]);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class TripsViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

            AppCompatButton mDate, mViewDetails;
            MapView mMapView;
            GoogleMap mMap;

            TripsViewHolder(View itemView) {
                super(itemView);

                mDate = (AppCompatButton)itemView.findViewById(R.id.date_button);
                mViewDetails = (AppCompatButton)itemView.findViewById(R.id.view_details_button);
                mMapView = (MapView)itemView.findViewById(R.id.map_view);

                mMapView.getMapAsync(this);
            }



            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                LatLng sydney = new LatLng(-34, 151);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        }
    }
}
