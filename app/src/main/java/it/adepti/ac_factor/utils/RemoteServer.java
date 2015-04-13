package it.adepti.ac_factor.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteServer {

    public static boolean checkFileExistenceOnServer(String url){
        int responseCode = 0;
        try {
            URL checkContentURL = new URL(url);
            HttpURLConnection connection =  ( HttpURLConnection )  checkContentURL.openConnection ();
            connection.setRequestMethod("GET");
            connection.connect() ;
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(responseCode == HttpURLConnection.HTTP_OK)
            return true;
        else
            return false;
    }

}
