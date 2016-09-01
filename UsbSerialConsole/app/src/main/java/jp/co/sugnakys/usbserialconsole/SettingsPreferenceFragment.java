package jp.co.sugnakys.usbserialconsole;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.co.sugnakys.usbserialconsole.util.Log;

public class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsPreferenceFragment";

    private static List<String> prefSerialList;
    private static String[] listPrefKeys;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);
        prefSerialList = new ArrayList<>(Arrays.asList(
                getString(R.string.baudrate_key), getString(R.string.databits_key),
                getString(R.string.parity_key), getString(R.string.stopbits_key),
                getString(R.string.flowcontrol_key)
        ));
        listPrefKeys = new String[]{
                getString(R.string.baudrate_key), getString(R.string.databits_key),
                getString(R.string.stopbits_key), getString(R.string.parity_key),
                getString(R.string.flowcontrol_key), getString(R.string.timestamp_format_key)
        };

    }

    private void setSummary() {
        ListPreference listPref;
        for (String prefKey : listPrefKeys) {
            listPref = (ListPreference) findPreference(prefKey);
            Log.d(TAG, "Preference: " + prefKey + ", value: " + listPref.getEntry());
            listPref.setSummary(listPref.getEntry());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (prefSerialList.contains(key)) {
            Log.d(TAG, "Restart UsbService");
            Intent intent = new Intent(UsbService.ACTION_SERIAL_CONFIG_CHANGED);
            getActivity().sendBroadcast(intent);
        }

        Preference preference = findPreference(key);
        if (preference instanceof ListPreference) {
            preference.setSummary(((ListPreference) preference).getEntry());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        setSummary();
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
