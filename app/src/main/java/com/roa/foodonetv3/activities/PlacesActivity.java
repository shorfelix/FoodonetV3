package com.roa.foodonetv3.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.fragments.AddEditPublicationFragment;

import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView recentPlacesList;
    private int counter =0;
    private SharedPreferences preferences;
    private static final int REQUEST_PLACE_PICKER = 10;
    private Intent intentForResult;
    private LatLng latLng;
    private ArrayList<String> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        recentPlacesList = (ListView) findViewById(R.id.recentPlacesList);
        places = new ArrayList<>();
        places.add("New location");
        while (counter<=4){
                places.add(preferences.getString("place_name" + counter, "test"));
            counter++;
        }
        recentPlacesList.setAdapter(new ArrayAdapter<String >(this, android.R.layout.simple_list_item_1, places));
        recentPlacesList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position==0){
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
        }else {
            position--;
            intentForResult = new Intent();
            intentForResult.putExtra("address", preferences.getString("place_name" + position, "error"));
            intentForResult.putExtra("lat", preferences.getFloat("lat" + position, 0));
            intentForResult.putExtra("long", preferences.getFloat("long" + position, 0));
            setResult(RESULT_OK, intentForResult);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PLACE_PICKER:
                    final Place place = PlacePicker.getPlace(this, data);
                    String address = place.getAddress().toString();
                    latLng = place.getLatLng();
                    intentForResult = new Intent();
                    intentForResult.putExtra("place", (Parcelable) place);
                    setResult(RESULT_OK, intentForResult);
                    finish();
                    break;
            }
        }
    }
}









