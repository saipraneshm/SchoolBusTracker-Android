package com.sjsu.edu.schoolbustracker.driver.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.driver.adapter.CurrentTripFragmentFirebaseAdapter;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentTripFragment extends Fragment {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private static final String TAG = CurrentTripFragment.class.getSimpleName();
    private CurrentTripFragmentFirebaseAdapter currentTripFragmentFirebaseAdapter;

    public CurrentTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_trip, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.currentTripToolbar);
        mToolbar.setTitle(R.string.title_trip_list);
        mToolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(),R.color.black, null));

/*       FirebaseUtil.getPlannedRoutesDummyRef().addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.getChildren() + " ");
               *//* for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                  //  Log.d(TAG, " Checking the value of children "
                       //     + "  "  +  snapshot.getValue());
                }*//*
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        mRecyclerView = (RecyclerView) view.findViewById(R.id.currentTripRv);
        final LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(lm);
        currentTripFragmentFirebaseAdapter =
                new CurrentTripFragmentFirebaseAdapter(getActivity(),R.layout.adapter_current_trip,
                        CurrentTripFragmentFirebaseAdapter.CurrentTripViewHolder.class,
                        FirebaseUtil.getPlannedRoutesDummyRef());
        currentTripFragmentFirebaseAdapter.setOnCurrentTripClickListener(new
                CurrentTripFragmentFirebaseAdapter.OnCurrentTripClickListener(){
            @Override
            public void onTripCompleted(View view, final int pos) {
                Log.d(TAG, pos + " has been called on trip completed");
                currentTripFragmentFirebaseAdapter.notifyItemChanged(pos);
                //view = lm.findViewByPosition(pos);

            }
        });
        mRecyclerView.setAdapter(currentTripFragmentFirebaseAdapter);
        return view;
    }

}
