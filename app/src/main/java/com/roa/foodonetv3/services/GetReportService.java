package com.roa.foodonetv3.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.model.ReportFromServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class GetReportService extends IntentService {
    private static final String TAG = "GetReportService";
    public static final String ACTION_SERVICE_GET_REPORTS = "com.roa.foodonetv3.services.ACTION_SERVICE_GET_REPORTS";
    public static final String QUERY_REPORTS = "query_reports";
    public static final String QUERY_ERROR = "query_error";

    public GetReportService() {
        super("GetReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean queryError = false;
        ArrayList<ReportFromServer> reports = new ArrayList<>();
        if (intent != null) {
            long publicationID = intent.getLongExtra(Publication.PUBLICATION_UNIQUE_ID_KEY,-1);
            int publicationVersion = intent.getIntExtra(Publication.PUBLICATION_VERSION_KEY,1);
            StringBuilder urlAddressBuilder = new StringBuilder();
//            urlAddressBuilder.append("https://prv-fd-server.herokuapp.com/publications/2/publication_reports?publication_version=1");
            urlAddressBuilder.append(getResources().getString(R.string.foodonet_server));
            urlAddressBuilder.append(getResources().getString(R.string.foodonet_publications));
            urlAddressBuilder.append(String.format(Locale.US,"/%1$s",publicationID));
            urlAddressBuilder.append(getResources().getString(R.string.foodonet_reports_version));
            urlAddressBuilder.append(publicationVersion);
            // TODO: 21/11/2016 delete below code, test
            Log.d(TAG,"address: "+urlAddressBuilder.toString());
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            URL url;
            StringBuilder builder = new StringBuilder();
            try {
                url = new URL(urlAddressBuilder.toString());
                connection = (HttpsURLConnection) url.openConnection();
                if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    queryError = true;
                }
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                JSONArray root = new JSONArray(builder.toString());
                /** declarations */
                long reportId;
                int reportType;
                String active_device_dev_uuid;
                String createdDate;
                String updateDate;
                String dateOfReport;
                String reportUserName;
                String reportContactInfo;
                int reportUserId;
                int rating;
                for (int i = 0; i < root.length(); i++) {
                    JSONObject report = root.getJSONObject(i);
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

                    reports.add(new ReportFromServer(reportId, publicationID, publicationVersion, reportType, active_device_dev_uuid,
                            createdDate, updateDate, dateOfReport, reportUserName, reportContactInfo, reportUserId, rating));

                }

            } catch (IOException | JSONException e) {
                Log.e("GetReportTask", e.getMessage());
                queryError = true;
            }finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("GetReportTask", e.getMessage());
                    }
                }
            }
        }
        Intent i = new Intent(ACTION_SERVICE_GET_REPORTS);
        i.putExtra(QUERY_ERROR,queryError);
        i.putExtra(QUERY_REPORTS,reports);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}
