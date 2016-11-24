package com.roa.foodonetv3.commonMethods;


import android.content.Context;
import android.content.Intent;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.services.GetPublicationsService;

public class StartServiceMethods {
    private static final String TAG = "StartServiceMethods";
    public static final String ACTION_TYPE = "action_type";
    public static final int ACTION_GET_PUBLICATIONS_EXCEPT_USER = 1;
    public static final int ACTION_GET_USER_PUBLICATIONS = 2;

    public static void startGetPublicationsService(Context context, int action){
        Intent i = new Intent(context, GetPublicationsService.class);
        switch (action){
            case ACTION_GET_PUBLICATIONS_EXCEPT_USER:
                i.putExtra(ACTION_TYPE,ACTION_GET_PUBLICATIONS_EXCEPT_USER);
                break;
            case ACTION_GET_USER_PUBLICATIONS:
                i.putExtra(ACTION_TYPE,ACTION_GET_USER_PUBLICATIONS);
                break;
        }
        context.startService(i);
    }
}
