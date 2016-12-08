package com.roa.foodonetv3.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.fragments.GroupsOverviewFragment;
import com.roa.foodonetv3.fragments.NewGroupFragment;

public class GroupsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = "GroupsActivity";

    public static final String ACTION_OPEN_GROUPS = "action_open_groups";
//    public static final int OPEN_VIEW_GROUPS = 1;
//    public static final int OPEN_ADD_GROUP = 2;
//    public static final int OPEN_VIEW_GROUP = 3;

    public static final String GROUP_OVERVIEW_TAG = "groupsOverviewFrag";
    public static final String ADD_GROUP_TAG = "newGroupFrag";

    private String currentFrag;

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
            newGroupsFrag(GROUP_OVERVIEW_TAG);
        }
    }

    @Override
    public void onBackPressed() {
        switch (currentFrag){
            case GROUP_OVERVIEW_TAG:
                super.onBackPressed();
                break;
            case ADD_GROUP_TAG:
                replaceGroupsFrag(GROUP_OVERVIEW_TAG);
                break;
        }
    }

    private void newGroupsFrag(String openFragType){
        currentFrag = openFragType;
        switch (openFragType) {
            case GROUP_OVERVIEW_TAG:
                GroupsOverviewFragment groupsOverviewFragment = new GroupsOverviewFragment();
                fragmentManager.beginTransaction().add(R.id.containerGroups,groupsOverviewFragment,GROUP_OVERVIEW_TAG).commit();
                break;
        }
        updateFragViews(openFragType);
    }

    private void replaceGroupsFrag(String openFragType){
        currentFrag = openFragType;
        switch (openFragType) {
            case GROUP_OVERVIEW_TAG:
                GroupsOverviewFragment groupsOverviewFragment = new GroupsOverviewFragment();
                fragmentManager.beginTransaction().replace(R.id.containerGroups,groupsOverviewFragment,GROUP_OVERVIEW_TAG).commit();
                break;
            case ADD_GROUP_TAG:
                NewGroupFragment newGroupFragment = new NewGroupFragment();
                fragmentManager.beginTransaction().replace(R.id.containerGroups,newGroupFragment, ADD_GROUP_TAG).commit();
                break;
        }
        updateFragViews(openFragType);
    }

    private void updateFragViews(String openFragType){
        switch (openFragType){
            case GROUP_OVERVIEW_TAG:
                fab.setImageResource(R.drawable.white_plus);
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                break;
            case ADD_GROUP_TAG:
                fab.setImageResource(R.drawable.user);
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.FooGreen)));
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                switch (currentFrag){
                    case GROUP_OVERVIEW_TAG:
                        /** replace the main overview frag with the addGroup frag */
                        replaceGroupsFrag(ADD_GROUP_TAG);
                        break;
                    case ADD_GROUP_TAG:
                        break;
                }
                break;
        }
    }
}
