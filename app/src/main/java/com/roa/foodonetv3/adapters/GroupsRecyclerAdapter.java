package com.roa.foodonetv3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.model.Group;

import java.util.ArrayList;

public class GroupsRecyclerAdapter extends RecyclerView.Adapter<GroupsRecyclerAdapter.GroupHolder> {

    private static final String TAG = "GroupsRecyclerAdapter";

    private Context context;
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<Group> filteredGroups = new ArrayList<>();

    public GroupsRecyclerAdapter(Context context) {
        this.context = context;
        groups = new ArrayList<>();
    }

    public void updateGroups(ArrayList<Group> groups){
        filteredGroups.clear();
        filteredGroups.addAll(groups);
        this.groups = groups;
        notifyDataSetChanged();
    }

    public void filter(String text){
        filteredGroups.clear();
        if(text.isEmpty()){
            filteredGroups.addAll(groups);
        } else{
            text = text.toLowerCase();
            for (Group group : groups) {
                if(group.getGroupName().contains(text)){
                    filteredGroups.add(group);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.group_list_item,parent,false);
        return new GroupHolder(v);
    }

    @Override
    public void onBindViewHolder(GroupHolder holder, int position) {
        holder.bindGroup(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class GroupHolder extends RecyclerView.ViewHolder{
        private TextView textGroupName, textGroupMembers, textAdmin;
        private Group group;
        private boolean isAdmin = false;

        GroupHolder(View itemView) {
            super(itemView);
            textAdmin = (TextView) itemView.findViewById(R.id.textAdmin);
            textGroupName = (TextView) itemView.findViewById(R.id.textGroupName);
            textGroupMembers = (TextView) itemView.findViewById(R.id.textGroupMembers);
        }

        void bindGroup(Group group){
            this.group = group;
            textGroupName.setText(group.getGroupName());
            if(group.getMembers()== null){
                textGroupMembers.setText("");
            } else{
                textGroupMembers.setText(String.valueOf(group.getMembers().size()));
            }
            // TODO: 07/12/2016 add logic to check if the user is the admin
            if(isAdmin){
                textAdmin.setVisibility(View.VISIBLE);
            } else{
                textAdmin.setVisibility(View.INVISIBLE);
            }

        }
    }
}
