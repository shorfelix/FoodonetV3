package com.roa.foodonetv3.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.model.ReportFromServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class AddReportService extends IntentService {
    private static final String TAG = "AddReportService";


    public AddReportService() {
        super("AddReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("AddPublicationService","entered service");
            String jsonReport = intent.getStringExtra(ReportFromServer.REPORT_KEY);

            //todo use localID to later save the correct server id to the database
            long publicationLocalID = intent.getLongExtra(Publication.PUBLICATION_UNIQUE_ID_KEY,-1);

            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            ///publications/{0}/publication_reports.json
            StringBuilder urlAddressBuilder = new StringBuilder(getResources().getString(R.string.foodonet_server));
            urlAddressBuilder.append(getResources().getString(R.string.foodonet_publications));
            urlAddressBuilder.append(String.format(Locale.US,"/%1$d",publicationLocalID));
            urlAddressBuilder.append(getResources().getString(R.string.foodonet_publication_reports));
            Log.d(TAG,"address: " + urlAddressBuilder.toString());

            try {
                URL url = new URL(urlAddressBuilder.toString());
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.addRequestProperty("Accept","application/json");
                connection.addRequestProperty("Content-Type","application/json");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"utf-8"));
                writer.write(jsonReport);
                writer.flush();
                writer.close();
                os.close();
                StringBuilder builder = new StringBuilder();
                if(connection.getResponseCode()!= HttpsURLConnection.HTTP_OK){
                    //do something
                }
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while((line = reader.readLine())!= null){
                    builder.append(line);
                }

                Log.d("SERVER RESPONSE", builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
            }finally {
                if(connection!= null){
                    connection.disconnect();
                } if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG,e.getMessage());
                    }
                }
            }
        }
    }
}
