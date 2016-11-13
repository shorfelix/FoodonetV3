package com.roa.foodonetv3.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.adapters.PublicationsRecyclerAdapter;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.services.GetPublicationsService;
import com.roa.foodonetv3.services.GetReportService;

import java.util.ArrayList;

public class ActiveFragment extends Fragment {
    private static final String TAG = "ActiveFragment";

    private PublicationsRecyclerAdapter adapter;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** set the broadcast receiver for getting all publications from the server */
        GetPublicationsReceiver receiver = new GetPublicationsReceiver();
        IntentFilter filter = new IntentFilter(GetPublicationsService.ACTION_SERVICE_GET_PUBLICATIONS);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);
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
        /** temp request publications update from the server on fragment resume */
        // TODO: 09/11/2016 change!
        Intent i = new Intent(getContext(), GetPublicationsService.class);
        i.putExtra(GetPublicationsService.QUERY_ARGS,getResources().getString(R.string.foodonet_publications));
        getContext().startService(i);

        Intent r = new Intent(getContext(), GetReportService.class);
        getContext().startService(r);
    }

    public class GetPublicationsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for publications got from the service, temporary, as we'll want to move it to the activity probably */
            ArrayList<Publication> publications = intent.getParcelableArrayListExtra(GetPublicationsService.QUERY_PUBLICATIONS);
            adapter.updatePublications(publications);
        }
    }
}

