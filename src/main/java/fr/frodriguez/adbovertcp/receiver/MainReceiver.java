package fr.frodriguez.adbovertcp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import fr.frodriguez.adbovertcp.ADBManager;
import fr.frodriguez.adbovertcp.AppEngine;
import fr.frodriguez.adbovertcp.defines.Preferences;
import fr.frodriguez.library.utils.WifiUtils;

import static fr.frodriguez.adbovertcp.defines.Intents.ACTION_DISABLE;
import static fr.frodriguez.adbovertcp.defines.Intents.ACTION_ENABLE;

/**
 * By FloZone on 13/02/2017.
 */

public class MainReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("FLZ", "Intent received: " + intent);

        String action = intent.getAction();
        if (action == null) return;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        switch (action) {
            case ACTION_ENABLE:
                AppEngine.enableAdbOverTcp(context);
                break;

            case ACTION_DISABLE:
                AppEngine.disableAdbOverTcp(context);
                break;

            case Intent.ACTION_BOOT_COMPLETED:
                // If 'auto enable on boot" is enabled
                boolean bootEnabled = sharedPref.getBoolean(Preferences.KEY_ENABLE_BOOT, Preferences.KEY_ENABLE_BOOT_DEFAULT);
                if (bootEnabled) {
                    // If "auto toggle with wifi" is enabled
                    boolean wifiToggle = sharedPref.getBoolean(Preferences.KEY_TOGGLE_WITH_WIFI, Preferences.KEY_TOGGLE_WITH_WIFI_DEFAULT);
                    if (wifiToggle) {
                        if (WifiUtils.getWifiState(context).equals(WifiUtils.WifiState.WIFI_CONNECTED)) {
                            AppEngine.enableAdbOverTcp(context);
                        } else {
                            AppEngine.disableAdbOverTcp(context);
                        }
                    } else {
                        AppEngine.enableAdbOverTcp(context);
                    }
                }
                break;

            case ConnectivityManager.CONNECTIVITY_ACTION:
                // If "auto toggle with wifi" is enabled
                boolean wifiToggle = sharedPref.getBoolean(Preferences.KEY_TOGGLE_WITH_WIFI, Preferences.KEY_TOGGLE_WITH_WIFI_DEFAULT);
                if (wifiToggle) {
                    if (WifiUtils.getWifiState(context).equals(WifiUtils.WifiState.WIFI_CONNECTED)) {
                        AppEngine.enableAdbOverTcp(context);
                    } else {
                        AppEngine.disableAdbOverTcp(context);
                    }
                }
                // Else update the displayed info only if main activity is open, if "always notif" is enabled, or if "notif on enabled" & ADB TCP are enabled
                else {
                    boolean appActive = sharedPref.getBoolean(Preferences.KEY_APP_ACTIVE, Preferences.KEY_APP_ACTIVE_DEFAULT);
                    boolean alwaysNotif = sharedPref.getBoolean(Preferences.KEY_NOTIF_ALWAYS, Preferences.KEY_NOTIF_ALWAYS_DEFAULT);
                    boolean enabledNotif = sharedPref.getBoolean(Preferences.KEY_NOTIF_ENABLED, Preferences.KEY_NOTIF_ENABLED_DEFAULT);
                    if (appActive || alwaysNotif || (enabledNotif && ADBManager.isAdbOverTcpEnabled())) {
                        AppEngine.updateDisplayedInfo(context);
                    }
                }
                break;

            default:
                Log.d("FLZ", "Unknown action: " + intent.getAction());
                break;
        }
    }

}
