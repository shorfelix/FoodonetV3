package com.roa.foodonetv3.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.adapters.PublicationsRecyclerAdapter;
import com.roa.foodonetv3.commonMethods.ReceiverConstants;
import com.roa.foodonetv3.db.FoodonetDBProvider;
import com.roa.foodonetv3.db.PublicationsDBHandler;
import com.roa.foodonetv3.db.RegisteredUsersDBHandler;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.services.FoodonetService;
import java.util.ArrayList;

public class MyPublicationsFragment extends Fragment{
    private PublicationsRecyclerAdapter adapter;
    private FoodonetReceiver receiver;
    private RecyclerView recyclerMyPublications;

    private TextView textInfo;
    private View layoutInfo;

    LongSparseArray<Integer> registeredUsers;
    private ArrayList<Publication> publications;

    public MyPublicationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_publications, container, false);

        /** set title */
        getActivity().setTitle(R.string.drawer_my_shares);

        /** set recycler view */
        recyclerMyPublications = (RecyclerView) v.findViewById(R.id.recyclerMyPublications);
        recyclerMyPublications.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PublicationsRecyclerAdapter(getContext());
        recyclerMyPublications.setAdapter(adapter);

        /** set info screen for when there are no user publication yet */
        layoutInfo = v.findViewById(R.id.layoutInfo);
        layoutInfo.setVisibility(View.GONE);
        textInfo = (TextView) v.findViewById(R.id.textInfo);
        textInfo.setText(getResources().getString(R.string.hi_what_would_you_like_to_share));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        /** set the broadcast receiver for getting all publications from the server */
        receiver = new FoodonetReceiver();
        IntentFilter filter =  new IntentFilter(ReceiverConstants.BROADCAST_FOODONET);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver,filter);

        /** update the recycler view from publications from the db */
        PublicationsDBHandler publicationsDBHandler = new PublicationsDBHandler(getContext());
        publications = publicationsDBHandler.getPublications(FoodonetDBProvider.PublicationsDB.TYPE_GET_USER_PUBLICATIONS);
        RegisteredUsersDBHandler registeredUsersDBHandler = new RegisteredUsersDBHandler(getContext());
        registeredUsers = registeredUsersDBHandler.getAllRegisteredUsersCount();
        if(publications.size()==0){
            recyclerMyPublications.setVisibility(View.GONE);
            layoutInfo.setVisibility(View.VISIBLE);
        } else{
            recyclerMyPublications.setVisibility(View.VISIBLE);
            layoutInfo.setVisibility(View.GONE);
            adapter.updatePublications(publications,registeredUsers);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    private class FoodonetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /** receiver for reports got from the service */
            int action = intent.getIntExtra(ReceiverConstants.ACTION_TYPE, -1);
            switch (action) {
                /** get users publications from service */
//                case ReceiverConstants.ACTION_GET_USER_PUBLICATIONS:
//                    /** receiver for publications got from the service, temporary, as we'll want to move it to the activity probably */
//                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
//                        // TODO: 27/11/2016 add logic if fails
//                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
//                    } else{
//                        publications = intent.getParcelableArrayListExtra(Publication.PUBLICATION_KEY);
//                        /** get number of registered users of each publication */
//                        Intent getRegUsersIntent = new Intent(getContext(),FoodonetService.class);
//                        getRegUsersIntent.putExtra(ReceiverConstants.ACTION_TYPE,ReceiverConstants.ACTION_GET_ALL_PUBLICATIONS_REGISTERED_USERS);
//                        getContext().startService(getRegUsersIntent);
//                    }
//                    break;
                /** got registered users */
//                case ReceiverConstants.ACTION_GET_ALL_PUBLICATIONS_REGISTERED_USERS:
//                    // TODO: 20/12/2016 add logic to differentiate from the main publications
//                    if(intent.getBooleanExtra(ReceiverConstants.SERVICE_ERROR,false)){
//                        // TODO: 20/12/2016 add logic if fails
//                        Toast.makeText(context, "service failed", Toast.LENGTH_SHORT).show();
//                    } else{
//                        Toast.makeText(context, "got registered users", Toast.LENGTH_SHORT).show();
//                        GetPubsRegUsersTask getPubsRegUsersTask = new GetPubsRegUsersTask(MyPublicationsFragment.this ,publications,
//                                intent.getStringExtra(Publication.PUBLICATION_COUNT_OF_REGISTER_USERS_KEY));
//                        getPubsRegUsersTask.execute();
//                    }
//                    break;
            }
        }
    }
}
