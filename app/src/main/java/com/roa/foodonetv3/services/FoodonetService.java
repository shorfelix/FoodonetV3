package com.roa.foodonetv3.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.commonMethods.StartServiceMethods;
import com.roa.foodonetv3.db.GroupsDBHandler;
import com.roa.foodonetv3.db.PublicationsDBHandler;
import com.roa.foodonetv3.db.RegisteredUsersDBHandler;
import com.roa.foodonetv3.model.Group;
import com.roa.foodonetv3.model.GroupMember;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.model.PublicationReport;
import com.roa.foodonetv3.model.RegisteredUser;
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

/** main service to communicate with foodonet server */
public class FoodonetService extends IntentService {
    private static final String TAG = "FoodonetService";

    private static final int TIMEOUT_TIME = 5000;

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
                    default:
                        serviceError = true;
                }
                int responseCode = connection.getResponseCode();
                if(responseCode !=HttpsURLConnection.HTTP_OK && responseCode != HttpsURLConnection.HTTP_CREATED){
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
            switch (actionType) {
                case ReceiverConstants.ACTION_GET_PUBLICATIONS:
                    /** get the users groups id, as we don't care about the others */
                    GroupsDBHandler groupsDBHandler1 = new GroupsDBHandler(this);
                    ArrayList<Long> groupsIDs = groupsDBHandler1.getGroupsIDs();
                    ArrayList<Publication> publications = new ArrayList<>();
                    JSONArray rootGetPublications;
                    rootGetPublications = new JSONArray(responseRoot);
                    /** declarations */
                    long id,audience;
                    String title,subtitle,address,startingDate,endingDate,contactInfo,activeDeviceDevUUID,photoURL,identityProviderUserName,priceDescription;
                    short typeOfCollecting;
                    Double lat,lng,price;
                    boolean isOnAir;
                    int version;

                    for (int i = 0; i < rootGetPublications.length(); i++) {
                        JSONObject publication = rootGetPublications.getJSONObject(i);
                        audience = publication.getInt("audience");

                        if(audience == 0 || groupsIDs.contains(audience)){
                            long publisherID = publication.getLong("publisher_id");
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
                            isOnAir = publication.getBoolean("is_on_air");
                            activeDeviceDevUUID = publication.getString("active_device_dev_uuid");
                            photoURL = publication.getString("photo_url");

                            identityProviderUserName = publication.getString("identity_provider_user_name");
                            price = publication.getDouble("price");
                            priceDescription = publication.getString("price_description");
                            publications.add(new Publication(id, version, title, subtitle, address, typeOfCollecting, lat, lng, startingDate, endingDate, contactInfo, isOnAir,
                                    activeDeviceDevUUID, photoURL, publisherID, audience, identityProviderUserName, price, priceDescription));
                        }
                    }
                    PublicationsDBHandler publicationsDBHandler = new PublicationsDBHandler(this);
                    publicationsDBHandler.replaceAllPublications(publications);
                    Intent getDataIntent2 = new Intent(this,GetDataService.class);
                    getDataIntent2.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_GET_ALL_PUBLICATIONS_REGISTERED_USERS);
                    startService(getDataIntent2);
                    break;

                case ReceiverConstants.ACTION_ADD_PUBLICATION:
                    // TODO: 27/11/2016 add logic to save the publication id to db
                    Log.d(TAG,responseRoot);
                    JSONObject rootAddPublication = new JSONObject(responseRoot);
                    intent.putExtra(Publication.PUBLICATION_ID,rootAddPublication.getLong("id"));
                    intent.putExtra(Publication.PUBLICATION_VERSION,rootAddPublication.getInt("version"));
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
                    long reportId,publicationID,reportUserID;
                    int publicationVersion,rating;
                    String active_device_dev_uuid,dateOfReport,reportUserName,reportContactInfo;
                    short reportType;

                    for (int i = 0; i < rootGetReports.length(); i++) {
                        JSONObject report = rootGetReports.getJSONObject(i);
                        reportId = report.getLong("id");
                        publicationID = report.getLong("publication_id");
                        publicationVersion = report.getInt("publication_version");
                        reportType = (short) report.getInt("report");
                        active_device_dev_uuid = report.getString("active_device_dev_uuid");
                        dateOfReport = report.getString("date_of_report");
                        reportUserName = "No user name";
                        if (report.getString("report_user_name") != null) {
                            reportUserName = report.getString("report_user_name");
                        }
                        reportContactInfo = report.getString("report_contact_info");
                        reportUserID = report.getLong("reporter_user_id");
                        rating = report.getInt("rating");

                        reports.add(new PublicationReport(reportId, publicationID, publicationVersion, reportType, active_device_dev_uuid,
                                //createdDate, updateDate,
                                dateOfReport, reportUserName, reportContactInfo, reportUserID, rating));
                        }
                    intent.putParcelableArrayListExtra(PublicationReport.REPORT_KEY, reports);
                    break;

                case ReceiverConstants.ACTION_ADD_REPORT: // not tested
                    // TODO: 27/11/2016 add logic
                    break;

                case ReceiverConstants.ACTION_ADD_USER:
                    JSONObject rootAddUser;
                    rootAddUser = new JSONObject(responseRoot);
                    long newUserID = rootAddUser.getLong("id");
                    CommonMethods.setMyUserID(this, newUserID);
                    Log.d("Add user response", "id: " + newUserID);
                    break;

                case ReceiverConstants.ACTION_REGISTER_TO_PUBLICATION:
                    // TODO: 27/11/2016 update
                    break;

                case ReceiverConstants.ACTION_GET_PUBLICATION_REGISTERED_USERS:
                    // TODO: 20/12/2016 not tested yet!
                    JSONArray registeredUsersArray = new JSONArray(responseRoot);
                    intent.putExtra(Publication.PUBLICATION_COUNT_OF_REGISTER_USERS_KEY,registeredUsersArray.length());
                    break;

                case ReceiverConstants.ACTION_GET_ALL_PUBLICATIONS_REGISTERED_USERS:
                    ArrayList<RegisteredUser> registeredUsers = new ArrayList<>();

                    PublicationsDBHandler publicationsDBHandler1 = new PublicationsDBHandler(this);
                    ArrayList<Long> publicationsIDs = publicationsDBHandler1.getPublicationsIDs();

                    long currentPublicationID, collectorUserID;
                    int publicationVersion1;

                    JSONArray root = new JSONArray(responseRoot);
                    for (int i = 0; i < root.length(); i++) {
                        JSONObject registeredUser = root.getJSONObject(i);
                        currentPublicationID = registeredUser.getLong("publication_id");
                        if(publicationsIDs.contains(currentPublicationID)){
                            publicationVersion1 = registeredUser.getInt("publication_version");
                            collectorUserID = registeredUser.getLong("collector_user_id");

                            registeredUsers.add(new RegisteredUser(currentPublicationID,-1,null,publicationVersion1,null,null,collectorUserID));
                        }
                    }
                    RegisteredUsersDBHandler registeredUsersDBHandler = new RegisteredUsersDBHandler(this);
                    registeredUsersDBHandler.replaceAllRegisteredUsers(registeredUsers);
                    break;

                case ReceiverConstants.ACTION_ADD_GROUP:
                    // TODO: 06/12/2016 add logic according to what we receive
                    JSONObject rootAddGroup = new JSONObject(responseRoot);
                    long newGroupID = rootAddGroup.getLong("id");
                    intent.putExtra(Group.KEY,newGroupID);
                    break;

                case ReceiverConstants.ACTION_GET_GROUPS:
                    JSONArray groupArray = new JSONArray(responseRoot);
                    /** declarations */
                    ArrayList<Group> groups = new ArrayList<>();
                    long groupID,memberID,userID;
                    String groupName,phoneNumber,memberName;
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
                    GroupsDBHandler groupsDBHandler = new GroupsDBHandler(this);
                    groupsDBHandler.replaceAllGroups(groups);

                    Intent getDataIntent1 = new Intent(this,GetDataService.class);
                    getDataIntent1.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_GET_PUBLICATIONS);
                    startService(getDataIntent1);
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
