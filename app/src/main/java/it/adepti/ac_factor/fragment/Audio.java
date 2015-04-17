package it.adepti.ac_factor.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.logging.LogRecord;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.utils.CheckConnectivity;
import it.adepti.ac_factor.utils.Constants;
import it.adepti.ac_factor.utils.FilesSupport;
import it.adepti.ac_factor.utils.RemoteServer;

public class Audio extends Fragment implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private static final String TAG = "Audio";

    // Media Player
    private MediaPlayer mediaPlayer;

    // Media Controller
    private MediaController mediaController;

    // Audio URL
    private String streamingAudioURL;

    // String for today
    private String todayString;

    // Image View
    private ImageView audioIcon;

    // Check Connectivity
    private CheckConnectivity checkConnectivity;

    // Check audio source
    private boolean checkAudioSource;

    // Util Constants
    public static final String CURRENT_POSITION = "curr_pos";
    private long time;

    private View v;

    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "Audio onCreate");
        super.onCreate(savedInstanceState);

        //-----------------------------------------------------
        // INITIALIZE VARIABLES
        //-----------------------------------------------------

        // Set up connectivity
        checkConnectivity = new CheckConnectivity(getActivity());

        // Initialize todayString in a format ggMMyy
        todayString = FilesSupport.dateTodayToString();

        // Initialize directory for download the file. It depends from todayString
        streamingAudioURL = new String(Constants.DOMAIN +
                                        todayString +
                                        Constants.AUDIO_RESOURCE +
                                        todayString +
                                        Constants.AUDIO_EXTENSION);

        Log.d(TAG, "Streaming url: " + streamingAudioURL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("LifeCycle", "Audio onCreateView");
        v = inflater.inflate(R.layout.audio_layout, container, false);

        // Audio Icon findById
        audioIcon = (ImageView) v.findViewById(R.id.audio_image_view);

        // AsyncTask Setting
        CheckFileExistence checkFileExistence = new CheckFileExistence(streamingAudioURL, savedInstanceState);
        checkFileExistence.execute();

        Log.d(TAG,"checkAudio " + checkAudioSource);

        // Check Connectivity
        if(!checkConnectivity.isConnected()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_noConnection), Toast.LENGTH_SHORT).show();
        }

        // Set up the Media Player
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);

        // Set up the Media Controller
        mediaController = new MediaController(v.getContext());

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save audio position
        Log.d("LifeCycle", "Audio onSavedInstanceState");
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, mediaPlayer.getCurrentPosition());
        Log.d("VideoBug","curr_audio_pos " + mediaPlayer.getCurrentPosition());
    }

    @Override
    public void onPause() {
        Log.d("LifeCycle", "Audio onPause");
        super.onPause();
        mediaPlayer.pause();
    }

    //-----------------------------------------------------
    // PREPARE THE MEDIA CONTROLLER
    //-----------------------------------------------------
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("LifeCycle", "Audio onPrepared");

        // Media Controller set-up
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(v.findViewById(R.id.media_controller_view));

        // Thread for Media Controller
        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });

        // Show the Media Controller onTouch
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //if(System.currentTimeMillis() - time > 1500)
                mediaController.show();
                return false;
            }
        });
    }

    //-----------------------------------------------------
    // MEDIA CONTROLLER METHODS
    //-----------------------------------------------------
    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    //-----------------------------------------------------
    // CHECK FILE EXISTENCE ON SERVER
    //-----------------------------------------------------
    private class CheckFileExistence extends AsyncTask {

        private String url;
        private Bundle savedInstanceState;
        private boolean checkAudio;
        private RemoteServer remoteServer = new RemoteServer();


        public CheckFileExistence(String url, Bundle savedInstanceState){
            this.url = url;
            this.savedInstanceState = savedInstanceState;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            Log.d("LifeCycle", "Audio doInBackground");

            if(!remoteServer.checkFileExistenceOnServer(url)){
                Log.d(TAG, "No audio content");
                checkAudio = false;
            } else checkAudio = true;

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d("LifeCycle", "Audio onPostExecute");
            if(!checkAudio) {
                if(checkConnectivity.isConnected())
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_audio_content), Toast.LENGTH_SHORT).show();
            } else {
                // Media Player Settings
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                // Set Data Source
                try {
                    mediaPlayer.setDataSource(streamingAudioURL);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Media Player Prepare
                try {
                    mediaPlayer.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (savedInstanceState != null)
                    mediaPlayer.seekTo(savedInstanceState.getInt(CURRENT_POSITION));

                // Media Player Start
                mediaPlayer.start();
            }
        }
    }
}
