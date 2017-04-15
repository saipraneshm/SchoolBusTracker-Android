package com.sjsu.edu.schoolbustracker.parentuser.fragments;

import android.content.Context;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sjsu.edu.schoolbustracker.R;
import com.sjsu.edu.schoolbustracker.helperclasses.CustomFragmentPagerAdapter;
import com.sjsu.edu.schoolbustracker.parentuser.activity.MainActivity;
import com.sjsu.edu.schoolbustracker.helperclasses.ActivityHelper;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.AccountSettingsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.NotificationSettingsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.ProfileInfoFragment;
import com.sjsu.edu.schoolbustracker.parentuser.model.ParentUsers;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Firebase
    private FirebaseAuth mAuth;


    private Toolbar mToolbar;

    private OnFragmentInteractionListener mListener;
    private final String TAG = "UserProfileFragment";
    private GoogleApiClient mGoogleApiClient;
    private CircleImageView mParentImageView;

    //CollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    TabLayout mTabLayout;


    private AppCompatTextView mProfileName,mProfileNumber;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference parentProfileRef;
    private String mUserUID;
    private ParentUsers parentUser;


    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Log.d(TAG,"Creating views");
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        setHasOptionsMenu(true);


        mToolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        mToolbar.setTitle("Profile");
        mToolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(),R.color.cardview_light_background, null));
        mToolbar.inflateMenu(R.menu.appbar_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.logout_btn:
                        LoginManager.getInstance().logOut();
                        mAuth.signOut();
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build();
                        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                                .enableAutoManage(getActivity() /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) getActivity() /* OnConnectionFailedListener */)
                                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                .build();
                        mGoogleApiClient.connect();
                        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                if(mGoogleApiClient.isConnected()){
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            if (status.isSuccess()) {
                                                Log.d(TAG, "User Logged out");
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                            }
                        });

                        Intent intent =new Intent(getActivity(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                        return true;
                    default: return false;
                }
            }
        });
        if(getActivity().getActionBar()!= null)
            getActivity().getActionBar().show();
        /*mDemoCollectionPagerAdapter =
                new CollectionPagerAdapter(
                        getChildFragmentManager());*/
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.profile_view_pager);
        setupViewPager(mViewPager);
        Log.d(TAG,"setting tab layout with view pager");
        mTabLayout.setupWithViewPager(mViewPager);

        mProfileName = (AppCompatTextView) view.findViewById(R.id.profile_top_name);
        mProfileNumber = (AppCompatTextView) view.findViewById(R.id.profile_top_phone);

        mUserUID = ActivityHelper.getUID(getActivity());
        Log.d(TAG,"User UID -->"+ mUserUID);
        parentProfileRef = mDatabaseReference
                .child(getString(R.string.firebase_profile_node))
                .child(getString(R.string.firebase_parent_node)).child(mUserUID);
        setupDataFromFirebase();

        //mViewPager.setAdapter(mDemoCollectionPagerAdapter);

        /*AppCompatButton logout = (AppCompatButton) view.findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.getInstance().signOut();
                Intent intent =new Intent(getActivity(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });*/

        mParentImageView = (CircleImageView) view.findViewById(R.id.parent_profile_pic);
        return view;
    }


    private void setupDataFromFirebase() {
        parentProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parentUser = dataSnapshot.getValue(ParentUsers.class);
                setUpDataInUI(parentUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpDataInUI(ParentUsers parentUser) {
        mProfileNumber.setText(parentUser.getPhone());
        mProfileName.setText(parentUser.getName());
        Glide.with(this).load(parentUser.getPhotoUri()).into(mParentImageView);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setupViewPager(ViewPager viewPager) {

        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ProfileInfoFragment(), "Profile");
        adapter.addFragment(new NotificationSettingsFragment(), "Notifications");
        adapter.addFragment(new AccountSettingsFragment(), "Settings");
        viewPager.setAdapter(adapter);
    }

}
