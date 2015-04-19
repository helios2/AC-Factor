package it.adepti.ac_factor.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import it.adepti.ac_factor.R;
import it.adepti.ac_factor.utils.CheckConnectivity;
import it.adepti.ac_factor.utils.Constants;
import it.adepti.ac_factor.utils.FilesSupport;
import it.adepti.ac_factor.utils.RemoteServer;

public class Video extends Fragment implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

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
    private boolean isConnected;
    // Progress Bar
    private ProgressBar progressBar = null;
    private Handler handler;
    // Layout View
    private View v;
    // Video Ready
    private boolean videoReady = false;
    // Video visibility
    private boolean videoVisible = false;
    // Check File Result
    private boolean checkFileResult;
    // Check Connectivity
    private CheckConnectivity checkConnectivity;

//    @SuppressLint("ValidFragment")
//    public Video(boolean isConnected){
//        this.isConnected = isConnected;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("LifeCycle", "Video onCreate");

        super.onCreate(savedInstanceState);

        // Initialize todayString in a format ggMMyy
        todayString = FilesSupport.dateTodayToString();
        // Initialize directory for download the file. It depends from todayString
        streamingVideoURL = new String(Constants.DOMAIN +
                todayString +
                Constants.VIDEO_RESOURCE +
                todayString +
                Constants.VIDEO_EXTENSION);
        // Check for connection status
        checkConnectivity = new CheckConnectivity(getActivity());
        isConnected = checkConnectivity.isConnected();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("LifeCycle", "Video onCreateView");

        CheckFileExistence checkFileExistence = new CheckFileExistence(streamingVideoURL);
        checkFileExistence.execute();

        v = inflater.inflate(R.layout.video_layout, container, false);

        // Create the video view on the activity
        vidView = new VideoView(getActivity());

        // Set Up the Video View
        vidView = (VideoView) v.findViewById(R.id.video_view);
        vidView.setVisibility(View.INVISIBLE);

        // Set up progressBar
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar_vid);

        // Address URI Parsing and Setting
        vidUri = Uri.parse(streamingVideoURL);
        vidView.setVideoURI(vidUri);

        // Progress Bar
        if (isConnected)
            progressBar.setVisibility(View.VISIBLE);

        // Prepared Listner for Video View
        vidView.setOnPreparedListener(this);

        // Set up the Media Controller
        mediaController = new MediaController(v.getContext());

        // Set the media controller for the Video View
        vidView.setMediaController(mediaController);

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d("LifeCycle", "Video seUserVisibleHint");
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set video as Visible
            videoVisible = true;
            vidView.setVisibility(View.VISIBLE);
            // Start video
            vidView.start();


        } else {
            // Set video as Invisible
            videoVisible = false;
            // Pause if video is ready and the fragment is not visible to user
            if (videoReady) {
                vidView.pause();
                // Hide the Media Controller
                mediaController.hide();
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("LifeCycle", "Video onSaveInstanceState");
        // Salva l'ultimo punto in cui si stava visualizzando il video
        super.onSaveInstanceState(outState);
        if (vidView != null)
            outState.putInt("video_pos", vidView.getCurrentPosition());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d("LifeCycle", "Video onViewStateRestored");
        // Restore dell'ultimo punto in cui si stava visualizzando il video
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            vidView.seekTo(savedInstanceState.getInt("video_pos"));
        }
    }

    @Override
    public void onPause() {
        Log.d("LifeCycle", "Video onPause");
        super.onPause();
        vidView.pause();
    }

    @Override
    public void start() {
        try {
            vidView.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("LifeCycle", "Video onPrepared");
        // Anchor mediaController View
        mediaController.setAnchorView(v.findViewById(R.id.media_controller_video_view));

        if (videoVisible)
            mediaController.setEnabled(true);

        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                progressBar.setVisibility(View.GONE);
            }
        });

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoVisible)
                    mediaController.show();
                return false;
            }
        });

        // Video ready (settato perché la prima chiamata dell'activity è fatta s setUserVisibleHint)
        if (!videoReady)
            videoReady = true;
    }

    private class CheckFileExistence extends AsyncTask {

        private String url;
        private RemoteServer remoteServer = new RemoteServer();


        public CheckFileExistence(String url) {
            this.url = url;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Log.d("LifeCycle", "Video doInBackground");
            if (!remoteServer.checkFileExistenceOnServer(url)) {
                Log.d(TAG, "No video content");
                checkFileResult = false;
            } else checkFileResult = true;
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            Log.d("LifeCycle", "Video onPostExecute");
            if (!checkFileResult && isConnected) {
                Log.d(TAG, "Toast created. checkFile: " + checkFileResult + ", isConnected: " + isConnected);
                Toast.makeText(getActivity(), getResources().getString(R.string.no_video_content), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
