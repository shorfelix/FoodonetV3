package com.roa.foodonetv3.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import java.util.List;

public class FoodonetDBProvider extends ContentProvider {
    private static final String TAG = "FoodonetDBProvider";
    private static final String AUTHORITY = "com.roa.foodonetv3.db.FoodonetDBProvider";
    private FoodonetDBHelper helper;

    /** declarations of the different tables */
    public static class PublicationsDB{
        public static final String TABLE_NAME = "publications";
        public static final String PUBLICATION_ID_COLUMN = "publication_id";
        public static final String TITLE_COLUMN = "title";
        public static final String DETAILS_COLUMN = "details";
        public static final String ADDRESS_COLUMN = "address";
        public static final String TYPE_OF_COLLECTING_COLUMN = "type_of_collecting";
        public static final String LATITUDE_COLUMN = "latitude";
        public static final String LONGITUDE_COLUMN = "longitude";
        public static final String STARTING_TIME_COLUMN = "starting_time";
        public static final String ENDING_TIME_COLUMN = "ending_time";
        public static final String CONTACT_PHONE_COLUMN = "contact_phone";
        public static final String PHOTO_URL_COLUMN = "photo_url";
        public static final String IS_ON_AIR_COLUMN = "is_on_air";
        public static final String PUBLISHER_ID_COLUMN = "publisher_id";
        public static final String PRICE_COLUMN = "price";
        public static final String AUDIENCE_COLUMN = "audience";
        public static final String PRICE_DESC_COLUMN = "price_desc";
        public static final String PUBLICATION_VERSION_COLUMN = "publication_version";
        public static final String PROVIDER_USER_NAME_COLUMN = "provider_user_name";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);

        public static final short TYPE_GET_USER_PUBLICATIONS = 1;
        public static final short TYPE_GET_NON_USER_PUBLICATIONS = 2;
    }

    public static class ReportsDB{
        public static final String TABLE_NAME = "reports";
        public static final String REPORT_ID_COLUMN = "report_id";
        public static final String PUBLICATION_ID_COLUMN = "publication_id";
        public static final String PUBLICATION_VERSION_COLUMN = "publication_version";
        public static final String REPORT_COLUMN = "report";
        public static final String TIME_OF_REPORT_COLUMN = "time_of_report";
        public static final String REPORT_RATING_COLUMN = "report_rating";
        public static final String USER_ID_COLUMN = "user_id";
        public static final String USER_NAME_COLUMN = "user_name";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);
    }

    public static class GroupsDB{
        public static final String TABLE_NAME = "groups";
        public static final String GROUP_ID_COLUMN = "group_id";
        public static final String GROUP_NAME_COLUMN = "group_name";
        public static final String ADMIN_ID_COLUMN = "admin_id";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);
    }

    public static class MembersDB{
        public static final String TABLE_NAME = "users";
        public static final String _ID_COLUMN = "_id";
        public static final String GROUP_ID_COLUMN = "group_id";
        public static final String USER_ID_COLUMN = "user_id";
        public static final String USER_NAME_COLUMN = "user_name";
        public static final String USER_PHONE_COLUMN = "user_phone";
        public static final String IS_ADMIN_COLUMN = "is_admin";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);
    }

    public static class LatestPlacesDB{
        public static final String TABLE_NAME = "latest_places";
        public static final String _ID_COLUMN = "_id";
        public static final String POSITION_COLUMN = "position";
        public static final String ADDRESS_COLUMN = "address";
        public static final String LAT_COLUMN = "lat";
        public static final String LNG_COLUMN = "lng";
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);
    }

    @Override
    public boolean onCreate() {
        helper = new FoodonetDBHelper(getContext());
        if(helper!=null){
            return true;
        } return false;
    }

    public String getTableName(Uri uri){
        List<String> pathSegment = uri.getPathSegments();
        return pathSegment.get(0);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(getTableName(uri),projection,selection,selectionArgs,null,null,sortOrder);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long id = db.insert(getTableName(uri),null,values);
        if(id != -1){
            return ContentUris.withAppendedId(uri,id);
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.delete(getTableName(uri),selection,selectionArgs);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = db.update(getTableName(uri),values,selection,selectionArgs);
        return count;
    }
}
