package com.roa.foodonetv3.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.Tasks.GetPubsRegUsersTask;
import com.roa.foodonetv3.activities.MainActivity;
import com.roa.foodonetv3.adapters.PublicationsRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.services.FoodonetService;

import java.util.ArrayList;

public class ActiveFragment extends Fragment implements GetPubsRegUsersTask.OnGetRegisteredUsersListener {
    private static final String TAG = "ActiveFragment";

    private PublicationsRecyclerAdapter adapter;
    private FoodonetReceiver receiver;
    private ArrayList<Publication> publications;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver = new FoodonetReceiver();
        setHasOptionsMenu(true);
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
        IntentFilter filter =  new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);

        /** temp request publications update from the server on fragment resume */
        // TODO: 21/12/2016 probably change to activity and get from db
        Intent intent = new Intent(getContext(), FoodonetService.class);
        intent.putExtra(ReceiverConstants.ACTION_TYPE, ReceiverConstants.ACTION_GET_PUBLICATIONS_EXCEPT_USER);
        getContext().startService(intent);
        /** show that the list is being updated */
        if(getView()!= null){
            Snackbar.make(getView(), R.string.updating,Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    /** set the menu, get it from the activity and add the specific items for this fragment */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
    }

    /** interface from the asynctask that calculates how many users are logged in to the publications, it is the last step, so update after receiving */
    @Override
    public void onGetRegisteredUsers(ArrayList<Publication> publications) {
        this.publications = publications;
        adapter.updatePublications(this.publications);
    }

    private class FoodonetReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: 20/12/2016 should be moved to the activity
            switch (intent.getIntExtra(ReceiverConstants.ACTION_TYPE,-1)){
                case ReceiverConstants.ACTION_GET_PUBLICATIONS_EXCEPT_USER:
                    /** receiver for publications got from the service, temporary, as we'll want to move it to the activity probably */
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 20/12/2016 add logic if fails
                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                    } else{
                        publications = intent.getParcelableArrayListExtra(Publication.PUBLICATION_KEY);
                        /** get number of registered users of each publication */
                        Intent getRegUsersIntent = new Intent(getContext(),FoodonetService.class);
                        getRegUsersIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_GET_ALL_PUBLICATIONS_REGISTERED_USERS);
                        getContext().startService(getRegUsersIntent);
                    }
                    break;
                case ReceiverConstants.ACTION_GET_ALL_PUBLICATIONS_REGISTERED_USERS:
                    // TODO: 20/12/2016 add logic to differentiate from the main publications
                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
                        // TODO: 20/12/2016 add logic if fails
                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(context, "got registered users", Toast.LENGTH_SHORT).show();
                        GetPubsRegUsersTask getPubsRegUsersTask = new GetPubsRegUsersTask(ActiveFragment.this ,publications,
                                intent.getStringExtra(Publication.PUBLICATION_COUNT_OF_REGISTER_USERS_KEY));
                        getPubsRegUsersTask.execute();
                    }
                    break;
            }
        }
    }
}

