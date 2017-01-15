package com.roa.foodonetv3.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.db.GroupMembersDBHandler;
import com.roa.foodonetv3.db.GroupsDBHandler;
import com.roa.foodonetv3.db.LatestPlacesDBHandler;
import com.roa.foodonetv3.db.PublicationsDBHandler;
import com.roa.foodonetv3.db.RegisteredUsersDBHandler;
import com.roa.foodonetv3.db.ReportsDBHandler;
import com.roa.foodonetv3.model.GroupMember;
import com.roa.foodonetv3.model.User;

public class SignOutService extends IntentService {

    public SignOutService() {
        super("SignOutService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // TODO: 05/12/2016 check if it is written as it should...
            FirebaseAuth.getInstance().signOut();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            /** remove user phone number and foodonet user ID from sharedPreferences */
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(User.PHONE_NUMBER);
            editor.remove(User.IDENTITY_PROVIDER_USER_ID);
            editor.apply();
            /** remove data from the db */
            PublicationsDBHandler publicationsDBHandler = new PublicationsDBHandler(this);
            publicationsDBHandler.deleteAllPublications();      //
            ReportsDBHandler reportsDBHandler = new ReportsDBHandler(this);
            reportsDBHandler.deleteAllReports();
            GroupsDBHandler groupsDBHandler = new GroupsDBHandler(this);
            groupsDBHandler.deleteAllGroups();
            RegisteredUsersDBHandler registeredUsersDBHandler = new RegisteredUsersDBHandler(this);
            registeredUsersDBHandler.deleteAllRegisteredUsers();
            GroupMembersDBHandler groupMembersDBHandler = new GroupMembersDBHandler(this);
            groupMembersDBHandler.deleteAllGroupsMembers();
            LatestPlacesDBHandler latestPlacesDBHandler = new LatestPlacesDBHandler(this);
            latestPlacesDBHandler.deleteAllPlaces();
            /** get new data */

        }
    }
}
