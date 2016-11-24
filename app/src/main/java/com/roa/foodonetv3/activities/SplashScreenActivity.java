package com.roa.foodonetv3.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.roa.foodonetv3.R;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity implements LocationListener {
    private LocationManager locationManager;
//    private boolean gotLocation;
    private String providerName;
//    private Timer timer;
    public static final String USER_LATITUDE = "user_latitude";
    public static final String USER_LONGITUDE = "user_longitude";
    private static final int PERMISSION_REQUEST_NEW_LOCATION = 1;
    private static final int PERMISSION_REQUEST_UNREGISTER = 2;
    private static final String GOT_LOCATION = "gotLocation";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        startGps();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainDrawerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    public void startGps(){
//        gotLocation = false;
        sharedPreferences.edit().putBoolean(GOT_LOCATION,false).apply();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        providerName = LocationManager.NETWORK_PROVIDER;
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(providerName, 1000, 100, SplashScreenActivity.this);
        }
//        timer = new Timer("provider");
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                // if we do not have a location yet
//                if(!gotLocation) {
//                    try {
//                        // remove old location provider(gps)
//                        locationManager.removeUpdates((LocationListener) SplashScreenActivity.this);
//                        // change provider name to NETWORK
//                        providerName = LocationManager.NETWORK_PROVIDER;
//                        // start listening to location again on the main thread
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
//                                    builder.setMessage("Your NETWORK or your GPS seems to be disabled, please turn it on")
//                                            .setPositiveButton("Ok", null);
//                                    AlertDialog dialog = builder.create();
//                                    dialog.show();
//                                }
//                                try {
//                                    locationManager.requestLocationUpdates(providerName, 1000, 100, (LocationListener) SplashScreenActivity.this);
//                                } catch (SecurityException e) {
//                                    Log.e("Location Timer", e.getMessage());
//                                }
//                            }
//                        });
//                    } catch (SecurityException e) {
//                        Log.e("Location", e.getMessage());
//                    }
//                }
//            }
//        };
//        // schedule the timer to run the task after 5 seconds from now
//        timer.schedule(task, new Date(System.currentTimeMillis() + 5000));
    }

    @Override
    public void onLocationChanged(Location location) {
//        gotLocation = true;
//        timer.cancel();
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_LATITUDE, String.valueOf(userLocation.latitude));
        editor.putString(USER_LONGITUDE, String.valueOf(userLocation.longitude));
        editor.putBoolean(GOT_LOCATION,true);
        editor.apply();
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            locationManager.removeUpdates(this);
        } else{
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_UNREGISTER);
        }
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
    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /** marshmallow and up, if location permission not allowed yet, run */
        switch (requestCode){
            case PERMISSION_REQUEST_NEW_LOCATION:
                /** in case of a request */
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /** if granted, run getLocation again */
                    startGps();
                } else {
                    /** request denied, give the user a message */
                    Toast.makeText(this, getResources().getString(R.string.toast_needs_location_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_UNREGISTER:
                /** in case of a user shutting down the permission in the app and before it unregisters, slim chances... */
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
                    if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        locationManager.removeUpdates(this);
                    }
                } else{
                    Toast.makeText(this, getResources().getString(R.string.toast_needs_location_permission), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
