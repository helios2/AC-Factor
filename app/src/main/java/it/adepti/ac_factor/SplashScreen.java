package it.adepti.ac_factor;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;

import it.adepti.ac_factor.push_notification.NotificationService;
import it.adepti.ac_factor.utils.CheckConnectivity;
import it.adepti.ac_factor.utils.Constants;
import it.adepti.ac_factor.utils.FilesSupport;
import it.adepti.ac_factor.utils.RemoteServer;


public class SplashScreen extends Activity {

    private final String TAG = "SplashScreen";

    protected long ms = 0;
    protected long splashTime = 3000;
    protected boolean splashActive = true;
    protected boolean paused = false;

    // Animation
    private Animation animFadein;
    // Image View
    private ImageView splashView;
    // Progress Bar
    private ProgressBar progressBar = null;
    // Check Connectivity
    private CheckConnectivity checkConnectivity = new CheckConnectivity(this);
    // Strings
    private String downloadTextURL;
    private String streamingVideoURL;
    private String stringDownloadedFileOnDevice;
    // Files existence on server
    private boolean txtExistence;
    private boolean vidExistence;
    // File downloaded on device
    private File downloadedFileOnDevice;
    // Intents constants
    public static final String EXTRA_TEXT = "testoVisible";
    public static final String EXTRA_VIDEO = "videoVisible";
    public static final String EXTRA_TEXT_URL = "textUrl";
    public static final String EXTRA_VIDEO_URL = "videoUrl";
    public static final String EXTRA_TEXT_DEVICE = "textDevice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Hide the action bar
        if(Build.VERSION.SDK_INT >= 11)
            getActionBar().hide();
        // Get animation Fade-in
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        // Get Progress Bar
        progressBar = (ProgressBar) findViewById(R.id.splashProgressBar);
        // Get image view
        splashView = (ImageView) findViewById(R.id.splashIcon);
        // Set animation
        splashView.setAnimation(animFadein);

        //------------------------------
        // TODAY STRINGS
        //------------------------------

        // Initialize todayString in a format ggMMyy
        String todayString = FilesSupport.dateTodayToString();
        // Initialize directory for download the file. It depends from todayString
        downloadTextURL = new String(Constants.DOMAIN +
                todayString +
                Constants.TEXT_RESOURCE +
                todayString +
                Constants.TEXT_EXTENSION);
        // Initialize directory for download the file. It depends from todayString
        streamingVideoURL = new String(Constants.DOMAIN +
                todayString +
                Constants.VIDEO_RESOURCE +
                todayString +
                Constants.VIDEO_EXTENSION);
        // Initalize Text File Downloaded
        stringDownloadedFileOnDevice = new String(Environment.getExternalStorageDirectory().toString() +
                Constants.APP_ROOT_FOLDER +
                "/" + todayString +
                Constants.TEXT_RESOURCE +
                todayString +
                Constants.TEXT_EXTENSION);
        downloadedFileOnDevice = new File(stringDownloadedFileOnDevice);

        Thread mythread = new Thread() {
            public void run() {

                try {
                    while (splashActive && ms < splashTime) {
                        if (!paused)
                            ms = ms + 100;
                        sleep(100);
                    }
                } catch (Exception e) {
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                    // Connectivity Status
                    if (!checkConnectivity.isConnected()) {
                        // Search text File Existence on SDCard
                        if (downloadedFileOnDevice.exists()) {
                            txtExistence = true;
                            intentMainActivity();
                        } else {
                            intentNoConnection();
                        }
                    } else {
                        CheckFileExistence checkFileExistence = new CheckFileExistence(downloadTextURL, streamingVideoURL);
                        checkFileExistence.run();
                    }
                }
            }
        };
        mythread.start();
    }

    private void intentNoConnection(){
        Intent intent = new Intent(SplashScreen.this, NoConnectionActivity.class);
        startActivity(intent);
        finish();
    }

    private void intentMainActivity(){
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        intent.putExtra(EXTRA_TEXT, txtExistence);
        intent.putExtra(EXTRA_VIDEO, vidExistence);

        intent.putExtra(EXTRA_TEXT_URL, downloadTextURL);
        intent.putExtra(EXTRA_VIDEO_URL, streamingVideoURL);
        intent.putExtra(EXTRA_TEXT_DEVICE, stringDownloadedFileOnDevice);

        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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

    private class CheckFileExistence extends Thread {

        private RemoteServer remoteServer = new RemoteServer();
        private String urlTxt;
        private String urlVid;

        public CheckFileExistence(String url_txt, String url_vid) {
            this.urlTxt = url_txt;
            this.urlVid = url_vid;
        }

        @Override
        public void run() {
            // Video Check
            if (remoteServer.checkFileExistenceOnServer(urlVid)) {
                vidExistence = true;
            } else {
                vidExistence = false;
            }

            // Testo Check
            if (remoteServer.checkFileExistenceOnServer(urlTxt)) {
                txtExistence = true;
            } else {
                txtExistence = false;
            }

            Log.d("LifeCycle", "SplashScreen onPostExecute");
            intentMainActivity();

            super.run();
        }
    }
}
