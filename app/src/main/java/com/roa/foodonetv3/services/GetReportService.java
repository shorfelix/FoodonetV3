package com.roa.foodonetv3.services;

import android.app.IntentService;
import android.content.Intent;
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

import javax.net.ssl.HttpsURLConnection;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class GetReportService extends IntentService {
    public static final String ACTION_SERVICE_GET_PUBLICATIONS = "com.roa.foodonetv3.services.ACTION_SERVICE_GET_REPORT";

    public GetReportService() {
        super("GetReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean queryError = false;
        ArrayList<ReportFromServer> reports = new ArrayList<>();
        if (intent != null) {
            StringBuilder urlAddressBuilder = new StringBuilder();
            urlAddressBuilder.append("https://prv-fd-server.herokuapp.com/publications/2/publication_reports?publication_version=1");
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
                for (int i = 0; i < root.length(); i++) {
                    JSONObject report = root.getJSONObject(i);
                    long reportId = report.getLong("id");
                    long publicationId = report.getLong("publication_id");
                    int publicationVersion = report.getInt("publication_version");
                    int reportType = report.getInt("report");
                    String active_device_dev_uuid = report.getString("active_device_dev_uuid");
                    String createdDate = report.getString("created_at");
                    String updateDate = report.getString("updated_at");
                    String dateOfReport = report.getString("date_of_report");
                    String reportUserName = "No user name";
                    if (report.getString("report_user_name") != null) {
                         reportUserName = report.getString("report_user_name");
                    }
                    String reportContactInfo = report.getString("report_contact_info");
                    int reportUserId = report.getInt("reporter_user_id");
                    int rating = report.getInt("rating");

                    reports.add(new ReportFromServer(reportId, publicationId, publicationVersion, reportType, active_device_dev_uuid,
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
    }


}
