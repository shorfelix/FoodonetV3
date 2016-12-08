package com.roa.foodonetv3.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.adapters.GroupMembersRecyclerAdapter;
import com.roa.foodonetv3.model.GroupMember;

import java.util.ArrayList;

public class NewGroupFragment extends Fragment {
    private static final String TAG = "NewGroupFragment";

    private GroupMembersRecyclerAdapter adapter;

    public NewGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_group, container, false);

        RecyclerView recyclerGroupMembers = (RecyclerView) v.findViewById(R.id.recyclerGroupMembers);
        recyclerGroupMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupMembersRecyclerAdapter(getContext());
        recyclerGroupMembers.setAdapter(adapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: 08/12/2016 test
        ArrayList<GroupMember> members = new ArrayList<>();
        members.add(new GroupMember(-1,-1,"054-bla bla bla","testName",false));
        members.add(new GroupMember(-1,-1,"054-bla bla bla","name2",false));
        members.add(new GroupMember(-1,-1,"054-bla bla bla","testName3",false));
        members.add(new GroupMember(-1,-1,"054-bla bla bla","testName4",false));
        members.add(new GroupMember(-1,-1,"054-bla bla bla","testName5",false));
        members.add(new GroupMember(-1,-1,"054-bla bla bla","testName6",false));
        adapter.updateMembers(members);
    }
}
