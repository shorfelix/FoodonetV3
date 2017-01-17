package com.roa.foodonetv3.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.roa.foodonetv3.commonMethods.CommonConstants;
import com.roa.foodonetv3.model.GroupMember;

import java.util.ArrayList;

public class GroupMembersDBHandler {
    private Context context;

    public GroupMembersDBHandler(Context context) {
        this.context = context;
    }

    /** get all groups members */
    public ArrayList<GroupMember> getAllGroupsMembers(){
        ArrayList<GroupMember> members = new ArrayList<>();
        Cursor c = context.getContentResolver().query(FoodonetDBProvider.MembersDB.CONTENT_URI,null,null,null,null);

        long groupID, userID;
        String name, phoneNumber;
        boolean isAdmin;
        while(c!=null && c.moveToNext()){
            groupID = c.getLong(c.getColumnIndex(FoodonetDBProvider.MembersDB.GROUP_ID_COLUMN));
            userID = c.getLong(c.getColumnIndex(FoodonetDBProvider.MembersDB.USER_ID_COLUMN));
            phoneNumber = c.getString(c.getColumnIndex(FoodonetDBProvider.MembersDB.USER_PHONE_COLUMN));
            name = c.getString(c.getColumnIndex(FoodonetDBProvider.MembersDB.USER_NAME_COLUMN));
            isAdmin = c.getShort(c.getColumnIndex(FoodonetDBProvider.MembersDB.IS_ADMIN_COLUMN)) == CommonConstants.VALUE_TRUE;

            members.add(new GroupMember(groupID,userID,phoneNumber,name,isAdmin));
        }
        if(c!=null){
            c.close();
        }
        return members;
    }

    /** replaces all groups members in the db */
    public void replaceAllGroupsMembers(ArrayList<GroupMember> members){
        /** first, delete all existing members */
        deleteAllGroupsMembers();

        ContentResolver resolver = context.getContentResolver();
        ContentValues values;
        GroupMember member;
        for (int i = 0; i < members.size(); i++) {
            values = new ContentValues();
            member = members.get(i);
            values.put(FoodonetDBProvider.MembersDB.GROUP_ID_COLUMN,member.getGroupID());
            values.put(FoodonetDBProvider.MembersDB.USER_ID_COLUMN,member.getUserID());
            values.put(FoodonetDBProvider.MembersDB.USER_NAME_COLUMN,member.getName());
            values.put(FoodonetDBProvider.MembersDB.USER_PHONE_COLUMN,member.getPhoneNumber());
            if(member.isAdmin()){
                values.put(FoodonetDBProvider.MembersDB.IS_ADMIN_COLUMN,CommonConstants.VALUE_TRUE);
            } else{
                values.put(FoodonetDBProvider.MembersDB.IS_ADMIN_COLUMN,CommonConstants.VALUE_FALSE);
            }

            resolver.insert(FoodonetDBProvider.MembersDB.CONTENT_URI,values);
        }
    }

    /** deletes all groups members from db */
    public void deleteAllGroupsMembers(){
        context.getContentResolver().delete(FoodonetDBProvider.MembersDB.CONTENT_URI,null,null);
    }
}
