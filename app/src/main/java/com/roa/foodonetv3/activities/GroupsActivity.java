package com.roa.foodonetv3.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roa.foodonetv3.dialogs.NewGroupDialog;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonConstants;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.FabAnimation;
import com.roa.foodonetv3.commonMethods.OnReplaceFragListener;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.fragments.GroupsOverviewFragment;
import com.roa.foodonetv3.fragments.AdminGroupFragment;
import com.roa.foodonetv3.model.Group;
import com.roa.foodonetv3.model.GroupMember;
import com.roa.foodonetv3.serverMethods.ServerMethods;
import com.roa.foodonetv3.services.FoodonetService;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener , OnReplaceFragListener,NewGroupDialog.OnNewGroupClickListener {
    private static final String TAG = "GroupsActivity";

    public static final String GROUPS_OVERVIEW_TAG = "groupsOverviewFrag";
    public static final String ADMIN_GROUP_TAG = "adminGroupFrag";
    public static final String OPEN_GROUP_TAG = "openGroupFrag";

    public static final int CONTACT_PICKER = 1;

    private String currentFrag, previousFrag;
    private NewGroupDialog newGroupDialog;
    private CircleImageView circleImageView;
    private TextView headerTxt;

    private FloatingActionButton fab;
    private FragmentManager fragmentManager;
    private FoodonetReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        receiver = new FoodonetReceiver();

        /** set the fragment manager */
        fragmentManager = getSupportFragmentManager();

        /** set the drawer layout */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /** set the floating action button */
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        /** set header imageView */
        View hView = navigationView.getHeaderView(0);
        circleImageView = (CircleImageView) hView.findViewById(R.id.headerCircleImage);
        headerTxt = (TextView) hView.findViewById(R.id.headerNavTxt);


        if(savedInstanceState== null){
            /** if new activity, open the overview group fragment */
            newGroupsFrag(GROUPS_OVERVIEW_TAG);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** register receiver */
        IntentFilter filter = new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);

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
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        /** dismiss the dialog if open*/
        if(newGroupDialog!= null){
            newGroupDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(previousFrag != null){
                replaceFrags(previousFrag, new ArrayList<Parcelable>());
                previousFrag = null;
                return;
            }
            super.onBackPressed();
        }
    }

    private void newGroupsFrag(String openFragType){
        currentFrag = openFragType;
        switch (openFragType) {
            case GROUPS_OVERVIEW_TAG:
                /** no need to animate the fab since the group overview is set up in xml as it should */
                GroupsOverviewFragment groupsOverviewFragment = new GroupsOverviewFragment();
                fragmentManager.beginTransaction().add(R.id.containerGroups,groupsOverviewFragment, GROUPS_OVERVIEW_TAG).commit();
                break;
        }
    }

    @Override
    public void replaceFrags(String openFragType, ArrayList<Parcelable> arrayList) {
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

        /** replace the fragment and animate the fab accordingly */
        switch (openFragType) {
            case GROUPS_OVERVIEW_TAG:
                GroupsOverviewFragment groupsOverviewFragment = new GroupsOverviewFragment();
                updateContainer(isAddNewFragment,groupsOverviewFragment,GROUPS_OVERVIEW_TAG);
                animateFab(openFragType, true, duration);
//                fragmentManager.beginTransaction().replace(R.id.containerGroups,groupsOverviewFragment, GROUPS_OVERVIEW_TAG).commit();
                break;
            case ADMIN_GROUP_TAG:
                AdminGroupFragment adminGroupFragment = new AdminGroupFragment();
                Group newGroup = (Group) arrayList.get(0);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Group.GROUP,newGroup);
                adminGroupFragment.setArguments(bundle);
                updateContainer(isAddNewFragment,adminGroupFragment,ADMIN_GROUP_TAG);
//                fragmentManager.beginTransaction().replace(R.id.containerGroups, adminGroupFragment, ADMIN_GROUP_TAG).commit();
                animateFab(openFragType, true, duration);
                break;
        }
    }

    private void updateContainer(boolean isAddNewFragment, Fragment fragment, String fragmentTag){
        if(isAddNewFragment){
            fragmentManager.beginTransaction().add(R.id.containerGroups, fragment, fragmentTag).commit();
        } else{
            fragmentManager.beginTransaction().replace(R.id.containerGroups, fragment, fragmentTag).commit();
        }
    }

    private void animateFab(String fragmentTag, boolean setVisible, long duration){
        int imgResource = -1;
        int color = -1;
        // TODO: 13/02/2017 add different fab icons and colors
        switch (fragmentTag){
            case GROUPS_OVERVIEW_TAG:
                imgResource = R.drawable.user;
                color = getResources().getColor(R.color.fooGreen);
                break;
            case ADMIN_GROUP_TAG:
                imgResource = R.drawable.user;
                color = getResources().getColor(R.color.fooGreen);
                break;
        }
        FabAnimation.animateFAB(this,fab,duration,imgResource,color,setVisible);
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
        /** after a user creates a new group from the dialog, run the service to create the group */
        Group newGroup = new Group(groupName, CommonMethods.getMyUserID(this),(long)-1);
        ServerMethods.addGroup(this,newGroup);
    }

    /** handles the floating action button presses from the different fragments of GroupsActivity */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                if(currentFrag!=null){
                    switch (currentFrag){
                        case GROUPS_OVERVIEW_TAG:
                            /** pressed on create a new group - shows the dialog of creating a new group */
                            if(CommonMethods.getMyUserID(this)==-1){
                                Intent intent = new Intent(this,SignInActivity.class);
                                startActivity(intent);
                            } else{
                                newGroupDialog = new NewGroupDialog(this);
                                newGroupDialog.show();
                            }
                            break;
                        case ADMIN_GROUP_TAG:
                            /** pressed on create a new user in a group the user is the admin of */
                            // TODO: 14/12/2016 currently not working... getting a 440 json response
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

    private class FoodonetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for reports got from the service */
            int action = intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1);
            switch (action){
                /** response from service of adding a new group */
                case ReceiverConstants.ACTION_ADD_GROUP:
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 14/12/2016 add logic if fails
                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                    } else{
                        // TODO: 21/02/2017 CHANGE!!!
//                        long groupID = intent.getLongExtra(Group.GROUP_ID,-1);
//                        if(groupID!=-1){
//                            String[] args = {String.valueOf(groupID)};
//                            ArrayList<GroupMember> userAdmin = new ArrayList<>();
//                            String userName = CommonMethods.getMyUserName(context);
//                            GroupMember adminMember = new GroupMember(groupID,CommonMethods.getMyUserID(GroupsActivity.this),
//                                    CommonMethods.getMyUserPhone(GroupsActivity.this),userName,true);
//                            userAdmin.add(adminMember);
//                            ServerMethods.addGroupMember(getBaseContext(),adminMember);
//                            String newAdminJson = Group.getAddGroupMembersJson(userAdmin).toString();
//                            Intent addAdminIntent = new Intent(GroupsActivity.this,FoodonetService.class);
//                            addAdminIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_ADD_GROUP_MEMBER);
//                            addAdminIntent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
//                            addAdminIntent.putExtra(ReceiverConstants.JSON_TO_SEND,newAdminJson);
//                            GroupsActivity.this.startService(addAdminIntent);
//                        } else{
//                            // TODO: 22/01/2017 do something
//                        }

                    }
            }
        }
    }
}
