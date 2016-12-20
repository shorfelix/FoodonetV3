package com.roa.foodonetv3.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Group implements Parcelable {
    private static final String TAG = "Group";

    public static final String KEY = "group";
    public static final String GROUP_MEMBERS = "group_members";

    public static final String GROUP = "group";
    public static final String ADD_GROUP_NAME = "name";
    public static final String USER_ID = "user_id";
    public static final String MEMBERS = "members";
    public static final String GROUP_ID = "group_id";
    public static final String GET_GROUP_NAME = "group_name";

    private String groupName;
    private int userID, groupID;
    private ArrayList<GroupMember> members;

    public Group(String groupName, int userID, ArrayList<GroupMember> members, int groupID) {
        this.groupName = groupName;
        this.userID = userID;
        this.members = members;
        this.groupID = groupID;
    }

    protected Group(Parcel in) {
        groupName = in.readString();
        userID = in.readInt();
        members = in.createTypedArrayList(GroupMember.CREATOR);
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public void addToMembers(GroupMember member){
        members.add(member);
    }

    public JSONObject getAddGroupJson(){
        JSONObject groupRoot = new JSONObject();
        JSONObject group = new JSONObject();
        try {
            group.put(ADD_GROUP_NAME,getGroupName());
            group.put(USER_ID,getUserID());

            groupRoot.put(GROUP,group);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
        return groupRoot;
    }

    public JSONObject getAddGroupMembersJson(){
        ArrayList<GroupMember> members = getMembers();
        JSONObject groupMembersRoot = new JSONObject();
        JSONArray groupMembersArray = new JSONArray();
        GroupMember member;
        try {
            for (int i = 0; i < members.size(); i++) {
                member = members.get(i);
                JSONObject groupMember = member.getAddMemberJson();
                groupMembersArray.put(groupMember);
            }
            groupMembersRoot.put(GROUP_MEMBERS,groupMembersArray);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
        return groupMembersRoot;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public ArrayList<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<GroupMember> members) {
        this.members = members;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        dest.writeInt(userID);
        dest.writeInt(groupID);
        dest.writeTypedList(members);
    }
}
