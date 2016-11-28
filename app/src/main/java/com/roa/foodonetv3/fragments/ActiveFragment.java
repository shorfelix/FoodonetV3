package com.roa.foodonetv3.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.roa.foodonetv3.R;
import com.roa.foodonetv3.adapters.PublicationsRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.StartServiceMethods;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.services.FoodonetService;

import java.util.ArrayList;

public class ActiveFragment extends Fragment{
    private static final String TAG = "ActiveFragment";

    private PublicationsRecyclerAdapter adapter;
    private GetPublicationsReceiver receiver;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_active, container, false);

        /** set the recycler view and adapter for all publications */
        RecyclerView activePubRecycler = (RecyclerView) v.findViewById(R.id.activePubRecycler);
        activePubRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PublicationsRecyclerAdapter(getContext());
        activePubRecycler.setAdapter(adapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /** set the broadcast receiver for getting all publications from the server */
        receiver = new GetPublicationsReceiver();
        IntentFilter filter =  new IntentFilter(FoodonetService.BROADCAST_FOODONET_SERVER_FINISH);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);
        /** temp request publications update from the server on fragment resume */
        Intent intent = new Intent(getContext(), FoodonetService.class);
        intent.putExtra(StartServiceMethods.ACTION_TYPE,StartServiceMethods.ACTION_GET_PUBLICATIONS_EXCEPT_USER);
        getContext().startService(intent);
        /** show that the list is being updated */
        if(getView()!= null){
            Snackbar.make(getView(), R.string.updating,Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    public class GetPublicationsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for publications got from the service, temporary, as we'll want to move it to the activity probably */
            if(intent.getIntExtra(StartServiceMethods.ACTION_TYPE,-1)==StartServiceMethods.ACTION_GET_PUBLICATIONS_EXCEPT_USER){
                if(intent.getBooleanExtra(FoodonetService.SERVICE_ERROR,false)){
                    // TODO: 27/11/2016 add logic if fails
                    Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                } else{
                    ArrayList<Publication> publications = intent.getParcelableArrayListExtra(Publication.PUBLICATION_KEY);
                    adapter.updatePublications(publications);
                }
            }
        }
    }
}

