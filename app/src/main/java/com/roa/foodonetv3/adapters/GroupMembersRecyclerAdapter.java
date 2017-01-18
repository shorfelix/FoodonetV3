package com.roa.foodonetv3.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.db.GroupMembersDBHandler;
import com.roa.foodonetv3.model.GroupMember;
import java.util.ArrayList;

/** recycler for groupsMembers */
public class GroupMembersRecyclerAdapter extends RecyclerView.Adapter<GroupMembersRecyclerAdapter.MemberHolder>{
    private static final String TAG = "GroupMembersRecyclerAdapter";

    private Context context;
    private ArrayList<GroupMember> members = new ArrayList<>();

    public GroupMembersRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void updateMembers(long groupID){
        GroupMembersDBHandler handler = new GroupMembersDBHandler(context);
        this.members = handler.getGroupMembers(groupID);
        notifyDataSetChanged();
    }

    @Override
    public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MemberHolder(inflater.inflate(R.layout.item_group_member,parent,false));
    }

    @Override
    public void onBindViewHolder(MemberHolder holder, int position) {
        holder.bindMember(members.get(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class MemberHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private GroupMember member;
        private ImageView imageMember, imageRemoveMember;
        private TextView textMemberName;

        MemberHolder(View itemView) {
            super(itemView);
            // TODO: 21/12/2016 add imageMember logic to show if the member is a foodonet user or not
            imageMember = (ImageView) itemView.findViewById(R.id.imageMember);
            textMemberName = (TextView) itemView.findViewById(R.id.textMemberName);
            imageRemoveMember = (ImageView) itemView.findViewById(R.id.imageRemoveMember);
            imageRemoveMember.setOnClickListener(this);
        }

        void bindMember(GroupMember member){
            this.member = member;
            textMemberName.setText(member.getName());
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imageRemoveMember:
                    // TODO: 14/12/2016 add alert dialog and service logic
                    Toast.makeText(context, "remove user", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
