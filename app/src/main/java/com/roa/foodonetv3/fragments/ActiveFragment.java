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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends Fragment {
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
        ArrayList<Publication> publications = new ArrayList<>();
        publications.add(new Publication(123L,1,"Pizza", "good pizza", "my place",(short) 2, 34.44444d,34.5555555d,"now", "later", "phone number", true));
        adapter.updatePublications(publications);
    }
}

