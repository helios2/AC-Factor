package it.adepti.ac_factor.push_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootNotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Leggere da preferenze e this.abortBroadcast() // Funziona solo con sendOrderedBroadcast
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) && sharedPreferences.getBoolean("prefPushNotify", true)) {
            Intent startServiceIntent = new Intent(context, NotificationService.class);
            context.startService(startServiceIntent);
        }
    }
}
