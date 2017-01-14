package com.roa.foodonetv3.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

public class LatestPlacesDBHandler {
    private Context context;

    public LatestPlacesDBHandler(Context context) {
        this.context = context;
    }

    /** add a new place to the top of the list of last searched places */
    public void addLatestPlace(String address, double lat, double lng){
        ContentResolver resolver = context.getContentResolver();
        ContentValues values;

        /** change the position of each of the places, and change the values of the last one which will become the first with the data that we received */
        int arraySize = FoodonetDBHelper.NUMBER_OF_LATEST_SEARCHES;

        for (int i = arraySize -1; i >= -1 ; i--) {
            values = new ContentValues();
            String where = String.format("%1$s = ?",FoodonetDBProvider.LatestPlacesDB.POSITION_COLUMN);
            String[] whereArgs = {String.valueOf(i)};

            /** put the values in the last position and change it's position to -1 temporarily */
            if(i == arraySize -1){
                values.put(FoodonetDBProvider.LatestPlacesDB.ADDRESS_COLUMN,address);
                values.put(FoodonetDBProvider.LatestPlacesDB.LAT_COLUMN,lat);
                values.put(FoodonetDBProvider.LatestPlacesDB.LNG_COLUMN,lng);
                values.put(FoodonetDBProvider.LatestPlacesDB.POSITION_COLUMN,-1);
            } else{
                values.put(FoodonetDBProvider.LatestPlacesDB.POSITION_COLUMN, i +1);
            }
            resolver.update(FoodonetDBProvider.LatestPlacesDB.CONTENT_URI,values,where,whereArgs);
        }
    }

    /** empties all places, doesn't delete the rows themselves*/
    public void deleteAllPlaces(){
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(FoodonetDBProvider.LatestPlacesDB.ADDRESS_COLUMN,"");
        values.put(FoodonetDBProvider.LatestPlacesDB.LAT_COLUMN,-9999);
        values.put(FoodonetDBProvider.LatestPlacesDB.LNG_COLUMN,-9999);

        resolver.update(FoodonetDBProvider.LatestPlacesDB.CONTENT_URI,values,null,null);
    }
}
