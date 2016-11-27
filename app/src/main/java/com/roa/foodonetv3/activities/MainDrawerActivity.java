package com.roa.foodonetv3.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.fragments.ActiveFragment;
import com.roa.foodonetv3.fragments.ClosestFragment;
import com.roa.foodonetv3.fragments.RecentFragment;
import com.roa.foodonetv3.model.User;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,TabLayout.OnTabSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainDrawerActivity";

    // TODO: 12/11/2016 move two constants to different class
    public static final String ACTION_OPEN_PUBLICATION = "action_open_publication";
    public static final int OPEN_ADD_PUBLICATION = 1;
    public static final int OPEN_EDIT_PUBLICATION = 2;
    public static final int OPEN_PUBLICATION_DETAIL = 3;
    public static final int OPEN_MY_PUBLICATIONS = 4;

    private ViewPager viewPager;
    private ViewHolderAdapter adapter;
    private TabLayout tabs;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /** get the string into a static field or a resource string*/
        /** check if the app is initialized*/
        preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        if(!preferenceManager.getBoolean("initialized",false)){
            init();
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        View hView =  navigationView.inflateHeaderView(R.layout.nav_header_main);
//        circleImageView imgvw = (CircleImageView)hView.findViewById(R.id.headerCircleImage);

        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //set the header imageView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        CircleImageView circleImageView = (CircleImageView) hView.findViewById(R.id.headerCircleImage);
        TextView headerTxt = (TextView) hView.findViewById(R.id.headerNavTxt);
        circleImageView.setImageResource(R.drawable.foodonet_image);
        if (mFirebaseUser == null) {
            // TODO: 24/11/2016 add logic?
        } else {
            String mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                String mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                Glide.with(this).load(mPhotoUrl).into(circleImageView);
                headerTxt.setText(mUsername);
            }
        }

        tabs = (TabLayout) findViewById(R.id.tabs);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ViewHolderAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        tabs.setOnTabSelectedListener(this);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent i;
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    /** no user logged in yet, open the sign in activity */
                    i = new Intent(MainDrawerActivity.this,SignInActivity.class);
                } else{
                    /** a user is logged in, continue to open the activity and fragment of the add publication */
                    i = new Intent(MainDrawerActivity.this,PublicationActivity.class);
                    i.putExtra(MainDrawerActivity.ACTION_OPEN_PUBLICATION,OPEN_ADD_PUBLICATION);
                }
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void init(){
        /** get the string into a static field or a resource string*/
        SharedPreferences.Editor edit = preferenceManager.edit();
        edit.putBoolean("initialized",true);
        String deviceUUID = UUID.randomUUID().toString();
        edit.putString(User.ACTIVE_DEVICE_DEV_UUID, deviceUUID).apply();
        Log.v("Got new device UUID",deviceUUID);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                FirebaseAuth.getInstance().signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                /** remove user phone number and foodonet user ID from sharedPreferences */
                SharedPreferences.Editor editor = preferenceManager.edit();
                editor.remove(User.PHONE_NUMBER);
                editor.remove(User.IDENTITY_PROVIDER_USER_ID);
                editor.apply();
                Snackbar.make(viewPager, R.string.signed_out_successfully,Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        /** handle the navigation actions in the common methods class */
        CommonMethods.navigationItemSelectedAction(this,item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    //view pager adapter...
    public static class ViewHolderAdapter extends FragmentPagerAdapter {

        public ViewHolderAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return new ActiveFragment();
                case 1:
                    return new RecentFragment();
                case 2:
                    return new ClosestFragment();
            }
            return null;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Active";
                case 1:
                    return "Recent";
                case 2:
                    return "Closest";
            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


}


