package it.adepti.ac_factor.push_notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import it.adepti.ac_factor.MainActivity;
import it.adepti.ac_factor.R;
import it.adepti.ac_factor.utils.CheckConnectivity;

public class NotificationService extends Service{

    // Broadcast Receiver
    private BroadcastReceiver networkStateReceiver;
    // Intent Filter
    private IntentFilter filter;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStartCommand");

        //-----------------------------------------------------
        // REGISTERING RECEIVER
        //-----------------------------------------------------
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Service", "Called onReceive");
                Bundle extras = intent.getExtras();
                NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");

                NetworkInfo.State state = info.getState();

                if(state == NetworkInfo.State.CONNECTED){
                    // onNetworkUp()
                }else{
                    // onNetworkDown()
                }

            }
        };

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(networkStateReceiver, filter);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "onDestroy");
        unregisterReceiver(networkStateReceiver);
        super.onDestroy();
    }
}
