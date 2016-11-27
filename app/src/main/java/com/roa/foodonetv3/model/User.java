package com.roa.foodonetv3.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private static final String TAG = "User";
    private String identityProvider, identityProviderUserID,identityProviderToken,phoneNumber,identityProviderEmail,identityProviderUserName,activeDeviceDevUuid;
    private boolean isLoggedIn;
    public static final String USER_KEY = "user";
    public static final String IDENTITY_PROVIDER = "identity_provider";
    public static final String IDENTITY_PROVIDER_USER_ID = "identity_provider_user_id";
    public static final String IDENTITY_PROVIDER_USER_TOKEN = "identity_provider_token";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String IDENTITY_PROVIDER_EMAIL = "identity_provider_email";
    public static final String IDENTITY_PROVIDER_USER_NAME = "identity_provider_user_name";
    public static final String IS_LOGGED_IN= "is_logged_in";
    public static final String ACTIVE_DEVICE_DEV_UUID= "active_device_dev_uuid";


    public User(String identityProvider, String identityProviderUserID, String identityProviderToken, String phoneNumber, String identityProviderEmail,
                String identityProviderUserName, boolean isLoggedIn, String activeDeviceDevUuid) {
        this.identityProvider = identityProvider;
        this.identityProviderUserID = identityProviderUserID;
        this.identityProviderToken = identityProviderToken;
        this.phoneNumber = phoneNumber;
        this.identityProviderEmail = identityProviderEmail;
        this.identityProviderUserName = identityProviderUserName;
        this.isLoggedIn = isLoggedIn;
        this.activeDeviceDevUuid = activeDeviceDevUuid;
    }

    public JSONObject getUserJson(){
        // not tested yet!!!
        JSONObject userJsonRoot = new JSONObject();
        JSONObject userJson = new JSONObject();
        try {
            userJson.put(IDENTITY_PROVIDER, getIdentityProvider());
            userJson.put(IDENTITY_PROVIDER_USER_ID, getIdentityProviderUserID());
            userJson.put(IDENTITY_PROVIDER_USER_TOKEN, getIdentityProviderToken());
            userJson.put(PHONE_NUMBER, getPhoneNumber());
            userJson.put(IDENTITY_PROVIDER_EMAIL, getIdentityProviderEmail());
            userJson.put(IDENTITY_PROVIDER_USER_NAME, getIdentityProviderUserName());
            userJson.put(IS_LOGGED_IN, isLoggedIn());
            userJson.put(ACTIVE_DEVICE_DEV_UUID, getActiveDeviceDevUuid());

            userJsonRoot.put(USER_KEY,userJson);
        } catch (JSONException e) {
            Log.e("getPublicationJson", e.getMessage());
        }
        return userJsonRoot;
    }

    public String getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(String identityProvider) {
        this.identityProvider = identityProvider;
    }

    public String getIdentityProviderUserID() {
        return identityProviderUserID;
    }

    public void setIdentityProviderUserID(String identityProviderUserID) {
        this.identityProviderUserID = identityProviderUserID;
    }

    public String getIdentityProviderToken() {
        return identityProviderToken;
    }

    public void setIdentityProviderToken(String identityProviderToken) {
        this.identityProviderToken = identityProviderToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdentityProviderEmail() {
        if(identityProviderEmail==null){
            return "";
        }
        return identityProviderEmail;
    }

    public void setIdentityProviderEmail(String identityProviderEmail) {
        this.identityProviderEmail = identityProviderEmail;
    }

    public String getIdentityProviderUserName() {
        return identityProviderUserName;
    }

    public void setIdentityProviderUserName(String identityProviderUserName) {
        this.identityProviderUserName = identityProviderUserName;
    }

    public String getActiveDeviceDevUuid() {
        return activeDeviceDevUuid;
    }

    public void setActiveDeviceDevUuid(String activeDeviceDevUuid) {
        this.activeDeviceDevUuid = activeDeviceDevUuid;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
