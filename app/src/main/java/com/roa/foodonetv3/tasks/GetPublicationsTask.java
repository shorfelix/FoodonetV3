package com.roa.foodonetv3.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.roa.foodonetv3.model.Publication;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Owner on 05/10/2016.
 */

public class GetPublicationsTask extends AsyncTask<String,Void,ArrayList<Publication>> {
    private GetPublicationsListener listener;

    public GetPublicationsTask(GetPublicationsListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<Publication> doInBackground(String... params) {
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append("https://prv-fd-server.herokuapp.com/");
        addressBuilder.append(params[0]);
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        URL url;
        StringBuilder builder = new StringBuilder();
        try {
            url = new URL(addressBuilder.toString());
            connection = (HttpsURLConnection) url.openConnection();
            if(connection.getResponseCode()!= HttpsURLConnection.HTTP_OK){
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = reader.readLine())!= null){
                builder.append(line);
            }
            JSONObject root = new JSONObject(builder.toString());
            ArrayList<Publication> publications = new ArrayList<>();

            // temp
            long id = root.getLong("id");
            int version = root.getInt("version");
            String title = root.getString("title");
            String subtitle = root.getString("subtitle");
            String address = root.getString("address");
            short typeOfCollecting = (short)root.getInt("type_of_collecting");
            Double lat = root.getDouble("latitude");
            Double lng = root.getDouble("longitude");
            String startingDate = root.getString("starting_date");
            String endingDate = root.getString("ending_date");
            String contactInfo = root.getString("contact_info");
            boolean isOnAir = root.getBoolean("is_on_air");
            publications.add(new Publication(id,version,title,subtitle,address,typeOfCollecting,lat,lng,startingDate,endingDate,contactInfo,isOnAir));
            //

            return publications;


        } catch (IOException | JSONException e) {
            Log.e("GetPublicationsTask",e.getMessage());
        } finally {
            if(connection != null){
                connection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("GetPublicationsTask",e.getMessage());
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Publication> publications) {
        super.onPostExecute(publications);
        listener.onGetPublications(publications);
    }

    public interface GetPublicationsListener{
        void onGetPublications(ArrayList<Publication> publications);
    }
}
