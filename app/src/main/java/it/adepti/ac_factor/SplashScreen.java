package it.adepti.ac_factor;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    // Files existence
    private boolean txtExistence;
    private boolean vidExistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Hide the action bar
        getActionBar().hide();

        // Get animation Fade-in
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);

        // Get Progress Bar
        progressBar = (ProgressBar) findViewById(R.id.splashProgressBar);

        // Get image view
        splashView = (ImageView) findViewById(R.id.splashIcon);

        // Set animation
        splashView.setAnimation(animFadein);

        Thread mythread = new Thread() {
            public void run() {
                try {
                    while (splashActive && ms < splashTime) {
                        if(!paused)
                            ms=ms+100;
                        sleep(100);
                    }
                } catch(Exception e) {}
                finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                    // Connectivity Status
                    if(!checkConnectivity.isConnected()){
                        Log.d(TAG, "No connection");
                        //Toast.makeText(this, getResources().getString(R.string.text_noConnection), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SplashScreen.this, NoConnectionActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Initialize todayString in a format ggMMyy
                        String todayString = FilesSupport.dateTodayToString();
                        // Initialize directory for download the file. It depends from todayString
                        String downloadTextURL = new String(Constants.DOMAIN +
                                todayString +
                                Constants.TEXT_RESOURCE +
                                todayString +
                                Constants.TEXT_EXTENSION);
                        // Initialize directory for download the file. It depends from todayString
                        String streamingVideoURL = new String(Constants.DOMAIN +
                                todayString +
                                Constants.VIDEO_RESOURCE +
                                todayString +
                                Constants.VIDEO_EXTENSION);
                        // TODO: possibile passare le stringhe con intent
                        // Check Existence
                        CheckFileExistence checkFileExistence = new CheckFileExistence(downloadTextURL,streamingVideoURL);
                        checkFileExistence.execute();
                    }
                }
            }
        };
        mythread.start();
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

    private class CheckFileExistence extends AsyncTask {

        private RemoteServer remoteServer = new RemoteServer();
        private String urlTxt;
        private String urlVid;
        private int txtResultCode;
        private int vidResultCode;

        public CheckFileExistence(String url_txt, String url_vid){
            this.urlTxt = url_txt;
            this.urlVid = url_vid;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d("LifeCycle", "SplashScreen doInBackground");

            // Video Check
            if(remoteServer.checkFileExistenceOnServer(urlVid)){
                vidExistence = true;
                Log.d(TAG, "vidExistence set to true");
            } else {
                vidExistence = false;
                Log.d(TAG, "vidExistence set to false");
            }

            // Testo Check
            HttpURLConnection connectionTesto = null;

            try {
                URL urlTesto = new URL(urlTxt);
                connectionTesto = (HttpURLConnection) urlTesto.openConnection();
                connectionTesto.connect();

                // Expect HTTP 200 OK
                if (connectionTesto.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    txtResultCode = connectionTesto.getResponseCode();
                    return "(Testo) Server returned HTTP " + connectionTesto.getResponseCode()
                            + " " + connectionTesto.getResponseMessage();
                }

                Log.d(TAG, "txtResultCode: " + txtResultCode);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connectionTesto != null)
                connectionTesto.disconnect();

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("LifeCycle", "SplashScreen onPostExecute");
            if (txtResultCode == HttpURLConnection.HTTP_NOT_FOUND) {
                txtExistence = false;
                Log.d(TAG, "txtExistence set to false");
            }
            else {
                txtExistence = true;
                Log.d(TAG, "txtExistence set to true");
            }

//            if (vidResultCode == HttpURLConnection.HTTP_NOT_FOUND){
//                vidExistence = false;
//                Log.d(TAG, "vidExistence set to false");
//            }
//            else {
//                vidExistence = true;
//                Log.d(TAG, "vidExistence set to true");
//            }

            // Intent alla MainActivity
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            intent.putExtra("testoVisible",txtExistence);
            intent.putExtra("videoVisible",vidExistence);
            startActivity(intent);
            finish();
        }
    }
}
