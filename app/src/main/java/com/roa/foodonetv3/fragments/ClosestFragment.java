package com.roa.foodonetv3.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.roa.foodonetv3.R;

public class ClosestFragment extends Fragment{

    private static final String TAG = "ClosestFragment";
    public ClosestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_closest, container, false);

        return v;
    }
}
