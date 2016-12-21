package com.roa.foodonetv3.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.activities.GroupsActivity;
import com.roa.foodonetv3.adapters.GroupsRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.OnReplaceFragListener;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.model.Group;
import com.roa.foodonetv3.services.FoodonetService;

import java.util.ArrayList;

public class GroupsOverviewFragment extends Fragment {
    private static final String TAG = "GroupsOverviewFragment";

    private GroupsRecyclerAdapter adapter;
    private FoodonetReceiver receiver;
//    private ProgressDialog progressDialog;
//    private String newGroupName;
    private OnReplaceFragListener replaceFragListener;

    public GroupsOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new FoodonetReceiver();
        replaceFragListener = (OnReplaceFragListener) getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_groups_overview, container, false);

        getActivity().setTitle(R.string.drawer_groups);

        RecyclerView recyclerGroupsOverview = (RecyclerView) v.findViewById(R.id.recyclerGroupsOverview);
        recyclerGroupsOverview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupsRecyclerAdapter(getContext());
        recyclerGroupsOverview.setAdapter(adapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);

        Intent intent = new Intent(getContext(),FoodonetService.class);
        intent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_GET_GROUPS);
        String[] args = new String[]{String.valueOf(CommonMethods.getMyUserID(getContext()))};
        intent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
        getContext().startService(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
//        if(progressDialog!= null){
//            progressDialog.dismiss();
//        }
    }

//    @Override
//    public void onNewGroupClick(String groupName){
//        newGroupName = groupName;
//        Group newGroup = new Group(groupName, CommonMethods.getMyUserID(getContext()),null,-1);
//        Intent intent = new Intent(getContext(), FoodonetService.class);
//        intent.putExtra(StartServiceMethods.ACTION_TYPE,StartServiceMethods.ACTION_ADD_GROUP);
//        intent.putExtra(FoodonetService.JSON_TO_SEND,newGroup.getAddGroupJson().toString());
//        getContext().startService(intent);
//        progressDialog = new ProgressDialog(getContext());
//        progressDialog.setTitle(R.string.please_wait);
//        progressDialog.show();
//    }

    private class FoodonetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for reports got from the service */
            int action = intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1);
            switch (action){
                case ReceiverConstants.ACTION_GET_GROUPS:
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 27/11/2016 add logic if fails
                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                    } else{
                        ArrayList<Group> groups = intent.getParcelableArrayListExtra(Group.KEY);
                        adapter.updateGroups(groups);
                        // TODO: 07/12/2016 remove toast after testing
                        Toast.makeText(context, "got groups", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case ReceiverConstants.ACTION_ADD_GROUP:
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 27/11/2016 add logic if fails
                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                    } else {
                        int groupID = intent.getIntExtra(Group.GROUP,-1);
                        Intent updateIntent = new Intent(getContext(),FoodonetService.class);
                        updateIntent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_GET_GROUPS);
                        String[] args = new String[]{String.valueOf(CommonMethods.getMyUserID(getContext()))};
                        updateIntent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
                        getContext().startService(updateIntent);
                        break;
                    }
            }
        }
    }
}
