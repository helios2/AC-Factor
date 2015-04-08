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
import java.util.Calendar;

import it.adepti.ac_factor.utils.StringUtils;

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
        downloadDirectory = new File(Environment.getExternalStorageDirectory().toString(), "/ACFactor/");
//        downloadDirectory = Environment.getExternalStorageDirectory();
//        File subDirectory = new File(downloadDirectory.toString(), "/ACFactor/");
        if(!downloadDirectory.exists())downloadDirectory.mkdirs();
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
                Log.d(TAG, "Connect - Working Directory Setted: " + workingDirectory);
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
            Log.d(TAG, "Set - Working Directory Setted: " + this.workingDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setDownloadDirectoryToTodayDirectory(){
        boolean created;
        Calendar nowDate = Calendar.getInstance();
        String dataDirectoryName = StringUtils.dateToString(nowDate);
        File todayDirectory = new File(downloadDirectory.toString() + "/" + dataDirectoryName);
        if (todayDirectory.exists()){
            created = false;
        }else {
            todayDirectory.mkdirs();
            downloadDirectory = new File(todayDirectory.toString());
            Log.d(TAG, "Setting today Directory " + this.downloadDirectory);
            created = true;
        }
        return created;
    }

    public boolean downloadFile(String file, String extension){
        // Create Directory For Todays Files.
        File destinationFile = new File(downloadDirectory, file + "." + extension);

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

    public File getDownloadDirectory() {
        return downloadDirectory;
    }
}


