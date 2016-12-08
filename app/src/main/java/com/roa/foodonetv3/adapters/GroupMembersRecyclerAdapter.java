package com.roa.foodonetv3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.model.GroupMember;
import java.util.ArrayList;

public class GroupMembersRecyclerAdapter extends RecyclerView.Adapter<GroupMembersRecyclerAdapter.MemberHolder>{
    private static final String TAG = "GroupMembersRecyclerAdapter";

    private Context context;
    private ArrayList<GroupMember> members = new ArrayList<>();

    public GroupMembersRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void updateMembers(ArrayList<GroupMember> members){
        this.members = members;
        notifyDataSetChanged();
    }

    @Override
    public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MemberHolder(inflater.inflate(R.layout.group_member_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MemberHolder holder, int position) {
        holder.bindMember(members.get(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class MemberHolder extends RecyclerView.ViewHolder {
        private GroupMember member;
        private ImageView imageMember;
        private TextView textMemberName;

        public MemberHolder(View itemView) {
            super(itemView);
            imageMember = (ImageView) itemView.findViewById(R.id.imageMember);
            textMemberName = (TextView) itemView.findViewById(R.id.textMemberName);
        }

        public void bindMember(GroupMember member){
            this.member = member;
            textMemberName.setText(member.getName());
        }
    }
}
