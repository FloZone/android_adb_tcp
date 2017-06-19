package fr.frodriguez.adbovertcp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

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
        Log.d("Croustade", "Intent received: " + intent);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        switch (intent.getAction()) {
            case ACTION_ENABLE:
                AppEngine.enableAdbOverTcp(context);
                break;

            case ACTION_DISABLE:
                AppEngine.disableAdbOverTcp(context);
                break;

            case Intent.ACTION_BOOT_COMPLETED:
                boolean bootEnabled = sharedPref.getBoolean(Preferences.KEY_ENABLE_BOOT, Preferences.KEY_ENABLE_BOOT_DEFAULT);
                if(bootEnabled) {
                    boolean wifiToggle = sharedPref.getBoolean(Preferences.KEY_TOGGLE_WITH_WIFI, Preferences.KEY_TOGGLE_WITH_WIFI_DEFAULT);
                    if(wifiToggle) {
                        if(WifiUtils.getWifiState(context).equals(WifiUtils.WifiState.WIFI_CONNECTED)) {
                            AppEngine.enableAdbOverTcp(context);
                        }
                        else {
                            AppEngine.disableAdbOverTcp(context);
                        }
                    }
                    else {
                        AppEngine.enableAdbOverTcp(context);
                    }
                }
                break;

            case ConnectivityManager.CONNECTIVITY_ACTION:
                boolean wifiToggle = sharedPref.getBoolean(Preferences.KEY_TOGGLE_WITH_WIFI, Preferences.KEY_TOGGLE_WITH_WIFI_DEFAULT);
                if(wifiToggle) {
                    if(WifiUtils.getWifiState(context).equals(WifiUtils.WifiState.WIFI_CONNECTED)) {
                        AppEngine.enableAdbOverTcp(context);
                    }
                    else {
                        AppEngine.disableAdbOverTcp(context);
                    }
                }
                else {
                    AppEngine.updateDisplayedInfo(context);
                }
                break;

            default:
                Log.d("Croustade", "Unknown action: " + intent.getAction());
                break;
        }

    }
}
