package fr.frodriguez.adbovertcp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.frodriguez.adbovertcp.AppEngine;
import fr.frodriguez.adbovertcp.License;
import fr.frodriguez.adbovertcp.R;
import fr.frodriguez.adbovertcp.defines.Preferences;
import fr.frodriguez.library.compat.AppCompatPreferenceActivity;
import fr.frodriguez.library.utils.AppUtils;

/**
 * By FloZone on 12/02/2017.
 */
// TODO deprecated: method has been moved to PreferenceFragment
@SuppressWarnings("deprecation")
public class SettingsActivity extends AppCompatPreferenceActivity {

    // Licences list
    private static List<License> licenses = new ArrayList<License>(){{
        add(new License("Butter Knife","Apache License, Version 2.0","http://www.apache.org/licenses/LICENSE-2.0"));
        add(new License("Google Material Icons","Apache License, Version 2.0","http://www.apache.org/licenses/LICENSE-2.0"));
        add(new License("Android Material Icon Generator","Attribution-NonCommercial 3.0 License","https://creativecommons.org/licenses/by-nc/3.0/"));
    }};


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

        // Set the Port value as summary
        findPreference(Preferences.KEY_PORT).setSummary(sharedPreferences.getString(Preferences.KEY_PORT, ""));

        // Set the version name as summary
        findPreference(Preferences.KEY_VERSION).setSummary(AppUtils.getAppVersion(this));

        // Display licences
        findPreference(Preferences.KEY_LICENSE).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Adapter for the listview
                ArrayAdapter<License> adapter = new ArrayAdapter<License>(SettingsActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, licenses) {
                    @NonNull
                    @Override
                    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                        text1.setText(licenses.get(position).title);
                        text2.setText(licenses.get(position).license);
                        return view;
                    }
                };

                LayoutInflater inflater = getLayoutInflater();
                View dialogview = inflater.inflate(R.layout.dialog_license, null);
                ListView listview = (ListView) dialogview.findViewById(R.id.listview);
                listview.setAdapter(adapter);
                // Handle click on license = open browser
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String url = licenses.get(position).url;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);

                alertDialog.setView(dialogview);
                alertDialog.show();

                return true;
            }
        });
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
