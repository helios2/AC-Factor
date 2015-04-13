package it.adepti.ac_factor.push_notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.io.File;

import it.adepti.ac_factor.MainActivity;
import it.adepti.ac_factor.R;
import it.adepti.ac_factor.utils.Constants;
import it.adepti.ac_factor.utils.FilesSupport;
import it.adepti.ac_factor.utils.RemoteServer;

public class NotificationService extends Service{

    // Intent Filter
    private IntentFilter filter;
    // File downloaded on device
    private File downloadedFileOnDevice;
    // String for today
    private String todayString;
    // String for URL download
    private String downloadTextURL;
    // Media state
    private String mediaState;
    // Receiver State
    private boolean isRegistered = false;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "onBind");
        return null;
    }

    private class CheckOnNetworkUpTask extends AsyncTask{

        private Context mContext;

        public CheckOnNetworkUpTask(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            if (RemoteServer.checkFileExistenceOnServer(downloadTextURL)){
                createNotification(getString(R.string.text_newContent));

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mContext.stopService(new Intent(mContext, NotificationService.class));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStartCommand");

        // Initialize Media State
        mediaState = Environment.getExternalStorageState();

        if(isOnline()){
            // Connection is already UP

            // Check if file already exist on device
            if (mediaState.equals(Environment.MEDIA_MOUNTED)) {
                initializeTodayVariables();
                if (!downloadedFileOnDevice.exists()) {
                    CheckOnNetworkUpTask myTask = new CheckOnNetworkUpTask(this);
                    myTask.execute();
                } else {
                    // FILE ALREADY EXIST (APP ALREADY OPENED)
                    this.stopSelf();
                }
            }else{
                Log.e("Service", "Media not mounted, it is impossible to read memory");
            }
        }else {
            // Connection is down...register receiver to wait connectivity.
            filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkStateReceiver, filter);
            isRegistered = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "onDestroy");
        if(isRegistered) {
            unregisterReceiver(networkStateReceiver);
            isRegistered = false;
        }
        super.onDestroy();
    }

    private final BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Service", "Called onReceive");
            Bundle extras = intent.getExtras();
            NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");

            NetworkInfo.State state = info.getState();

            initializeTodayVariables();

            if(state == NetworkInfo.State.CONNECTED){
                // NETWORK UP
                if (mediaState.equals(Environment.MEDIA_MOUNTED)) {
                    // MEDIA MOUNTED
                    if(!downloadedFileOnDevice.exists()) {
                        // FILE NOT EXIST (CHECK FOR UPDATES)
                        CheckOnNetworkUpTask myTask = new CheckOnNetworkUpTask(context);
                        myTask.execute();
                    }else{
                        // FILE ALREADY EXIST (APP ALREADY OPENED)
                        context.stopService(new Intent(context, NotificationService.class));
                    }
                }else{
                    // MEDIA NOT MOUNTED
                }
            }else{
                // NETWORK DOWN
            }
        }
    };

    private void initializeTodayVariables(){

        // Initialize todayString in a format ggMMyy
        todayString = FilesSupport.dateTodayToString();

        // Initialize directory in device to put the file. It depends from todayString
        downloadedFileOnDevice = new File(Environment.getExternalStorageDirectory().toString() +
                Constants.APP_ROOT_FOLDER +
                "/" + todayString +
                Constants.TEXT_RESOURCE +
                todayString +
                Constants.TEXT_EXTENSION);

        // Initialize directory for download the file. It depends from todayString
        downloadTextURL = new String(Constants.DOMAIN +
                todayString +
                Constants.TEXT_RESOURCE +
                todayString +
                Constants.TEXT_EXTENSION);
    }

    private void createNotification(String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // TODO controllare che sia giusto mettere MainActivity
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(R.id.startupNotificationID, mBuilder.build());
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
