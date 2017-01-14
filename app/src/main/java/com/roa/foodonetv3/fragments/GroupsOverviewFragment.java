package com.roa.foodonetv3.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.adapters.GroupsRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.db.GroupsDBHandler;
import com.roa.foodonetv3.model.Group;
import com.roa.foodonetv3.services.FoodonetService;
import java.util.ArrayList;

public class GroupsOverviewFragment extends Fragment {
    private static final String TAG = "GroupsOverviewFragment";

    private GroupsRecyclerAdapter adapter;
    private TextView textInfo;
    private View layoutInfo;

    private FoodonetReceiver receiver;

    public GroupsOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new FoodonetReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_groups_overview, container, false);

        /** set title */
        getActivity().setTitle(R.string.drawer_groups);

        /** set recycler for publications */
        RecyclerView recyclerGroupsOverview = (RecyclerView) v.findViewById(R.id.recyclerGroupsOverview);
        recyclerGroupsOverview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupsRecyclerAdapter(getContext());
        recyclerGroupsOverview.setAdapter(adapter);

        /** set info screen for when there are no groups */
        layoutInfo = v.findViewById(R.id.layoutInfo);
        layoutInfo.setVisibility(View.GONE);
        textInfo = (TextView) v.findViewById(R.id.textInfo);
        textInfo.setText(R.string.you_dont_have_any_groups_yet);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /** register receiver */
        IntentFilter filter = new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);

        GroupsDBHandler handler = new GroupsDBHandler(getContext());
        ArrayList<Group> groups = handler.getAllGroups();
        if(groups.size() == 0){
            layoutInfo.setVisibility(View.VISIBLE);
            textInfo.setText(R.string.you_dont_have_any_groups_yet);
        } else{
            layoutInfo.setVisibility(View.GONE);
            adapter.updateGroups(groups);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    private class FoodonetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for reports got from the service */
            int action = intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1);
            switch (action){
                /** got user's groups */
//                case ReceiverConstants.ACTION_GET_GROUPS:
//                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
//                        // TODO: 27/11/2016 add logic if fails
//                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
//                    } else{
//                        ArrayList<Group> groups = intent.getParcelableArrayListExtra(Group.KEY);
//                        if(groups.size() == 0){
//                            layoutInfo.setVisibility(View.VISIBLE);
//                            textInfo.setText(R.string.you_dont_have_any_groups_yet);
//                        } else{
//                            layoutInfo.setVisibility(View.GONE);
//                        }
//                        adapter.updateGroups(groups);
//                    }
//                    break;
//                /** the new group was added */
//                case ReceiverConstants.ACTION_ADD_GROUP:
//                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
//                        // TODO: 27/11/2016 add logic if fails
//                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
//                    } else {
//                        int groupID = intent.getIntExtra(Group.GROUP,-1);
//
//                        // TODO: 21/12/2016 test, should be through db and not running the service again
//                        Intent updateIntent = new Intent(getContext(),FoodonetService.class);
//                        updateIntent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_GET_GROUPS);
//                        String[] args = new String[]{String.valueOf(CommonMethods.getMyUserID(getContext()))};
//                        updateIntent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
//                        getContext().startService(updateIntent);
//                        break;
//                    }
            }
        }
    }
}
