package it.adepti.ac_factor.utils;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.util.Log;

public class CheckConnectivity extends Activity {

    private final String TAG = "CheckConnectivity";

    private ConnectivityManager connectivityManager;

    public CheckConnectivity(){

    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        connectivityManager = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

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
}
