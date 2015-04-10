package it.adepti.ac_factor.utils;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckConnectivity {

    private final String TAG = "CheckConnectivity";

    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetwork;

    private Activity activity;

    public CheckConnectivity(Activity activity){
        this.activity = activity;
    }

    @Deprecated
    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        connectivityManager = (ConnectivityManager)activity.getSystemService(activity.getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

            Log.d(TAG,"Connesso");
            return true;

        } else if (
                connectivityManager.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connectivityManager.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

           Log.d(TAG,"Non connesso");
            return false;
        }
        return false;
    }

    public boolean isConnected(){
        connectivityManager = (ConnectivityManager) activity.getSystemService(activity.getApplicationContext().CONNECTIVITY_SERVICE);

        activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public boolean isWiFi(){
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean isMobile(){
        return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
    }
}
