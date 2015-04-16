package it.adepti.ac_factor.fragment;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.utils.CheckConnectivity;
import it.adepti.ac_factor.utils.Constants;
import it.adepti.ac_factor.utils.FilesSupport;
import it.adepti.ac_factor.utils.RemoteServer;

public class Video extends Fragment implements MediaController.MediaPlayerControl {

    private final String TAG = "Video";

    // Video View
    private VideoView vidView;
    // Video address
    private String streamingVideoURL;
    private Uri vidUri;
    // String for today
    private String todayString;
    // Media Controller
    private MediaController mediaController;
    // Check connectivity
    private CheckConnectivity checkConnectivity;
    // Progress Bar
    private ProgressBar progressBar = null;

    private long time;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the video view on the activity
        vidView = new VideoView(getActivity());
        // Set up the Media Controller
        mediaController = new MediaController(getActivity());
        // Check Connectivity
        checkConnectivity = new CheckConnectivity(getActivity());
        // Initialize todayString in a format ggMMyy
        todayString = FilesSupport.dateTodayToString();
        // Initialize directory for download the file. It depends from todayString
        streamingVideoURL = new String(Constants.DOMAIN +
                                        todayString +
                                        Constants.VIDEO_RESOURCE +
                                        todayString +
                                        Constants.VIDEO_EXTENSION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.video_layout, container, false);

        CheckFileExistence checkFileExistence = new CheckFileExistence(streamingVideoURL);
        checkFileExistence.execute();

        if(!checkConnectivity.isConnected()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_noConnection), Toast.LENGTH_SHORT).show();
        }

        // Anchor mediaController View
        mediaController.setAnchorView(v);

        // Set Up the Video View
        vidView = (VideoView) v.findViewById(R.id.video_view);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);

        // Address URI Parsing and Setting
        vidUri = Uri.parse(streamingVideoURL);
        vidView.setVideoURI(vidUri);

        // Video start
        try{
            vidView.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);

        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                    }
                });
            }
        });

        // Start timer
        time = System.currentTimeMillis();

        // Set the media controller for the Video View
        vidView.setMediaController(mediaController);
        v.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(System.currentTimeMillis() - time > 1500)
                    mediaController.show();
                return false;
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Salva l'ultimo punto in cui si stava visualizzando il video
        super.onSaveInstanceState(outState);
        outState.putInt("video_pos", vidView.getCurrentPosition());
    }



    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        // Restore dell'ultimo punto in cui si stava visualizzando il video
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
            vidView.seekTo(savedInstanceState.getInt("video_pos"));
    }

    @Override
    public void onPause() {
        super.onPause();
        vidView.pause();
    }

    @Override
    public void start() {
        vidView.start();
    }

    @Override
    public void pause() {
        vidView.pause();
    }

    @Override
    public int getDuration() {
        return vidView.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return vidView.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        vidView.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return vidView.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return vidView.getBufferPercentage();
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

    private class CheckFileExistence extends AsyncTask {

        private String url;
        private boolean checkFileResult;
        private RemoteServer remoteServer = new RemoteServer();


        public CheckFileExistence(String url){
            this.url = url;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d(TAG,"doInBackground called");

            if(!remoteServer.checkFileExistenceOnServer(url)){
                Log.d(TAG, "No video content");
                checkFileResult = false;
            } else checkFileResult = true;

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(!checkFileResult && checkConnectivity.isConnected())
                Toast.makeText(getActivity(), getResources().getString(R.string.no_video_content), Toast.LENGTH_SHORT).show();
        }
    }
}
