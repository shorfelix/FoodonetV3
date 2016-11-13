package com.roa.foodonetv3.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.model.User;

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
        v.findViewById(R.id.buttonAddUserTest).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonAddUserTest:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String uuid = sharedPreferences.getString(User.ACTIVE_DEVICE_DEV_UUID,null);
                // TODO: 05/11/2016 currently hard coded, test
                User user = new User("google",null,"token1","0501234567","test@test.com","Test User",true,uuid);
                Intent i = new Intent();
                i.putExtra(User.USER_KEY,user.getUserJson().toString());
                startActivity(i);
                Toast.makeText(getContext(), "pressed add user", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonDeleteUserTest:
                Toast.makeText(getContext(), "not implemented yet", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
