package it.adepti.ac_factor;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import it.adepti.ac_factor.fragment.Audio;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import it.adepti.ac_factor.fragment.Testo;
import it.adepti.ac_factor.fragment.Video;
import it.adepti.ac_factor.ftp.FTPManager;
import it.adepti.ac_factor.utils.StringUtils;

public class MainActivity extends FragmentActivity {

    // Fragment per i tab
    private FragmentTabHost mTabHost;

    // FTP Manager
    private FTPManager myManager;

    private StringBuilder text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            connectTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        else
            connectTask.execute();

        /** Tab settings */
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab_txt").setIndicator(buildTabLayout(getResources().getString(R.string.tab_testo))), Testo.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab_video").setIndicator(buildTabLayout(getResources().getString(R.string.tab_video))), Video.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab_audio").setIndicator(buildTabLayout(getResources().getString(R.string.tab_audio))), Audio.class, null);

    }

    private View buildTabLayout(String tag) {
        View tab = getLayoutInflater().inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) tab.findViewById(R.id.tab_layout_tv);
        tv.setText(tag);
        return tab;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public FTPManager getMyManager() {
        return myManager;
    }

    public AsyncTask getConnectTask() {
        return connectTask;
    }

    public StringBuilder getStringBuilderText() {
        return text;
    }

    /** FTP Setting Up */
    AsyncTask connectTask = new AsyncTask(){
        @Override
        protected void onPreExecute() {
            myManager = new FTPManager();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            myManager.connectWithFTP("ftp.androidprova.altervista.org",
                    "androidprova",
                    "dukcivosne70",
                    FTPManager.PASSIVE_MODE);

            Calendar nowDate = Calendar.getInstance();
            myManager.setWorkingDirectory("/" + StringUtils.dateToString(nowDate));
            myManager.setDownloadDirectoryToTodayDirectory();
            myManager.downloadFile("Text_080415", "txt");

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Calendar nowDate = Calendar.getInstance();
            File readFile = new File(myManager.getDownloadDirectory().toString(),
                    StringUtils.dateToString(nowDate) + ".txt");

            text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(readFile));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }
            super.onPostExecute(o);
        }
    };
}
