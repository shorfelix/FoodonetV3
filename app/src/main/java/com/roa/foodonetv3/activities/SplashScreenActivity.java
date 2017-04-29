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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.maps.model.LatLng;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.commonMethods.CommonConstants;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.model.User;

import java.util.UUID;

public class SplashScreenActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = "SplashScreenActivity";

    private LocationManager locationManager;
    private static final int PERMISSION_REQUEST_NEW_LOCATION = 1;
    private static final int PERMISSION_REQUEST_UNREGISTER = 2;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setTitle(R.string.foodonet);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if(!sharedPreferences.getBoolean(getString(R.string.key_prefs_initialized),false)){
            init();
        }
        startGps();
        CommonMethods.getNewData(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 1000);
        if (PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.key_prefs_notification_token),null) == null){
            generateNotificationToken();
        }
    }

    private void init(){
        /** in first use, get a new UUID for the device and save it in the shared preferences */
        SharedPreferences.Editor edit = sharedPreferences.edit();
        // TODO: 21/12/2016 get the string from a static field or a resource string
        edit.putBoolean(getString(R.string.key_prefs_initialized),true);
        String deviceUUID = UUID.randomUUID().toString();
        edit.putString(getString(R.string.key_prefs_device_uuid), deviceUUID).apply();
        Log.d("Got new device UUID",deviceUUID);
    }

    public void generateNotificationToken(){
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String token = InstanceID.getInstance(SplashScreenActivity.this).getToken(getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this).edit();
                    editor.putString(getString(R.string.key_prefs_notification_token), token);
                    editor.apply();

                    Log.i(TAG, "GCM Registration Token: " + token);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to complete token refresh " + e.getMessage(), e);
                }
            }
        };
        t.start();
    }

    public void startGps(){
        /** get a network based position (fastest, and accuracy is not an issue) so when the app starts it will have a reference to distances */
        // TODO: 21/12/2016 change the logic to be run from a different class, add common methods - getUserLocation method
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationType = null;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationType = LocationManager.NETWORK_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationType = LocationManager.GPS_PROVIDER;
        } else{
            Toast.makeText(this, "no location provider", Toast.LENGTH_SHORT).show();
        }
        if(locationType != null){
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(locationType, 1000, 100, SplashScreenActivity.this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.key_prefs_user_lat), String.valueOf(userLocation.latitude));
        editor.putString(getString(R.string.key_prefs_user_lng), String.valueOf(userLocation.longitude));
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

    /** marshmallow and up, if location permission not allowed yet, run */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
