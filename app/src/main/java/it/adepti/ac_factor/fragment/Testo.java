package it.adepti.ac_factor.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.utils.CheckConnectivity;
import it.adepti.ac_factor.utils.Constants;
import it.adepti.ac_factor.utils.FilesSupport;

public class Testo extends Fragment {

    // Text field where the downloaded text will appear.
    private TextView mTextView;
    // Progress bar for trace download.
    private ProgressDialog mProgressDialog;
    // File downloaded on device
    private File downloadedFileOnDevice;
    // String for today
    private String todayString;
    // String for URL download
    private String downloadTextURL;
    // Media state
    private String mediaState;
    // Broadcast Receiver
    private BroadcastReceiver networkStateReceiver;
    // Check Connectivity Manager
    private CheckConnectivity connectivityManager;
    // Intent Filter
    private IntentFilter filter;
    // Dialog Already Shown
    private boolean alreadyShown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "Testo onCreate");
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
        //-----------------------------------------------------
        // INITIALIZE VARIABLES
        //-----------------------------------------------------

        // Initialize Connectivity Manager
        connectivityManager = new CheckConnectivity(getActivity());

        // Initialize Media State
        mediaState = Environment.getExternalStorageState();

        // Initialize todayString in a format ggMMyy
        todayString = FilesSupport.dateTodayToString();

        // Initialize directory in device to put the file. It depends from TodayString
        downloadedFileOnDevice = new File(Environment.getExternalStorageDirectory().toString() +
                Constants.APP_ROOT_FOLDER +
                "/" + todayString +
                Constants.TEXT_RESOURCE +
                todayString +
                Constants.TEXT_EXTENSION);

        // Initialize directory for download the file. It depends from todayString
        downloadTextURL = new String(Constants.DOMAIN +
                                    todayString +
                                    Constants.TEXT_RESOURCE +
                                    todayString +
                                    Constants.TEXT_EXTENSION);

        // Initialize already shown
        alreadyShown = false;

        //-----------------------------------------------------
        // REGISTERING RECEIVER
        //-----------------------------------------------------
        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Receiver", "Called onReceive");
                downloadTodayText();
            }
        };

        filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("LifeCycle", "Testo onCreateView");
        View v = inflater.inflate(R.layout.text_layout, container, false);
        mTextView = (TextView) v.findViewById(R.id.text);
        Log.d("Media", mediaState);

        if(savedInstanceState != null){
            downloadedFileOnDevice = new File(savedInstanceState.getString("downloadFile"));
            downloadTextURL = savedInstanceState.getString("downloadUrl");
            alreadyShown = savedInstanceState.getBoolean("alreadyShown");
        }

        downloadTodayText();

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(downloadedFileOnDevice != null) outState.putString("downloadFile", downloadedFileOnDevice.toString());
        if(downloadTextURL != null) outState.putString("downloadUrl", downloadTextURL);
        outState.putBoolean("alreadyShown", alreadyShown);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d("LifeCycle", "Testo onDestroy");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.d("LifeCycle", "Testo onPause");
        getActivity().unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.d("LifeCycle", "Testo onResume");
        getActivity().registerReceiver(networkStateReceiver,filter);
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d("LifeCycle", "Testo onStop");
        if(mProgressDialog != null)mProgressDialog.dismiss();
        super.onStop();
    }

    private void downloadTodayText() {
        //TODO Rivedere l'ordine dei controlli
        if (!downloadedFileOnDevice.exists()) {
            if (connectivityManager.isConnected()) {
                if (mediaState.equals(Environment.MEDIA_MOUNTED)) {

                    // Progress dialog for download
                    mProgressDialog = new ProgressDialog(getActivity());
                    mProgressDialog.setMessage(getResources().getString(R.string.text_downloadText));
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(true);

                    // execute this when the downloader must be fired
                    final DownloadTextTask downloadTextTask = new DownloadTextTask(getActivity());
                    downloadTextTask.execute(downloadTextURL);
                    Log.d("Download", "Try to download " + downloadTextURL);

                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            downloadTextTask.cancel(true);
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.external_memory_problem), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.text_noConnection), Toast.LENGTH_LONG).show();
            }
        } else {
            mTextView.setText(FilesSupport.readTextFromFile(downloadedFileOnDevice.toString()));
        }
    }

    // Task to download text
    private class DownloadTextTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private int resultCode;

        public DownloadTextTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            if(!alreadyShown)
                mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Expect HTTP 200 OK
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    resultCode = connection.getResponseCode();
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // Download the file
                input = connection.getInputStream();
                output = new FileOutputStream(downloadedFileOnDevice);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // Allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // Publishing the progress....
                    if (fileLength > 0) // Only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // If we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Dialog", "PostExecute");
            super.onPostExecute(result);
            mWakeLock.release();
            if(mProgressDialog.isShowing()) {
                Log.d("Dialog", "Dismiss");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                });
                alreadyShown = true;
            }
            if (result != null){
                if(resultCode == HttpURLConnection.HTTP_NOT_FOUND)
                    Toast.makeText(context, getResources().getString(R.string.no_text_content), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, getResources().getString(R.string.server_response) + result,
                            Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, getResources().getString(R.string.resource_downloaded),
                        Toast.LENGTH_SHORT).show();
                mTextView.setText(FilesSupport.readTextFromFile(downloadedFileOnDevice.toString()));
            }
        }
    }
}
