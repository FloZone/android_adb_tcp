package fr.frodriguez.adbovertcp;

import android.content.Context;
import androidx.annotation.NonNull;

import fr.frodriguez.library.ShellCommand;
import fr.frodriguez.library.utils.MessageUtils;
import fr.frodriguez.library.utils.StringUtils;

/**
 * By FloZone on 11/02/2017.
 */
@SuppressWarnings("WeakerAccess")
public final class ADBManager {

    /**
     * Enable ADB over TCP
     *
     * @return true in case of success, false if an error occurs
     */
    public static boolean enableAdbOverTcp(@NonNull Context context, @NonNull String port) {
        // Enable ADB over TCP and restart ADB
        String[] commands = {
                "setprop service.adb.tcp.port " + port,
                "stop adbd",
                "start adbd"
        };
        // Run the commands
        ShellCommand cmdAdbTcp = ShellCommand.runAsRoot(commands);

        // Check if an error occurs
        if (cmdAdbTcp.isError()) {
            MessageUtils.showToast(context, "Error:\n"
                    + cmdAdbTcp.getError() + "\n"
                    + cmdAdbTcp.getLocalError()
            );
            return false;
        }
        return true;
    }

    /**
     * Disable ADB over TCP
     *
     * @return true if ADB over TCP is disabled, false if an error occurs
     */
    public static boolean disableAdbOverTcp(@NonNull Context context) {
        // Commands to disable ADB over TCP
        String[] commands = {
                "setprop service.adb.tcp.port -1",
                "stop adbd",
                "start adbd"
        };
        // Run the commands
        ShellCommand cmdAdbTcp = ShellCommand.runAsRoot(commands);

        // Check if an error occurs
        if (cmdAdbTcp.isError()) {
            MessageUtils.showToast(context, "Error:\n"
                    + cmdAdbTcp.getError() + "\n"
                    + cmdAdbTcp.getLocalError()
            );
            return false;
        }

        return true;
    }

    /**
     * Return whether ADB over TCP is enabled or not.
     */
    @SuppressWarnings("RedundantIfStatement")
    public static boolean isAdbOverTcpEnabled() {
        ShellCommand shellCommand = ShellCommand.runSimpleCommand("getprop service.adb.tcp.port");

        // Error getting ADB over TCP state
        if (shellCommand.isError()) {
            //Log.d("FLZ", "Error getting adb tcp state: " + shellCommand.getError()+ "\n" + shellCommand.getLocalError());
            return false;
        }
        // ADB over TCP disabled
        else if (StringUtils.isEmpty(shellCommand.getOutput())
                || shellCommand.getOutput().equals("-1")) {
            return false;
        }
        // ADB over TCP enabled
        else {
            return true;
        }
    }

}
