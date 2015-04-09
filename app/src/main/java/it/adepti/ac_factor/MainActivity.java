package it.adepti.ac_factor;

//import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import it.adepti.ac_factor.fragment.Audio;
import java.util.Calendar;

import it.adepti.ac_factor.fragment.Testo;
import it.adepti.ac_factor.fragment.Video;
import it.adepti.ac_factor.ftp.FTPManager;
import it.adepti.ac_factor.utils.Constants;
import it.adepti.ac_factor.utils.StringUtils;

public class MainActivity extends FragmentActivity {

    // Fragment per i tab
    private FragmentTabHost mTabHost;

    // FTP Manager
    private FTPManager myManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Tab settings */
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab_txt").setIndicator(buildTabLayout(getResources().getString(R.string.tab_testo))), Testo.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab_video").setIndicator(buildTabLayout(getResources().getString(R.string.tab_video))), Video.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab_audio").setIndicator(buildTabLayout(getResources().getString(R.string.tab_audio))), Audio.class, null);

        /** Async Task Execution */
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            connectTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        else
            connectTask.execute();

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

    /** FTP Setting Up Task*/
    AsyncTask connectTask = new AsyncTask(){
        @Override
        protected void onPreExecute() {
            myManager = FTPManager.getManagerInstance();
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
            myManager.downloadFile("Text_090415", "txt");
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            FragmentManager fm = getSupportFragmentManager();
            Testo testo = (Testo)fm.findFragmentByTag("tab_txt");
            testo.getTextHandler().obtainMessage(Constants.DOWNLOAD_DONE, Constants.DOWNLOAD_TEXT_DONE, -1,
                    -1).sendToTarget();
            super.onPostExecute(o);
        }
    };
}
