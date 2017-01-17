package com.roa.foodonetv3.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.roa.foodonetv3.R;

public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private OnSignOutClickListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_screen);

        listener = (OnSignOutClickListener) getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Preference button = findPreference(getString(R.string.prefs_sign_out));
        button.setOnPreferenceClickListener(this);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        listener.onSignOutClick();
        return true;
    }

    public interface OnSignOutClickListener{
        void onSignOutClick();
    }
}
