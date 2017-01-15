package com.roa.foodonetv3.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.roa.foodonetv3.R;
import com.roa.foodonetv3.services.SignOutService;

public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private SharedPreferences preferences;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_screen);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Preference button = findPreference(getString(R.string.prefs_sign_out));
        button.setOnPreferenceClickListener(this);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Intent intent = new Intent(context,SignOutService.class);
        context.startService(intent);
        return true;
    }
}
