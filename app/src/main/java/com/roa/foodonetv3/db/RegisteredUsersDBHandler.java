package com.roa.foodonetv3.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.util.LongSparseArray;

import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.model.RegisteredUser;

import java.util.ArrayList;

public class RegisteredUsersDBHandler {
    private Context context;

    public RegisteredUsersDBHandler(Context context) {
        this.context = context;
    }

    public ArrayList<RegisteredUser> getAllRegisteredUsers(){
        Cursor c = context.getContentResolver().query(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,null,null,null,null);
        ArrayList<RegisteredUser> registeredUsers = new ArrayList<>();
        long publicationID, registeredUserID;
        int publicationVersion;

        while(c!=null && c.moveToNext()){
            publicationID = c.getLong(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN));
            publicationVersion = c.getInt(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_VERSION_COLUMN));
            registeredUserID = c.getLong(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ID_COLUMN));
            registeredUsers.add(new RegisteredUser(publicationID,-1,null,publicationVersion,null,null,registeredUserID));
        }
        if(c!=null){
            c.close();
        }
        return registeredUsers;
    }

    public LongSparseArray<Integer> getAllRegisteredUsersCount(){
        Cursor c = context.getContentResolver().query(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,null,null,null,null);
        LongSparseArray<Integer> registeredUsers = new LongSparseArray<>();
        long publicationID;
        Integer count;

        while(c!=null && c.moveToNext()){
            publicationID = c.getLong(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN));
            count = registeredUsers.get(publicationID);
            if(count==null){
                registeredUsers.put(publicationID,1);
            } else{
                registeredUsers.put(publicationID,count+1);
            }
        }
        if(c!=null){
            c.close();
        }
        return registeredUsers;
    }

    public int getRegisteredUsersCount(long publicationID){
        String[] projection = {FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ID_COLUMN};
        String where = String.format("%1$s = ?",FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN);
        String[] whereArgs = {String.valueOf(publicationID)};
        Cursor c = context.getContentResolver().query(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,projection,where,whereArgs,null);
        int count = 0;

        while(c!=null && c.moveToNext()){
            count++;
        }
        if(c!=null){
            c.close();
        }
        return count;
    }

    public boolean isUserRegistered(long publicationID){
        long userID = CommonMethods.getMyUserID(context);
        String[] projection = {FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ID_COLUMN};
        String where = String.format("%1$s = ? AND %2$s = ?",FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN,FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ID_COLUMN);
        String[] whereArgs = {String.valueOf(publicationID),String.valueOf(userID)};

        Cursor c = context.getContentResolver().query(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,projection,where,whereArgs,null);
        boolean found = false;
        if(c!=null && c.moveToNext()){
            found = true;
        }
        if(c!=null){
            c.close();
        }
        return found;
    }

    public void replaceAllRegisteredUsers(ArrayList<RegisteredUser> registeredUsers){
        deleteAllRegisteredUsers();

        ContentResolver resolver = context.getContentResolver();
        ContentValues values;
        RegisteredUser registeredUser;

        for(int i = 0; i < registeredUsers.size(); i++) {
            values = new ContentValues();
            registeredUser = registeredUsers.get(i);

            values.put(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN,registeredUser.getPublicationID());
            values.put(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_VERSION_COLUMN,registeredUser.getPublicationVersion());
            values.put(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ID_COLUMN,registeredUser.getCollectorUserID());

            resolver.insert(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,values);
        }
    }

    public void deleteAllRegisteredUsers(){
        context.getContentResolver().delete(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,null,null);
    }
}
