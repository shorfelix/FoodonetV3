package com.roa.foodonetv3.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.commonMethods.StartServiceMethods;
import com.roa.foodonetv3.model.Group;
import com.roa.foodonetv3.model.GroupMember;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.model.PublicationReport;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;


public class FoodonetService extends IntentService {
    private static final String TAG = "FoodonetService";
    private static final int TIMEOUT_TIME = 5000;

    private int responseCode;

    public FoodonetService() {
        super("FoodonetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /** a universal service for all foodonet server communications */
        if (intent != null) {
            Intent finishedIntent = new Intent(ReceiverConstants.BROADCAST_FOODONET);
            boolean serviceError = false;
            int actionType = intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1);
            String[] args = intent.getStringArrayExtra(ReceiverConstants.ADDRESS_ARGS);
            String urlAddress = StartServiceMethods.getUrlAddress(this,actionType, args);
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            URL url;
            StringBuilder builder = new StringBuilder();
            try {
                url = new URL(urlAddress);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setConnectTimeout(TIMEOUT_TIME);
                // TODO: 28/11/2016 add logic for timeout
                int httpType = StartServiceMethods.getHTTPType(actionType);
                switch (httpType){
                    case StartServiceMethods.HTTP_GET:
                        break;
                    case StartServiceMethods.HTTP_POST:
                        connection.setRequestMethod("POST");
                        connection.addRequestProperty("Accept","application/json");
                        connection.addRequestProperty("Content-Type","application/json");
                        connection.setDoOutput(true);
                        OutputStream postOs = connection.getOutputStream();
                        BufferedWriter postWriter = new BufferedWriter(new OutputStreamWriter(postOs,"utf-8"));
                        String postJsonToSend = intent.getStringExtra(ReceiverConstants.JSON_TO_SEND);
                        postWriter.write(postJsonToSend);
                        postWriter.flush();
                        postWriter.close();
                        postOs.close();
                        break;
                    case StartServiceMethods.HTTP_PUT:
                        connection.setRequestMethod("PUT");
                        connection.addRequestProperty("Accept","application/json");
                        connection.addRequestProperty("Content-Type","application/json");
                        connection.setDoOutput(true);
                        OutputStream putOs = connection.getOutputStream();
                        BufferedWriter putWriter = new BufferedWriter(new OutputStreamWriter(putOs,"utf-8"));
                        String putJsonToSend = intent.getStringExtra(ReceiverConstants.JSON_TO_SEND);
                        putWriter.write(putJsonToSend);
                        putWriter.flush();
                        putWriter.close();
                        putOs.close();
                        break;
                    case StartServiceMethods.HTTP_DELETE:
                        connection.setRequestMethod("DELETE");
                        break;
                }
                responseCode = connection.getResponseCode();
                if(responseCode!=HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED){
                    serviceError = true;
                } else{
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while((line = reader.readLine())!= null){
                        builder.append(line);
                    }
                    finishedIntent = addResponseToIntent(actionType,builder.toString(),finishedIntent);
                }
            } catch (IOException e) {
                serviceError = true;
                Log.e(TAG,e.getMessage());
            }
            finally {
                if(connection!= null){
                    connection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());
                    }
                }
            }
            finishedIntent.putExtra(ReceiverConstants.ACTION_TYPE,actionType);
            finishedIntent.putExtra(ReceiverConstants.SERVICE_ERROR,serviceError);
            LocalBroadcastManager.getInstance(this).sendBroadcast(finishedIntent);
        }
    }

    private Intent addResponseToIntent(int actionType, String responseRoot, Intent intent){
        try {
            int userID = CommonMethods.getMyUserID(this);
            switch (actionType) {
                case ReceiverConstants.ACTION_GET_PUBLICATIONS_EXCEPT_USER:
                case ReceiverConstants.ACTION_GET_USER_PUBLICATIONS:
                    ArrayList<Publication> publications = new ArrayList<>();
                    JSONArray rootGetPublications;
                    rootGetPublications = new JSONArray(responseRoot);
                    /** declarations */
                    long id;
                    int version;
                    String title;
                    String subtitle;
                    String address;
                    short typeOfCollecting;
                    Double lat;
                    Double lng;
                    String startingDate;
                    String endingDate;
                    String contactInfo;
                    boolean isOnAir;
                    String activeDeviceDevUUID;
                    String photoURL;
                    int audience;
                    String identityProviderUserName;
                    Double price;
                    String priceDescription;
                    for (int i = 0; i < rootGetPublications.length(); i++) {
                        JSONObject publication = rootGetPublications.getJSONObject(i);
                        int publisherID = publication.getInt("publisher_id");
                        if (publisherID != userID && actionType == ReceiverConstants.ACTION_GET_PUBLICATIONS_EXCEPT_USER
                                || publisherID == userID && actionType == ReceiverConstants.ACTION_GET_USER_PUBLICATIONS) {
                            /** depending on the action intended, either get all publications except the ones created by the user, or get only the publications created by the user */
                            id = publication.getLong("id");
                            version = publication.getInt("version");
                            title = publication.getString("title");
                            subtitle = publication.getString("subtitle");
                            address = publication.getString("address");
                            typeOfCollecting = (short) publication.getInt("type_of_collecting");
                            lat = publication.getDouble("latitude");
                            lng = publication.getDouble("longitude");
                            startingDate = publication.getString("starting_date");
                            endingDate = publication.getString("ending_date");
                            contactInfo = publication.getString("contact_info");
                            if (publication.getBoolean("is_on_air")) isOnAir = true;
                            else isOnAir = false;
                            activeDeviceDevUUID = publication.getString("active_device_dev_uuid");
                            photoURL = publication.getString("photo_url");
                            audience = publication.getInt("audience");
                            identityProviderUserName = publication.getString("identity_provider_user_name");
                            price = publication.getDouble("price");
                            priceDescription = "price_description";
                            publications.add(new Publication(id, version, title, subtitle, address, typeOfCollecting, lat, lng, startingDate, endingDate, contactInfo, isOnAir,
                                    activeDeviceDevUUID, photoURL, publisherID, audience, identityProviderUserName, price, priceDescription));
                        }
                    }
                    intent.putParcelableArrayListExtra(Publication.PUBLICATION_KEY, publications);
                    break;

                case ReceiverConstants.ACTION_ADD_PUBLICATION:
                    // TODO: 27/11/2016 add logic to save the publication id to db
                    break;

                case ReceiverConstants.ACTION_EDIT_PUBLICATION: // not tested
                    // TODO: 27/11/2016 check versions and the like
                    break;

                case ReceiverConstants.ACTION_DELETE_PUBLICATION:
                    Log.d(TAG,responseRoot);
                    break;

                case ReceiverConstants.ACTION_GET_REPORTS:
                    ArrayList<PublicationReport> reports = new ArrayList<>();
                    JSONArray rootGetReports;
                    rootGetReports = new JSONArray(responseRoot);
                    /** declarations */
                    long reportId;
                    long publicationID;
                    int publicationVersion;
                    int reportType;
                    String active_device_dev_uuid;
                    String createdDate;
                    String updateDate;
                    String dateOfReport;
                    String reportUserName;
                    String reportContactInfo;
                    int reportUserId;
                    int rating;
                    for (int i = 0; i < rootGetReports.length(); i++) {
                        JSONObject report = rootGetReports.getJSONObject(i);
                        reportId = report.getLong("id");
                        publicationID = report.getLong("publication_id");
                        publicationVersion = report.getInt("publication_version");
                        reportType = report.getInt("report");
                        active_device_dev_uuid = report.getString("active_device_dev_uuid");
                        createdDate = report.getString("created_at");
                        updateDate = report.getString("updated_at");
                        dateOfReport = report.getString("date_of_report");
                        reportUserName = "No user name";
                        if (report.getString("report_user_name") != null) {
                            reportUserName = report.getString("report_user_name");
                        }
                        reportContactInfo = report.getString("report_contact_info");
                        reportUserId = report.getInt("reporter_user_id");
                        rating = report.getInt("rating");

                        reports.add(new PublicationReport(reportId, publicationID, publicationVersion, reportType, active_device_dev_uuid,
                                createdDate, updateDate, dateOfReport, reportUserName, reportContactInfo, reportUserId, rating));
                        }
                    intent.putParcelableArrayListExtra(PublicationReport.REPORT_KEY, reports);
                    break;

                case ReceiverConstants.ACTION_ADD_REPORT: // not tested
                    // TODO: 27/11/2016 add logic
                    break;

                case ReceiverConstants.ACTION_ADD_USER:
                    JSONObject rootAddUser;
                    rootAddUser = new JSONObject(responseRoot);
                    int newUserID = rootAddUser.getInt("id");
                    CommonMethods.setMyUserID(this, newUserID);
                    Log.d("Add user response", "id: " + newUserID);
                    break;

                case ReceiverConstants.ACTION_REGISTER_TO_PUBLICATION:
                    // TODO: 27/11/2016 update
                    break;

                case ReceiverConstants.ACTION_ADD_GROUP:
                    // TODO: 06/12/2016 add logic according to what we receive
                    JSONObject rootAddGroup = new JSONObject(responseRoot);
                    int newGroupID = rootAddGroup.getInt("id");
                    intent.putExtra(Group.KEY,newGroupID);
                    break;

                case ReceiverConstants.ACTION_GET_GROUPS:
                    JSONArray groupArray = new JSONArray(responseRoot);
                    /** declarations */
                    ArrayList<Group> groups = new ArrayList<>();
                    int groupID;
                    String groupName;
                    int memberID;
                    String phoneNumber;
                    String memberName;
                    boolean isAdmin;
                    for (int i = 0; i < groupArray.length(); i++) {
                        JSONObject group = groupArray.getJSONObject(i);
                        userID = group.getInt(Group.USER_ID);
                        groupID = group.getInt(Group.GROUP_ID);
                        groupName = group.getString(Group.GET_GROUP_NAME);
                        ArrayList<GroupMember> members = new ArrayList<>();
                        JSONArray membersArray = group.getJSONArray(Group.MEMBERS);
                        for (int j = 0; j < membersArray.length(); j++) {
                            JSONObject member = membersArray.getJSONObject(j);
                            memberID = member.getInt(GroupMember.USER_ID);
                            phoneNumber = member.getString(GroupMember.PHONE_NUMBER);
                            memberName = member.getString(GroupMember.NAME);
                            isAdmin = member.getBoolean(GroupMember.IS_ADMIN);
                            members.add(new GroupMember(groupID,memberID,phoneNumber,memberName,isAdmin));
                        }
                        groups.add(new Group(groupName,userID,members,groupID));
                    }
                    intent.putParcelableArrayListExtra(Group.KEY,groups);
                    break;

                case ReceiverConstants.ACTION_ADD_GROUP_MEMBER:
                    Log.d("TEST!!!!!!!",responseRoot);
                    break;

                case ReceiverConstants.ACTION_POST_FEEDBACK:
                    break;

                case ReceiverConstants.ACTION_ACTIVE_DEVICE_NEW_USER:
                    Log.d(TAG,responseRoot);
                    break;
            }
        } catch (JSONException e){
            Log.e(TAG,e.getMessage());
        }
        return intent;
    }
}
