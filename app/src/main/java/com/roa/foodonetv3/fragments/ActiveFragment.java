package com.roa.foodonetv3.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.adapters.PublicationsRecyclerAdapter;
import com.roa.foodonetv3.model.Publication;
import com.roa.foodonetv3.tasks.GetPublicationsTask;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends Fragment implements GetPublicationsTask.GetPublicationsListener {
    private PublicationsRecyclerAdapter adapter;

    public ActiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_active, container, false);

        RecyclerView activePubRecycler = (RecyclerView) v.findViewById(R.id.activePubRecycler);
        activePubRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PublicationsRecyclerAdapter(getContext());
        activePubRecycler.setAdapter(adapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        String args = "publications/31";
        GetPublicationsTask task = new GetPublicationsTask(this);
        task.execute(args);
    }

    @Override
    public void onGetPublications(ArrayList<Publication> publications) {
        adapter.updatePublications(publications);
    }
}

