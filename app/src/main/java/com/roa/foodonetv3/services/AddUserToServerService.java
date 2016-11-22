package com.roa.foodonetv3.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class AddUserToServerService extends IntentService {
    public AddUserToServerService() {
        super("AddUserToServerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // TODO: 08/11/2016 not tested yet!!!
            Log.d("AddUserToServerService","entered service");
            String jsonUser = intent.getStringExtra(User.USER_KEY);

            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            StringBuilder urlAddressBuilder = new StringBuilder(getResources().getString(R.string.foodonet_server));
            urlAddressBuilder.append(getResources().getString(R.string.foodonet_user));
            try {
                URL url = new URL(urlAddressBuilder.toString());
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.addRequestProperty("Accept","application/json");
                connection.addRequestProperty("Content-Type","application/json");
                connection.setDoOutput(true);
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"utf-8"));
                writer.write(jsonUser);
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
                JSONObject root = new JSONObject(builder.toString());
                int id = root.getInt("id");
                CommonMethods.setMyUserID(this,id);
                Log.d("Add user response","id: "+id);
                // TODO: 23/11/2016 add some response that the user was successfully signed to foodonet server
//                Log.d("SERVER RESPONSE", builder.toString());
            } catch (IOException e) {
                Log.e("AddUserService",e.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection!= null){
                    connection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("AddUserService",e.getMessage());
                    }
                }
            }
        }
    }
}
