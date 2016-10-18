package com.roa.foodonetv3.commonMethods;

import android.content.Context;
import android.content.Intent;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.activities.MapActivity;

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
}
