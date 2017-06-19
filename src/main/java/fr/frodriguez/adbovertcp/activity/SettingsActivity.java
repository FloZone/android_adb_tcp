package fr.frodriguez.adbovertcp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import fr.frodriguez.adbovertcp.AppEngine;
import fr.frodriguez.adbovertcp.R;
import fr.frodriguez.adbovertcp.defines.Preferences;
import fr.frodriguez.library.compat.AppCompatPreferenceActivity;

/**
 * By FloZone on 12/02/2017.
 */
// TODO deprecated: method has been moved to PreferenceFragment
@SuppressWarnings("deprecation")
public class SettingsActivity extends AppCompatPreferenceActivity {

    // Listener when a preference is modified
    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Set the Port value as its summary
            if (key.equals(Preferences.KEY_PORT)) {
                findPreference(Preferences.KEY_PORT).setSummary(sharedPreferences.getString(key, ""));
            }

            // Update the notification
            if (key.equals(Preferences.KEY_NOTIF_ENABLED) || key.equals(Preferences.KEY_NOTIF_ALWAYS)) {
                AppEngine.updateDisplayedInfo(SettingsActivity.this);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Register the listener
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);

        // Set the Port value as its summary
        findPreference(Preferences.KEY_PORT).setSummary(sharedPreferences.getString(Preferences.KEY_PORT, ""));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Back button
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener again
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener);
    }
}
