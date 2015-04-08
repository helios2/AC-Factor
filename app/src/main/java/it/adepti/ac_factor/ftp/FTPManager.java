package it.adepti.ac_factor.ftp;

import android.os.Environment;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

public class FTPManager {

    // Debug
    private final static String TAG = "FTPManager";

    // Mode
    public final static int ACTIVE_MODE = 100;
    public final static int PASSIVE_MODE = 200;

    // Members field
    private File downloadDirectory;
    private String workingDirectory;

    // Instance of Apache FTPClient
    private FTPClient myClient = null;

    public FTPManager(){
        downloadDirectory = Environment.getExternalStorageDirectory();
//        downloadDirectory = new File("/sdcard/");
        Log.d(TAG, "Constructor set download directory to: " + downloadDirectory.toString());
    }



/**
     * Connection to a remote ftp host
     * @param host   String address of remote host
     * @param user   String Username
     * @param pass   String Password
     * @param mode   Mode of connection - Use ACTIVE_MODE or PASSIVE_MODE
     */


    public void connectWithFTP(String host, String user, String pass, int mode){
        Log.d(TAG, "Try to connect with " + host);
        boolean status;

        myClient = new FTPClient();
        myClient.setConnectTimeout(10*1000);
        myClient.setDefaultPort(21);
        try {
            InetAddress hostAddress = InetAddress.getByName(host);
            myClient.connect(hostAddress);
            Log.d(TAG, myClient.getReplyString());

            switch (mode){
                case ACTIVE_MODE:
                    myClient.enterLocalActiveMode();
                    Log.d(TAG, "Enter Active Mode");
                    break;
                case PASSIVE_MODE:
                    myClient.enterLocalPassiveMode();
                    Log.d(TAG, "Enter Passive Mode");
                    break;
                default:
                    Log.e(TAG, "Invalid mode type: " + mode);
                    break;
            }

            status = myClient.login(user, pass);
            if (status){
                Log.d(TAG, "Log in success");
                Log.d(TAG, myClient.getReplyString());
                myClient.setFileType(FTP.BINARY_FILE_TYPE);
                workingDirectory = myClient.printWorkingDirectory();
                Log.d(TAG, "Working Directory Setted: " + workingDirectory);
            }else{
                Log.d(TAG, "Log in failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



/**
     * Return current working directory on FTP server
     * @return
     */

    public String getWorkingDirectory() {
        return workingDirectory;
    }

/**
     * Set working directory on FTP server
     * @param workingDirectory String that shows current directory
     */

    public void setWorkingDirectory(String workingDirectory) {
        try {
            myClient.changeWorkingDirectory(workingDirectory);
            this.workingDirectory = workingDirectory;
            Log.d(TAG, "Working Directory Setted: " + this.workingDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean downloadFile(String file, String extension){
        File fileDirectory = new File(downloadDirectory.toString() + "/ACFactor/");
        fileDirectory.mkdirs();
        File destinationFile = new File(fileDirectory, file + "." + extension);

        OutputStream out = null;

        try {
            out = new BufferedOutputStream(new FileOutputStream(destinationFile));
            myClient.setFileType(FTP.BINARY_FILE_TYPE);
            Log.d(TAG, "Try to download " + workingDirectory + file);
            return myClient.retrieveFile(workingDirectory + file, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}


