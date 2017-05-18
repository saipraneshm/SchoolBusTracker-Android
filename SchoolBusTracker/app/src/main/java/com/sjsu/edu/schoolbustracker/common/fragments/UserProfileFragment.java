package com.sjsu.edu.schoolbustracker.common.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.sjsu.edu.schoolbustracker.parentuser.adapter.CustomFragmentPagerAdapter;
import com.sjsu.edu.schoolbustracker.helperclasses.FirebaseUtil;
import com.sjsu.edu.schoolbustracker.common.activity.MainActivity;
import com.sjsu.edu.schoolbustracker.parentuser.adapter.StudentFirebaseRecyclerAdapter;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.AccountSettingsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.NotificationSettingsFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.childfragments.ProfileInfoFragment;
import com.sjsu.edu.schoolbustracker.parentuser.fragments.dialogfragments.StudentDetailFragment;
import com.sjsu.edu.schoolbustracker.common.model.ParentUsers;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileFragment extends Fragment {
    //Firebase
    private FirebaseAuth mAuth;


    private Toolbar mToolbar;

    private final String TAG = "UserProfileFragment";
    private GoogleApiClient mGoogleApiClient;
    private CircleImageView mParentImageView;
    private RecyclerView mChildImageLayout;
    private StudentFirebaseRecyclerAdapter mAdapter;

    //CollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    TabLayout mTabLayout;


    private AppCompatTextView mProfileName,mProfileNumber;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference parentProfileRef;
    private DatabaseReference mStudentReference;
    private String mUserUID;
    private ParentUsers parentUser;
    private CircleImageView mNewStudentButton;
    private static final String IS_DRIVER = "isDriver";
    private static boolean isDriver = false;


    public UserProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(boolean isDriver) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle arg = new Bundle();
        arg.putBoolean(IS_DRIVER, isDriver);
        fragment.setArguments(arg);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        isDriver = getArguments().getBoolean(IS_DRIVER);
        Log.d(TAG, isDriver + " : is the current user a driver?");
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
        mToolbar.setTitleTextColor(ResourcesCompat.getColor(getResources(),R.color.black, null));
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
        mNewStudentButton = (CircleImageView) view.findViewById(R.id.iv_add_new_student);
        mNewStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newStudentFragment = StudentDetailFragment.newInstance(null);
                newStudentFragment.show(getFragmentManager(),"New Student");

            }
        });
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.profile_view_pager);
        setupViewPager(mViewPager);
        Log.d(TAG,"setting tab layout with view pager");
        mTabLayout.setupWithViewPager(mViewPager);

        mProfileName = (AppCompatTextView) view.findViewById(R.id.profile_top_name);
        mProfileNumber = (AppCompatTextView) view.findViewById(R.id.profile_top_phone);

        mUserUID = FirebaseUtil.getCurrentUserId();
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
        mChildImageLayout = (RecyclerView) view.findViewById(R.id.student_list_view);

        mStudentReference = FirebaseUtil.getStudentsRef();

        mChildImageLayout.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL,false));
        mAdapter = new StudentFirebaseRecyclerAdapter(mStudentReference,getActivity(),false);
        mAdapter.setOnItemClickListener(new StudentFirebaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String studentId, int pos) {
                DialogFragment studentDetailFragment = StudentDetailFragment.newInstance(studentId);
                studentDetailFragment.show(getFragmentManager(),"Student Detail");

            }

            @Override
            public void getPrevPos(int position) {

            }
        });

        mChildImageLayout.setAdapter(mAdapter);
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

    private void setupViewPager(ViewPager viewPager) {

        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new ProfileInfoFragment(), "Profile");
        adapter.addFragment(new NotificationSettingsFragment(), "Notifications");
        adapter.addFragment(new AccountSettingsFragment(), "Settings");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }
}
