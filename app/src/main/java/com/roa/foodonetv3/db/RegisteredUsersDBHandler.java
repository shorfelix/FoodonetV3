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

    /** gets all the registered users */
    public ArrayList<RegisteredUser> getAllRegisteredUsers(){
        Cursor c = context.getContentResolver().query(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,null,null,null,null);
        ArrayList<RegisteredUser> registeredUsers = new ArrayList<>();
        long publicationID, registeredUserID;
        int publicationVersion;
        String activeDeviceUUID, userName, userPhone;

        while(c!=null && c.moveToNext()){
            publicationID = c.getLong(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN));
            publicationVersion = c.getInt(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_VERSION_COLUMN));
            registeredUserID = c.getLong(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ID_COLUMN));
            activeDeviceUUID = c.getString(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ACTIVE_DEVICE_UUID));
            userName = c.getString(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_NAME));
            userPhone = c.getString(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_PHONE));

            registeredUsers.add(new RegisteredUser(publicationID,(double)-1,activeDeviceUUID,publicationVersion,userName,userPhone,registeredUserID));
        }
        if(c!=null){
            c.close();
        }
        return registeredUsers;
    }

    /** @return an array with the publicationID as key and value of the number of registered users for that publication */
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

    /** @return the count of registered users the publication has */
    public int getPublicationRegisteredUsersCount(long publicationID){
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

    /** @return ArrayList<RegisteredUser> the registered users of the publication */
    public ArrayList<RegisteredUser> getPublicationRegisteredUsers(long publicationID){
        String where = String.format("%1$s = ?",FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN);
        String[] whereArgs = {String.valueOf(publicationID)};
        Cursor c = context.getContentResolver().query(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,null,where,whereArgs,null);
        ArrayList<RegisteredUser> registeredUsers = new ArrayList<>();
        long registeredUserID;
        int publicationVersion;
        String activeDeviceUUID, userName, userPhone;

        while(c!=null && c.moveToNext()){
            publicationID = c.getLong(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN));
            publicationVersion = c.getInt(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_VERSION_COLUMN));
            registeredUserID = c.getLong(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ID_COLUMN));
            activeDeviceUUID = c.getString(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ACTIVE_DEVICE_UUID));
            userName = c.getString(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_NAME));
            userPhone = c.getString(c.getColumnIndex(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_PHONE));

            registeredUsers.add(new RegisteredUser(publicationID,(double)-1,activeDeviceUUID,publicationVersion,userName,userPhone,registeredUserID));
        }
        if(c!=null){
            c.close();
        }
        return registeredUsers;
    }

    /** @return true if the user is registered to this publication */
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

    /** manually insert a new registered user for a publication */
    public void insertRegisteredUser(RegisteredUser registeredUser){
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN,registeredUser.getPublicationID());
        values.put(FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_VERSION_COLUMN,registeredUser.getPublicationVersion());
        values.put(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ID_COLUMN,registeredUser.getCollectorUserID());
        values.put(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ACTIVE_DEVICE_UUID,registeredUser.getActiveDeviceDevUUID());
        values.put(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_NAME,registeredUser.getCollectorName());
        values.put(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_PHONE,registeredUser.getCollectorContactInfo());

        resolver.insert(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,values);
    }

    /** replaces all registered users */
    public void replaceAllRegisteredUsers(ArrayList<RegisteredUser> registeredUsers){
        /** first, delete all data */
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
            values.put(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_ACTIVE_DEVICE_UUID,registeredUser.getActiveDeviceDevUUID());
            values.put(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_NAME,registeredUser.getCollectorName());
            values.put(FoodonetDBProvider.RegisteredUsersDB.REGISTERED_USER_PHONE,registeredUser.getCollectorContactInfo());

            resolver.insert(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,values);
        }
    }

    /** delete the user from the publication registered users */
    public void deleteRegisteredUser(long publicationID){
        ContentResolver resolver = context.getContentResolver();
        String where = String.format("%1$s = ?",FoodonetDBProvider.RegisteredUsersDB.PUBLICATION_ID_COLUMN);
        String[] whereArgs = {String.valueOf(publicationID)};
        resolver.delete(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,where,whereArgs);
    }

    /** deletes all registered users */
    public void deleteAllRegisteredUsers(){
        context.getContentResolver().delete(FoodonetDBProvider.RegisteredUsersDB.CONTENT_URI,null,null);
    }
}
