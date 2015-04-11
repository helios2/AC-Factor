package it.adepti.ac_factor.utils;

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
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
