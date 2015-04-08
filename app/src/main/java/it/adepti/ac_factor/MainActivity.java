package it.adepti.ac_factor;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import it.adepti.ac_factor.ftp.FTPManager;


public class MainActivity extends ActionBarActivity {

    private FTPManager myManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
//                myManager.downloadFile("Text_080415");
//                return null;
//            }
//        };
//
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
//            connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        else
//            connectTask.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
