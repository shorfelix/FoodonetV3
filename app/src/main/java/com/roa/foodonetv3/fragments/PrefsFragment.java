package com.roa.foodonetv3.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.roa.foodonetv3.R;

public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_screen);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}
