package com.roa.foodonetv3.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.db.GroupMembersDBHandler;
import com.roa.foodonetv3.db.LatestPlacesDBHandler;
import com.roa.foodonetv3.db.ReportsDBHandler;
import com.roa.foodonetv3.model.User;

public class GetDataService extends IntentService {
    private static final String TAG = "GetDataService";

    public GetDataService() {
        super("GetDataService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Intent getDataIntent;
            Log.d(TAG,"entered "+ intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1 ));

            switch (intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1)){
                case ReceiverConstants.ACTION_SIGN_OUT:
                    // TODO: 05/12/2016 check if it is written as it should...
                    FirebaseAuth.getInstance().signOut();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    /** remove user phone number and foodonet user ID from sharedPreferences */
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(getString(R.string.key_prefs_user_phone));
                    editor.remove(getString(R.string.key_prefs_user_id));
                    editor.apply();
                    /** delete the data from the db that won't be re-downloaded */
                    GroupMembersDBHandler groupMembersDBHandler = new GroupMembersDBHandler(this);
                    groupMembersDBHandler.deleteAllGroupsMembers();
                    LatestPlacesDBHandler latestPlacesDBHandler = new LatestPlacesDBHandler(this);
                    latestPlacesDBHandler.deleteAllPlaces();
                    ReportsDBHandler reportsDBHandler = new ReportsDBHandler(this);
                    reportsDBHandler.deleteAllReports();
                    /** continue to get new data from the server */

                case ReceiverConstants.ACTION_GET_GROUPS:
                    /** get groups */
                    long userID = CommonMethods.getMyUserID(this);
                    if (userID != (long)-1) {
                        getDataIntent = new Intent(this,FoodonetService.class);
                        getDataIntent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_GET_GROUPS);
                        String[] args = new String[]{String.valueOf(userID)};
                        getDataIntent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
                        this.startService(getDataIntent);
                        break;
                    }
                    /** if the user is not registered yet, with userID -1, skip getting the groups and get the publications (which will get only the 'audience 0 - public' group) */

                case ReceiverConstants.ACTION_GET_PUBLICATIONS:
                    /** get publications */
                    getDataIntent = new Intent(GetDataService.this, FoodonetService.class);
                    getDataIntent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_GET_PUBLICATIONS);
                    GetDataService.this.startService(getDataIntent);
                    break;

                case ReceiverConstants.ACTION_GET_ALL_PUBLICATIONS_REGISTERED_USERS:
                    /** get registered users */
                    getDataIntent = new Intent(GetDataService.this,FoodonetService.class);
                    getDataIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_GET_ALL_PUBLICATIONS_REGISTERED_USERS);
                    GetDataService.this.startService(getDataIntent);
                    break;
            }
        }
    }
}
