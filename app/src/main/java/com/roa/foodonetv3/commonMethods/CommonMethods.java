package com.roa.foodonetv3.commonMethods;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.activities.MapActivity;

import java.text.DecimalFormat;

/**
 * Created by Owner on 13/10/2016.
 */

public class CommonMethods {


    public static void navigationItemSelectedAction(Context context, int id){
        /** handle the navigation actions from the drawer*/
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(context, MapActivity.class);
            context.startActivity(intent);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
    }

    public static String getDeviceUUID(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString("device_uuid",null);
    }

    public static long getNewLocalPublicationID() {
        //todo add a check for available negative id, currently hard coded
        return -1;
    }

    public static String getRoundedStringFromNumber(float num){
        DecimalFormat df = new DecimalFormat("####0.00");
        return df.format(num);
    }
    public static String getRoundedStringFromNumber(double num){
        DecimalFormat df = new DecimalFormat("####0.00");
        return df.format(num);
    }
}
