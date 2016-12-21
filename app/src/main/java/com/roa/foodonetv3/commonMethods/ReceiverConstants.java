package com.roa.foodonetv3.commonMethods;

public class ReceiverConstants {

    /** Receiver ACTION */
    public static final String BROADCAST_FOODONET = "broadcastFoodonet";

    /** ACTION_TYPE */
    public static final String ACTION_TYPE = "action_type";

    /** Server Extras */
    public static final String ADDRESS_ARGS = "addressArgs";
    public static final String JSON_TO_SEND = "jsonToSend";
    public static final String SERVICE_ERROR = "serviceError";

    /** Server types */
    public static final int ACTION_GET_PUBLICATIONS_EXCEPT_USER = 1;
    public static final int ACTION_GET_USER_PUBLICATIONS = 2;
    public static final int ACTION_ADD_PUBLICATION = 3;
    public static final int ACTION_EDIT_PUBLICATION = 4;
    public static final int ACTION_DELETE_PUBLICATION = 5;
    public static final int ACTION_GET_REPORTS = 10;
    public static final int ACTION_ADD_REPORT = 11;
    public static final int ACTION_ADD_USER = 20;
    public static final int ACTION_REGISTER_TO_PUBLICATION = 30;
    public static final int ACTION_ADD_GROUP = 40;
    public static final int ACTION_GET_GROUPS = 41;
    public static final int ACTION_ADD_GROUP_MEMBER = 50;
    public static final int ACTION_POST_FEEDBACK = 60;
    public static final int ACTION_ACTIVE_DEVICE_NEW_USER = 70;

    /** Local types */
    public static final int ACTION_FAB_CLICK = 100;
    public static final String FAB_TYPE = "fab_type";

    /** Local Extras*/
    public static final int FAB_TYPE_NEW_GROUP_MEMBER = 1;
    public static final int FAB_TYPE_SAVE_NEW_PUBLICATION = 2;
    public static final int FAB_TYPE_ADD_NEW_PUBLICATION = 3;
}
