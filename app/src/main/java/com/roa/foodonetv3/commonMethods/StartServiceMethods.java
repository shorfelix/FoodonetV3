package com.roa.foodonetv3.commonMethods;


import android.content.Context;

import com.roa.foodonetv3.R;

import java.util.Locale;

public class StartServiceMethods {
    private static final String TAG = "StartServiceMethods";
    public static final String ACTION_TYPE = "action_type";

    /** ACTION_TYPE */
    public static final int ACTION_GET_PUBLICATIONS_EXCEPT_USER = 1;
    public static final int ACTION_GET_USER_PUBLICATIONS = 2;
    public static final int ACTION_ADD_PUBLICATION = 3;
    public static final int ACTION_EDIT_PUBLICATION = 4;
    public static final int ACTION_GET_REPORTS = 10;
    public static final int ACTION_ADD_REPORT = 11;
    public static final int ACTION_ADD_USER = 20;
    public static final int ACTION_REGISTER_TO_PUBLICATION = 30;

    /** HTTP_TYPE */
    public static final int HTTP_GET = 1;
    public static final int HTTP_POST = 2;
    public static final int HTTP_PUT = 3;
    public static final int HTTP_DELETE = 4;


    /** SEND INTENTS: (putExtra)
     * all actions - add String ACTION_TYPE to intent
     * edit publication - add args[0] = (String)publication id
     * get reports - add args[0] = (String) publication id, args[1] = (String) publication version
     * add report - add args[0] = (String) publication id
     * register for publication - add args[0] = (String) publication id
     * actions in post - add String JSON_TO_SEND to intent */

    public static String getUrlAddress(Context context, int actionType, String[] args) {
        /** prepares the url address according to the action intended */
        StringBuilder builder = new StringBuilder();

        /** add the server to foodonet */
        builder.append(context.getResources().getString(R.string.foodonet_server));
        switch (actionType){
            case ACTION_GET_PUBLICATIONS_EXCEPT_USER:
            case ACTION_GET_USER_PUBLICATIONS:
                builder.append(context.getString(R.string.foodonet_publications));
                break;
            case ACTION_ADD_PUBLICATION:
                builder.append(context.getResources().getString(R.string.foodonet_publications));
                builder.append(context.getResources().getString(R.string._json));
                break;
            case ACTION_EDIT_PUBLICATION: // not tested
                builder.append(context.getResources().getString(R.string.foodonet_publications));
                builder.append(String.format(Locale.US,"/%1$s",args[0]));
                builder.append(context.getResources().getString(R.string._json));
                break;
            case ACTION_GET_REPORTS:
                builder.append(context.getResources().getString(R.string.foodonet_publications));
                builder.append(String.format(Locale.US,"/%1$s",args[0]));
                builder.append(context.getResources().getString(R.string.foodonet_reports_version));
                builder.append(args[1]);
                break;
            case ACTION_ADD_REPORT: // not tested
                builder.append(context.getResources().getString(R.string.foodonet_publications));
                builder.append(String.format(Locale.US,"/%1$s",args[0]));
                builder.append(context.getResources().getString(R.string.foodonet_publication_reports));
                break;
            case ACTION_ADD_USER:
                builder.append(context.getResources().getString(R.string.foodonet_user));
                break;
            case ACTION_REGISTER_TO_PUBLICATION:
                builder.append(context.getResources().getString(R.string.foodonet_publications));
                builder.append(String.format(Locale.US,"/%1$s",args[0]));
                builder.append(context.getResources().getString(R.string.foodonet_registered_user_for_publication));
                break;
        }
        return builder.toString();
    }

    public static int getHTTPType(int actionType){
        switch (actionType){
            case ACTION_GET_PUBLICATIONS_EXCEPT_USER:
                return HTTP_GET;
            case ACTION_GET_USER_PUBLICATIONS:
                return HTTP_GET;
            case ACTION_ADD_PUBLICATION:
                return HTTP_POST;
            case ACTION_EDIT_PUBLICATION: // not tested
                return HTTP_POST;
            case ACTION_GET_REPORTS:
                return HTTP_GET;
            case ACTION_ADD_REPORT: // not tested
                return HTTP_POST;
            case ACTION_ADD_USER:
                return HTTP_POST;
            case ACTION_REGISTER_TO_PUBLICATION:
                return HTTP_POST;
        }
        return -1;
    }
}
