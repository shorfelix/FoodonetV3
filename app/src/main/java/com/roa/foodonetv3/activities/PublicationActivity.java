package com.roa.foodonetv3.activities;

import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.adapters.PublicationsRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.CommonConstants;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.FabAnimation;
import com.roa.foodonetv3.commonMethods.OnFabChangeListener;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.fragments.AddEditPublicationFragment;
import com.roa.foodonetv3.fragments.MyPublicationsFragment;
import com.roa.foodonetv3.fragments.PublicationDetailFragment;
import com.roa.foodonetv3.model.Publication;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnFabChangeListener, PublicationsRecyclerAdapter.OnPublicationClickListener{
    private static final String TAG = "PublicationActivity";

    public static final String ACTION_OPEN_PUBLICATION = "action_open_publication";
    public static final String ADD_PUBLICATION_TAG = "addPublicationFrag";
    public static final String EDIT_PUBLICATION_TAG = "editPublicationFrag";
    public static final String PUBLICATION_DETAIL_TAG = "publicationDetailFrag";
    public static final String MY_PUBLICATIONS_TAG = "myPublicationsFrag";

    private FloatingActionButton fab;
    private String currentFrag, previousFrag;
    private CircleImageView circleImageView;
    private TextView headerTxt;
    private Publication publication;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /** set the fragment manager */
        fragmentManager = getSupportFragmentManager();

        /** set the floating action button */
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        /** set the drawer */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        circleImageView = (CircleImageView) hView.findViewById(R.id.headerCircleImage);
        headerTxt = (TextView) hView.findViewById(R.id.headerNavTxt);

        /** get which fragment should be opened from the intent, and open it */
        Intent intent = getIntent();
        publication = getIntent().getParcelableExtra(Publication.PUBLICATION_KEY);
        String openFragType = intent.getStringExtra(ACTION_OPEN_PUBLICATION);
        if(savedInstanceState==null){
            openNewPublicationFrag(openFragType);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** set drawer header and image */
        // TODO: 19/02/2017 currently loading the image from the web
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser !=null && mFirebaseUser.getPhotoUrl()!=null) {
            Glide.with(this).load(mFirebaseUser.getPhotoUrl()).into(circleImageView);
            headerTxt.setText(CommonMethods.getMyUserName(this));
        }else{
            Glide.with(this).load(android.R.drawable.sym_def_app_icon).into(circleImageView);
            headerTxt.setText(getResources().getString(R.string.not_signed_in));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(previousFrag != null){
                openNewPublicationFrag(previousFrag);
                previousFrag = null;
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /** handle the navigation actions in the common methods class */
        if(item.getItemId()== R.id.nav_my_shares){
            if(currentFrag== null || !currentFrag.equals(MY_PUBLICATIONS_TAG)){
                openNewPublicationFrag(MY_PUBLICATIONS_TAG);
            }
        } else{
            CommonMethods.navigationItemSelectedAction(this,item.getItemId());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** opens a new fragment and sets the fab */
    private void openNewPublicationFrag(String openFragType){
        /** get the values for the fab animation */
        long duration;
        boolean isAddNewFragment;
        if(currentFrag==null){
            /** if this is the first frag - don't make a long animation */
            duration = 1;
            isAddNewFragment = true;
        } else{
            duration = CommonConstants.FAB_ANIM_DURATION;
            isAddNewFragment = false;
            previousFrag = currentFrag;
        }

        /** set the current frag to be the new one */
        currentFrag = openFragType;

        Bundle bundle;

        /** replace the fragment and animate the fab accordingly */
        switch (openFragType){
            case ADD_PUBLICATION_TAG:
                AddEditPublicationFragment addPublicationFragment = new AddEditPublicationFragment();
                bundle = new Bundle();
                bundle.putInt(AddEditPublicationFragment.TAG,AddEditPublicationFragment.TYPE_NEW_PUBLICATION);
                addPublicationFragment.setArguments(bundle);
                updateContainer(isAddNewFragment, addPublicationFragment,"addEditPublicationFrag");
                animateFab(openFragType,true,duration);
                break;
            case EDIT_PUBLICATION_TAG:
                if(publication!=null){
                    AddEditPublicationFragment editPublicationFragment = new AddEditPublicationFragment();
                    bundle = new Bundle();
                    bundle.putInt(AddEditPublicationFragment.TAG,AddEditPublicationFragment.TYPE_EDIT_PUBLICATION);
                    bundle.putParcelable(Publication.PUBLICATION_KEY,publication);
                    editPublicationFragment.setArguments(bundle);
                    updateContainer(isAddNewFragment, editPublicationFragment,"addEditPublicationFrag");
                    animateFab(openFragType,true,duration);
                }
                break;
            case PUBLICATION_DETAIL_TAG:
                if(publication!=null) {
                    PublicationDetailFragment publicationDetailFragment = new PublicationDetailFragment();
                    bundle = new Bundle();
                    bundle.putParcelable(Publication.PUBLICATION_KEY, publication);
                    publicationDetailFragment.setArguments(bundle);
                    updateContainer(isAddNewFragment, publicationDetailFragment, "publicationDetailFrag");
                    animateFab(openFragType, true, duration);
                }
                break;
            case MY_PUBLICATIONS_TAG:
                updateContainer(isAddNewFragment, new MyPublicationsFragment(),"my_publications");
                animateFab(openFragType,true,duration);
                break;
        }
    }

    private void updateContainer(boolean isAddNewFragment, Fragment fragment, String fragmentTag){
        if(isAddNewFragment){
            fragmentManager.beginTransaction().add(R.id.container_publication, fragment, fragmentTag).commit();
        } else{
            fragmentManager.beginTransaction().replace(R.id.container_publication, fragment, fragmentTag).commit();
        }
    }

    private void animateFab(String fragmentTag, boolean setVisible, long duration){
        int imgResource = -1;
        int color = -1;
        // TODO: 13/02/2017 add different fab icons and colors
        switch (fragmentTag){
            case ADD_PUBLICATION_TAG:
                imgResource = R.drawable.user;
                color = getResources().getColor(R.color.fooGreen);
                break;
            case EDIT_PUBLICATION_TAG:
                imgResource = R.drawable.user;
                color = getResources().getColor(R.color.fooGreen);
                break;
            case PUBLICATION_DETAIL_TAG:
                imgResource = R.drawable.user;
                color = getResources().getColor(R.color.fooGreen);
                break;
            case MY_PUBLICATIONS_TAG:
                imgResource = R.drawable.user;
                color = getResources().getColor(R.color.fooGreen);
                break;
        }
        FabAnimation.animateFAB(this,fab,duration,imgResource,color,setVisible);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                if(currentFrag!= null){
                    switch (currentFrag){
                        /** clicked on save (the new publication)
                         * send the fab click to the fragment */
                        case ADD_PUBLICATION_TAG:
                            Intent fabClickIntent = new Intent(ReceiverConstants.BROADCAST_FOODONET);
                            fabClickIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_FAB_CLICK);
                            fabClickIntent.putExtra(ReceiverConstants.SERVICE_ERROR,false);
                            fabClickIntent.putExtra(ReceiverConstants.FAB_TYPE,ReceiverConstants.FAB_TYPE_SAVE_NEW_PUBLICATION);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(fabClickIntent);
                            break;

                        /** clicked on create new publication */
                        case MY_PUBLICATIONS_TAG:
                            if(CommonMethods.getMyUserID(this)==-1){
                                Intent intent = new Intent(this,SignInActivity.class);
                                startActivity(intent);
                            } else{
                                openNewPublicationFrag(ADD_PUBLICATION_TAG);
                            }
                            break;

                        /** clicked on register for publication
                         * send the fab click to the fragment */
                        case PUBLICATION_DETAIL_TAG:
                            Intent registerToPublicationIntent = new Intent(ReceiverConstants.BROADCAST_FOODONET);
                            registerToPublicationIntent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_FAB_CLICK);
                            registerToPublicationIntent.putExtra(ReceiverConstants.SERVICE_ERROR,false);
                            registerToPublicationIntent.putExtra(ReceiverConstants.FAB_TYPE,ReceiverConstants.FAB_TYPE_REGISTER_TO_PUBLICATION);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(registerToPublicationIntent);
                            break;
                    }
                    break;
                }
        }
    }

    @Override
    public void onFabChange(String fragmentTag, boolean setVisible) {
        animateFab(fragmentTag,setVisible,1);
    }

    @Override
    public void onPublicationClick(Publication publication) {
        this.publication = publication;
        openNewPublicationFrag(PUBLICATION_DETAIL_TAG);
    }
}
