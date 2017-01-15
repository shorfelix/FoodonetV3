package com.roa.foodonetv3.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.db.LatestPlacesDBHandler;
import com.roa.foodonetv3.model.SavedPlace;

import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static final int REQUEST_PLACE_PICKER = 10;
    private Intent intentForResult;
    private ArrayList<SavedPlace> places;
    private LatestPlacesDBHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        ListView recentPlacesList = (ListView) findViewById(R.id.recentPlacesList);
        recentPlacesList.setOnItemClickListener(this);
        TextView textLatestPicker = (TextView) findViewById(R.id.textLatestPicker);
        textLatestPicker.setOnClickListener(this);

        handler = new LatestPlacesDBHandler(this);
        places = handler.getAllPlaces();
        String[] placesNames = new String[places.size()];
        String address;
        SavedPlace place;

        for (int i = 0; i < places.size(); i++) {
            place = places.get(i);
            address = place.getAddress();
            if(address==null){
                address = "";
            }
            placesNames[i] = address;
        }
        recentPlacesList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placesNames));

//        preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        recentPlacesList = (ListView) findViewById(R.id.recentPlacesList);
//        places = new ArrayList<>();
//        places.add("New location");
//        while (counter<=4){
//                places.add(preferences.getString("place_name" + counter, "test"));
//            counter++;
//        }
//        recentPlacesList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, places));
//        recentPlacesList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        /** start the google places autocomplete widget */
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
//                    Intent intent =
//                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                                    .build(getActivity());
            // Start the Intent by requesting a result, identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

            // Hide the pick option in the UI to prevent users from starting the picker
            // multiple times.
//                    showPickAction(false);

        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(places.get(position)==null || places.get(position).getAddress()==null || places.get(position).getAddress().equals("")){
            return;
        }
        SavedPlace place = places.get(position);
        intentForResult = new Intent();
        intentForResult.putExtra("place",place);
        setResult(RESULT_OK, intentForResult);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PLACE_PICKER:
                    Place placeData = PlacePicker.getPlace(this, data);
                    SavedPlace place = new SavedPlace(String.valueOf(placeData.getAddress()),placeData.getLatLng().latitude,placeData.getLatLng().longitude);
                    handler.addLatestPlace(place);
                    intentForResult = new Intent();
                    intentForResult.putExtra("place",place);
                    setResult(RESULT_OK, intentForResult);
                    finish();
                    break;
            }
        }
    }

}









