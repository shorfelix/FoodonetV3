package com.roa.foodonetv3.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
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
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.roa.foodonetv3.NewGroupDialog;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.OnReplaceFragListener;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.fragments.GroupsOverviewFragment;
import com.roa.foodonetv3.fragments.AdminGroupFragment;
import com.roa.foodonetv3.model.Group;
import com.roa.foodonetv3.services.FoodonetService;

import java.util.ArrayList;

public class GroupsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener , OnReplaceFragListener,NewGroupDialog.OnNewGroupClickListener {
    private static final String TAG = "GroupsActivity";

    public static final String GROUPS_OVERVIEW_TAG = "groupsOverviewFrag";
    public static final String ADMIN_GROUP_TAG = "newGroupFrag";
    public static final String OPEN_GROUP_TAG = "openGroupFrag";

    private static final long FAB_ANIM_DURATION = 600;
    private static final int FAB_SIZE = 56;
    public static final int CONTACT_PICKER = 1;

    private String currentFrag;
    private ProgressDialog progressDialog;
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

        if(savedInstanceState== null){
            newGroupsFrag(GROUPS_OVERVIEW_TAG);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(progressDialog!= null){
            progressDialog.dismiss();
        }
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
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
        switch (openFragType) {
            case GROUPS_OVERVIEW_TAG:
                GroupsOverviewFragment groupsOverviewFragment = new GroupsOverviewFragment();
                fragmentManager.beginTransaction().add(R.id.containerGroups,groupsOverviewFragment, GROUPS_OVERVIEW_TAG).commit();
                break;
        }
        updateFragViews(openFragType);
    }

    @Override
    public void replaceFrags(String openFragType, ArrayList<Parcelable> arrayList) {
        currentFrag = openFragType;
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
        switch (openFragType) {
            case GROUPS_OVERVIEW_TAG:
                GroupsOverviewFragment groupsOverviewFragment = new GroupsOverviewFragment();
                fragmentManager.beginTransaction().replace(R.id.containerGroups,groupsOverviewFragment, GROUPS_OVERVIEW_TAG).commit();
                break;
            case ADMIN_GROUP_TAG:
                // TODO: 13/12/2016 test when the service to add a new group will be fixed, should open automatically, currently hard coded empty members
                AdminGroupFragment adminGroupFragment = new AdminGroupFragment();
                Group newGroup = (Group) arrayList.get(0);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Group.GROUP,newGroup);
                adminGroupFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.containerGroups, adminGroupFragment, ADMIN_GROUP_TAG).commit();
                break;
            case OPEN_GROUP_TAG:
                // TODO: 13/12/2016 add fragment
//                OpenGroupFragment openGroupFragment = new OpenGroupFragment();
                Toast.makeText(this, "Open Group", Toast.LENGTH_SHORT).show();
                break;
        }
        updateFragViews(openFragType);
    }

    private void updateFragViews(String openFragType){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        final int normalFabY = height - (int)(getResources().getDimension(R.dimen.fab_margin) + FAB_SIZE*2);
        switch (openFragType){
            case GROUPS_OVERVIEW_TAG:
                animateFAB(normalFabY,FAB_ANIM_DURATION,R.drawable.white_plus,getResources().getColor(R.color.colorPrimary));
                break;
            case ADMIN_GROUP_TAG:
                animateFAB(normalFabY,FAB_ANIM_DURATION,R.drawable.user,getResources().getColor(R.color.FooGreen));
                break;
        }
    }

    private void animateFAB(int y, long duration, int imageResource, int color){
        fab.setClickable(false);
        final Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),imageResource);

        AnimatorSet animation = new AnimatorSet();

        ObjectAnimator colorAnimation = ObjectAnimator.ofInt(fab,"backgroundTint", fab.getBackgroundTintList().getDefaultColor(),color);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.setInterpolator(new DecelerateInterpolator());
        colorAnimation.setDuration(duration);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                fab.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
            }
        });

        ObjectAnimator moveAnimation = ObjectAnimator.ofFloat(fab,"y",y);
        moveAnimation.setInterpolator(new DecelerateInterpolator());
        moveAnimation.setDuration(duration);

        ObjectAnimator fadeOutImageAnimation = ObjectAnimator.ofInt(fab,"imageAlpha",255,0);
        fadeOutImageAnimation.setDuration(duration/2);

        ObjectAnimator fadeInImageAnimation = ObjectAnimator.ofInt(fab,"imageAlpha",0,255);
        fadeInImageAnimation.setDuration(duration/2);
        fadeInImageAnimation.setStartDelay(duration/2);
        fadeInImageAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                fab.setImageBitmap(imageBitmap);
            }
            @Override
            public void onAnimationEnd(Animator animator) {
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                fab.setClickable(true);
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animation.play(colorAnimation).with(moveAnimation).with(fadeOutImageAnimation).with(fadeInImageAnimation);
        animation.start();
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
        intent.putExtra(ReceiverConstants.ADDRESS_ARGS, groupName);
        this.startService(intent);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
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
                break;
        }
    }




}
