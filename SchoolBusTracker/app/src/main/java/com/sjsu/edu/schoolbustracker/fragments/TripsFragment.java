package com.sjsu.edu.schoolbustracker.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sjsu.edu.schoolbustracker.R;

import java.util.HashSet;

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
        mRecyclerView.setAdapter(new TripsRecyclerViewAdapter(getActivity() , LIST_LOCATIONS));


        return view;
    }


    private static class NamedLocation{
        public final String name;
        public final LatLng location;
        public final String date;

        NamedLocation(String name , LatLng location, String date){
            this.name = name;
            this.location = location;
            this.date = date;
        }
    }

    private static void setMapLocation(GoogleMap map, NamedLocation data){
        // Add a marker for this item and set the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.location, 13f));
        map.addMarker(new MarkerOptions().position(data.location));

        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private class TripsRecyclerViewAdapter extends RecyclerView.Adapter<TripsViewHolder>{

        private final HashSet<MapView> mMaps = new HashSet<>();

        private Context mContext;
        private NamedLocation[] mNamedLocations;
        public TripsRecyclerViewAdapter(Context context , NamedLocation[] namedLocations){
            mNamedLocations = namedLocations;
            mContext = context;
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

            boolean hasTag = holder.mCardView.getTag() != null;
            NamedLocation item = mNamedLocations[position];
            if(hasTag){
                holder = (TripsViewHolder) holder.mCardView.getTag();
            }else{
                mMaps.add(holder.mMapView);
                holder.bindView(item);
            }

            holder.mMapView.setTag(item);
            if(holder.mMap != null){
                setMapLocation(holder.mMap, item);
            }
            holder.mDate.setText(item.name);

        }

        @Override
        public int getItemCount() {
            return mNamedLocations.length;
        }

    }

    class TripsViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        AppCompatButton mDate, mViewDetails;
        MapView mMapView;
        GoogleMap mMap;
        CardView mCardView;

        TripsViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.previous_trips_card_view);
            mDate = (AppCompatButton)itemView.findViewById(R.id.date_button);
            mViewDetails = (AppCompatButton)itemView.findViewById(R.id.view_details_button);
            mMapView = (MapView)itemView.findViewById(R.id.map_view);


        }

        void bindView(NamedLocation namedLocation){
            mCardView.setTag(this);
            this.initializeMapView();
        }

        public void initializeMapView(){
            if(mMapView != null){
                mMapView.onCreate(null);
                mMapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getActivity());
            mMap = googleMap;
            NamedLocation data = (NamedLocation) mMapView.getTag();
            if( data != null){
                setMapLocation(mMap, data);
            }

        }
    }

    private static final NamedLocation[] LIST_LOCATIONS = new NamedLocation[]{
            new NamedLocation("Cape Town", new LatLng(-33.920455, 18.466941), "Date"),
            new NamedLocation("Beijing", new LatLng(39.937795, 116.387224), "Date"),
            new NamedLocation("Bern", new LatLng(46.948020, 7.448206), "Date"),
            new NamedLocation("Breda", new LatLng(51.589256, 4.774396), "Date"),
            new NamedLocation("Brussels", new LatLng(50.854509, 4.376678), "Date"),
            new NamedLocation("Copenhagen", new LatLng(55.679423, 12.577114), "Date"),
            new NamedLocation("Hannover", new LatLng(52.372026, 9.735672), "Date"),
            new NamedLocation("Helsinki", new LatLng(60.169653, 24.939480), "Date"),
            new NamedLocation("Hong Kong", new LatLng(22.325862, 114.165532), "Date"),
            new NamedLocation("Istanbul", new LatLng(41.034435, 28.977556), "Date"),
            new NamedLocation("Johannesburg", new LatLng(-26.202886, 28.039753), "Date"),
            new NamedLocation("Lisbon", new LatLng(38.707163, -9.135517), "Date"),
            new NamedLocation("London", new LatLng(51.500208, -0.126729), "Date"),
            new NamedLocation("Madrid", new LatLng(40.420006, -3.709924), "Date"),
            new NamedLocation("Mexico City", new LatLng(19.427050, -99.127571), "Date"),
            new NamedLocation("Moscow", new LatLng(55.750449, 37.621136), "Date"),
            new NamedLocation("New York", new LatLng(40.750580, -73.993584), "Date"),
            new NamedLocation("Oslo", new LatLng(59.910761, 10.749092), "Date"),
            new NamedLocation("Paris", new LatLng(48.859972, 2.340260), "Date"),
            new NamedLocation("Prague", new LatLng(50.087811, 14.420460), "Date"),
            new NamedLocation("Rio de Janeiro", new LatLng(-22.90187, -43.232437), "Date"),
            new NamedLocation("Rome", new LatLng(41.889998, 12.500162), "Date"),
            new NamedLocation("Sao Paolo", new LatLng(-22.863878, -43.244097), "Date"),
            new NamedLocation("Seoul", new LatLng(37.560908, 126.987705), "Date"),
            new NamedLocation("Stockholm", new LatLng(59.330650, 18.067360), "Date"),
            new NamedLocation("Sydney", new LatLng(-33.873651, 151.2068896), "Date"),
            new NamedLocation("Taipei", new LatLng(25.022112, 121.478019), "Date"),
            new NamedLocation("Tokyo", new LatLng(35.670267, 139.769955), "Date"),
            new NamedLocation("Tulsa Oklahoma", new LatLng(36.149777, -95.993398), "Date"),
            new NamedLocation("Vaduz", new LatLng(47.141076, 9.521482), "Date"),
            new NamedLocation("Vienna", new LatLng(48.209206, 16.372778), "Date"),
            new NamedLocation("Warsaw", new LatLng(52.235474, 21.004057), "Date"),
            new NamedLocation("Wellington", new LatLng(-41.286480, 174.776217), "Date"),
            new NamedLocation("Winnipeg", new LatLng(49.875832, -97.150726), "Date")
    };
}
