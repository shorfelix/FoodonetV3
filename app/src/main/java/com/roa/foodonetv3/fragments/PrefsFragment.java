package com.roa.foodonetv3.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.roa.foodonetv3.R;

public class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "PrefsFragment";
    private OnSignOutClickListener listener;
    private ListPreference listNotificationRadius;
    private String keyListNotificationRadius, keyUserName, keyUserPhone;
    private Preference userPhone;
    private Preference userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_screen);

        listener = (OnSignOutClickListener) getActivity();

        keyListNotificationRadius = getString(R.string.key_prefs_list_notification_radius);
        keyUserName = getString(R.string.key_prefs_user_name);
        keyUserPhone = getString(R.string.key_prefs_user_phone);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        listNotificationRadius = (ListPreference) findPreference(getString(R.string.key_prefs_list_notification_radius));
        String[] notificationRadiusListKMValues = getResources().getStringArray(R.array.prefs_notification_radius_values_km);
        String currentValueNotificationRadiusListKM = preferences.getString(keyListNotificationRadius, notificationRadiusListKMValues[2]);
        listNotificationRadius.setSummary(getEntryStringFromList(
                R.array.prefs_notification_radius_entries_km,
                R.array.prefs_notification_radius_values_km,
                currentValueNotificationRadiusListKM));
        listNotificationRadius.setOnPreferenceChangeListener(this);

        userName = findPreference(keyUserName);
        userName.setSummary(preferences.getString(keyUserName,""));
        userName.setOnPreferenceChangeListener(this);

        userPhone = findPreference(keyUserPhone);
        userPhone.setSummary(preferences.getString(keyUserPhone,""));
        userPhone.setOnPreferenceChangeListener(this);

        findPreference(getString(R.string.prefs_sign_out)).setOnPreferenceClickListener(this);

    }

    private String getEntryStringFromList(int entriesListRes, int valuesListRes, String value){
        String[] entriesList = getResources().getStringArray(entriesListRes);
        String[] valuesList = getResources().getStringArray(valuesListRes);

        for (int i = 0; i < valuesList.length; i++) {
            if(valuesList[i].equals(value)){
                return entriesList[i];
            }
        }
        return "";
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String preferenceKey = preference.getKey();
        if (preferenceKey.equals(keyListNotificationRadius)) {
            listNotificationRadius.setSummary(getEntryStringFromList(
                    R.array.prefs_notification_radius_entries_km,
                    R.array.prefs_notification_radius_values_km,
                    newValue.toString()));
        } else if(preferenceKey.equals(keyUserName)){
            // TODO: 19/02/2017 add logic to send to server
            userName.setSummary(newValue.toString());
        } else if(preferenceKey.equals(keyUserPhone)){
            // TODO: 19/02/2017 add logic to send to server
            userPhone.setSummary(newValue.toString());
        }
        return true;
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
