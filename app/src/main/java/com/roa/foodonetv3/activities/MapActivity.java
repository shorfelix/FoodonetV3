package com.roa.foodonetv3.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.fragments.ActiveFragment;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.services.GetPublicationsService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends FragmentActivity implements LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Publication> publications = new ArrayList<>();
    private LocationManager locationManager;
    private Timer timer;
    private boolean gotLocation;
    private String providerName, hashMapKey;
    private LatLng userLocation;
    private HashMap<String, Publication> hashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        GetPublicationsReceiver receiver = new GetPublicationsReceiver();
        IntentFilter filter = new IntentFilter(GetPublicationsService.ACTION_SERVICE_GET_PUBLICATIONS);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);
        startGps();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        hashMap = new HashMap<>();
//        providerName = LocationManager.GPS_PROVIDER;
//        try {
//            locationManager.requestLocationUpdates(providerName, 1000, 100, MapActivity.this);
//        }
//        catch(SecurityException e){
//            Log.e("Location", e.getMessage());
//        }





    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if(userLocation!=null){
            mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
        // Add a publications markers
        for(int i = 0; i< publications.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_xh));
            LatLng publicationTest = new LatLng(publications.get(i).getLat(), publications.get(i).getLng());
            //put in the hashMap's key the value of thr marker to get it later
            hashMapKey = publicationTest.latitude+","+publicationTest.longitude;
            hashMap.put(hashMapKey, publications.get(i));
            mMap.addMarker(markerOptions.position(publicationTest).title("Publication Marker"));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                hashMapKey = marker.getPosition().latitude+","+marker.getPosition().longitude;
                String ms = hashMap.get(hashMapKey).getSubtitle();
                Toast.makeText(MapActivity.this, ms , Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        gotLocation = true;
        timer.cancel();
        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        // temp
        Intent i = new Intent(this, GetPublicationsService.class);
        i.putExtra(GetPublicationsService.QUERY_ARGS,"publications.json");
        startService(i);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class GetPublicationsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            publications = intent.getParcelableArrayListExtra(GetPublicationsService.QUERY_PUBLICATIONS);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if(mapFragment!=null) {
                mapFragment.getMapAsync(MapActivity.this);
            }
        }
    }

    public void startGps(){
        gotLocation = false;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        providerName = LocationManager.GPS_PROVIDER;
        try {
            locationManager.requestLocationUpdates(providerName, 1000, 100, MapActivity.this);
        }
        catch(SecurityException e){
            Log.e("Location", e.getMessage());
        }
        timer = new Timer("provider");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // if we do not have a location yet
                if(!gotLocation) {
                    try {
                        // remove old location provider(gps)
                        locationManager.removeUpdates(MapActivity.this);
                        // change provider name to NETWORK
                        providerName = LocationManager.NETWORK_PROVIDER;
                        // start listening to location again on the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                                    builder.setMessage("Your NETWORK or your GPS seems to be disabled, please turn it on")
                                            .setPositiveButton("Ok", null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                                try {
                                    locationManager.requestLocationUpdates(providerName, 1000, 100, (LocationListener) MapActivity.this);
                                } catch (SecurityException e) {
                                    Log.e("Location Timer", e.getMessage());
                                }
                            }
                        });
                    } catch (SecurityException e) {
                        Log.e("Location", e.getMessage());
                    }
                }
            }
        };
        // schedule the timer to run the task after 5 seconds from now
        timer.schedule(task, new Date(System.currentTimeMillis() + 5000));
    }
}
