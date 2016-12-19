package com.roa.foodonetv3.activities;

import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonConstants;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.FabAnimation;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.fragments.AddEditPublicationFragment;
import com.roa.foodonetv3.fragments.MyPublicationsFragment;
import com.roa.foodonetv3.fragments.PublicationDetailFragment;
import com.roa.foodonetv3.model.Publication;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    // TODO: 12/11/2016 move two constants to different class
    private static final String TAG = "PublicationActivity";

    public static final String ACTION_OPEN_PUBLICATION = "action_open_publication";
    public static final String ADD_PUBLICATION_TAG = "addPublicationFrag";
    public static final String EDIT_PUBLICATION_TAG = "editPublicationFrag";
    public static final String PUBLICATION_DETAIL_TAG = "publicationDetailFrag";
    public static final String MY_PUBLICATIONS_TAG = "myPublicationsFrag";

    private FloatingActionButton fab;
    private String currentFrag;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        String openFragType = intent.getStringExtra(ACTION_OPEN_PUBLICATION);

        fragmentManager = getSupportFragmentManager();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        // Initialize Firebase Auth
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        //set header imageView
        View hView = navigationView.getHeaderView(0);
        CircleImageView circleImageView = (CircleImageView) hView.findViewById(R.id.headerCircleImage);
        TextView headerTxt = (TextView) hView.findViewById(R.id.headerNavTxt);

        if (mFirebaseUser !=null && mFirebaseUser.getPhotoUrl()!=null) {
            Glide.with(this).load(mFirebaseUser.getPhotoUrl()).into(circleImageView);
            headerTxt.setText(mFirebaseUser.getDisplayName());
        }else {
            circleImageView.setImageResource(R.drawable.foodonet_image);
        }

        if(savedInstanceState==null){
            openNewPublicationFrag(openFragType);
        }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /** handle the navigation actions in the common methods class */
        CommonMethods.navigationItemSelectedAction(this,item.getItemId());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openNewPublicationFrag(String openFragType){
        long duration = CommonConstants.FAB_ANIM_DURATION;
        if(currentFrag==null){
            /** if this is the first frag - don't make a long animation */
            duration = 1;
        }
        currentFrag = openFragType;
        Publication publication;
        Bundle bundle;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        final int normalFabY = height - (int)(getResources().getDimension(R.dimen.fab_margin) + CommonConstants.FAB_SIZE*2);
        switch (openFragType){
            case ADD_PUBLICATION_TAG:
                AddEditPublicationFragment addPublicationFragment = new AddEditPublicationFragment();
                bundle = new Bundle();
                bundle.putInt(AddEditPublicationFragment.TAG,AddEditPublicationFragment.TYPE_NEW_PUBLICATION);
                addPublicationFragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.container_publication, addPublicationFragment, "addEditPublicationFrag").commit();
                FabAnimation.animateFAB(this,fab,normalFabY,duration,R.drawable.user,getResources().getColor(R.color.FooGreen),false);
                break;
            case EDIT_PUBLICATION_TAG:
                publication = getIntent().getParcelableExtra(Publication.PUBLICATION_KEY);
                AddEditPublicationFragment editPublicationFragment = new AddEditPublicationFragment();
                bundle = new Bundle();
                bundle.putInt(AddEditPublicationFragment.TAG,AddEditPublicationFragment.TYPE_EDIT_PUBLICATION);
                bundle.putParcelable(Publication.PUBLICATION_KEY,publication);
                editPublicationFragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.container_publication, editPublicationFragment, "addEditPublicationFrag").commit();
                FabAnimation.animateFAB(this,fab,normalFabY,duration,R.drawable.user,getResources().getColor(R.color.FooGreen),false);
                break;
            case PUBLICATION_DETAIL_TAG:
                publication = getIntent().getParcelableExtra(Publication.PUBLICATION_KEY);
                PublicationDetailFragment publicationDetailFragment = new PublicationDetailFragment();
                bundle = new Bundle();
                bundle.putParcelable(Publication.PUBLICATION_KEY,publication);
                publicationDetailFragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.container_publication, publicationDetailFragment, "publicationDetailFrag").commit();
                FabAnimation.animateFAB(this,fab,normalFabY,duration,R.drawable.user,getResources().getColor(R.color.FooGreen),true);
                break;
            case MY_PUBLICATIONS_TAG:
                fragmentManager.beginTransaction().add(R.id.container_publication, new MyPublicationsFragment(), "my_publications").commit();
                FabAnimation.animateFAB(this,fab,normalFabY,duration,R.drawable.user,getResources().getColor(R.color.FooGreen),false);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                if(currentFrag!= null){
                    switch (currentFrag){
                        case ADD_PUBLICATION_TAG:
                            Intent fabClickIntent = new Intent(ReceiverConstants.BROADCAST_FOODONET);
                            fabClickIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_FAB_CLICK);
                            fabClickIntent.putExtra(ReceiverConstants.SERVICE_ERROR,false);
                            fabClickIntent.putExtra(ReceiverConstants.FAB_TYPE,ReceiverConstants.FAB_TYPE_SAVE_NEW_PUBLICATION);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(fabClickIntent);
                            break;
                        case MY_PUBLICATIONS_TAG:
                            // TODO: 18/12/2016 currently instantiating another activity just for the back press
                            Intent newAddPublicationIntent = new Intent(this,PublicationActivity.class);
                            newAddPublicationIntent.putExtra(ACTION_OPEN_PUBLICATION,ADD_PUBLICATION_TAG);
                            startActivity(newAddPublicationIntent);
                    }
                    break;
                }
        }
    }
}
