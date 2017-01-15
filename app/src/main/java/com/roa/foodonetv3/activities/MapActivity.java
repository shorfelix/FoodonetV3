package com.roa.foodonetv3.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.FrameLayout;
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
import com.roa.foodonetv3.adapters.MapPublicationRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.CommonMethods;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.db.FoodonetDBProvider;
import com.roa.foodonetv3.db.PublicationsDBHandler;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.services.FoodonetService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, MapPublicationRecyclerAdapter.OnImageAdapterClickListener {

    private GoogleMap mMap;
    private ArrayList<Publication> publications = new ArrayList<>();
    private String hashMapKey;
    private LatLng userLocation;
    private HashMap<String, Publication> hashMap;
    private FoodonetReceiver receiver;
    private RecyclerView mapRecycler;
    private MapPublicationRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        receiver = new FoodonetReceiver();
        mapRecycler = (RecyclerView) findViewById(R.id.mapRecycler);
        mapRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new MapPublicationRecyclerAdapter(this);
        mapRecycler.setAdapter(adapter);
        hashMap = new HashMap<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        userLocation = CommonMethods.getCurrentLocation(this);
        PublicationsDBHandler handler = new PublicationsDBHandler(this);
        publications = handler.getPublications(FoodonetDBProvider.PublicationsDB.TYPE_GET_NON_USER_PUBLICATIONS);
        adapter.updatePublications(publications);
        /** set the broadcast receiver for getting all publications from the server */
        receiver = new FoodonetReceiver();
        IntentFilter filter = new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapFragment!=null) {
            mapFragment.getMapAsync(MapActivity.this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 8));
        }
        // Add a publications markers
        for(int i = 0; i< publications.size(); i++){
            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_xh));
            LatLng publicationTest = new LatLng(publications.get(i).getLat(), publications.get(i).getLng());
            mMap.addMarker(markerOptions.position(publicationTest).title("Publication Marker"));
            //put in the hashMap's key the value of thr marker to get it later
            hashMapKey = publicationTest.latitude+","+publicationTest.longitude;
            hashMap.put(hashMapKey, publications.get(i));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                hashMapKey = marker.getPosition().latitude+","+marker.getPosition().longitude;
                if (hashMap.get(hashMapKey)!=null) {
                    String ms = hashMap.get(hashMapKey).getTitle();
                    Toast.makeText(MapActivity.this, ms, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    @Override
    public void onImageAdapterClicked(LatLng latLng) {
        /**move the camera to publication location*/
        if(mMap!=null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        }
    }

    private class FoodonetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: 15/01/2017 delete?
        }
    }
}
