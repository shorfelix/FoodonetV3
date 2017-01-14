package com.roa.foodonetv3.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.roa.foodonetv3.model.Group;

import java.util.ArrayList;

public class GroupsDBHandler {
    private Context context;

    public GroupsDBHandler(Context context) {
        this.context = context;
    }

    /** get all groups */
    public ArrayList<Group> getAllGroups(){
        Cursor c = context.getContentResolver().query(FoodonetDBProvider.GroupsDB.CONTENT_URI,null,null,null,null);
        ArrayList<Group> groups = new ArrayList<>();
        /** declarations */
        String groupName;
        long userID, groupID;
        Group group;
        while(c!=null && c.moveToNext()){
            groupID = c.getLong(c.getColumnIndex(FoodonetDBProvider.GroupsDB.GROUP_ID_COLUMN));
            groupName = c.getString(c.getColumnIndex(FoodonetDBProvider.GroupsDB.GROUP_NAME_COLUMN));
            userID = c.getLong(c.getColumnIndex(FoodonetDBProvider.GroupsDB.ADMIN_ID_COLUMN));

            groups.add(new Group(groupName,userID,null,groupID));
        }
        if(c!=null){
            c.close();
        }
        return groups;
    }

    /** get all groups IDs */
    public ArrayList<Long> getGroupsIDs(){
        Cursor c = context.getContentResolver().query(FoodonetDBProvider.GroupsDB.CONTENT_URI,null,null,null,null);
        ArrayList<Long> groupsIDs = new ArrayList<>();
        long groupID;
        while(c!=null && c.moveToNext()){
            groupID = c.getLong(c.getColumnIndex(FoodonetDBProvider.GroupsDB.GROUP_ID_COLUMN));

            groupsIDs.add(groupID);
        }
        if(c!=null){
            c.close();
        }
        return groupsIDs;
    }

    public void replaceAllGroups(ArrayList<Group> groups){
        /** first, delete all current groups from db */
        deleteAllGroups();

        ContentResolver resolver = context.getContentResolver();
        /** declarations */
        ContentValues values;
        Group group;
        for (int i = 0; i < groups.size(); i++) {
            values = new ContentValues();
            group = groups.get(i);
            values.put(FoodonetDBProvider.GroupsDB.GROUP_ID_COLUMN,group.getGroupID());
            values.put(FoodonetDBProvider.GroupsDB.GROUP_NAME_COLUMN,group.getGroupName());
            values.put(FoodonetDBProvider.GroupsDB.ADMIN_ID_COLUMN,group.getUserID());

            resolver.insert(FoodonetDBProvider.GroupsDB.CONTENT_URI,values);
        }
    }

    /** deletes all groups from the db */
    public void deleteAllGroups(){
        context.getContentResolver().delete(FoodonetDBProvider.GroupsDB.CONTENT_URI,null,null);
    }
}
