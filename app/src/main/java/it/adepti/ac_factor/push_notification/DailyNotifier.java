package it.adepti.ac_factor.push_notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.SplashScreen;


public class DailyNotifier extends BroadcastReceiver{

    private final String TAG = "ServiceNotification";

    private final GregorianCalendar toDate = new GregorianCalendar(2015,4,10);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive DailyNotifier");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        Calendar todayDate = Calendar.getInstance();
        if(todayDate.before(toDate) && todayDate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            createNotification(context, context.getResources().getString(R.string.text_dailyNotification));
    }

    /**
     * Create a notification on android notification bar
     * @param message Message to display into notification.
     */
    public void createNotification(Context context, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_notification_icon)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true);
        Intent resultIntent = new Intent(context, SplashScreen.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SplashScreen.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.id.dailyNotificationID, mBuilder.build());
    }

    public void setAlarm(Context context){
        Log.d(TAG, "Alarm Setted");
        AlarmManager am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent appIntent = new Intent(context, DailyNotifier.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, appIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, pi);
    }

    public void cancelAlarm(Context context){
        Log.d(TAG, "Alarm Canceled");
        Intent intent = new Intent(context, DailyNotifier.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}
