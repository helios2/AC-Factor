package it.adepti.ac_factor;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

//import it.adepti.ac_factor.ftp.FTPManager;

public class Text extends FragmentTab {

    //private FTPManager myManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        AsyncTask connectTask = new AsyncTask(){
//            @Override
//            protected Object doInBackground(Object[] params) {
//                Log.d("FTPManager", "doInBackground");
//                myManager = new FTPManager();
//                myManager.connectWithFTP("ftp.androidprova.altervista.org",
//                        "androidprova",
//                        "dukcivosne70",
//                        FTPManager.PASSIVE_MODE);
//                myManager.setWorkingDirectory("/080415/");
//                myManager.downloadFile("Text_080415", "txt");
//                return null;
//            }
//        };
//
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
//            connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        else
//            connectTask.execute();
    }
}
