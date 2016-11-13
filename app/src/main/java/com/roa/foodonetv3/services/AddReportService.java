package com.roa.foodonetv3.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.model.Publication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class AddReportService extends IntentService {
    private static final String TAG = "AddReportService";


    public AddReportService() {
        super("AddReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d("AddPublicationService","entered service");
            String jsonReport = intent.getStringExtra("report");

            //todo use localID to later save the correct server id to the database
            long publicationLocalID = intent.getLongExtra(Publication.PUBLICATION_UNIQUE_ID_KEY,-1);

            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            StringBuilder urlAddressBuilder = new StringBuilder(getResources().getString(R.string.foodonet_server));
            ///publications/<id>/rpublication_reports.json
            urlAddressBuilder.append("/publications/" + publicationLocalID + "/rpublication_reports.json");
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
