package com.roa.foodonetv3.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.model.User;
import com.roa.foodonetv3.services.AddUserToServerService;

public class ClosestFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ClosestFragment";
    public ClosestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_closest, container, false);
        // TODO: 05/11/2016 test for adding user
        v.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String uuid = sharedPreferences.getString(User.ACTIVE_DEVICE_DEV_UUID,null);
        String contactInfo = sharedPreferences.getString(User.PHONE_NUMBER,"");
        // TODO: 05/11/2016 currently hard coded, test
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String providerId = "";
        for (UserInfo userInfo : firebaseUser.getProviderData()) {
            String tempProviderId = userInfo.getProviderId();
            Log.d("WOWWWWWW TEST", tempProviderId);
            if(tempProviderId.equals("google.com")){
                providerId = "google";
            }
            if (tempProviderId.equals("facebook.com")) {
                providerId = "facebook";
            }
        }
        User user = new User(providerId,firebaseUser.getUid(),"token1",contactInfo,firebaseUser.getEmail(),firebaseUser.getDisplayName(),true,uuid);

        Intent i = new Intent(getContext(), AddUserToServerService.class);
        i.putExtra(User.USER_KEY,user.getUserJson().toString());
        getContext().startService(i);

        String message = "user: "+user.getUserJson().toString();
        Log.d(TAG,message);
    }
}
