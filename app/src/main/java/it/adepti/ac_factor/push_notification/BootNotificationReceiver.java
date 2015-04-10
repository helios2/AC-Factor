package it.adepti.ac_factor.push_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootNotificationReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent startServiceIntent = new Intent(context, NotificationService.class);
            context.startService(startServiceIntent);
        }
    }
}
