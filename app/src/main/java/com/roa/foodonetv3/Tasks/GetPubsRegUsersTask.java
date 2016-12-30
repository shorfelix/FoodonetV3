package com.roa.foodonetv3.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.roa.foodonetv3.model.Publication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/** asynctask to check the number of users that have joined each relevant publication */
public class GetPubsRegUsersTask extends AsyncTask<Void,Void,ArrayList<Publication>>{
    private static final String TAG = "GetPubsRegUsersTask";
    private ArrayList<Publication> publications;
    private String registersJson;
    private OnGetRegisteredUsersListener listener;

    public GetPubsRegUsersTask(OnGetRegisteredUsersListener listener,ArrayList<Publication> publications,String registersJson) {
        this.publications = publications;
        this.registersJson = registersJson;
        this.listener = listener;
    }

    @Override
    protected ArrayList<Publication> doInBackground(Void... voids) {
        try {
            JSONArray root = new JSONArray(registersJson);
            int publicationID;
            Publication publication;
            for (int i = 0; i < root.length(); i++) {
                JSONObject registeredUser = root.getJSONObject(i);
                publicationID = registeredUser.getInt("publication_id");
                for(int j = 0; j < publications.size(); j++){
                    publication = publications.get(j);
                    if(publication.getId()==publicationID){
                        publication.addToRegisteredCount();
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
        return publications;
    }

    @Override
    protected void onPostExecute(ArrayList<Publication> publications) {
        listener.onGetRegisteredUsers(publications);
    }
    public interface OnGetRegisteredUsersListener{
        void onGetRegisteredUsers(ArrayList<Publication> publications);
    }
}