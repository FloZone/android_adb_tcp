package fr.frodriguez.adbovertcp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;

import fr.frodriguez.adbovertcp.activity.MainActivity;
import fr.frodriguez.adbovertcp.defines.AppDefines;
import fr.frodriguez.adbovertcp.defines.Intents;
import fr.frodriguez.adbovertcp.defines.Preferences;
import fr.frodriguez.library.utils.MessageUtils;
import fr.frodriguez.library.utils.WifiUtils;

import static fr.frodriguez.adbovertcp.defines.Intents.ACTION_DISABLE;
import static fr.frodriguez.adbovertcp.defines.Intents.ACTION_ENABLE;

/**
 * By FloZone on 18/02/2017.
 */
@SuppressWarnings("WeakerAccess")
public final class AppEngine {

    /**
     * Enable ADB over TCP and update the displayed information
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean enableAdbOverTcp(@NonNull Context context) {
        // Get port
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String port = sharedPref.getString(Preferences.KEY_PORT, Preferences.KEY_PORT_DEFAULT);

        // If successfully enabled
        if (ADBManager.enableAdbOverTcp(context, port)) {
            MessageUtils.showToast(context, context.getString(R.string.aot_enabled));

            updateDisplayedInfo(context);

            return true;
        }
        // Error while enabling
        else {
            MessageUtils.showToast(context, context.getString(R.string.aot_enable_error));

            // Send an intent with info
            Intent infoIntent = new Intent(Intents.ACTION_INFO);
            infoIntent.putExtra(Intents.EXTRA_ENABLED, false);
            infoIntent.putExtra(Intents.EXTRA_MESSAGE, context.getResources().getString(R.string.needs_root));
            context.sendBroadcast(infoIntent);

            return false;
        }
    }

    /**
     * Disable ADB over TCP and update the displayed information
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean disableAdbOverTcp(@NonNull Context context) {
        // If successfully disabled
        if (ADBManager.disableAdbOverTcp(context)) {
            MessageUtils.showToast(context, context.getString(R.string.aot_disabled));

            updateDisplayedInfo(context);

            return true;
        }
        // Error while disabling
        else {
            MessageUtils.showToast(context, context.getString(R.string.aot_disable_error));

            // Send an intent with info
            Intent infoIntent = new Intent(Intents.ACTION_INFO);
            infoIntent.putExtra(Intents.EXTRA_ENABLED, true);
            infoIntent.putExtra(Intents.EXTRA_MESSAGE, context.getResources().getString(R.string.needs_root));
            context.sendBroadcast(infoIntent);

            return false;
        }
    }

    /**
     * Update the displayed information: main activity and notification
     */
    public static void updateDisplayedInfo(@NonNull Context context) {
        // Show "ADB over TCP is enabled" info
        if (ADBManager.isAdbOverTcpEnabled()) {
            // Get port
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String port = sharedPref.getString(Preferences.KEY_PORT, Preferences.KEY_PORT_DEFAULT);

            String title = context.getResources().getString(R.string.aot_enabled);
            // Get the Wifi state to set the information message
            String message;
            switch (WifiUtils.getWifiState(context)) {
                case WIFI_OFF:
                    message = context.getResources().getString(R.string.wifi_off);
                    break;
                case WIFI_NOT_CONNECTED:
                    message = context.getResources().getString(R.string.wifi_not_connected);
                    break;
                case WIFI_CONNECTED:
                    // Get the Wifi IP address
                    message = "adb connect " + WifiUtils.getWifiIPAddress(context) + ":" + port;
                    break;
                default:
                    message = context.getResources().getString(R.string.wifi_unknown_state);
                    break;
            }

            // Send an intent with info
            Intent infoIntent = new Intent(Intents.ACTION_INFO);
            infoIntent.putExtra(Intents.EXTRA_ENABLED, true);
            infoIntent.putExtra(Intents.EXTRA_TITLE, title);
            infoIntent.putExtra(Intents.EXTRA_MESSAGE, message);
            context.sendBroadcast(infoIntent);

            // If notification preference is enabled, show a notification
            boolean notifEnabled = sharedPref.getBoolean(Preferences.KEY_NOTIF_ENABLED, Preferences.KEY_NOTIF_ENABLED_DEFAULT);
            if (notifEnabled) {
                showNotification(context, title, message, false);
            } else {
                removeNotification(context);
            }
        }
        // Else show "ADB over TCP is disabled" info
        else {
            String title = context.getResources().getString(R.string.aot_disabled);

            // Send an intent with info
            Intent infoIntent = new Intent(Intents.ACTION_INFO);
            infoIntent.putExtra(Intents.EXTRA_ENABLED, false);
            infoIntent.putExtra(Intents.EXTRA_TITLE, title);
            infoIntent.putExtra(Intents.EXTRA_MESSAGE, "");
            context.sendBroadcast(infoIntent);

            // If notification has to be displayed
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            boolean notifEnabled = sharedPref.getBoolean(Preferences.KEY_NOTIF_ENABLED, Preferences.KEY_NOTIF_ENABLED_DEFAULT);
            boolean notifAlwaysEnabled = sharedPref.getBoolean(Preferences.KEY_NOTIF_ALWAYS, Preferences.KEY_NOTIF_ALWAYS_DEFAULT);
            if (notifEnabled && notifAlwaysEnabled) {
                showNotification(context, title, "", true);
            } else {
                removeNotification(context);
            }
        }
    }

    /**
     * Show the notification showing the given state
     */
    public static void showNotification(@NonNull Context context, @NonNull String title, @NonNull String message, boolean enableButton) {
        // Create the main activity intent
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, AppDefines.REQUEST_CODE_MAIN_ACTIVITY, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_adb_white_36dp)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(message)
                .setOngoing(true) // Non-removable notification
                .setContentIntent(mainActivityPendingIntent); // Open main activity on click

        // Set the enable button
        if (enableButton) {
            // Create the enable intent
            Intent enableIntent = new Intent(ACTION_ENABLE);
            PendingIntent enablePendingIntent = PendingIntent.getBroadcast(context, AppDefines.REQUEST_CODE_ENABLE_INTENT, enableIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Add the button
            builder.addAction(R.drawable.ic_done_white_36dp, "Enable", enablePendingIntent);
        }
        // Set the disable button
        else {
            // Create the disable intent
            Intent disableIntent = new Intent(ACTION_DISABLE);
            PendingIntent disablePendingIntent = PendingIntent.getBroadcast(context, AppDefines.REQUEST_CODE_DISABLE_INTENT, disableIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Add the button
            builder.addAction(R.drawable.ic_clear_white_36dp, "Disable", disablePendingIntent);
        }

        // Show the notification
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(AppDefines.NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Remove the current notification
     */
    public static void removeNotification(@NonNull Context context) {
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(AppDefines.NOTIFICATION_ID);
        }
    }

}
