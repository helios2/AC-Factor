package it.adepti.ac_factor.utils;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteServer {

    public static boolean checkFileExistenceOnServer(String url){
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                Log.d("NotificationService", connection.getResponseMessage());
                return true;
            }else{
                Log.d("NotificationService", connection.getResponseMessage());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
