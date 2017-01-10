package com.roa.foodonetv3.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationPublication {
    private static final String TAG = "RegistrationPublication";

    private static final String REGISTERED_USER_FOR_PUBLICATION = "registered_user_for_publication";
    private static final String PUBLICATION_ID = "publication_id";
    private static final String DATE_OF_REGISTRATION = "date_of_registration";
    private static final String ACTIVE_DEVICE_DEV_UUID = "active_device_dev_uuid";
    private static final String PUBLICATION_VERSION = "publication_version";
    private static final String COLLECTOR_NAME = "collector_name";
    private static final String COLLECTOR_CONTACT_INFO = "collector_contact_info";
    private static final String COLLECTOR_USER_ID = "collector_user_id";

    private int publicationVersion;
    private long publicationID,collectorUserID;
    private double dateOfRegistration;
    private String activeDeviceDevUUID,collectorName,collectorContactInfo;


    public RegistrationPublication(long publicationID, double dateOfRegistration, String activeDeviceDevUUID, int publicationVersion, String collectorName, String collectorContactInfo, long collectorUserID) {
        this.publicationID = publicationID;
        this.dateOfRegistration = dateOfRegistration;
        this.activeDeviceDevUUID = activeDeviceDevUUID;
        this.publicationVersion = publicationVersion;
        this.collectorName = collectorName;
        this.collectorContactInfo = collectorContactInfo;
        this.collectorUserID = collectorUserID;
    }

    /** creates a json object to be sent to the server */
    public JSONObject getJsonForRegistration(){
        JSONObject root = new JSONObject();
        JSONObject registration = new JSONObject();
        try {
            registration.put(PUBLICATION_ID,getPublicationID());
            registration.put(DATE_OF_REGISTRATION,getDateOfRegistration());
            registration.put(ACTIVE_DEVICE_DEV_UUID,getActiveDeviceDevUUID());
            registration.put(PUBLICATION_VERSION,getPublicationVersion());
            registration.put(COLLECTOR_NAME,getCollectorName());
            registration.put(COLLECTOR_CONTACT_INFO,getCollectorContactInfo());
            registration.put(COLLECTOR_USER_ID,getCollectorUserID());
            root.put(REGISTERED_USER_FOR_PUBLICATION,registration);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
        return root;
    }

    public long getPublicationID() {
        return publicationID;
    }

    public void setPublicationID(long publicationID) {
        this.publicationID = publicationID;
    }

    public int getPublicationVersion() {
        return publicationVersion;
    }

    public void setPublicationVersion(int publicationVersion) {
        this.publicationVersion = publicationVersion;
    }

    public long getCollectorUserID() {
        return collectorUserID;
    }

    public void setCollectorUserID(long collectorUserID) {
        this.collectorUserID = collectorUserID;
    }

    public double getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(double dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public String getActiveDeviceDevUUID() {
        return activeDeviceDevUUID;
    }

    public void setActiveDeviceDevUUID(String activeDeviceDevUUID) {
        this.activeDeviceDevUUID = activeDeviceDevUUID;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

    public String getCollectorContactInfo() {
        return collectorContactInfo;
    }

    public void setCollectorContactInfo(String collectorContactInfo) {
        this.collectorContactInfo = collectorContactInfo;
    }
}
