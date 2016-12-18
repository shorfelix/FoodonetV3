package com.roa.foodonetv3.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.adapters.GroupMembersRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.model.Group;
import com.roa.foodonetv3.model.GroupMember;
import com.roa.foodonetv3.services.FoodonetService;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.roa.foodonetv3.activities.GroupsActivity.CONTACT_PICKER;

public class AdminGroupFragment extends Fragment {
    private static final String TAG = "AdminGroupFragment";

    private GroupMembersRecyclerAdapter adapter;
    private TextView textGroupName;
    private Group group;
    private FoodonetReceiver receiver;

    public AdminGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = getArguments().getParcelable(Group.GROUP);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_group, container, false);

        RecyclerView recyclerGroupMembers = (RecyclerView) v.findViewById(R.id.recyclerGroupMembers);
        recyclerGroupMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupMembersRecyclerAdapter(getContext());
        recyclerGroupMembers.setAdapter(adapter);

        textGroupName = (TextView) v.findViewById(R.id.textGroupName);
        textGroupName.setText(group.getGroupName());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new FoodonetReceiver();
        IntentFilter filter = new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);

        // TODO: 18/12/2016 test, view a member
        ArrayList<GroupMember> members = group.getMembers();
        members.add(new GroupMember(-1,-1,"Phone", "Test", true));
        adapter.updateMembers(members);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e(TAG, getString(R.string.failed_to_pick_contact));
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phone = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if(cursor!=null){
                cursor.moveToFirst();
                // column index of the phone number
                int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                // column index of the contact name
                int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                phone = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);
            }
            Log.d(TAG,"phone:"+phone+" ,name:"+name);
            boolean error = false;
            if(phone==null || name == null){
                error = true;
            }
            GroupMember member = new GroupMember(group.getGroupID(),0,phone,name,false);
            group.addToMembers(member);
            Intent addMemberIntent = new Intent(getContext(), FoodonetService.class);
            addMemberIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_ADD_GROUP_MEMBER);
            addMemberIntent.putExtra(ReceiverConstants.JSON_TO_SEND,group.getAddGroupMembersJson().toString());
            String[] args = {String.valueOf(group.getGroupID())};
            addMemberIntent.putExtra(ReceiverConstants.ADDRESS_ARGS,args);
            getContext().startService(addMemberIntent);
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        } finally {
            if(cursor!=null){
                cursor.close();
            }
        }
    }

    private class FoodonetReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for reports got from the service */
            int action = intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1);
            switch (action){
                case ReceiverConstants.ACTION_FAB_CLICK:
                    if(intent.getIntExtra(ReceiverConstants.FAB_TYPE,-1)==ReceiverConstants.FAB_TYPE_NEW_GROUP_MEMBER){
                        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(contactPickerIntent, CONTACT_PICKER);
                    }
                    break;
                case ReceiverConstants.ACTION_ADD_GROUP_MEMBER:
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 14/12/2016 add logic if fails
                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                    } else{
                        // TODO: 14/12/2016 add logic
                        Log.d(TAG,"ADDED MEMBER!");
                        Toast.makeText(context, "member added!", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }
}
