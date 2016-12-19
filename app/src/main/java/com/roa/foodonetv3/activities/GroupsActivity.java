package com.roa.foodonetv3.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.NewGroupDialog;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonConstants;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.FabAnimation;
import com.roa.foodonetv3.commonMethods.OnReplaceFragListener;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.fragments.GroupsOverviewFragment;
import com.roa.foodonetv3.fragments.AdminGroupFragment;
import com.roa.foodonetv3.model.Group;
import com.roa.foodonetv3.services.FoodonetService;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener , OnReplaceFragListener,NewGroupDialog.OnNewGroupClickListener {
    private static final String TAG = "GroupsActivity";

    public static final String GROUPS_OVERVIEW_TAG = "groupsOverviewFrag";
    public static final String ADMIN_GROUP_TAG = "newGroupFrag";
    public static final String OPEN_GROUP_TAG = "openGroupFrag";

    public static final int CONTACT_PICKER = 1;

    private String currentFrag;
    private NewGroupDialog newGroupDialog;
    private String newGroupName;

    private FloatingActionButton fab;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        if(savedInstanceState== null){
            newGroupsFrag(GROUPS_OVERVIEW_TAG);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(newGroupDialog!= null){
            newGroupDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        switch (currentFrag){
            case GROUPS_OVERVIEW_TAG:
                super.onBackPressed();
                break;
            case ADMIN_GROUP_TAG:
                replaceFrags(GROUPS_OVERVIEW_TAG, null);
                break;
        }
    }

    private void newGroupsFrag(String openFragType){
        currentFrag = openFragType;
        switch (openFragType) {
            case GROUPS_OVERVIEW_TAG:
                GroupsOverviewFragment groupsOverviewFragment = new GroupsOverviewFragment();
                fragmentManager.beginTransaction().add(R.id.containerGroups,groupsOverviewFragment, GROUPS_OVERVIEW_TAG).commit();
                break;
        }
    }

    @Override
    public void replaceFrags(String openFragType, ArrayList<Parcelable> arrayList) {
        long duration = CommonConstants.FAB_ANIM_DURATION;
        if(currentFrag==null){
            /** if this is the first frag - don't make a long animation */
            duration = 1;
        }
        currentFrag = openFragType;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        final int normalFabY = height - (int)(getResources().getDimension(R.dimen.fab_margin) + CommonConstants.FAB_SIZE*2);
        switch (openFragType) {
            case GROUPS_OVERVIEW_TAG:
                GroupsOverviewFragment groupsOverviewFragment = new GroupsOverviewFragment();
                fragmentManager.beginTransaction().replace(R.id.containerGroups,groupsOverviewFragment, GROUPS_OVERVIEW_TAG).commit();
                FabAnimation.animateFAB(this,fab,normalFabY, duration,R.drawable.white_plus,getResources().getColor(R.color.colorPrimary),false);
                break;
            case ADMIN_GROUP_TAG:
                // TODO: 13/12/2016 test when the service to add a new group will be fixed, should open automatically, currently hard coded empty members
                AdminGroupFragment adminGroupFragment = new AdminGroupFragment();
                Group newGroup = (Group) arrayList.get(0);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Group.GROUP,newGroup);
                adminGroupFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.containerGroups, adminGroupFragment, ADMIN_GROUP_TAG).commit();
                // TODO: 19/12/2016 change the image for the fab
                FabAnimation.animateFAB(this,fab,normalFabY, duration,R.drawable.user,getResources().getColor(R.color.FooGreen),false);
                break;
            case OPEN_GROUP_TAG:
                // TODO: 13/12/2016 add fragment
//                OpenGroupFragment openGroupFragment = new OpenGroupFragment();
                Toast.makeText(this, "Open Group", Toast.LENGTH_SHORT).show();
                break;
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

    @Override
    public void onNewGroupClick(String groupName){
        newGroupName = groupName;
        Group newGroup = new Group(groupName, CommonMethods.getMyUserID(this),null,-1);
        Intent intent = new Intent(this, FoodonetService.class);
        intent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_ADD_GROUP);
        intent.putExtra(ReceiverConstants.JSON_TO_SEND,newGroup.getAddGroupJson().toString());
        String[] args = {groupName};
        intent.putExtra(ReceiverConstants.ADDRESS_ARGS, args);
        this.startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                if(currentFrag!=null){
                    switch (currentFrag){
                        case GROUPS_OVERVIEW_TAG:
    //                        /** replace the main overview frag with the addGroup frag */
    //                        replaceFrags(ADMIN_GROUP_TAG, null);
                            newGroupDialog = new NewGroupDialog(this);
                            newGroupDialog.show();
                            break;
                        case ADMIN_GROUP_TAG:
                            // TODO: 14/12/2016 add logic
                            Toast.makeText(this, "add new member", Toast.LENGTH_SHORT).show();
                            Intent fabClickIntent = new Intent(ReceiverConstants.BROADCAST_FOODONET);
                            fabClickIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_FAB_CLICK);
                            fabClickIntent.putExtra(ReceiverConstants.FAB_TYPE,ReceiverConstants.FAB_TYPE_NEW_GROUP_MEMBER);
                            fabClickIntent.putExtra(ReceiverConstants.SERVICE_ERROR,false);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(fabClickIntent);
                            break;
                    }
                }
                break;
        }
    }




}
