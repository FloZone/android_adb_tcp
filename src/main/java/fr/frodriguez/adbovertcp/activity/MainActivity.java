package fr.frodriguez.adbovertcp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.frodriguez.adbovertcp.AppEngine;
import fr.frodriguez.adbovertcp.R;
import fr.frodriguez.adbovertcp.defines.Intents;
import fr.frodriguez.adbovertcp.defines.Preferences;
import fr.frodriguez.library.utils.AppUtils;


@SuppressWarnings("unused")
//TODO html page for how it works + licences info in an xml file (if possible)
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.adbSwitch)
    SwitchButton adbSwitch;
    @BindView(R.id.state)
    TextView tvState;
    @BindView(R.id.info)
    TextView tvInformation;

    BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the action bar
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set default preferences values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        ButterKnife.bind(this);

        // Create the intent receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intents.ACTION_INFO);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) return;

                if (action.equals(Intents.ACTION_INFO)) {
                    boolean enabled = intent.getBooleanExtra(Intents.EXTRA_ENABLED, false);
                    String title = intent.getStringExtra(Intents.EXTRA_TITLE);
                    String message = intent.getStringExtra(Intents.EXTRA_MESSAGE);

                    adbSwitch.setChecked(enabled);
                    tvState.setText(title);
                    tvInformation.setText(message);
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Display ADB over TCP state & info
        AppEngine.updateDisplayedInfo(this);
        // Save that the main activity is displayed
        SharedPreferences.Editor spe = PreferenceManager.getDefaultSharedPreferences(this).edit();
        spe.putBoolean(Preferences.KEY_APP_ACTIVE, true);
        spe.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save that the main activity is not displayed
        SharedPreferences.Editor spe = PreferenceManager.getDefaultSharedPreferences(this).edit();
        spe.putBoolean(Preferences.KEY_APP_ACTIVE, false);
        spe.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the intent receiver
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    /**
     * Add a menu button to the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    /**
     * Handle click on the button added just above
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemMenuSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On enable/disable switch click
     */
    @OnClick(R.id.adbSwitch)
    public void onSwitchClick() {
        // Check if root permission is granted
        if (!AppUtils.isRootPermissionGranted()) {
            tvState.setText(R.string.needs_root);
            tvInformation.setText("");
            adbSwitch.setChecked(false);
            return;
        }

        if (adbSwitch.isChecked()) {
            AppEngine.enableAdbOverTcp(this);
        } else {
            AppEngine.disableAdbOverTcp(this);
        }
    }

}
